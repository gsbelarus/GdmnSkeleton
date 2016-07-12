package com.gsbelarus.gedemin.skeleton.core.data;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SyncResult;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.gsbelarus.gedemin.skeleton.base.BaseApplication;
import com.gsbelarus.gedemin.skeleton.base.BaseSyncService;
import com.gsbelarus.gedemin.skeleton.core.UnsupportedDataTypeException;
import com.gsbelarus.gedemin.skeleton.core.util.Logger;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientDeletedEntity;
import org.apache.olingo.client.api.domain.ClientDelta;
import org.apache.olingo.client.api.domain.ClientDeltaLink;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class CoreSyncService extends BaseSyncService implements CoreDatabaseManager.Callback {

    private static final String HEADER_DATABASE_VERSION = "Database-Version";
    private static final int DEFAULT_DEMO_VERSION = 1;
    private static final int DEFAULT_SCHEMA_VERSION = 2;

    private String url;
    private String namespace;
    private CoreDatabaseManager databaseManager;
    private ODataClient oDataClient;
    private boolean insertedDataSent;

    /**
     * @return url для соединения или null для создания демо данных.
     * Если null, вызывается {@link CoreSyncService#onCreateDemoDatabase(CoreDatabaseManager)}
     */
    @Nullable
    protected abstract String getUrl(Account account, Bundle extras);

    @NonNull
    protected abstract String getNamespace();

    protected void onHandleRow(String tableName, ContentValues contentValues) {

    }

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
    protected void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) throws IOException {
        boolean isSuccessful = false;
        databaseManager.beginTransactionNonExclusive();
        try {
            url = getUrl(account, extras);
            if (url == null) {
                databaseManager.recreateDatabase();
                databaseManager.setVersion(DEFAULT_DEMO_VERSION, this);
                onCreateDemoDatabase(databaseManager);

            } else {
                if (databaseManager.getVersion() == DEFAULT_DEMO_VERSION) {
                    databaseManager.recreateDatabase();
                }
                namespace = getNamespace();

                ODataRetrieveResponse<Edm> metadataResponse = oDataClient.getRetrieveRequestFactory().getMetadataRequest(url).execute();
                Map<String, String> tokens = databaseManager.setVersion(getDatabaseVersion(metadataResponse), this);

                pullData(metadataResponse.getBody(), tokens);

            try {
                pushDeletedData();
                pushUpdatedData(metadataResponse.getBody());
                pushInsertedData(metadataResponse.getBody());
//                startSync(getApplicationContext(), this.getClass(), TypeTask.FOREGROUND);
            } catch (Exception e) {
                LogUtil.d(e.getMessage());

                // Tracking exception
                BaseApplication.getInstance().trackException(e);
                try {
                    pushDeletedData();
                    pushUpdatedData(metadataResponse.getBody());
                    pushInsertedData(metadataResponse.getBody());
                    if (insertedDataSent) {
                        ContentResolver.requestSync(account, authority, extras);
                    }
                } catch (Exception e) {
                    Logger.d(e.getMessage());
                }
            }

            databaseManager.setTransactionSuccessful();
            isSuccessful = true;
        } catch (HttpClientException e) {
            Logger.e(e);
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            } else if (e.getCause() instanceof IllegalStateException) {
                throw new UnknownHostException(e.toString());
            } else {
                throw new RuntimeException(e);
            }
        } finally {
            databaseManager.endTransaction();
            if (isSuccessful) {
                databaseManager.notifyDataChanged();
            }
        }
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
            Logger.d(s, request.getHeader(s));
        Logger.d(request.execute().getStatusCode());
    }

    /**
     * Смотри {@link CoreSyncService#getUrl(Account, Bundle)}
     */
    public void onCreateDemoDatabase(CoreDatabaseManager coreDatabaseManager) {
        Logger.d();
    }

    /**
     * Вызывается при первой синхронизации, создает схему данных БД
     */
    @Override
    public void onCreateDatabase(CoreDatabaseManager coreDatabaseManager) {
        Logger.d();
        createDatabase(oDataClient.getRetrieveRequestFactory().getMetadataRequest(url).execute().getBody());
    }

    /**
     * Вызывается при необходимости обновить схему данных. По умолчанию пересоздает бд
     */
    @Override
    public void onUpgradeDatabase(CoreDatabaseManager coreDatabaseManager, int oldVersion, int newVersion) {
        Logger.d();
        coreDatabaseManager.recreateDatabase();
        onCreateDatabase(coreDatabaseManager);
    }

    private void createDatabase(Edm metadata) {
        for (EdmEntitySet edmEntitySet : metadata.getSchema(namespace).getEntityContainer().getEntitySets()) {
            EdmEntityType edmEntityType = metadata.getEntityType(edmEntitySet.getEntityType().getFullQualifiedName());

            databaseManager.createTable(edmEntitySet.getName(), createTableColumns(edmEntityType), edmEntityType.getKeyPropertyRefs().get(0).getName());
        }
    }

    private Map<String, String> createTableColumns(EdmEntityType edmEntityType) {
        if (edmEntityType.getKeyPropertyRefs().size() > 1)
            throw new RuntimeException("more then 1 primary key");

        EdmKeyPropertyRef edmKeyPropertyRef = edmEntityType.getKeyPropertyRefs().get(0);
        if (!edmKeyPropertyRef.getProperty().isPrimitive())
            throw new RuntimeException("primary key must be primitive");

        EdmPrimitiveTypeKind type = EdmPrimitiveTypeKind.valueOfFQN(edmKeyPropertyRef.getProperty().getType().getFullQualifiedName());
        if (type != EdmPrimitiveTypeKind.Int16 && type != EdmPrimitiveTypeKind.Int32 && type != EdmPrimitiveTypeKind.Int64) {
//                throw new RuntimeException("primary key must be int"); TODO
        }

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

                // Tracking exception
                BaseApplication.getInstance().trackException(e);
                Logger.d(e.getMessage());
            }
        }
        if (edmEntityType.getBaseType() != null) {
            columns.putAll(createTableColumns(edmEntityType.getBaseType()));
        }
        return columns;
    }

    private void pullData(Edm metadata, Map<String, String> tokens) {
        for (EdmEntitySet edmEntitySet : getSchema(metadata).getEntityContainer().getEntitySets()) {
            EdmEntityType edmEntityType = metadata.getEntityType(edmEntitySet.getEntityType().getFullQualifiedName());
            String entitySetName = edmEntitySet.getName();
            String entitySetKey = edmEntityType.getKeyPropertyRefs().get(0).getName();

            databaseManager.dropLogChangesTriggers(entitySetName);
            databaseManager.createLogChangesSyncTriggers(entitySetName, entitySetKey);
            if (tokens.get(entitySetName) == null) {
                ClientEntitySet entitySet = oDataClient.getRetrieveRequestFactory().getEntitySetRequest(
                        oDataClient.newURIBuilder(url).appendEntitySetSegment(entitySetName).build()
                ).execute().getBody();
                if (entitySet.getDeltaLink() != null) {
                    databaseManager.putToken(entitySetName, entitySet.getDeltaLink().toString());
                }

                for (ClientEntity entity : entitySet.getEntities()) {
                    insertEntity(entitySetName, entity);
                }
            } else {
                ClientDelta delta = oDataClient.getRetrieveRequestFactory().getDeltaRequest(URI.create(tokens.get(entitySetName))).execute().getBody();
                if (delta.getDeltaLink() != null) {
                    databaseManager.putToken(entitySetName, delta.getDeltaLink().toString());
                }
                for (ClientDeltaLink link : delta.getAddedLinks()) {
                    insertEntity(entitySetName, oDataClient.getRetrieveRequestFactory().getEntityRequest(link.getSource()).execute().getBody());
                }
                for (ClientDeletedEntity deletedEntity : delta.getDeletedEntities()) {
//                    deletedEntity.getId()                 TODO удаление, обновление
                }
            }
            databaseManager.dropLogChangesSyncTriggers(entitySetName);
            databaseManager.createLogChangesTriggers(entitySetName, entitySetKey);
        }
    }

    private void insertEntity(String entitySetName, ClientEntity entity) {
        ContentValues cv = new ContentValues();
        for (ClientProperty property : entity.getProperties()) {
            try {
                if (property.getValue().isPrimitive() && property.getPrimitiveValue() != null) {
                    TypeProvider.putProperty(property, cv);
                }
            } catch (UnsupportedDataTypeException e) {
                LogUtil.d(e.getMessage());

                // Tracking exception
                BaseApplication.getInstance().trackException(e);
                Logger.d(e.getMessage());
            }
        }
        onHandleRow(entitySetName, cv);
        databaseManager.insert(entitySetName, null, cv);
    }

    private void prepareChangedData(Edm metadata, Map<String, List<Map<String, String>>> changedRows, PrepareCallback prepareCallback) {
        for (final Map.Entry<String, List<Map<String, String>>> tableEntry : changedRows.entrySet()) {
            Logger.d("change", tableEntry);
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
                    String value = null;
                    if (property.getValue().asPrimitive().toValue() != null) {
                        value = property.getValue().toString();
                    }
                    values.put(property.getName(), value);
                }
                values.put("@odata.type", "#" + entityType.getFullQualifiedName().toString());
                prepareCallback.onPrepare(tableEntry.getKey(), entityType.getKeyPropertyRefs().get(0).getName(), row.get(BaseColumns._ID), values);
            }
        }
    }

    private void pushInsertedData(final Edm metadata) {
        insertedDataSent = false;                                   //temp
        prepareChangedData(metadata, getDatabaseManager().getInsertedRows(), new PrepareCallback() {
            @Override
            public void onPrepare(final String tableName, final String externalKeyName, final String internalKey,
                                  final Map<String, String> values) {
                sendData(tableName, HttpMethod.POST.name(), values, new HttpCallback() {
                    @Override
                    public void onResponse(int responseCode) {
                        Logger.d("insert", HttpStatusCode.fromStatusCode(responseCode));
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
                            insertedDataSent = true;
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
                sendData(tableName + "(" + values.get(externalKeyName) + ")", HttpMethod.PATCH.name(), values, new HttpCallback() {
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
        for (Map.Entry<String, List<Long>> tableEntry : getDatabaseManager().getDeletedRowsId().entrySet()) {
            Logger.d("delete", tableEntry);
            for (Object value : tableEntry.getValue()) {
                ODataDeleteResponse deleteResponse = oDataClient.getCUDRequestFactory().getDeleteRequest(
                        oDataClient.newURIBuilder(url)
                                .appendEntitySetSegment(tableEntry.getKey())
                                .appendKeySegment(value)
                                .build()
                ).execute();
                if (deleteResponse.getStatusCode() == HttpStatusCode.NO_CONTENT.getStatusCode()) {
                    Logger.d(getDatabaseManager().delete(CoreContract.TableLogChanges.TABLE_NAME,
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

    private int getDatabaseVersion(ODataRetrieveResponse<Edm> metadataResponse) {
        Collection<String> versions = metadataResponse.getHeader(HEADER_DATABASE_VERSION);
        if (versions != null && !versions.isEmpty())
            return Integer.valueOf(versions.iterator().next());
        return DEFAULT_SCHEMA_VERSION;
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

            // Tracking exception
            BaseApplication.getInstance().trackException(e);
        }
    }

    /**
     * Выполняется после загрузки, имеет доступ к UI потоку
     *
     * @param status статус с каким завершилась синхронизация
     *               (NO_INTERNET_CONNECTION, INVALID_RESPONSE, EMPTY_RESPONSE, TIMEOUT, NOT_REQUIRED,
     *               ELSE_FAILED, SUCCESSFUL, STOP_SYNC, CUSTOM_SYNC_FAILED)
     */
    //TODO public void onPostExecute(){}

    private interface HttpCallback {
        void onResponse(int responseCode);
    }

    private interface PrepareCallback {
        void onPrepare(String tableName, String externalKeyName, String internalKey, Map<String, String> values);
    }
}
