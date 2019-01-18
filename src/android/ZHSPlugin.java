package base.plugin.zhs;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import base.plugin.zhs.PrintHelper;

/**
 * This class echoes a string called from JavaScript.
 */
public class ZHSPlugin extends CordovaPlugin {

   //private String file = "/storage/emulated/0/Download/Java_manual.pdf";
   private String file = "";
   private Activity activity ;
   private CallbackContext myCallbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        activity = cordova.getActivity();
        myCallbackContext = callbackContext;

        // JSONArray array = new JSONArray();
        // array.put("xxx");
        // array.put("Test");
        // array.put("StudentId,Name");
        // array.put("1,jinyiming");
        // this.insertTable(array);
        // JSONArray array1 = new JSONArray();
        // array1.put("xxx");
        // array1.put("Test");
        // array1.put("StudentId,Name");
        // array1.put("");
        // array1.put("");
        // array1.put("");
        // array1.put("");
        // this.getDataSet(array1);

        switch(action){
            case "printPdf":
                file =args.getString(0);
                if (cordova.hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)&& cordova.hasPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    this.printPdf(file,callbackContext);
                    return true;
                }else{
                    String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    getPermissions(permissions,PrintPermission);
                    return true;
                }
            case "execSQL":
                this.execSQL(args);
                return true;
            case "getDataSet":
                this.getDataSet(args);
                return true;
            case "updateTable":
                this.updateTable(args);
                return true;
             case "deleteTable":
                this.deleteTable(args);
                return true;
            case "insertTable":
                this.insertTable(args);
                return true;
        }

        return false;
        
    }

    //打印Pdf文件
    private void printPdf(String filePath,CallbackContext callbackContext){
        PrintHelper.doPrintPdf(activity,file);
    }

    //权限相关

    private static int PrintPermission = 1;

    private void getPermission(String permission,int requestCode){
        cordova.requestPermission(this,requestCode,permission);
    }

    private void getPermissions(String[] permissions,int requestCode){
        cordova.requestPermissions(this,requestCode,permissions);
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        super.onRequestPermissionResult(requestCode, permissions, grantResults);
        for(int r:grantResults)
        {
            if(r == PackageManager.PERMISSION_DENIED)
            {

                myCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR,"权限获取失败"));
                return;
            }
        }
        if (requestCode==PrintPermission){
            this.printPdf(file,myCallbackContext);
        }
    }

    //SQLite相关

    public void execSQL(JSONArray argus)
    {
        boolean isSuccess =false;
        base.plugin.zhs.LocalDataHelper dataHelper = new base.plugin.zhs.LocalDataHelper(activity);
         try {
            String sql = argus.getString(0);
            isSuccess =dataHelper.execSQL(sql);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        if (isSuccess)
            myCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
         else
            myCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR));
    }

    public void getDataSet(JSONArray argus)
    {
        JSONArray array = new JSONArray();
        base.plugin.zhs.LocalDataHelper dataHelper = new base.plugin.zhs.LocalDataHelper(activity);
        if (null != argus && argus.length() ==6)
        {
            try {

                String tableName = argus.getString(0);
                String selectFields = argus.getString(1);
                String whereCluse = argus.getString(2);
                String whereArgus = argus.getString(3);
                String order = argus.getString(4);
                String limit = argus.getString(5);
                String[] select = base.plugin.zhs.SysUtils.splitString(selectFields,",");
                String[] whereA = base.plugin.zhs.SysUtils.splitString(whereArgus,",");
                array = dataHelper.getJsonData(tableName,select,whereCluse,whereA,order,limit);

            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }

         myCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK,array));
        //return array;
    }

    public void updateTable(JSONArray argus)
    {
        boolean isSuccess= false;
        base.plugin.zhs.LocalDataHelper dataHelper = new base.plugin.zhs.LocalDataHelper(activity);
        if (null != argus && argus.length() ==5) {
            try {
                String tableName = argus.getString(0);
                String updateFields = argus.getString(1);
                String updateValue = argus.getString(2);
                String primaryKey = argus.getString(3);
                String primaryValue = argus.getString(4);

                String[] fields = base.plugin.zhs.SysUtils.splitString(updateFields,",");
                String[] values = base.plugin.zhs.SysUtils.splitString(updateValue,",");
                String[] key= base.plugin.zhs.SysUtils.splitString(primaryKey,",");
                String[] value = base.plugin.zhs.SysUtils.splitString(primaryValue,",");
                dataHelper.update(tableName,fields,values,key,value);
                isSuccess=true;
            }catch (Exception e)
            {
                e.printStackTrace();
                isSuccess=false;
            }
        }
        if (isSuccess)
            myCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
        else
            myCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR));
    }

    public void insertTable(JSONArray argus)
    {
        boolean isSuccess =false;
        base.plugin.zhs.LocalDataHelper dataHelper = new base.plugin.zhs.LocalDataHelper(activity);
        if (null != argus && argus.length() == 3)
        {
            try {
                String tableName = argus.getString(0);
                String insertFields = argus.getString(1);
                String insertValue = argus.getString(2);
                String[] fields = base.plugin.zhs.SysUtils.splitString(insertFields,",");
                String[] values = base.plugin.zhs.SysUtils.splitString(insertValue,",");
                dataHelper.insert(tableName,fields,values);
                isSuccess=true;
            }catch (Exception e)
            {
                e.printStackTrace();
                isSuccess=false;
            }
        }
       if (isSuccess)
           myCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
       else
           myCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR));
    }

    public void deleteTable(JSONArray argus)
    {
        boolean isSuccess= false;
        base.plugin.zhs.LocalDataHelper dataHelper = new base.plugin.zhs.LocalDataHelper(activity);
        if (null != argus && argus.length() ==3) {
            try {
                String tableName = argus.getString(0);
                String primaryKey = argus.getString(1);
                String primaryValue = argus.getString(2);

                String[] key= base.plugin.zhs.SysUtils.splitString(primaryKey,",");
                String[] value = base.plugin.zhs.SysUtils.splitString(primaryValue,",");
                dataHelper.delete(tableName,key,value);
                isSuccess=true;
            }catch (Exception e)
            {
                e.printStackTrace();
                isSuccess=false;
            }
        }
        if (isSuccess)
            myCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
        else
            myCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR));
    }
}