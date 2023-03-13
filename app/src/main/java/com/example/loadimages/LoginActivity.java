package com.example.loadimages;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apxor.androidsdk.core.ApxorSDK;
import com.example.loadimages.db.AppDatabase;
import com.example.loadimages.db.UserEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    EditText login_id;
    Button login_btn;

    TextView sign_up_txt;

    FloatingActionButton fab;

    float xDown,yDown;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_id = findViewById(R.id.login_id);
        login_btn = findViewById(R.id.login_btn);

        sign_up_txt = findViewById(R.id.sign_up_txt);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Retrofit retrofit = new Retrofit.Builder()
//                        .baseUrl("https://tt.apxor.com/")
//                        .addConverterFactory(GsonConverterFactory.create())
//                        .build();
//
//                JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
//                HashMap<String,String> headermap = new HashMap<String,String>();
//                headermap.put("Content-Type","application/json");
//
//                Call<List<User>> call = jsonPlaceHolderApi.authUser(login_id.getText().toString());
//                call.enqueue(new Callback<List<User>>() {
//                    @Override
//                    public void onResponse(Call<List<User>>call, Response<List<User>> response) {
//                        if (!response.isSuccessful()) {
//                            Log.d("Debug :","Code: " + response.code());
//                            return;
//                        }
//                        User u= response.body().get(0);
//                        SharedPreferences sharedPreferences = getSharedPreferences("GStation", MODE_PRIVATE);
//                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
//                        myEdit.putString("name", u.getName());
//                        myEdit.putString("email", u.getEmail() );
//                        myEdit.putString("contact",u.getContact());
//                        myEdit.apply();
//
//                        Intent i=new Intent(LoginActivity.this,MainActivity.class);
//                        startActivity(i);
//                        finish();
//                    }
//
//                    @Override
//                    public void onFailure(Call<List<User>> call, Throwable t) {
//                        Log.d("Debug :","User Login Failed");
//                    }
//                });
                AppDatabase appDatabase= AppDatabase.getInstance(getApplicationContext());
                List<UserEntity> list=appDatabase.userDao().find(login_id.getText().toString());
                if(list.size()>=1){
                    SharedPreferences sharedPreferences = getSharedPreferences("GStation", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("contact",login_id.getText().toString());
                        myEdit.apply();
                        Intent i=new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(i);
                        finish();
                }else{
                    Toast.makeText(getApplicationContext(),"Invalid Login Details",Toast.LENGTH_SHORT).show();
                }
            }
        });
        sign_up_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(LoginActivity.this, signUpActivity.class);
                startActivity(i);
                
            }
        });
        ApxorSDK.registerSimpleNotification("inapp_shown",LoginActivity.this::hideFab);
        ApxorSDK.registerSimpleNotification("inapp_dismissed",LoginActivity.this::showFab);
        fab=findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApxorSDK.logAppEvent("ReactEvent1");
            }
        });

        fab.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event){
                switch (event.getActionMasked()){
                    case MotionEvent.ACTION_DOWN:
                        xDown=event.getX();
                        yDown=event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float movedX,movedY;
                        movedX=event.getX();
                        movedY=event.getY();

                        float distanceX=movedX-xDown;
                        float distanceY=movedY-yDown;

                        fab.setX(fab.getX()+distanceX);
                        fab.setY(fab.getY()+distanceY);
                        break;
                }
                return true;
            }
        });
    }
    public void hideFab(){
        Handler mainHandler = new Handler(this.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                // your code here
                fab.setVisibility(View.GONE);
            }
        });
    }

    public void showFab(){
        Handler mainHandler = new Handler(this.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                // your code here
                fab.setVisibility(View.VISIBLE);
            }
        });
    }
}