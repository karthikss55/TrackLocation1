package com.mamahome360.mamahomele.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.mamahome360.mamahomele.R;
import com.mamahome360.mamahomele.model.DatabaseHelper;
import com.mamahome360.mamahomele.model.ResObj;
import com.mamahome360.mamahomele.remote.ApiUtils;
import com.mamahome360.mamahomele.remote.UserService;
import com.mamahome360.mamahomele.view.LoginActivity;
import com.mamahome360.mamahomele.view.MapActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mamahome360.mamahomele.utils.SendingNotification.CHANNEL_ID;

public class Location_ForeGround_Service extends Service {

    PendingIntent pendingIntent;
    LocationListener locationListener;
    LocationManager locationManager;
    String UserID, LatLngText, Snap_Latlng, CurrentDate, KMs, time;
    DatabaseHelper databaseHelper;
    UserService userService;
    int[] type = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        databaseHelper = new DatabaseHelper(this);
        userService = ApiUtils.getUserService(getApplicationContext());
        CurrentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        SharedPreferences prefs = getSharedPreferences("MH_LE_DATA", MODE_PRIVATE);
        Boolean check_user_status = prefs.getBoolean("USER_LOGGED_IN", false);
        UserID = prefs.getString("USER_ID", null);
        if (check_user_status.equals(true)) {
            Intent Notificationintent = new Intent(this, MapActivity.class);
            pendingIntent = PendingIntent.getActivity(this,
                    0, Notificationintent, 0);
        } else {
            Intent Notificationintent = new Intent(this, LoginActivity.class);
            pendingIntent = PendingIntent.getActivity(this,
                    0, Notificationintent, 0);
        }
        if (isLocationEnabled(getApplicationContext())) {
            getLocation();
        } else {
            // Toast.makeText(getApplicationContext(),"Turn On Gps",Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enable GPS");  // GPS not found
            builder.setMessage("The app needs GPS to be enabled  "); // Want to enable?
            builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });

            builder.setCancelable(false);
            builder.create().show();
        }

        return START_NOT_STICKY;
    }

    private void getLocation() {

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLngText = Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());
                Snap_Latlng = Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());
                String lat = Double.toString(location.getLatitude());
                String lon = Double.toString(location.getLongitude());
                String Lat_Lng = databaseHelper.getColumnLatlng(CurrentDate);
                KMs = distanceCalculation(Lat_Lng);
                java.util.Calendar calendar = Calendar.getInstance();
                SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
                time = mdformat.format(calendar.getTime());
                //     Toast.makeText(getApplicationContext(), KMs+" KM(s)", Toast.LENGTH_SHORT).show();
                if (databaseHelper.checkDateExist(CurrentDate)) {
                    String PrevData = databaseHelper.getColumnLatlng(CurrentDate);
                    String PrevSnap_data = databaseHelper.getColumnSnapLatlng(CurrentDate);
                    String PrevTime = databaseHelper.getColumnTime(CurrentDate);
                    LatLngText = PrevData + "," + LatLngText;
                    Snap_Latlng = PrevSnap_data + "|" + Snap_Latlng;
                    time = PrevTime + "," + time;
                    databaseHelper.updateUserLocation(LatLngText, Snap_Latlng, CurrentDate, time, KMs);

                    if (isNetWorkAvailable(type)) {

                        updateLocation(UserID, Snap_Latlng, time, KMs, CurrentDate);


                    } else {
                        //dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplication());
//                        builder1.setMessage("Turn On Your Internet");
//                        builder1.setCancelable(false);
//                        AlertDialog alert11 = builder1.create();
//                        alert11.show();

                    }

                } else {

                    databaseHelper.addUserLocation(UserID, LatLngText, Snap_Latlng, CurrentDate, time, KMs);
                    //    databaseHelper.updateUserFl(true,CurrentDate);
                    if (isNetWorkAvailable(type)) {

                        currentLocation(UserID, Snap_Latlng, time, CurrentDate, KMs);


                    } else {
//                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplicationContext());
//                        builder1.setMessage("Turn On Your Internet");
//                        builder1.setCancelable(false);
//                        AlertDialog alert11 = builder1.create();
//                        alert11.show();

                    }
                }
                //  Toast.makeText(getApplicationContext(), "Added Value", Toast.LENGTH_LONG).show();
                Intent i = new Intent("LOCATION_UPDATE");
                i.putExtra("lat", lat);
                i.putExtra("lng", lon);
                sendBroadcast(i);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 50, locationListener);

        int color = getResources().getColor(R.color.Mama_Orange);
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Tracking Active...")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Last Updated At : " + currentDateTimeString))
                .setColor(color)
                .setSmallIcon(R.drawable.motorbike)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
    }

    private void updateLocation(String user_id, String lat_long, String time, String kms, String date) {
        Call<ResObj> call3 = userService.updateLocation(user_id, lat_long, time, kms, date);
        call3.enqueue(new Callback<ResObj>() {
            @Override
            public void onResponse(@NonNull Call<ResObj> call, @NonNull Response<ResObj> response) {
                if (response.isSuccessful()) {
                    ResObj resObj = response.body();
                    Toast.makeText(getApplicationContext(), "updating", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "" + response.body(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResObj> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private String distanceCalculation(String latlogtext) {
        int commas = 0;
        String[] twoStringArray, firstLang;
        for (int i = 0; i < latlogtext.length(); i++) {
            if (latlogtext.charAt(i) == ',') {
                commas++;
            }
        }
        double commaCount = commas - 1;
        Double kms1 = commaCount / 2 * 50 / 1000;
        return Double.toString(kms1);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

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

    private void currentLocation(String user_id, String lat_long, String time, String date, String kms) {

        Call<ResObj> call2 = userService.addLocation(user_id, lat_long, time, date, kms);

        call2.enqueue(new Callback<ResObj>() {

            @Override
            public void onResponse(@NonNull Call<ResObj> call, @NonNull Response<ResObj> response) {
                if (response.isSuccessful()) {
                    ResObj resObj = response.body();
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error! Please try again later", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResObj> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onLowMemory() {

    }

    @Override
    public void onTrimMemory(int level) {

    }
}