package com.example.loadimages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.apxor.androidsdk.core.ApxorSDK;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {
    int likes,shares,image_id;
    boolean liked=true;

    String user_id;

    FloatingActionButton fab;
    ImageView like_btn;
    float xDown,yDown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        String headline = getIntent().getStringExtra("headline");
        String desc = getIntent().getStringExtra("description");
        String image_url = getIntent().getStringExtra("image_url");
        likes = getIntent().getIntExtra("likes",0);
        shares = getIntent().getIntExtra("shares",0);
        String share_url = getIntent().getStringExtra("share_url");
        image_id=getIntent().getExtras().getInt("image_id");

        ApxorSDK.logAppEvent("HomeActivity Opened");
        like_btn = findViewById(R.id.like_btn);
        ImageView share_btn = findViewById(R.id.share_btn);
        TextView tv_headline= findViewById(R.id.headline);
        TextView tv_dec = findViewById(R.id.desc);
        ImageView show_img = findViewById(R.id.show_image);
        TextView share_count=findViewById(R.id.share_count);
        TextView likes_count=findViewById(R.id.likes_count);

        SharedPreferences shared = getSharedPreferences("GStation", MODE_PRIVATE);
        user_id = (shared.getString("contact", ""));

        share_count.setText(String.valueOf(shares));
        likes_count.setText(String.valueOf(likes));
        tv_headline.setText(headline);
        tv_dec.setText(desc);
        Glide.with(this)
                .load(image_url)
                .into(show_img);

        checkLiked();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tt.apxor.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApxorSDK.logAppEvent("Share Button");
                shares+=1;
                share_count.setText(String.valueOf(shares));
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = share_url;
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));

                HashMap<String,String> headermap = new HashMap<String,String>();
                headermap.put("Content-Type","application/json");
                JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

                Call<ResponseBody> call = jsonPlaceHolderApi.updateshares(headermap,new ShareUtil(image_id));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!response.isSuccessful()) {
                            Log.d("Debug :","Code: " + response.code());
                            return;
                        }
                        try {
                            Log.d("Debug :","Share Posted Successfully"+response.body().string()+ " Image ID "+ image_id+" Response code "+response.code());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("Debug :",t.getMessage());
                    }
                });
            }
        });

        like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApxorSDK.logAppEvent("Like Button");
                if(liked)
                {
                    likes+=1;
                    liked=false;
                    like_btn.setImageResource(R.drawable.appreciate_select);
                }else{
                    likes-=1;
                    liked=true;
                    like_btn.setImageResource(R.drawable.appreciate);
                }
                likes_count.setText(String.valueOf(likes));
                HashMap<String,String> headermap = new HashMap<String,String>();
                headermap.put("Content-Type","application/json");
                JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

                if(!liked){
                    Call<ResponseBody> call = jsonPlaceHolderApi.createLike(headermap,new ImageLike(user_id,image_id));
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (!response.isSuccessful()) {
                                Log.d("Debug :","Code: " + response.code());
                                return;
                            }
                            try {
                                Log.d("Debug :","Like Posted Successfully"+response.body().string()+ " Image ID "+ image_id+" Response code "+response.code()+"User Id "+user_id);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d("Debug :",t.getMessage());
                        }
                    });
                }else{
                    Call<ResponseBody> call = jsonPlaceHolderApi.deleteLike(headermap,new ImageLike(user_id,image_id));
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (!response.isSuccessful()) {
                                Log.d("Debug :","Code: " + response.code());
                                return;
                            }
                            try {
                                Log.d("Debug :","DisLiked Posted Successfully"+response.body().string()+ " Image ID "+ image_id+" Response code "+response.code());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d("Debug :",t.getMessage());
                        }
                    });

                }
                Call<ResponseBody> call = jsonPlaceHolderApi.updateLikes(headermap,new LikeUtil(image_id,liked ? 0:1));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!response.isSuccessful()) {
                            Log.d("Debug :","Code: " + response.code());
                            return;
                        }
                        Log.d("Debug :","Likes Updated");
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("Debug :",t.getMessage());
                    }
                });
            }
        });
        ApxorSDK.registerSimpleNotification("inapp_shown",HomeActivity.this::hideFab);
        ApxorSDK.registerSimpleNotification("inapp_dismissed",HomeActivity.this::showFab);
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

    private void checkLiked() {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("https://tt.apxor.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
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
                Log.d("Debug :","check Liked");
                List<ImageLike> likedImages = response.body();
                if(likedImages != null){
                    for(ImageLike image:likedImages){
                        if(image_id==image.getImage_id()){
                            Log.d("Debug :","Liked");
                            like_btn.setImageResource(R.drawable.appreciate_select);
                            liked=false;
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ImageLike>> call, Throwable t) {
                Log.d("failure",t.getMessage());
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

class ImageLike {
    @SerializedName("user_id")
    String user_id;
    @SerializedName("image_id")
    int image_id;

    public ImageLike(String user_id, int image_id) {
        this.user_id = user_id;
        this.image_id = image_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }
}

class User{
    @SerializedName("user_id")
    String user_id;

    @SerializedName("name")
    String name;

    @SerializedName("email")
    String email;

    @SerializedName("contact")
    String contact;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public User( String id,String name,String email,String contact) {
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.user_id=id;
    }
}

class LikeUtil{
    @SerializedName("image_id")
    int image_id;

    @SerializedName("liked")
    int liked;

    public LikeUtil(int image_id, int liked) {
        this.image_id = image_id;
        this.liked = liked;
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }
}

class ShareUtil {
    @SerializedName("image_id")
    private int image_id;

    public ShareUtil(int image_id) {
        this.image_id = image_id;
    }

    public int getImage_id() {
        return image_id;
    }
}