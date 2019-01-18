package base.plugin.zhs;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.Time;

/**
 * Created by chixm on 17/5/22.
 */

public class SysUtils {

    public static final byte CC_EMPTY = 0;
    public static final byte CC_OBJECT = 1;
    public static final byte CC_DBNULL = 2;
    public static final byte CC_BOOLEAN = 3;
    public static final byte CC_CHAR = 4;
    public static final byte CC_SBYTE = 5;
    public static final byte CC_BYTE = 6;
    public static final byte CC_INT16 = 7;
    public static final byte CC_UINT16 = 8;
    public static final byte CC_INT32 = 9;
    public static final byte CC_UINT32 = 10;
    public static final byte CC_INT64 = 11;
    public static final byte CC_UINT64 = 12;
    public static final byte CC_SINGLE = 13;
    public static final byte CC_DOUBLE = 14;
    public static final byte CC_DECIMAL = 15;
    public static final byte CC_DATETIME = 16;
    public static final byte CC_STRING = 18;

    public static byte getClassCode(Class<?> type)
    {
        if (type == null)
            return CC_EMPTY;
        else if (type.equals(boolean.class) || type.equals(Boolean.class))
            return CC_BOOLEAN;
        else if (type.equals(char.class) || type.equals(Character.class))
            return CC_CHAR;
        else if (type.equals(byte.class) || type.equals(Byte.class))
            return CC_BYTE;
        else if (type.equals(short.class) || type.equals(Short.class))
            return CC_INT16;
        else if (type.equals(int.class)||type.equals(Integer.class))
            return CC_INT32;
        else if (type.equals(long.class)||type.equals(Long.class))
            return CC_INT64;
        else if (type.equals(float.class) || type.equals(Float.class))
            return CC_SINGLE;
        else if (type.equals(double.class)||type.equals(Double.class))
            return CC_DOUBLE;
        else if (type.equals(Time.class))
            return CC_DATETIME;
        else if (type.equals(String.class))
            return CC_STRING;
        else
            return CC_OBJECT;
    }

    public static String[] splitString(String str,String cha)
    {
        if (null == str || str.length()<=0)
            return null;
        String[] result=str.split(cha);
        return result;
    }

    public static String inputStreamToString(InputStream inputStream) throws Exception {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        String str = result.toString(StandardCharsets.UTF_8.name());
        return str;
    }

}
