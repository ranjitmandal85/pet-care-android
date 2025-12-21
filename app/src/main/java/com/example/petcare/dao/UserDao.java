package com.example.petcare.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.petcare.models.User;

@Dao
public interface UserDao {

    @Insert
    void register(User user);



    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User checkEmail(String email);

    @Query("DELETE FROM users WHERE email = :email")
    void deleteByEmail(String email);
}
