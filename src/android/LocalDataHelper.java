package base.plugin.zhs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.text.TextUtils;
import base.plugin.zhs.*;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by chixm on 17/5/18.
 */

public class LocalDataHelper {

    private base.plugin.zhs.DBHelper dbHelper;

    public LocalDataHelper(Context context)
    {
        dbHelper = base.plugin.zhs.DBHelper.getInstance(context);
    }

    public synchronized SQLiteDatabase getDatabase()
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db;
    }

    public synchronized JSONArray getJsonData(String tableName,String[] selectFields,
                                                String whereClause,String[] whereArgs,String order,String limit) {
        JSONArray array = new JSONArray();
        Object[][] data= null;
        try {
            data = DBOpenDataSet(tableName,selectFields,whereClause,whereArgs,order,limit);
            for (int i =0; i<data.length;i++)
            {
                JSONObject object = new JSONObject();
                for (int j=0;j<data[i].length;j++)
                {
                    object.put(selectFields[j],data[i][j]);
                }
                array.put(object);
            }
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return array;
    }

    public synchronized Object[][] DBOpenDataSet(String tableName,String[] selectFields,
                                                 String whereClause,String[] whereArgs,String order,String limit) {
        Object[][] result = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getDatabase();
            cursor = db.query(tableName, selectFields, whereClause, whereArgs, "", "", order, limit);
            int count = cursor.getCount();
            if (count > 0) {
                int retcount = selectFields.length;
                result = new Object[count][retcount];
                int j = 0;
                while (cursor.moveToNext()) {
                    for (int i = 0; i < retcount; i++) {
                        result[j][i] = getValue(cursor, i);
                    }
                    j++;
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result = new Object[0][0];
            return result;
        } finally {
            if (null != cursor)
                cursor.close();
            if (null != db)
                db.close();
        }
    }

    public synchronized boolean execSQL(String sql)
    {
        SQLiteDatabase database=getDatabase();
        Boolean isSuccess=false;
        database.beginTransaction();
        try {
            database.execSQL(sql);
            database.setTransactionSuccessful();
            isSuccess =true;
        }catch (Exception ex)
        {
            ex.printStackTrace();
            isSuccess =false;
        }
        finally {
            database.endTransaction();
            database.close();
        }
        return isSuccess;
    }

    public void update(String tableName ,String[] updateFields, Object[] UpdateValues,String[] primaryKeys,String[] keyValues) {
        SQLiteDatabase db = null;
        try {
            db = getDatabase();
            ContentValues values = getContentValues(updateFields, UpdateValues);
            db.update(tableName, values, getWhereClause(primaryKeys), keyValues);
        } finally {
            if (db != null)
                db.close();
        }
    }

    public void insert(String tableName,String[] insertFields, Object[] insertValues) {
        SQLiteDatabase db = null;
        try {
            db = getDatabase();
            ContentValues values = getContentValues(insertFields, insertValues);
            db.insert(tableName, null, values);
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (db != null)
                db.close();
        }
    }

    public void delete(String tableName,String[] primaryKeys,String[] keyValues) {
        String whereClause = getWhereClause(primaryKeys);
        SQLiteDatabase db = null;
        try {
            db = getDatabase();
            if (TextUtils.isEmpty(whereClause))
                db.delete(tableName, null, null);
            else
                db.delete(tableName, whereClause, keyValues);
        } finally {
            if (db != null)
                db.close();
        }
    }

    private Object getValue(Cursor cursor, int columnIndex) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            int type = cursor.getType(columnIndex);
            switch (type) {
                case Cursor.FIELD_TYPE_NULL:
                    return null;
                case Cursor.FIELD_TYPE_INTEGER:
                    return cursor.getInt(columnIndex);
                case Cursor.FIELD_TYPE_FLOAT:
                    return cursor.getDouble(columnIndex);
                case Cursor.FIELD_TYPE_STRING:
                    return cursor.getString(columnIndex);
                case Cursor.FIELD_TYPE_BLOB:
                    return cursor.getBlob(columnIndex);
            }
        } else {
            if (cursor.isNull(columnIndex))
                return null;
            if (((SQLiteCursor) cursor).isString(columnIndex))
                return cursor.getString(columnIndex);
            if (((SQLiteCursor) cursor).isLong(columnIndex))
                return cursor.getInt(columnIndex);
            if (((SQLiteCursor) cursor).isFloat(columnIndex))
                return cursor.getDouble(columnIndex);
            if (((SQLiteCursor) cursor).isBlob(columnIndex))
                return cursor.getBlob(columnIndex);
        }
        return null;
    }

    private ContentValues getContentValues(String[] flds,Object[] values) {
        ContentValues result = new ContentValues();
        for (int i = 0; i < flds.length; i++) {
            if (null == values[i])
                result.putNull(flds[i]);
            else
                switch (base.plugin.zhs.SysUtils.getClassCode(values[i].getClass())) {
                    case base.plugin.zhs.SysUtils.CC_EMPTY:
                        result.putNull(flds[i]);
                        break;
                    case base.plugin.zhs.SysUtils.CC_BOOLEAN:
                        result.put(flds[i], (Boolean) values[i]);
                        break;
                    case base.plugin.zhs.SysUtils.CC_BYTE:
                        result.put(flds[i], (Byte) values[i]);
                        break;
                    case base.plugin.zhs.SysUtils.CC_CHAR:
                    case base.plugin.zhs.SysUtils.CC_STRING:
                        result.put(flds[i], (String) values[i]);
                        break;
                    case base.plugin.zhs.SysUtils.CC_INT16:
                        result.put(flds[i], (Short) values[i]);
                        break;
                    case base.plugin.zhs.SysUtils.CC_INT32:
                        result.put(flds[i], (Integer) values[i]);
                        break;
                    case base.plugin.zhs.SysUtils.CC_INT64:
                        result.put(flds[i], (Long) values[i]);
                        break;
                    case base.plugin.zhs.SysUtils.CC_DECIMAL:
                    case base.plugin.zhs.SysUtils.CC_DOUBLE:
                        result.put(flds[i], (Double) values[i]);
                        break;
                    case base.plugin.zhs.SysUtils.CC_SINGLE:
                        result.put(flds[i], (Float) values[i]);
                        break;
                    case base.plugin.zhs.SysUtils.CC_OBJECT:
                        result.put(flds[i], (byte[]) values[i]);
                        break;
                    default:
                        result.putNull(flds[i]);
                }
        }
        return result;
    }

    private String getWhereClause(String[] fields)
    {
        if (null==fields || 0 == fields.length)
            return null;
        String result="";
        for (int i= 0; i<fields.length;i++)
        {
            if (""!=result)
                result += " And ";
            result+= fields[i]+ " =? ";
        }
        return result;
    }
}
