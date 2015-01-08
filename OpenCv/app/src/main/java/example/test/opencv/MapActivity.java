package example.test.opencv;

import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zuby on 2015-01-08.
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.view_map);
        mapFragment.getMapAsync(this);
    }
    public void onMapReady(GoogleMap map) {

        UiSettings setting = map.getUiSettings();
        map.addMarker(new MarkerOptions()
                .position(getLocation())
                .title("hasBeen").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)).flat(true));
        setting.setZoomControlsEnabled(true);
        setting.setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(getLocation(), 13));
        new FourSquareAsyncTask().execute();
    }
    public LatLng getLocation(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(locationManager!=null) {
            Log.d("Location Manager","is active");
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
   public class FourSquareAsyncTask extends AsyncTask<Object, Void, JSONObject> {
        final static String FOURSQAURE_URL = "https://api.foursquare.com/v2/venues/search";
        final static String CLIENT_ID = "C3SQKKLIP04VY4LZNPCFKIXKSUU41NEB1T3NZL45KSE1VJ05";
        final static String CLIENT_SECRET = "LTBZTBAGTOUZSK4DDD5DUMR34EGJNBGTS5L4SLZSY33P1OXJ";
        final static String ID_PARAM = "client_id";
        final static String SECRET_PARAM = "client_secret";
        final static String DAY_PARAM =  "v";
        final static String LOCATION_PARAM= "ll";
        final static String QUERY_PARAM = "query";
        String ll ;
        @Override
        protected JSONObject doInBackground(Object... params) {
            LatLng location = getLocation();
            ll = location.latitude+","+location.longitude;
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            Uri uri;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            String currentDate = simpleDateFormat.format(new Date());
            try {

                uri = Uri.parse(FOURSQAURE_URL).buildUpon()
                        .appendQueryParameter(ID_PARAM, CLIENT_ID)
                        .appendQueryParameter(SECRET_PARAM,CLIENT_SECRET)
                        .appendQueryParameter(DAY_PARAM, currentDate)
                        .appendQueryParameter(LOCATION_PARAM,ll).build();
//                Log.e("Url",uri.toString());
                HttpGet get = new HttpGet(uri.toString());
                response = client.execute(get);
                return new JSONObject(EntityUtils.toString(response.getEntity()));

            }catch(Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
//                Log.e("object",result.toString());
                JSONObject jsonObject = result.getJSONObject("response");
                JSONArray jsonArray = new JSONArray(jsonObject.get("venues").toString());
                Toast.makeText(getBaseContext(),jsonArray.getJSONObject(0).get("name")+"",Toast.LENGTH_LONG).show();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
