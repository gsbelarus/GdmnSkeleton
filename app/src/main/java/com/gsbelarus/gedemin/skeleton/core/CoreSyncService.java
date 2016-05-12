package com.gsbelarus.gedemin.skeleton.core;

import android.content.ContentValues;
import android.content.Intent;
import android.provider.BaseColumns;

import com.gsbelarus.gedemin.skeleton.base.BaseSyncService;

import org.apache.olingo.client.api.ODataClient;
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

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CoreSyncService extends BaseSyncService {

    private CoreDatabaseManager databaseManager;
    private ODataClient oDataClient;

    public void onHandleRow(String tableName, ContentValues contentValues) {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        databaseManager = CoreDatabaseManager.getInstance(getApplicationContext());
        oDataClient = ODataClientFactory.getClient();
    }

    @Override
    protected void handleIntentBackground(Intent intent) {
        String url = "http://services.odata.org/V4/(S(5i2qvfszd0uktnpibrgfu2qs))/OData/OData.svc/";
        databaseManager.beginTransaction();
        try {
            int version = 32;
            LogUtil.d("server db version: " + version);
            databaseManager.updateMetadata(version, new Date());

            Edm edm = oDataClient.getRetrieveRequestFactory().getMetadataRequest(url).execute().getBody();

            publishProcess(100, 0);
            createDatabase(edm);
            publishProcess(100, 50);
            pullData(edm, url);
            if (pushData(edm, url)) {
                try {
//                    pullData(edm, url);
                } catch (Exception e) {
                    LogUtil.d(e);
                }
            }

            publishProcess(100, 100);
            databaseManager.notifyDataChanged();
            databaseManager.transactionSuccessful();
        } finally {
            databaseManager.endTransaction();
        }
    }

    private void createDatabase(Edm metadata) {
        if (databaseManager.migrateIfNeeded()) {
            for (EdmSchema edmSchema : metadata.getSchemas()) {
                for (EdmEntitySet edmEntitySet : edmSchema.getEntityContainer().getEntitySets()) {
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
        }
    }

    private void pullData(Edm metadata, String url) {
        for (EdmSchema edmSchema : metadata.getSchemas()) {
            for (EdmEntitySet edmEntitySet : edmSchema.getEntityContainer().getEntitySets()) {
                EdmEntityType edmEntityType = metadata.getEntityType(edmEntitySet.getEntityType().getFullQualifiedName());
                String entitySetName = edmEntitySet.getName();
                String entitySetKey = edmEntityType.getKeyPropertyRefs().get(0).getName();

                ClientEntitySetIterator<ClientEntitySet, ClientEntity> entitySetIterator =
                        oDataClient.getRetrieveRequestFactory().getEntitySetIteratorRequest(
                                oDataClient.newURIBuilder(url).appendEntitySetSegment(entitySetName).build()
                        ).execute().getBody();

                databaseManager.dropLogChangesTriggers(entitySetName);
                databaseManager.createLogChangesSyncTriggers(entitySetName, entitySetKey);
                try {
                    while (entitySetIterator.hasNext()) {
                        ClientEntity entity = entitySetIterator.next();
                        if (entity.getEditLink() != null && entity.getEditLink().toString().contains("/"))
                            continue;
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
                } finally {
                    databaseManager.dropLogChangesSyncTriggers(entitySetName);
                    databaseManager.createLogChangesTriggers(entitySetName, entitySetKey);
                }
            }
        }
    }

    private boolean pushData(Edm metadata, String url) {
//        for (Map.Entry<String, List<Map<String, String>>> tableEntry : getDatabaseManager().getInsertedRows().entrySet()) {
//            LogUtil.d("insert", tableEntry);
//            getDatabaseManager().dropLogChangesTriggers(tableEntry.getKey());
//            try {
//                for (Map<String, String> row : tableEntry.getValue()) {
//
//
//                    ArrayList<OProperty<?>> properties = new ArrayList<>();
//                    for (Map.Entry<String, Object> value : row.entrySet()) {
//                        if (!value.getKey().equals(BaseColumns._ID)) {
//                            properties.add(ODataSqLiteTypeConverter.convertToODataProperty(
//                                    entityType.findProperty(value.getKey()).getType(), value.getKey(), value.getValue()));
//                        }
//                    }
//                    consumer.createEntity(tableEntry.getKey()).properties(properties).execute();
//                    oDataClient.getCUDRequestFactory().getEntityCreateRequest()
//
//                    getDatabaseManager().beginTransaction();
//                    getDatabaseManager().delete(CoreContract.TableLogChanges.TABLE_NAME,
//                            CoreContract.TableLogChanges.COLUMN_INTERNAL_ID + "=" + row.get(BaseColumns._ID),
//                            null);
//                    getDatabaseManager().delete(tableEntry.getKey(),
//                            BaseColumns._ID + "=" + row.get(BaseColumns._ID),
//                            null);
//                    getDatabaseManager().transactionSuccessful();
//                    getDatabaseManager().endTransaction();
//                }
//            } finally {
//                getDatabaseManager().createLogChangesTriggers(tableEntry.getKey(), entityType.getKeys().get(0));
//            }
//        }

        for (Map.Entry<String, List<Object>> tableEntry : getDatabaseManager().getDeletedRowsId().entrySet()) {
            LogUtil.d("delete", tableEntry);
            for (Object value : tableEntry.getValue()) {
                ODataDeleteResponse deleteResponse = oDataClient.getCUDRequestFactory().getDeleteRequest(
                        oDataClient.newURIBuilder(url)
                                .appendEntitySetSegment(tableEntry.getKey())
                                .appendKeySegment(value)
                                .build()
                ).execute();
                if (deleteResponse.getStatusCode() == 204) {
                    LogUtil.d(getDatabaseManager().delete(CoreContract.TableLogChanges.TABLE_NAME,
                            CoreContract.TableLogChanges.COLUMN_EXTERNAL_ID + "=" + value,
                            null));
                }
            }
        }
        return false;
    }

//    private void pushData(ODataConsumer consumer) {
//        EdmDataServices metadata = consumer.getMetadata();
//
//        for (Map.Entry<String, List<Map<String, Object>>> tableEntry : dbHelper.getInsertedRows().entrySet()) {
//            LogUtil.d("insert", tableEntry);
//            EdmEntityType entityType = metadata.findEdmEntitySet(tableEntry.getKey()).getType();
//            dbHelper.dropLogChangesTriggers(tableEntry.getKey());
//            try {
//                for (Map<String, Object> row : tableEntry.getValue()) {
//                    ArrayList<OProperty<?>> properties = new ArrayList<>();
//                    for (Map.Entry<String, Object> value : row.entrySet()) {
//                        if (!value.getKey().equals(BaseColumns._ID)) {
//                            properties.add(ODataSqLiteTypeConverter.convertToODataProperty(
//                                    entityType.findProperty(value.getKey()).getType(), value.getKey(), value.getValue()));
//                        }
//                    }
//                    consumer.createEntity(tableEntry.getKey()).properties(properties).execute();
//
//                    dbHelper.beginTransaction();
//                    dbHelper.delete(GdmnContract.TableLogChanges.TABLE_NAME,
//                            GdmnContract.TableLogChanges.COLUMN_INTERNAL_ID + "=" + row.get(BaseColumns._ID),
//                            null);
//                    dbHelper.delete(tableEntry.getKey(),
//                            BaseColumns._ID + "=" + row.get(BaseColumns._ID),
//                            null);
//                    dbHelper.successfulTransaction();
//                    dbHelper.endTransaction();
//                }
//            } finally {
//                dbHelper.createLogChangesTriggers(tableEntry.getKey(), entityType.getKeys().get(0));
//            }
//        }
//        for (Map.Entry<String, List<Map<String, Object>>> tableEntry : dbHelper.getUpdatedRows().entrySet()) {
//            LogUtil.d("update", tableEntry);
//        }
//        for (Map.Entry<String, List<Long>> tableEntry : dbHelper.getDeletedRowsId().entrySet()) {
//            LogUtil.d("delete", tableEntry);
//            for (Long value : tableEntry.getValue()) {//TODO поддержка других типов
//                consumer.deleteEntity(tableEntry.getKey(), value.intValue()).execute();
//                LogUtil.d(dbHelper.delete(GdmnContract.TableLogChanges.TABLE_NAME,
//                        GdmnContract.TableLogChanges.COLUMN_EXTERNAL_ID + "=" + value,
//                        null));
//            }
//        }
//        //после успешной отправки новых запесей удалить их с клиента и провести новую синхронизацию
//    }


    public CoreDatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
