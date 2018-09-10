package com.mamahome360.mamahomele.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mamahome360.mamahomele.R;
import com.mamahome360.mamahomele.model.ResObj;
import com.mamahome360.mamahomele.remote.ApiUtils;
import com.mamahome360.mamahomele.remote.UserService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RemarkActivity extends AppCompatActivity {
    EditText remark;
    Button bt_submit_login;
    UserService userService;
    SharedPreferences prefs;
    String user_id,lattitude,longitude,CurrentDate;
    private static int NOTOFICATION_ID = 3;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remark_custom_dialog);
        prefs = getSharedPreferences("MH_LE_DATA", MODE_PRIVATE);
        user_id = prefs.getString("USER_ID", null);
        CurrentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
       remark = findViewById(R.id.ed_remark);
       final String Latitude = getIntent().getStringExtra("Latitude");
       final String Longitude = getIntent().getStringExtra("Longitude");
       bt_submit_login = findViewById(R.id.bt_sub_login);
        userService = ApiUtils.getUserService(getApplicationContext());
           bt_submit_login.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if(!remark.getText().toString().isEmpty()){
                       Calendar now = Calendar.getInstance();
                       fieldLogin(user_id,CurrentDate,now.getTime().toString(),remark.getText().toString(),Latitude,Longitude,null);
                       /*Intent in = new Intent(getApplicationContext(),MapActivity.class);
                       startActivity(in);*/
                       onBackPressed();
                       finish();
                   }
                   else{
                       Toast.makeText(getApplicationContext(),"Please enter remark ",Toast.LENGTH_LONG).show();
                   }
               }
           });


    }
    private void fieldLogin(String user_id,String login_date,String login_time,String remark,String latitude,String logitude,String address){
        Call<ResObj> call3 = userService.recordtime(user_id,login_date,login_time,remark,latitude,logitude,address);
        call3.enqueue(new Callback<ResObj>(){

            @Override
            public void onResponse(Call<ResObj> call, Response<ResObj> response) {
                if(response.isSuccessful()){

                }else{

                }
            }

            @Override
            public void onFailure(Call<ResObj> call, Throwable t) {
                Toast.makeText(RemarkActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.e("RemarkActivity:","Start");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("RemarkActivity:","Stop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("RemarkActivity:","Pause");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("RemarkActivity:","Resume");
    }
}
