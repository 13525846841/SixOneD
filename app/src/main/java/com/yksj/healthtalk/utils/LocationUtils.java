package com.yksj.healthtalk.utils;

import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationUtils {
	/**
	 * 获得当前的位置
	 * @param context
	 * @return 位置获取成功返回double[]{纬度,经度} 失败返回null
	 */
	public static double[] getLocation(Context context){
		LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
/*		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);//海拔
		criteria.setBearingRequired(false);//方位
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setCostAllowed(true);*/
		Location location = null;
		try{
		List<String> allProviders = locationManager.getAllProviders();
		for (String provider : allProviders) {
//			String provider = locationManager.getBestProvider(criteria,true);
			location = locationManager.getLastKnownLocation(provider);
			if(location != null)break;
		}
//			location = locationManager.getLastKnownLocation(provider);
//			if(location == null)location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}catch(SecurityException e){
			e.printStackTrace();
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}
		if(location == null)return null; 
		double latitude = location.getLatitude();//纬度
		double longitude = location.getLongitude();//经度
		double[] locations = {latitude,longitude};
		return locations;
	}
	
	
	/**
	 * 获得当前的位置
	 * @param context
	 * @return 位置
	 */
	public static Location getMyLocation(Context context ){
		LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
//		Criteria criteria = new Criteria();
//		criteria.setAccuracy(Criteria.ACCURACY_FINE);
//		criteria.setAltitudeRequired(false);//海拔
//		criteria.setBearingRequired(false);//方位
//		criteria.setPowerRequirement(Criteria.POWER_LOW);
//		criteria.setCostAllowed(true);
//		String provider = locationManager.getBestProvider(criteria,true);
	    List<String> providersList = locationManager.getAllProviders();
	    Location location = null;
	    for(String provider : providersList){
	    	if(provider == null)
	    		continue;
	    	boolean is = locationManager.isProviderEnabled(provider);
	    	locationManager.requestLocationUpdates(provider, 0, 0, new GPSListener());
	    	location = locationManager
					.getLastKnownLocation(provider);
	    	if(location != null)
	    		break;
	    }
//		try{
//			location = locationManager.getLastKnownLocation(provider);
//		}catch(SecurityException e){
//		}catch(IllegalArgumentException e){
//		}
		if(location == null)
			return null; 
		return location;
	}
	
	
	/**
	 * 获取经纬度  
	 */
	public static String getlocationManager(Context context,String proviso,String space) {
		// TODO Auto-generated method stub
		String extra = "";
		LocationManager locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if(locManager == null)
			return null;
	    List<String> providersList = locManager.getAllProviders();
	    Location location = null;
	    for(String provider : providersList){
	    	if(provider == null)
	    		continue;
	    	boolean is = locManager.isProviderEnabled(provider);
	    	GPSListener listener = new GPSListener();
	    	locManager.requestLocationUpdates(provider, 0, 0,listener);
	    	location = listener.getLocation();
	    	if(location == null)
		    	location = locManager
						.getLastKnownLocation(provider);
	    	if(location != null)
	    		break;
	    }
		double latitude = 0d;
		double longitude = 0d;
		if (location == null) {
			return null;
		} else {
			latitude = location.getLatitude();// 获取纬度
			longitude = location.getLongitude();// 获取经度
			extra = proviso+"," + longitude + "-" + latitude
					+ "-"+space;
		}
		return extra;
	}
	
	
	private static class GPSListener implements LocationListener{
        private Location location;
		@Override
		public void onLocationChanged(Location location) {
			this.location = location;
		}
		
		public Location getLocation() {
			return location;
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
		
	}

	
}
