package nz.co.monitorbm.ionicgpswrapper;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.TextView;
import android.util.Log;

import org.apache.cordova.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;
import java.util.Arrays;
import java.math.BigInteger;
import java.util.ArrayList;

public class CordovaIonicGpsWrapper extends CordovaPlugin implements LocationListener {

    public Location location = null;
    public static boolean locating = false;
    public static String locatingState = "";
    private static String TAG = "GPSWrapper";
    protected LocationManager locationManager;
    protected LocationListener locationListener;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        Log.d(TAG, "===== GPSWrapper Initialize ====");
        bindLocationServices();
    }

    @Override
    public boolean execute(String action, JSONArray data, final CallbackContext callbackContext) throws JSONException {
        boolean callBackResult = false;
        Log.d(TAG, "execute() called. action:" + action);

        switch (action) {
            case "GetLastKnownLocation": {
                String latLon = "";
                if (location != null) {
                    latLon = "Lat: " + location.getLatitude() + " Lon: " + location.getLongitude();
                } else {
                    latLon = "Still waiting on location";
                }
                callbackContext.success(latLon);
                callBackResult = true;
                break;
            }
            case "StartListeningToLocation": {
                if (locating == true && locatingState.equals("locating")) {
                    Log.d(TAG, "Device is already locating position");
                    callBackResult = true;
                    break;
                }
                locating = true;
                cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        locatingState = "locating";
                        Log.d(TAG, "StartListeningToLocation locating: " + locating);
                        while (locating == true) {
                            Log.d(TAG, "StartListeningToLocation looping threadId:" + Thread.currentThread().getId());
                            Location currentLocation = getCurrentLocation();
                            JSONObject latLonJson = new JSONObject();
                            try {
                                if (currentLocation != null) {
                                    Thread.sleep(500);
                                    latLonJson.put("lat", currentLocation.getLatitude());
                                    latLonJson.put("lon", currentLocation.getLongitude());
                                    PluginResult _pluginResult = new PluginResult(PluginResult.Status.OK, latLonJson);
                                    callbackContext.sendPluginResult(_pluginResult);
                                } else {
                                    Log.d(TAG, "Still waiting on location");
                                    latLonJson.put("lat", "0.0");
                                    latLonJson.put("lon", "0.0");
                                    PluginResult _pluginResult = new PluginResult(PluginResult.Status.OK, latLonJson);
                                    callbackContext.sendPluginResult(_pluginResult);
                                }
                                locatingState = "stopped";
                                locating = false;
                                Log.d(TAG, "StartListeningToLocation looping end try");
                            } catch (InterruptedException e) {
                                Log.e(TAG, "error while (StartListeningToLocation) sleeping:", e);
                                locatingState = "stopped";
                                locating = false;
                            } catch (JSONException jsonException) {
                                Log.e(TAG, "error jsonException (StartListeningToLocation) jsonException:", jsonException);
                                locatingState = "stopped";
                                locating = false;
                            }
                        }
                    }
                });
                callBackResult = true;
                break;
            }
            case "StopListeningToLocation": {
                locating = false;
                try {
                    while (locatingState.equals("stopped") == false) {
                        Log.e(TAG, "stopListeningToLocation called while loop locatingState: " + locatingState);
                        Thread.sleep(100);
                    }
                    Log.d(TAG, "stopListeningToLocation called: " + locating);
                    locationManager.removeUpdates(this);
                    locatingState = "stopped";
                } catch (Exception e) {
                    Log.e(TAG, "stopListeningToLocation error: " + e.getMessage());
                }

                callbackContext.success("StopListeningToLocation success");
                callBackResult = true;
                break;
            }
        }
        return callBackResult;
    }

    private void bindLocationServices() {
        Context context = this.cordova.getActivity().getApplicationContext();
        try {
            Log.d(TAG, "bindLocationServices called");
            locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, this);
            if (locationManager != null) {
                Log.d(TAG, "bindLocationServices locationManager not null");
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    location = location;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "bindLocationServices error: " + e.getMessage());
        }
    }

    private void startListeningToLocation() {
        locating = true;
    }

    private Location getCurrentLocation() {
        return this.location;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged called");
        if (location != null) {
            this.location = location;
            Log.d(TAG, "onLocationChanged - Lat: " + location.getLatitude() + " Lon: " + location.getLongitude());
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }
}
