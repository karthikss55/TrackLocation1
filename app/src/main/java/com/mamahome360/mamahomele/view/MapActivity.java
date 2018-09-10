package com.mamahome360.mamahomele.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.PolyUtil;
import com.mamahome360.mamahomele.R;
import com.mamahome360.mamahomele.model.DatabaseHelper;
import com.mamahome360.mamahomele.model.ResObj;
import com.mamahome360.mamahomele.remote.ApiUtils;
import com.mamahome360.mamahomele.remote.UserService;
import com.mamahome360.mamahomele.utils.Location_ForeGround_Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mamahome360.mamahomele.utils.SendingNotification.CHANNEL_ID;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static int NOTOFICATION_ID = 3;
    @BindView(R.id.bt_logout)
    Button bt_Logout;
    FusedLocationProviderClient mFusedLocationClient;
    Marker mMarker;
    ArrayList<Double> Latitudes, Longitudes;
    ArrayList<String> SplittedData;
    ArrayList<Double> poly_Latitudes, poly_Longitudes;
    ArrayList<String> poly_SplittedData;
    BroadcastReceiver broadcastReceiver;
    String user_id, lat_long, kms, CurrentDate, msg, formattedTime, check, end_time;
    double lat, lon;
    Date date1, date2 = null;
    Boolean isMarkerRotating;
    Polyline polyline;
    UserService userService;
    DatabaseHelper databaseHelper;
    String userName, Subward_id, latlon;
    LatLng latLng;
    ArrayList<LatLng> LatLangSubWard;
    SharedPreferences prefs;
    NotificationManager notificationManager;
    private GoogleMap mMap;
    private Bitmap myLogo;
    private Drawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        databaseHelper = new DatabaseHelper(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        isMarkerRotating = false;
        //date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        CurrentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        kms = "0";
        SplittedData = new ArrayList<>();
        Latitudes = new ArrayList<>();
        Longitudes = new ArrayList<>();
        prefs = getSharedPreferences("MH_LE_DATA", MODE_PRIVATE);
        userName = prefs.getString("USER_NAME", null);
        user_id = prefs.getString("USER_ID", null);
        Subward_id = prefs.getString("WARD_ASSIGNED", null);
        latlon = prefs.getString("LAT_LON", null);
        // check = prefs.getBoolean("FIELD_CHECK",false);
        drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.motorbike);
        myLogo = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(myLogo);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        userService = ApiUtils.getUserService(getApplicationContext());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("MapActivity", "Resume");

        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    getTime();
                    int[] type = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};
                    if (!isNetWorkAvailable(type)) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MapActivity.this);
                        builder1.setMessage("Turn On Your Internet Connection");
                        builder1.setCancelable(false);
                        builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }
                    LatLng latLng = new LatLng(Double.parseDouble(intent.getExtras().getString("lat")),
                            Double.parseDouble(intent.getExtras().getString("lng")));
                    String Latitude = intent.getExtras().getString("lat");
                    String Longitude = intent.getExtras().getString("lng");
                    float bearing1 = (float) bearingBetweenLocations(mMarker, latLng);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng)
                            .zoom(16)
                            .bearing(bearing1)
                            .tilt(30)
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    moveVechile(mMarker, latLng);
                    String LatLngText = databaseHelper.getColumnLatlng(CurrentDate);

                    //   bt_Logout.setText(km);

                    DrawCopiedDataPolyline(LatLngText + "," + Latitude + "," + Longitude);

                    int color = getResources().getColor(R.color.colorPrimary);
                    check = databaseHelper.getColumnTOCheck(CurrentDate);
                    Log.e("Checking", check.toString());

                    if (PolyUtil.containsLocation(latLng, LatLangSubWard, true)) {
                        //   Toast.makeText(getApplicationContext(),"You are in the ward",Toast.LENGTH_SHORT).show();
                        if (check.equals("1")) {
//                          String da1= date1.toString();
//                          String da2= date2.toString();

                            if (date1.before(date2)) {
                                Log.e("Checking3", date1.toString() + "" + date2.toString());
                                // Toast.makeText(getApplicationContext(), "You are on time", Toast.LENGTH_LONG).show();
                                fieldLogin(user_id, CurrentDate, formattedTime, null, Latitude, Longitude, null);
                                // Log.e("TIME_CHECK",now.getTime().toString());
                                //  Log.e("TO_TIME_CHECK",to_time.getTime().toString());

                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                        0, getIntent(), 0);
                                Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                        .setContentTitle("Field Login")
                                        .setStyle(new NotificationCompat.BigTextStyle().bigText("Field Entered Time : " + formattedTime))
                                        .setColor(color)
                                        .setAutoCancel(true)
                                        .setContentIntent(pendingIntent)
                                        .setSmallIcon(R.drawable.motorbike)
                                        .setVibrate(new long[]{1000, 1000, 1000, 1000})
                                        .build();
                                notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.notify(NOTOFICATION_ID, notification);
                                databaseHelper.updateUserFl(false, CurrentDate);

                            } else {
                                Toast.makeText(getApplicationContext(), "Time Exceeds", Toast.LENGTH_SHORT).show();
                                Intent Notificationintent = new Intent(getApplicationContext(), RemarkActivity.class);
                                Notificationintent.putExtra("Latitude", Latitude);
                                Notificationintent.putExtra("Longitude", Longitude);
                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                        0, Notificationintent, 0);
                                Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                        .setContentTitle("Late Field Login")
                                        .setStyle(new NotificationCompat.BigTextStyle().bigText("Field Entered Time : " + formattedTime))
                                        .setColor(color)
                                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})

                                        .setAutoCancel(true)
                                        .setContentIntent(pendingIntent)
                                        .setSmallIcon(R.drawable.motorbike)
                                        .build();
                                notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.notify(NOTOFICATION_ID, notification);
                                databaseHelper.updateUserFl(false, CurrentDate);

                            }
                        }
                    }


                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("LOCATION_UPDATE"));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        DrawSubWard(latlon);
        setTime("07:31:00");
        if (databaseHelper.checkDateExist(CurrentDate)) {
            String LatLngText = databaseHelper.getColumnLatlng(CurrentDate);

            DrawCopiedDataPolyline(LatLngText);

        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        mMap.setTrafficEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
//                        // Got last known location. In some rare situations this can be null.
//                            // Logic to handle location object

                        if (location != null) {
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                            lat_long = Double.toString(lat) + "," + Double.toString(lon);
                            //  Toast.makeText(getApplicationContext(), lat_long, Toast.LENGTH_SHORT).show();
                            latLng = new LatLng(lat, lon);
                            mMarker = mMap.addMarker(new MarkerOptions()
                                    .flat(false)
                                    .icon(BitmapDescriptorFactory.fromBitmap(myLogo))
                                    .anchor(0.5f, 1f)
                                    .position(latLng));
                            mMarker.setRotation(270);
                            float bearing1 = (float) bearingBetweenLocations(mMarker, latLng);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                            mMap.animateCamera(CameraUpdateFactory.zoomIn());
                            //  mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(latLng)
                                    .zoom(16)
                                    .bearing(bearing1)
                                    .tilt(30)
                                    .build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            String latlongText = Double.toString(lat) + "," + Double.toString(lon);


//                                if(!databaseHelper.checkDateExist(CurrentDate)) {
//                                    currentLocation(user_id,lat_long,CurrentDate,kms);
//                                  //  databaseHelper.addUserLocation(user_id, latlongText, latlongText, CurrentDate, "0" );
//                                }

                        }
                    }

                });


    }

    private void getTime() {
        //final String msg;

        Call<ResObj> call6 = userService.gettime();

        call6.enqueue(new Callback<ResObj>() {
            @Override
            public void onResponse(Call<ResObj> call, Response<ResObj> response) {
                //   ResObj resObj = response.body().;
                if (response.isSuccessful()) {

                    msg = response.body().getMessage();
                    // Toast.makeText(getApplicationContext(),  msg,Toast.LENGTH_LONG).show();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

                    try {
                        sdf.setLenient(false);
                        date1 = sdf.parse(msg);
                    } catch (ParseException e) {
                        Toast.makeText(MapActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    formattedTime = sdf.format(date1);

                } else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResObj> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setTime(String endtime) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        try {
            date2 = sdf.parse(endtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        end_time = sdf.format(date2);
    }

    private void fieldLogin(String user_id, String login_date, String login_time, String remark, String latitude, String logitude, String address) {
        Call<ResObj> call3 = userService.recordtime(user_id, login_date, login_time, remark, latitude, logitude, address);
        call3.enqueue(new Callback<ResObj>() {

            @Override
            public void onResponse(Call<ResObj> call, Response<ResObj> response) {
                if (response.isSuccessful()) {

                } else {

                }
            }

            @Override
            public void onFailure(Call<ResObj> call, Throwable t) {
                Toast.makeText(MapActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.Logout) {
            int[] type = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};
            if (isNetWorkAvailable(type)) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
                String strDate = mdformat.format(calendar.getTime());
                Toast.makeText(getApplicationContext(), strDate, Toast.LENGTH_SHORT).show();
                fieldLogout(user_id, strDate);
                stopService(new Intent(getApplicationContext(), Location_ForeGround_Service.class));
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MH_LE_DATA", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("USER_LOGGED_IN", false);
                editor.putString("USER_NAME", null);
                editor.putString("USER_ID", null);
                editor.putString("WARD_ASSIGNED", null);
                editor.putString("LAT_LON", null);
                editor.putBoolean("CHECK_SERVICE", false);
                editor.apply();
                unregisterReceiver(broadcastReceiver);
                databaseHelper.deleteOldRecord();
                Intent in = new Intent(MapActivity.this, LoginActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(in);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Connect To The Internet To Logout ", Toast.LENGTH_LONG).show();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void fieldLogout(String user_id, String logouttime) {
        Call<ResObj> call3 = userService.flogout(user_id, logouttime);
        call3.enqueue(new Callback<ResObj>() {

            @Override
            public void onResponse(Call<ResObj> call, Response<ResObj> response) {
                if (response.isSuccessful()) {

                } else {

                }
            }

            @Override
            public void onFailure(Call<ResObj> call, Throwable t) {
                Toast.makeText(MapActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void DrawCopiedDataPolyline(String CopiedData) {
        int commas = 0;
        String[] twoStringArray, firstLang;
        poly_SplittedData = new ArrayList<>();
        poly_Latitudes = new ArrayList<>();
        poly_Longitudes = new ArrayList<>();
        for (int i = 0; i < CopiedData.length(); i++) {
            if (CopiedData.charAt(i) == ',') {
                commas++;
            }
        }
        twoStringArray = CopiedData.split(",", commas);
        for (int i = 0; i < commas - 1; i++) {
            poly_SplittedData.add(twoStringArray[i]);
        }
        for (int i = 0; i < poly_SplittedData.size(); i++) {

            if (i == 0 || i % 2 == 0) {
                Double latti = new Double(poly_SplittedData.get(i));
                poly_Latitudes.add(latti);
            } else {
                Double longi = new Double(poly_SplittedData.get(i));
                poly_Longitudes.add(longi);
            }
        }
        PolylineOptions polylineOptions = new PolylineOptions();
        for (int i = 0; i < poly_Latitudes.size(); i++) {
            LatLng latLng = new LatLng(poly_Latitudes.get(i), poly_Longitudes.get(i));
            polylineOptions.add(new LatLng(latLng.latitude, latLng.longitude))
                    .geodesic(true);
        }
        //polyline = new Polyline(null);
        //mMap.addPolyline(polylineOptions);
        if (poly_Latitudes.size() > 1) {
            polyline = mMap.addPolyline(polylineOptions);
        }
    }


    private void DrawSubWard(String LatLon) {
        int commas = 0;
        String[] twoStringArray, firstLang;
        SplittedData = new ArrayList<>();
        Latitudes = new ArrayList<>();
        Longitudes = new ArrayList<>();
        LatLangSubWard = new ArrayList<>();
        for (int i = 0; i < LatLon.length(); i++) {
            if (LatLon.charAt(i) == ',') {
                commas++;
            }
        }
        twoStringArray = LatLon.split(",", commas);
        for (int i = 0; i < commas - 1; i++) {
            SplittedData.add(twoStringArray[i]);
        }
        for (int i = 0; i < SplittedData.size(); i++) {
            /*if(i == 0 ){
                String firstLng = SplittedData.get(i);
                firstLang = firstLng.split(":", 4);
                Latitudes.add(Double.parseDouble(firstLang[3]));
            }*/
            if (i == 0 || i % 2 == 0) {
                Latitudes.add(Double.parseDouble(SplittedData.get(i)));
            } else {
                Longitudes.add(Double.parseDouble(SplittedData.get(i)));
            }
        }
        PolygonOptions polygonOptions = new PolygonOptions();
        for (int i = 0; i < Latitudes.size(); i++) {
            LatLng latLng = new LatLng(Latitudes.get(i), Longitudes.get(i));
            LatLangSubWard.add(latLng);
            polygonOptions.add(new LatLng(latLng.latitude, latLng.longitude))
                    .geodesic(true).fillColor(0x7FFF0000);
        }
        mMap.addPolygon(polygonOptions);
    }


    private double bearingBetweenLocations(Marker marker, LatLng latLng2) {
        LatLng latLng1 = marker.getPosition();
        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;
        double dLon = (long2 - long1);
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);
        double brng = Math.atan2(y, x);
        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        return brng;
    }

    public void moveVechile(final Marker myMarker, final LatLng finalPosition) {
        final LatLng startPosition = myMarker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;
        final boolean hideMarker = false;
        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);
                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + (finalPosition.latitude) * t,
                        startPosition.longitude * (1 - t) + (finalPosition.longitude) * t);
                myMarker.setPosition(currentPosition);
                // myMarker.setRotation(finalPosition.getBearing());
                // Repeat till progress is completeelse
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                    // handler.postDelayed(this, 100);
                } else {
                    if (hideMarker) {
                        myMarker.setVisible(false);
                    } else {
                        myMarker.setVisible(true);
                    }
                }
            }
        });
    }


    //    @OnClick(R.id.bt_logout)
//    public void logout(){
//        stopService(new Intent(getApplicationContext(), Location_ForeGround_Service.class));
//        SharedPreferences pref = getApplicationContext().getSharedPreferences("MH_LE_DATA", MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putBoolean("USER_LOGGED_IN", false);
//        editor.putString("USER_NAME", null);
//        editor.putString("USER_ID", null);
//        editor.putString("WARD_ASSIGNED", null);
//        editor.putString("LAT_LON", null);
//        editor.putBoolean("FIELD_CHECK",true) ;
//        editor.apply();
//        unregisterReceiver(broadcastReceiver);
//
//        Intent intent = new Intent(MapActivity.this, LoginActivity.class);
//        startActivity(intent);
//        finish();
//    }
    private boolean isNetWorkAvailable(int[] type) {
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            for (int typeNetwork : type) {
                assert cm != null;
                NetworkInfo networkInfo = cm.getNetworkInfo(typeNetwork);
                if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("MapActivity:", "Start");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("MapActivity:", "Stop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("MapActivity:", "Pause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Boolean check_service = prefs.getBoolean("CHECK_SERVICE", false);
        if (check_service) {
            Intent intent = new Intent("com.mamahome360.mamahomele");
            sendBroadcast(intent);
        }
    }

}