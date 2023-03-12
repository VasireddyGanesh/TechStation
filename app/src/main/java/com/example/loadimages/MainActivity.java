package com.example.loadimages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.apxor.androidsdk.core.ApxorSDK;
import com.apxor.androidsdk.core.Attributes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface{
    private RecyclerView recyclerView;

    Context context;

    List<ApiResponse> posts ;

    SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView_Adapter adapter;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ApxorSDK.logAppEvent("MainActivity Opened");
        Attributes userInfo = new Attributes();
        userInfo.putAttribute("name", "ganesh");
        userInfo.putAttribute("gender", "Male");
        ApxorSDK.setUserCustomInfo(userInfo);
        ApxorSDK.setSessionCustomInfo("network", "4G");
        context =this;
        recyclerView=findViewById(R.id.mRecyclerView);
        swipeRefreshLayout=findViewById(R.id.swipe);
        showList();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showList();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        ApxorSDK.registerSimpleNotification("inapp_shown",MainActivity.this::hideFab);
        ApxorSDK.registerSimpleNotification("inapp_dismissed",MainActivity.this::showFab);
        fab=findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApxorSDK.logAppEvent("ReactEvent1");
            }
        });
    }
    public void showList(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tt.apxor.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<List<ApiResponse>> call = jsonPlaceHolderApi.getImages();

        call.enqueue(new Callback<List<ApiResponse>>() {
            @Override
            public void onResponse(Call<List<ApiResponse>> call, Response<List<ApiResponse>> response) {

                if (!response.isSuccessful()) {
                    Log.d("Unsuccessful","Code: " + response.code());
                    return;
                }

                posts = response.body();
                adapter=new RecyclerView_Adapter(context,posts, (RecyclerViewInterface) context);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            }

            @Override
            public void onFailure(Call<List<ApiResponse>> call, Throwable t) {
                Log.d("failure",t.getMessage());
            }
        });
    }
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(MainActivity.this , HomeActivity.class);

        intent.putExtra("headline", posts.get(position).getImage_headline());
        intent.putExtra("image_url",posts.get(position).getImage_url());
        intent.putExtra("description",posts.get(position).getImage_desc());
        intent.putExtra("likes",posts.get(position).getImage_likes());
        intent.putExtra("shares",posts.get(position).getImage_shares());
        intent.putExtra("share_url",posts.get(position).getShare_url());
        intent.putExtra("image_id",posts.get(position).getImage_id());
        startActivity(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_btn:
                SharedPreferences preferences =getSharedPreferences("GStation", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                Intent i=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
                return true;
            case R.id.favourites:
                Intent intent=new Intent(MainActivity.this,FavouritesActivity.class);
                startActivity(intent);
        }
        return (super.onOptionsItemSelected(item));
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