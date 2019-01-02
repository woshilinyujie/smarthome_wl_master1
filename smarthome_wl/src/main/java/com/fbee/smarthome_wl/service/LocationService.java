/**
 * Copyright (C) 2014-2015 Imtoy Technologies. All rights reserved.
 *
 * @charset UTF-8
 * @author xiong_it
 */
package com.fbee.smarthome_wl.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.fbee.smarthome_wl.common.ActivityPageManager;
import com.fbee.smarthome_wl.common.AppContext;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * @author xiong_it
 * @description
 * @charset UTF-8
 * @date 2015-7-20上午10:31:39
 */
public class LocationService extends Service implements LocationListener {
    private static final String TAG = "LocationSvc";
    private LocationManager locationManager;
    private StringBuffer stringBuffer;
    private String countryName;
    private String adminArea;
    private static String[] PERMISSIONS_RECORD = {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try{
            if (locationManager.getProvider(LocationManager.NETWORK_PROVIDER) != null) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ActivityPageManager.getTopActivity(), PERMISSIONS_RECORD,
                            1);
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

            } else if (locationManager.getProvider(LocationManager.GPS_PROVIDER) != null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            } else {
                Toast.makeText(this, "无法定位", Toast.LENGTH_SHORT).show();
            }

        }catch(Exception e){

        }

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();        //纬度
        double longitude = location.getLongitude();      //经度
        List<Address> locationList = getLocationList(latitude, longitude);
        if (locationList !=null && !locationList.isEmpty()) {
            stringBuffer = new StringBuffer();
            Address address = locationList.get(0);
            countryName = address.getCountryName();          //中国
            adminArea = address.getAdminArea();                //浙江省
            String locality = address.getLocality();           //杭州市
            String subLocality = address.getSubLocality();    //区域    滨江区
            String featureName = address.getFeatureName();   //滨安路55号 兴耀科技园

            AppContext.setMcountryName(countryName);
            AppContext.setMadminArea(adminArea);
            AppContext.setMlocality(locality);
            AppContext.setMsubLocality(subLocality);
            AppContext.setMfeatureName(featureName);
        }
        if (countryName != null || adminArea != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    private List<Address> getLocationList(double latitude, double longitude) {
        Geocoder gc = new Geocoder(this, Locale.getDefault());
        List<Address> locationList = null;
        try {
            locationList = gc.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return locationList;
    }
}
