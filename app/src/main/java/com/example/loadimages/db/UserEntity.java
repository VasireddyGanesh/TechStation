package com.example.loadimages.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserEntity {

    @ColumnInfo(name = "name")
    public String user_name;

    @ColumnInfo(name = "email")
    public String email;

    public UserEntity(String user_name, String email, String contact) {
        this.user_name = user_name;
        this.email = email;
        this.contact = contact;
    }

    @PrimaryKey
    @NonNull
    public String contact;
}
