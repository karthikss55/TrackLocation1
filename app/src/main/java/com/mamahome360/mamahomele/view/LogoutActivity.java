package com.mamahome360.mamahomele.view;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.mamahome360.mamahomele.model.DatabaseHelper;

public class LogoutActivity extends AppCompatActivity {

    Button bt_Logout, bt_refresh;
    TextView tv_latlng;
    BroadcastReceiver broadcastReceiver;
    String CurrentDate, LatLngText, KM;
    DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_logout);
//        databaseHelper = new DatabaseHelper(this);
//        CurrentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
//        LatLngText = databaseHelper.getColumnLatlng(CurrentDate);
//
//        tv_latlng = findViewById(R.id.tv_latlng);
//
//        if(LatLngText.equals("")){
//            LatLngText = "Currently Empty";
//        }
//        else{
//            tv_latlng.setText(LatLngText+"\n\n");
//        }
//        bt_refresh = findViewById(R.id.bt_refreshText);
//        bt_refresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                databaseHelper = new DatabaseHelper(getApplicationContext());
//
//                CurrentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
//                LatLngText = databaseHelper.getColumnLatlng(CurrentDate);
//                KM = databaseHelper.getColumnKm(CurrentDate);
//
//                if(LatLngText.equals("")){
//                    LatLngText = "Currently Empty";
//                }
//                else{
//                    tv_latlng.setText(LatLngText+"\n" + KM);
//
//                }
//            }
//        });
//        bt_Logout =  findViewById(R.id.bt_Logout);
//        bt_Logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopService(new Intent(getApplicationContext(), Location_ForeGround_Service.class));
//                SharedPreferences pref = getApplicationContext().getSharedPreferences("SP_USER_DATA", MODE_PRIVATE);
//                SharedPreferences.Editor editor = pref.edit();
//                editor.putBoolean("USER_LOGGED_IN", false);
//                editor.apply();
//                Intent intent = new Intent(LogoutActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        databaseHelper = new DatabaseHelper(this);
//        CurrentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
//        LatLngText = databaseHelper.getColumnLatlng(CurrentDate);
//        if(LatLngText.equals("")){
//            LatLngText = "Currently Empty";
//        }
//        else{
//            tv_latlng.setText(LatLngText+"\n\n");
//        }
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if(broadcastReceiver != null){
//            unregisterReceiver(broadcastReceiver);
//        }
    }
}
