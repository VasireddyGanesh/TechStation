package com.example.loadimages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class signUpActivity extends AppCompatActivity {

    EditText name,contact,email;
    Button submit_btn;

    TextView login_text_btn;
    FloatingActionButton fab;

    float xDown,yDown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_sign_up);
        ApxorSDK.registerSimpleNotification("inapp_shown",signUpActivity.this::hideFab);
        ApxorSDK.registerSimpleNotification("inapp_dismissed",signUpActivity.this::showFab);
        login_text_btn=findViewById(R.id.login_txt);
        fab=findViewById(R.id.fab);

        SharedPreferences sharedPrefs = getSharedPreferences("GStation", MODE_PRIVATE);
        SharedPreferences.Editor ed;
        if(sharedPrefs.contains("contact")){
           Intent i=new Intent(signUpActivity.this,MainActivity.class);
           startActivity(i);
           finish();
        }else{
            name=findViewById(R.id.name);
            contact = findViewById(R.id.contact);
            email =findViewById(R.id.email);
            submit_btn=findViewById(R.id.submit);

            submit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://tt.apxor.com/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
                    HashMap<String,String> headermap = new HashMap<String,String>();
                    headermap.put("Content-Type","application/json");
                    insertUserIntoRoom(name.getText().toString(),email.getText().toString(),contact.getText().toString());
                    Call<ResponseBody> call = jsonPlaceHolderApi.createUser(headermap,new User(contact.getText().toString(),name.getText().toString(),email.getText().toString(),contact.getText().toString()));
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (!response.isSuccessful()) {
                                Log.d("Debug :","Code: " + response.code());
                                return;
                            }
                            try {
                                Log.d("Debug :","User Inserted Successfully"+" Response Body: "+response.body().string());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            Intent i=new Intent(signUpActivity.this,LoginActivity.class);
                            startActivity(i);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d("Debug :","User Insertion Failed");
                            Toast.makeText(getApplicationContext(),"Account Creation Failed",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        }
        login_text_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(signUpActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

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

    public void insertUserIntoRoom(String name, String email, String contact) {
        Log.d("Debug :","Insert User into Room");
        AppDatabase appDatabase= AppDatabase.getInstance(this.getApplicationContext());
        appDatabase.userDao().insert(new UserEntity(name,email,contact));
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