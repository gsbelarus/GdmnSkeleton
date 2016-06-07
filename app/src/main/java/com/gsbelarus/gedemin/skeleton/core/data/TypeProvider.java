package com.gsbelarus.gedemin.skeleton.core.data;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.gsbelarus.gedemin.skeleton.base.data.SQLiteDataType.SQLiteStorageTypes;
import com.gsbelarus.gedemin.skeleton.core.UnsupportedDataTypeException;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.domain.ClientPrimitiveValue;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;

import java.util.Date;
import java.util.UUID;

public class TypeProvider {

    private static byte[] parseString(String strOfBytes) {
        String[] bytesString = strOfBytes.split(" ");
        byte[] bytes = new byte[bytesString.length];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = Byte.parseByte(bytesString[i]);
        }
        return bytes;
    }

    public static ClientProperty getProperty(ODataClient oDataClient, EdmProperty edmProperty, String value) throws UnsupportedDataTypeException {
        ClientPrimitiveValue.Builder builder = oDataClient.getObjectFactory().newPrimitiveValueBuilder();
        switch (EdmPrimitiveTypeKind.valueOfFQN(edmProperty.getType().getFullQualifiedName())) {
            case String:
                builder.buildString(value);
                break;
            case Guid:
                builder.buildGuid(UUID.fromString(value));
                break;
            case Binary:
            case Byte:
            case SByte:
                builder.setType(edmProperty.getType()).setValue(parseString(value));
                break;
            case Int16:
                builder.buildInt16(Short.valueOf(value));
                break;
            case Int32:
                builder.buildInt32(Integer.valueOf(value));
                break;
            case Int64:
                builder.buildInt64(Long.valueOf(value));
                break;
            case Double:
            case Single:
            case Decimal:
                builder.setType(edmProperty.getType()).setValue(Double.valueOf(value));
                break;
            case Boolean:
                builder.buildBoolean(Boolean.valueOf(value));
                break;
            case Date:
            case DateTimeOffset:
                builder.setType(edmProperty.getType()).setValue(CoreDatabaseManager.getDateTime(value));
                break;
            default:
                throw new UnsupportedDataTypeException(edmProperty.getType().getName());
        }
        return oDataClient.getObjectFactory().newPrimitiveProperty(edmProperty.getName(), builder.build());
    }

    public static ContentValues putProperty(ClientProperty property, ContentValues cv) throws UnsupportedDataTypeException {
        if (property.hasPrimitiveValue()) {
            EdmPrimitiveType edmType = property.getPrimitiveValue().getType();
            switch (EdmPrimitiveTypeKind.valueOfFQN(edmType.getFullQualifiedName())) {
                case String:
                case Guid:
                case Binary:
                case Byte:
                case SByte:
                    cv.put(property.getName(), property.getValue().toString());
                    return cv;
                case Int16:
                case Int32:
                case Int64:
                    cv.put(property.getName(), Long.valueOf(property.getValue().toString()));
                    return cv;
                case Double:
                case Single:
                case Decimal:
                    cv.put(property.getName(), Double.valueOf(property.getValue().toString()));
                    return cv;
                case Boolean:
                    cv.put(property.getName(), Boolean.valueOf(property.getValue().toString()));
                    return cv;
                case Date:
                case DateTimeOffset:
                    Date date = (Date) property.getPrimitiveValue().toValue();
                    cv.put(property.getName(), CoreDatabaseManager.getDateTime(date));
                    return cv;
            }
        }
        throw new UnsupportedDataTypeException(property.getValue().getTypeName());
    }

    @NonNull
    public static SQLiteStorageTypes convertToSqlStorageType(EdmType edmType) throws UnsupportedDataTypeException {
        if (edmType.getKind() == EdmTypeKind.PRIMITIVE) {
            switch (EdmPrimitiveTypeKind.valueOfFQN(edmType.getFullQualifiedName())) {
                case String:
                case Guid:
                    return SQLiteStorageTypes.TEXT;
                case Int16:
                case Int32:
                case Int64:
                    return SQLiteStorageTypes.INTEGER;
                case Binary:
                case Byte:
                case SByte:
                    return SQLiteStorageTypes.BLOB;
                case Double:
                case Single:
                    return SQLiteStorageTypes.REAL;
                case Decimal:
                case Date:
                case DateTimeOffset:
                case Boolean:
                    return SQLiteStorageTypes.NUMERIC;
            }
        }
        throw new UnsupportedDataTypeException(edmType.getFullQualifiedName().getFullQualifiedNameAsString());
    }

    public static String getCheck(EdmProperty edmProperty) {
        EdmType edmType = edmProperty.getType();
        if (edmProperty.isPrimitive()) {
            switch (EdmPrimitiveTypeKind.valueOfFQN(edmType.getFullQualifiedName())) {
                case String:
                case Guid:
                    return edmProperty.getMaxLength() != null && edmProperty.getMaxLength() != Integer.MAX_VALUE
                            ? " CHECK (length(" + edmProperty.getName() + ") < " + edmProperty.getMaxLength() + ")"
                            : " ";
                case Boolean:
                    return " CHECK (" + edmProperty.getName() + " IN (0, 1))";
            }
        }

        return "";
    }

    public static String getDefaultValue(EdmProperty edmProperty) {
        return edmProperty.getDefaultValue() != null ? " DEFAULT " + edmProperty.getDefaultValue() : "";
    }

    public static String getNullable(EdmProperty edmProperty) {
        return edmProperty.isNullable() ? "" : ""; //TODO tmp for test //" NOT NULL ";
    }
}
