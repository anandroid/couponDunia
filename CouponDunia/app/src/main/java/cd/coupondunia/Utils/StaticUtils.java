package cd.coupondunia.Utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cd.coupondunia.R;

/**
 * Created by anand on 08/02/16.
 */
public class StaticUtils {

    public static String getLocalityKeyword(Context context, Double latitude, Double longitude) {
        String keyword = null;

           if(!hasInternetConnection(context))
           {
               return null;

           }


            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 2); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                keyword=addresses.get(0).getAddressLine(0);

            } catch (IOException e) {
                e.printStackTrace();
            }



            return keyword;


    }

    public static void requestLocation(Context context, LocationListener locationListener) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria crta = new Criteria();
        crta.setAccuracy(Criteria.ACCURACY_COARSE);
        crta.setAltitudeRequired(true);
        crta.setBearingRequired(true);
        crta.setCostAllowed(true);
        crta.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(crta, true);
        Log.d("", "provider : " + provider);
        // String provider = LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider,context.getResources().getInteger(R.integer.min_time_listen), context.getResources().getInteger(R.integer.min_distance_listen), locationListener);

    }

    public static String getDistanceInString(Float distance)
    {

        if(distance>1000)
        {
            return  String.valueOf(Math.round(distance/1000)) + " kms" ;
        }

        else
            return  String.valueOf(distance)+" m ";



    }

    public static boolean hasInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }




}
