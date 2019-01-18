package base.plugin.zhs;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by chixm on 17/5/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    private Context context;
    private static DBHelper _instance;
    private int dbVersion = -1;
    private Hashtable<String,List<String[]>> dataStructs;
    private Hashtable<String,String> tblCreateSql;
    private Hashtable<Integer,List<String>> tblUpdateSql;
    public static String dbFileXml="www/plugins/base.plugin.zhs/www/db/dbstruct.xml";

    /**
     * 取DBHelper的唯一实例
     * @param context
     * @return
     */
    public synchronized static DBHelper getInstance(Context context)
    {
        if(null == _instance)
            _instance = new DBHelper(context);
        return _instance;

    }

    /**
     * 清楚当前实例对象
     */
    public synchronized static void clearInstance()
    {
        _instance=null;
    }

    /**
     * 第一次安装APP经过该方法，后续升级走onUpgrade
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        if (null == tblCreateSql)
            fillDataStruct();
        for (Map.Entry<String, String> entry : tblCreateSql.entrySet()) {
            db.execSQL(entry.getValue());
        }
    }

    /**
     * 更新数据执行的方法
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        getAllUpdateSql();
        if (null != tblUpdateSql && tblUpdateSql.size()>0)
        {
            for (Map.Entry<Integer, List<String>> entry : tblUpdateSql.entrySet()) {
                if (oldVersion < entry.getKey())
                {
                    for(int i=0;i<entry.getValue().size();i++)
                    {
                        db.execSQL(entry.getValue().get(i));
                    }
                }
            }
        }

    }

    //
    private DBHelper(Context context)
    {
        super(context,getDBName(),null,CurrentDBVersion(context));
        this.context =context;
    }

    private static String getDBName()
    {
        return "ZHSAppDBBase";
    }

    /**
     * 取Apps/HelloH5/www/dbstruct中的版本号。
     * @param context
     * @return
     */
    private static int CurrentDBVersion(Context context)
    {
        int version=1;
        try {
            InputStream inputStream =context.getAssets().open(dbFileXml);
            XmlPullParserFactory factory;
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String tagName = parser.getName();
                        if (("DBVersion").equalsIgnoreCase(tagName)) {
                            version = Integer.valueOf(parser.getAttributeValue(0));
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        }catch (Exception e)
        {
            version =1 ;
        }
        return version;
    }

    /**
     * 当前类初始化后才可以使用下面的方法取值。
     * @return Xml配置的DB版本
     */
    public int getDbVersion()
    {
        if (dbVersion <=0)
        {
            dbVersion= CurrentDBVersion(this.context);
        }
        return dbVersion;
    }


    /**
     * 根据dbstuct创建表结构
     */
    private void fillDataStruct()
    {
        dataStructs = new Hashtable<String, List<String[]>>();
        tblCreateSql = new Hashtable<String, String>();
        String tableName="";
        List<String[]> flds = new ArrayList<String[]>();
        try
        {
            //XmlResourceParser parser =context.getAssets().openXmlResourceParser("apps/HelloH5/www/dbstruct.xml");
            InputStream inputStream =context.getAssets().open(dbFileXml);
            XmlPullParserFactory factory;
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                switch (eventType)
                {
                    case XmlPullParser.START_TAG:
                        String tagName = parser.getName();
                        if (("Table").equals(tagName))
                        {
                            if (null != tableName && ""!=tableName)
                            {
                                tblCreateSql.put(tableName.toUpperCase(),getCreateSql(tableName,flds));
                                dataStructs.put(tableName.toUpperCase(),flds);
                                flds = new ArrayList<String[]>();
                            }
                            tableName = parser.getAttributeValue(0);
                        }
                        if (("Field").equals(tagName))
                        {
                            int count = parser.getAttributeCount();
                            String[] fldAttr = new String[count];
                            for (int i=0; i< count; i++)
                            {
                                fldAttr[i] = parser.getAttributeValue(i);
                            }
                            flds.add(fldAttr);
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
            tblCreateSql.put(tableName.toUpperCase(),getCreateSql(tableName,flds));
            dataStructs.put(tableName.toUpperCase(),flds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成语法
     * @param tbName
     * @param flds
     * @return
     */
    private String getCreateSql(String tbName,List<String[]> flds)
    {
        if (TextUtils.isEmpty(tbName) || null == flds || 0 == flds.size())
            return "";
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append("CREATE TABLE IF NOT EXISTS ").append(tbName).append("(");
        int count =flds.size();
        String primaryKeys="";
        String fileType="";
        for (int i=0;i<count;i++){
            Boolean isKey =false;
            if (flds.get(i).length>2 && Boolean.valueOf(flds.get(i)[0]))
                isKey=true;
            stringBuffer.append("[").append(flds.get(i)[isKey? 1:0]).append("]");
            fileType = flds.get(i)[isKey? 2:1];
            stringBuffer.append(fileType);
            if (fileType.equalsIgnoreCase("TEXT"))
                stringBuffer.append(" COLLATE NOCASE ");
            if (isKey)
            {
                if (""!=primaryKeys)
                    primaryKeys+=",";
                primaryKeys+=flds.get(i)[1];
            }
            if (i<count-1)
                stringBuffer.append(",");
        }

        if (""!=primaryKeys)
            stringBuffer.append(", PRIMARY KEY (").append(primaryKeys).append(")");
        stringBuffer.append(");");
        return stringBuffer.toString();
    }

    private void getAllUpdateSql()
    {
        tblUpdateSql = new Hashtable<Integer, List<String>>();
        try{
            //XmlResourceParser parser =context.getAssets().openXmlResourceParser("apps/HelloH5/www/dbstruct.xml");
            InputStream inputStream =context.getAssets().open(dbFileXml);
            XmlPullParserFactory factory;
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            int eventType = parser.getEventType();
            int UpdateVersion =-1;
            List<String> execSql = new ArrayList<String>();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                switch (eventType)
                {
                    case XmlPullParser.START_TAG:
                        String tagName = parser.getName();
                        if (("Update").equals(tagName))
                        {
                            if (UpdateVersion != -1) {
                                tblUpdateSql.put(UpdateVersion,execSql);
                                execSql = new ArrayList<String>();
                            }
                            UpdateVersion =Integer.valueOf(parser.getAttributeValue(0));
                        }
                        if (("SQL").equalsIgnoreCase(tagName))
                        {
                            execSql.add(parser.getAttributeValue(0));
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
            tblUpdateSql.put(UpdateVersion,execSql);
        }catch (Exception e)
        {

        }

    }

}
