package nz.co.monitorbm.ionicgpswrapper

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.TextView;
import android.util.Log;

public class CordovaIonicGpsWrapper extends CordovaPlugin implements LocationListener {

    public Location location = null;
    private static String TAG = "GPSWrapper";
    protected LocationManager locationManager;
    protected LocationListener locationListener;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        Log.d(TAG, "===== GPSWrapper Initialize ====");
    }

    @Override
    public boolean execute(String action, JSONArray data, final CallbackContext callbackContext) throws JSONException {
        boolean callBackResult = false;
        Log.d(TAG, "execute() called. action:" + action);

        switch (action) {
            case "GetCurrentLocation": {
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
        }
        return callBackResult;
    }

    private void bindLocationServices() {
        Context context = this.cordova.getActivity().getApplicationContext();
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "bindLocationServices error: " + e.getMessage());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        Log.d(TAG, "onLocationChanged - Lat: " + location.getLatitude() + " Lon: " + location.getLongitude(););
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