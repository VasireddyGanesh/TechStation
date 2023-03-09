package com.example.loadimages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_sign_up);

        login_text_btn=findViewById(R.id.login_txt);

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

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d("Debug :","User Insertion Failed");
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
            }
        });
    }

}