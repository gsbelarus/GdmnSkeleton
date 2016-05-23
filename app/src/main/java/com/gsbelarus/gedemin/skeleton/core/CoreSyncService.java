package com.gsbelarus.gedemin.skeleton.core;

import android.content.ContentValues;
import android.content.Intent;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.gsbelarus.gedemin.skeleton.base.BaseSyncService;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientEntitySetIterator;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpStatusCode;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class CoreSyncService extends BaseSyncService implements CoreDatabaseManager.Callback {

    private String url;
    private String namespace;
    private CoreDatabaseManager databaseManager;
    private ODataClient oDataClient;

    protected void onHandleRow(String tableName, ContentValues contentValues) {

    }

    @NonNull
    protected abstract String getUrl();

    @NonNull
    protected abstract String getNamespace();

    @Override
    public void onCreate() {
        super.onCreate();

        databaseManager = CoreDatabaseManager.getInstance(getApplicationContext());
        databaseManager.open();
        oDataClient = ODataClientFactory.getClient();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        databaseManager.close();
    }

    @Override
    protected void handleIntentBackground(Intent intent) throws Exception {
        url = getUrl();
        namespace = getNamespace();

        publishProcess(100, 0);
        boolean isSuccessful = false;
        databaseManager.beginTransactionNonExclusive();
        try {
            int serverVersion = 13;
            databaseManager.setVersion(serverVersion, this);

            Edm edm = oDataClient.getRetrieveRequestFactory().getMetadataRequest(url).execute().getBody();

            pullData(edm);
            publishProcess(100, 50);

            pushDeletedData();
            pushUpdatedData(edm);
            pushInsertedData(edm);
//            pullData(edm);

            databaseManager.setTransactionSuccessful();
            isSuccessful = true;
        } finally {
            databaseManager.endTransaction();
            if (isSuccessful) {
                databaseManager.notifyDataChanged();
            }
        }
        publishProcess(100, 100);
    }

    private void test() {
        ClientEntity entity = oDataClient.getObjectFactory().newEntity(new FullQualifiedName(namespace, "Category"));
        entity.getProperties().add(oDataClient.getObjectFactory().newPrimitiveProperty("ID",
                oDataClient.getObjectFactory().newPrimitiveValueBuilder().buildInt32(123)));
        entity.getProperties().add(oDataClient.getObjectFactory().newPrimitiveProperty("Name",
                oDataClient.getObjectFactory().newPrimitiveValueBuilder().buildString("TestName")));
        ODataEntityCreateRequest<ClientEntity> request = oDataClient.getCUDRequestFactory().getEntityCreateRequest(
                oDataClient.newURIBuilder(url).appendEntitySetSegment("Categories").build(),
                entity
        );
        request.setFormat(ODataFormat.APPLICATION_JSON);
        for (String s : request.getHeaderNames())
            LogUtil.d(s, request.getHeader(s));
        LogUtil.d(request.execute().getStatusCode());
    }

    @Override
    public void onCreateDatabase(CoreDatabaseManager coreDatabaseManager) {
        LogUtil.d();
        createDatabase(oDataClient.getRetrieveRequestFactory().getMetadataRequest(url).execute().getBody());
    }

    @Override
    public void onUpgradeDatabase(CoreDatabaseManager coreDatabaseManager, int oldVersion, int newVersion) {
        LogUtil.d();
        coreDatabaseManager.recreateDatabase();
        onCreateDatabase(coreDatabaseManager);
    }

    private void createDatabase(Edm metadata) {
        for (EdmEntitySet edmEntitySet : metadata.getSchema(namespace).getEntityContainer().getEntitySets()) {
            EdmEntityType edmEntityType = metadata.getEntityType(edmEntitySet.getEntityType().getFullQualifiedName());
            if (edmEntityType.getKeyPropertyRefs().size() > 1)
                throw new RuntimeException("more then 1 primary key");

            Map<String, String> columns = new LinkedHashMap<>();
            for (String propertyName : edmEntityType.getPropertyNames()) {
                EdmProperty edmProperty = edmEntityType.getStructuralProperty(propertyName);
                try {
                    columns.put(edmProperty.getName(),
                            TypeProvider.convertToSqlStorageType(edmProperty.getType()) +
                                    TypeProvider.getCheck(edmProperty) +
                                    TypeProvider.getNullable(edmProperty) +
                                    TypeProvider.getDefaultValue(edmProperty));

                } catch (UnsupportedDataTypeException e) {
                    LogUtil.d(e.getMessage());
                }
            }
            databaseManager.createTable(edmEntitySet.getName(), columns, edmEntityType.getKeyPropertyRefs().get(0).getName());
        }
    }

    private void pullData(Edm metadata) {
        for (EdmEntitySet edmEntitySet : getSchema(metadata).getEntityContainer().getEntitySets()) {
            EdmEntityType edmEntityType = metadata.getEntityType(edmEntitySet.getEntityType().getFullQualifiedName());
            String entitySetName = edmEntitySet.getName();
            String entitySetKey = edmEntityType.getKeyPropertyRefs().get(0).getName();
            ClientEntitySetIterator<ClientEntitySet, ClientEntity> entitySetIterator =
                    oDataClient.getRetrieveRequestFactory().getEntitySetIteratorRequest(
                            oDataClient.newURIBuilder(url).appendEntitySetSegment(entitySetName).build()
                    ).execute().getBody();

            databaseManager.dropLogChangesTriggers(entitySetName);
            databaseManager.createLogChangesSyncTriggers(entitySetName, entitySetKey);
            while (entitySetIterator.hasNext()) {
                ClientEntity entity = entitySetIterator.next();
                ContentValues cv = new ContentValues();
                for (ClientProperty property : entity.getProperties()) {
                    try {
                        if (property.getValue().isPrimitive() && property.getPrimitiveValue() != null) {
                            TypeProvider.putProperty(property, cv);
                        }
                    } catch (UnsupportedDataTypeException e) {
                        LogUtil.d(e.getMessage());
                    }
                }
                onHandleRow(entitySetName, cv);
                if (databaseManager.insert(entitySetName, null, cv) == null) {
//                            LogUtil.d(databaseManager.update(entitySetName, cv, entitySetKey + "=?",
//                                    new String[]{cv.getAsString(entitySetKey)}));
                }
            }
            databaseManager.dropLogChangesSyncTriggers(entitySetName);
            databaseManager.createLogChangesTriggers(entitySetName, entitySetKey);
        }
    }

    private void prepareChangedData(Edm metadata, Map<String, List<Map<String, String>>> changedRows, PrepareCallback prepareCallback) {
        for (final Map.Entry<String, List<Map<String, String>>> tableEntry : changedRows.entrySet()) {
            LogUtil.d("change", tableEntry);
            final EdmEntityType entityType = getSchema(metadata).getEntityContainer().getEntitySet(tableEntry.getKey()).getEntityType();

            for (final Map<String, String> row : tableEntry.getValue()) {
                List<ClientProperty> properties = new ArrayList<>();
                for (Map.Entry<String, String> value : row.entrySet()) {
                    if (!value.getKey().equals(BaseColumns._ID)) {
                        properties.add(TypeProvider.getProperty(
                                oDataClient,
                                entityType.getStructuralProperty(value.getKey()),
                                value.getValue()));
                    }
                }

                Map<String, String> values = new LinkedHashMap<>();
                for (ClientProperty property : properties) {
                    values.put(property.getName(), property.getValue().toString());
                }
                prepareCallback.onPrepare(tableEntry.getKey(), entityType.getKeyPropertyRefs().get(0).getName(), row.get(BaseColumns._ID), values);
            }
        }
    }

    private void pushInsertedData(Edm metadata) {
        prepareChangedData(metadata, getDatabaseManager().getInsertedRows(), new PrepareCallback() {
            @Override
            public void onPrepare(final String tableName, final String externalKeyName, final String internalKey,
                                  final Map<String, String> values) {
                sendData(tableName, "POST", values, new HttpCallback() {
                    @Override
                    public void onResponse(int responseCode) {
                        if (responseCode == HttpURLConnection.HTTP_CREATED) {
//                            ClientEntity entity = oDataClient.getObjectFactory().newEntity(entityType.getFullQualifiedName());
//                            entity.getProperties().addAll(properties);
//                            ODataEntityCreateResponse insertResponse = oDataClient.getCUDRequestFactory().getEntityCreateRequest(
//                                    oDataClient.newURIBuilder(url).appendEntitySetSegment(tableEntry.getKey()).build(),
//                                    entity
//                            ).execute();
//
//                            if (insertResponse.getStatusCode() == HttpStatusCode.CREATED.getStatusCode()) {
//                                getDatabaseManager().beginTransaction();
//                                try {
//                                    getDatabaseManager().delete(CoreContract.TableLogChanges.TABLE_NAME,
//                                            CoreContract.TableLogChanges.COLUMN_INTERNAL_ID + "=" + row.get(BaseColumns._ID),
//                                            null);
//                                    getDatabaseManager().delete(tableEntry.getKey(),
//                                            BaseColumns._ID + "=" + row.get(BaseColumns._ID),
//                                            null);
//                                    isPush = true;
//                                    getDatabaseManager().transactionSuccessful();
//                                } finally {
//                                    getDatabaseManager().endTransaction();
//                                }
//                            }
                            getDatabaseManager().dropLogChangesTriggers(tableName);
                            getDatabaseManager().delete(CoreContract.TableLogChanges.TABLE_NAME,
                                    CoreContract.TableLogChanges.COLUMN_INTERNAL_ID + "=" + internalKey,
                                    null);
                            getDatabaseManager().delete(tableName,
                                    BaseColumns._ID + "=" + internalKey,
                                    null);
                            getDatabaseManager().createLogChangesTriggers(tableName, externalKeyName);
                        }
                    }
                });
            }
        });
    }

    private void pushUpdatedData(Edm metadata) {
        prepareChangedData(metadata, getDatabaseManager().getUpdatedRows(), new PrepareCallback() {
            @Override
            public void onPrepare(final String tableName, final String externalKeyName, final String internalKey,
                                  final Map<String, String> values) {
                sendData(tableName, "PATCH", values, new HttpCallback() {
                    @Override
                    public void onResponse(int responseCode) {
                        if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                            getDatabaseManager().dropLogChangesTriggers(tableName);
                            getDatabaseManager().delete(CoreContract.TableLogChanges.TABLE_NAME,
                                    CoreContract.TableLogChanges.COLUMN_INTERNAL_ID + "=" + internalKey,
                                    null);
                            getDatabaseManager().createLogChangesTriggers(tableName, externalKeyName);
                        }
                    }
                });
            }
        });
    }

    private void pushDeletedData() {
        for (Map.Entry<String, List<Object>> tableEntry : getDatabaseManager().getDeletedRowsId().entrySet()) {
            LogUtil.d("delete", tableEntry);
            for (Object value : tableEntry.getValue()) {
                ODataDeleteResponse deleteResponse = oDataClient.getCUDRequestFactory().getDeleteRequest(
                        oDataClient.newURIBuilder(url)
                                .appendEntitySetSegment(tableEntry.getKey())
                                .appendKeySegment(value)
                                .build()
                ).execute();
                if (deleteResponse.getStatusCode() == HttpStatusCode.NO_CONTENT.getStatusCode()) {
                    LogUtil.d(getDatabaseManager().delete(CoreContract.TableLogChanges.TABLE_NAME,
                            CoreContract.TableLogChanges.COLUMN_EXTERNAL_ID + "=" + value,
                            null));
                }
            }
        }
    }

    private EdmSchema getSchema(Edm metadata) {
        EdmSchema edmSchema = metadata.getSchema(namespace);
        if (edmSchema == null) throw new RuntimeException("schema not found");
        return edmSchema;
    }

    public CoreDatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    // TODO: 23.05.2016 - переделать через API библиотеки
    private void sendData(String tableName, String requestMethod, Map<String, String> values, HttpCallback httpCallback) {
        try {
            URL url1 = new URL(url + tableName);
            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod(requestMethod);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(new Gson().toJson(values));
            out.close();

            httpCallback.onResponse(conn.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private interface HttpCallback {
        void onResponse(int responseCode);
    }

    private interface PrepareCallback {
        void onPrepare(String tableName, String externalKeyName, String internalKey, Map<String, String> values);
    }
}
