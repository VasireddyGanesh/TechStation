package com.example.loadimages;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface JsonPlaceHolderApi {

    @GET("images")
    Call<List<ApiResponse>> getImages();

    @POST("images/updatelikes")
    Call<ResponseBody> updateLikes(@HeaderMap Map<String,String> headers,@Body LikeUtil image);

    @POST("images/updateshares")
    Call<ResponseBody> updateshares(@HeaderMap Map<String,String> headers,@Body ShareUtil shareUtil);

    @POST("users/create")
    Call<ResponseBody> createUser(@HeaderMap Map<String,String> headers,@Body User user);

    @POST("users/authenticate/{contact}")
    Call<List<User>> authUser(@Path("contact") String contact);

    @POST("likes/create")
    Call<ResponseBody> createLike(@HeaderMap Map<String,String> headers,@Body ImageLike image);

    @POST("unlike")
    Call<ResponseBody> deleteLike(@HeaderMap Map<String,String> headers,@Body ImageLike image);

    @GET("likes/{userId}")
    Call<List<ImageLike>> getLikedImages(@Path("userId") String userId);
}
