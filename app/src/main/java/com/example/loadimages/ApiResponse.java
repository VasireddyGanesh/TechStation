package com.example.loadimages;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {
    @SerializedName("image_id")
    private int image_id;

    @SerializedName("image_url")
    private String image_url;

    @SerializedName("image_headline")
    private String image_headline;

    @SerializedName("image_description")
    private String image_desc;

    @SerializedName("likes")
    private int image_likes;

    @SerializedName("shares")
    private int image_shares;

    @SerializedName("share_url")
    private String share_url;

    public int getImage_id() {
        return image_id;
    }

    public int getImage_likes() {
        return image_likes;
    }

    public int getImage_shares() {
        return image_shares;
    }

    public String getShare_url() {
        return share_url;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getImage_headline() {
        return image_headline;
    }

    public String getImage_desc() {
        return image_desc;
    }
}
