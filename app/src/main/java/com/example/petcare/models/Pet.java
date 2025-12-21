package com.example.petcare.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pets")
public class Pet {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;
    public String type;

    // NEW FIELDS
    public String name;
    public int age;
    public String gender;
    public String likes;
}
