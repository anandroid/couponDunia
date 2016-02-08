package cd.coupondunia.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import cd.coupondunia.Adapters.OutLetAdapter;
import cd.coupondunia.AppController;
import cd.coupondunia.Objects.OutLet;
import cd.coupondunia.R;
import cd.coupondunia.UI.VerticalSpaceItemDecoration;
import cd.coupondunia.Utils.StaticUtils;
import cd.coupondunia.Utils.Volley;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    RecyclerView recyclerView;
    OutLetAdapter outLetAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    //ProgressBar locationProgressBar;
    TextView locationTextView;
    ImageView locationImageView;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;


    String locationKeyWord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setViews();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();




        return super.onOptionsItemSelected(item);
    }

    private void setViews()
    {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(20));

        //locationProgressBar=(ProgressBar)findViewById(R.id.locationProgressBar);
        locationTextView=(TextView)findViewById(R.id.locationTextView);
        locationImageView=(ImageView)findViewById(R.id.locationImageView);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetNearBy().executePreHeavy();
            }
        });




    }

    @Override
    protected void onStart()
    {
        if ( mGoogleApiClient == null ) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        initLocationClient();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mGoogleApiClient.disconnect();
        super.onPause();
    }

    private void initLocationClient()
    {
        boolean isLocationEnabled = checkAndPrompt();

        if (isLocationEnabled){

            mGoogleApiClient.connect();
        }
    }

    private boolean checkAndPrompt()
    {
        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
            dialog.setMessage(getApplicationContext().getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(getApplicationContext().getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(getApplicationContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }

        return gps_enabled;
    }



    @Override
    public void onConnected(Bundle bundle) {

        LocationListener locationListener= new LocationListener() {
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
                    public void onLocationChanged(final Location location) {


                        //Call the API only if the distance is changed much

                        if(location.distanceTo(mLastLocation)>getResources().getInteger(R.integer.min_distance_listen))
                        {
                            new GetNearBy().executePreHeavy();
                        }

                        mLastLocation=location;


                    }
                };

        StaticUtils.requestLocation(this,locationListener);

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        new GetNearBy().executePreHeavy();



    }

    @Override
    public void onConnectionSuspended(int i) {

        Toast.makeText(getApplicationContext(),getResources().getString(R.string.location_error),Toast.LENGTH_LONG).show();

        //locationProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Toast.makeText(getApplicationContext(),getResources().getString(R.string.location_error),Toast.LENGTH_LONG).show();

        //locationProgressBar.setVisibility(View.GONE);
    }

    private void initNearyBy()
    {
        if (mLastLocation != null) {

            locationKeyWord= StaticUtils.getLocalityKeyword(this,mLastLocation.getLatitude(),mLastLocation.getLongitude());

            if(locationKeyWord!=null) {
                locationTextView.setText(locationKeyWord);
            }

            new GetNearBy().executePreHeavy();



        }


    }





    public class GetNearBy extends Volley {

        ArrayList<OutLet> outLets;

        String url="http://staging.couponapitest.com/task.txt";

        protected void loadFromCache()
        {
            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(url);
            if(entry != null){
                try {
                    String data = new String(entry.data, "UTF-8");

                    outLets = parseNearBy(data);

                    outLetAdapter = new OutLetAdapter(getApplicationContext(),outLets);
                    recyclerView.setAdapter(outLetAdapter);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPreExecute()
        {

            swipeRefreshLayout.setRefreshing(true);
        }


        @Override
        protected void onPreExecuteHeavy()
        {

            if (mLastLocation != null) {

                locationKeyWord= StaticUtils.getLocalityKeyword(getApplicationContext(),mLastLocation.getLatitude(),mLastLocation.getLongitude());

                if(locationKeyWord==null)
                {
                    locationKeyWord = Prefs.getString("last_location_keyword","Location unavailable");
                }

            }

            runOnUiThread(new Runnable() {
                public void run() {
                    locationTextView.setText(locationKeyWord);
                    locationImageView.setVisibility(View.VISIBLE);
                }
            });






            loadFromCache();


        }

        @Override
        protected void setParamsUrlPriority()
        {

            setUrl(url);
            setParams(new HashMap<String, String>());
            setPriority(Request.Priority.IMMEDIATE);

        }

        @Override
        protected void onPostExecute(String response)
        {

            try {
                JSONObject jsonObject = new JSONObject(response);

                int code = jsonObject.getJSONObject("status").getInt("rcode");

                if(code==200)
                {

                    outLets  = parseNearBy(response);

                    if(outLetAdapter!=null)
                    {
                        outLetAdapter.notifyDataSetChanged();
                    }
                    else {

                        outLetAdapter = new OutLetAdapter(getApplicationContext(),outLets);
                        recyclerView.setAdapter(outLetAdapter);
                    }


                    Prefs.putString("last_location_keyword",locationKeyWord);

                }
                else
                {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.some_error),Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


            swipeRefreshLayout.setRefreshing(false);



        }

        private ArrayList<OutLet> parseNearBy(String data)
        {

            ArrayList<OutLet> outLets = new ArrayList<OutLet>();

            try {
                JSONObject jsonObject = new JSONObject(data);

                JSONArray dataArray = jsonObject.getJSONArray("data");

                int size = dataArray.length();

                for(int i=0;i<size;i++)
                {

                    OutLet outLet = new OutLet();
                    outLet.parseAndSet(dataArray.getJSONObject(i));

                    if(mLastLocation!=null) {

                        Location locationOutlet = new Location("outlet");
                        locationOutlet.setLatitude(new Double(outLet.latitude));
                        locationOutlet.setLongitude(new Double(outLet.longitude));
                        outLet.distance = new Float(locationOutlet.distanceTo(mLastLocation));
                    }


                    outLets.add(outLet);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Collections.sort(outLets, new Comparator<OutLet>(){
                public int compare(OutLet o1, OutLet o2){
                    return Math.round(o1.distance - o2.distance);
                }
            });

            return outLets;


        }

    }


}
