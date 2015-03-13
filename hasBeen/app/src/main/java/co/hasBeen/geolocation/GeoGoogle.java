package co.hasBeen.geolocation;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by zuby on 2015-01-12.
 */
public class GeoGoogle {
    Context mContext;
    Geocoder gcd;
    List<Address> address;
    String place_name;
    public GeoGoogle(Context context) {
        mContext = context;
        gcd = new Geocoder(mContext, Locale.getDefault());
    }


    public static LatLng getLocation(Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        if(locationManager!=null) {
            Log.d("Location Manager", "is active");
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Double lat, lon;
            try {
                lat = location.getLatitude();
                lon = location.getLongitude();
                Log.i("location", lat + " , " + lon);
                return new LatLng(lat, lon);
            } catch (Exception e1) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                try {
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                    return new LatLng(lat, lon);
                }catch(Exception e) {
                    e.printStackTrace();
                }
                return new LatLng(0, 0);
            }
        }
        return new LatLng(0, 0);
    }
    public static String getCountry(Context context, double lat , double lon) {
        List<Address> address = new ArrayList<>();
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        try {
            address = gcd.getFromLocation(lat,lon, 1);
        }catch(Exception e) {
            e.printStackTrace();
        }
        if (address.size()>0) {
//            Log.i("Address Country", address.get(0).getCountryName());
            return address.get(0).getCountryName();
        }
        return null;
    }
    public static String getCity(Context context, double lat , double lon){
        List<Address> address = new ArrayList<>();
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        try {
            address = gcd.getFromLocation(lat, lon, 1);
        }catch(Exception e) {
            e.printStackTrace();
        }
        if (address.size()>0) {
//            Log.i("Address City", address.get(0).getAdminArea());
            return address.get(0).getAdminArea();
        }
        return null;
    }

}
