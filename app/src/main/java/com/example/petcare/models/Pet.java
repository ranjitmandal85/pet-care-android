package com.example.petcare.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pet")
public class Pet {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;

    public String name;
    public int age;
    public String gender;
    public String likes;

    // 🔥 REQUIRED
    public String photoUri;

    public String videoUris;
    public String type;

}
