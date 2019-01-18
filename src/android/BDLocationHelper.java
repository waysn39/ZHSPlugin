package base.plugin.zhs;


import android.content.Context;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;


public  class BDLocationHelper {
  private LocationClientOption option = new LocationClientOption();
  private LocationClient ml = null;
  private CallbackContext callbackContext;

  public BDLocationHelper(Context context, CallbackContext callback) {
    callbackContext = callback;
    option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
    option.setCoorType("bd09ll");
    option.setOpenGps(true);
    option.setIsNeedAddress(true);
    option.setIgnoreKillProcess(false);
    ml = new LocationClient(context);
    ml.setLocOption(option);
    ml.registerLocationListener(myL);
  }


  public BDAbstractLocationListener myL = new BDAbstractLocationListener() {
    @Override
    public void onReceiveLocation(com.baidu.location.BDLocation bdLocation) {
      if (bdLocation != null) {
        JSONObject data = new JSONObject();
        try {
          JSONObject object = new JSONObject();
          object.put("type", bdLocation.getLocType());
          object.put("latitude", bdLocation.getLatitude());
          object.put("longitude", bdLocation.getLongitude());
          JSONObject address = new JSONObject();
          address.put("adcode", bdLocation.getAddress().adcode);
          address.put("address", bdLocation.getAddress().address);
          address.put("city", bdLocation.getAddress().city);
          address.put("cityCode", bdLocation.getAddress().cityCode);
          address.put("country", bdLocation.getAddress().country);
          address.put("countryCode", bdLocation.getAddress().countryCode);
          address.put("district", bdLocation.getAddress().district);
          address.put("province", bdLocation.getAddress().province);
          address.put("street", bdLocation.getAddress().street);
          address.put("streetNumber", bdLocation.getAddress().streetNumber);
          object.put("address", address);
          object.put("city", bdLocation.getCity());
          object.put("cityCode", bdLocation.getCityCode());
          data.put("status", "success");
          data.put("location", object);
          data.put("code", "000");
          data.put("errMsg", "");
          PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, data);
          callbackContext.sendPluginResult(pluginResult);
        } catch (JSONException e) {
          e.printStackTrace();
        }
        ml.stop();
      }
    }
  };

  public void startLocate() {
    ml.start();
  }
}

