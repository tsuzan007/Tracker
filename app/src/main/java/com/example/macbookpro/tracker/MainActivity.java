package com.example.macbookpro.tracker;

import android.app.Dialog;
import android.app.Fragment;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity  implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    SupportMapFragment mapFragment;
    GoogleMap mMap;
    List<LatLng> poly;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if(googleservicesavailable()){
            initUserLocation();

        }else{
            Toast.makeText(this, "Google Services not available", Toast.LENGTH_SHORT).show();

        }
    }

    private void initUserLocation() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker in Sydney, Australia, and move the camera.
        LatLng marshall = new LatLng(44.453353, -95.759699);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.addMarker(new MarkerOptions().position(marshall).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marshall,20));
        Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(44.443726, -95.786866),
                        new LatLng(44.450572, -95.795044))
                .width(5)
                .color(Color.RED));



    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public boolean googleservicesavailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int isavailable = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (isavailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApiAvailability.isUserResolvableError(isavailable)) {
            Dialog dialog = googleApiAvailability.getErrorDialog(this, isavailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Google play services not available", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        ApplicationInfo ai = null;
        String value="";
        String origin="44.4519266,-95.7630236";
        String destination="44.443726,-95.786866";
        String waypoints=
                "44.4532471,-95.76010819999999|" +
                        "44.4519266,-95.7630236|" +
                        "44.44847239999999,-95.76366100000001|44.447724,-95.763867|44.4473358,-95.7651639|" +
                        "44.4358258,-95.76202219999999|44.4322355,-95.7665222|44.437656,-95.77854259999998|" +
                        "44.4337999,-95.78392730000002|44.4304911,-95.79743259999998|" +
                        "44.4343307,-95.794848|44.4423132,-95.79428480000001|44.4547284,-95.75919590000001";
        try {
            ai = this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
            value=(String) ai.metaData.get("com.google.android.direction.API_KEY");
            Log.e(".....",value);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        HttpLoggingInterceptor httpLoggingInterceptor=new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder=new OkHttpClient.Builder();
        builder.addInterceptor(httpLoggingInterceptor);

        Retrofit.Builder retrofitbuilder=new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/directions/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit=retrofitbuilder.client(builder.build()).build();
        GoogleApiContract myGoogleApiClient=retrofit.create(GoogleApiContract.class);
        Call<GeoCoded_WayPoints> myGoogleApiClientRequest=myGoogleApiClient.getDirection(origin, destination,waypoints,value);
        myGoogleApiClientRequest.enqueue(new Callback<GeoCoded_WayPoints>() {
            @Override
            public void onResponse(Call<GeoCoded_WayPoints> call, Response<GeoCoded_WayPoints> response) {
                String polyline=response.body().getRoutes().get(0).getOverview_polyline().getPoints();
               poly =decodePoly(polyline);
                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .addAll(poly)
                        .width(5)
                        .color(Color.RED));


            }

            @Override
            public void onFailure(Call<GeoCoded_WayPoints> call, Throwable t) {

            }
        });




    }


    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
