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
   private Activity activity ;
   private CallbackContext myCallbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        activity = cordova.getActivity();
        myCallbackContext = callbackContext;
        file =args.getString(0);
        if (cordova.hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)&& cordova.hasPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            if (action.equals("printPdf")){
                this.printPdf(file,callbackContext);
                return true;
            }
        }else{
            String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
            getPermissions(permissions,PrintPermission);
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
}