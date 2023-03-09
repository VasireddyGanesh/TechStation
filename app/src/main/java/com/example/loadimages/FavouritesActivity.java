package com.example.loadimages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FavouritesActivity extends AppCompatActivity implements RecyclerViewInterface{

    List<ApiResponse> liked_posts ;
    RecyclerView_Adapter adapter;

    List<ApiResponse> show_list;

    Context context;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        recyclerView=findViewById(R.id.fav_recycler);
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("https://tt.apxor.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        context=this;
        SharedPreferences sharedPreferences= getSharedPreferences("GStation",MODE_PRIVATE);
        String userId=sharedPreferences.getString("contact","");


        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        Call<List<ImageLike>> call = jsonPlaceHolderApi.getLikedImages(userId);
        call.enqueue(new Callback<List<ImageLike>>() {
            @Override
            public void onResponse(Call<List<ImageLike>> call, Response<List<ImageLike>> response) {
                if (!response.isSuccessful()) {
                    Log.d("Unsuccessful","Code: " + response.code());
                    return;
                }
                getList(response.body());
            }

            @Override
            public void onFailure(Call<List<ImageLike>> call, Throwable t) {
                Log.d("failure",t.getMessage());
            }
        });
    }

    public void getList(List<ImageLike> liked_images){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tt.apxor.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<List<ApiResponse>> call=jsonPlaceHolderApi.getImages();
        call.enqueue(new Callback<List<ApiResponse>>() {
            @Override
            public void onResponse(Call<List<ApiResponse>> call, Response<List<ApiResponse>> response) {
                if (!response.isSuccessful()) {
                    Log.d("Unsuccessful","Code: " + response.code());
                    return;
                }
                liked_posts=response.body();
                show_list=new ArrayList<ApiResponse>();
                for(ImageLike liked_image:liked_images){
                    for(ApiResponse post:liked_posts){
                        if(post.getImage_id()==liked_image.getImage_id()){
                            show_list.add(post);
                        }
                    }
                }
                adapter=new RecyclerView_Adapter(context,show_list, (RecyclerViewInterface) context);
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
        Intent intent = new Intent(FavouritesActivity.this , HomeActivity.class);

        intent.putExtra("headline", show_list.get(position).getImage_headline());
        intent.putExtra("image_url",show_list.get(position).getImage_url());
        intent.putExtra("description",show_list.get(position).getImage_desc());
        intent.putExtra("likes",show_list.get(position).getImage_likes());
        intent.putExtra("shares",show_list.get(position).getImage_shares());
        intent.putExtra("share_url",show_list.get(position).getShare_url());
        intent.putExtra("image_id",show_list.get(position).getImage_id());
        startActivity(intent);
    }
}