package com.example.petcare.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.example.petcare.models.Pet;

@Dao
public interface PetDao {

    @Insert
    long insert(Pet pet);

    @Query("SELECT * FROM pets WHERE id = :petId")
    Pet getPetById(long petId);

    // ✅ THIS WAS MISSING
    @Update
    void update(Pet pet);
}
