package com.example.loadimages.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insert(UserEntity u);

    @Delete
    void delete(UserEntity u);

    @Query("SELECT * FROM UserEntity WHERE contact = :contact_id ")
    List<UserEntity> find(String contact_id);
}
