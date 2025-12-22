package com.example.petcare.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.petcare.models.User;
import com.example.petcare.models.Pet;
import com.example.petcare.dao.UserDao;
import com.example.petcare.dao.PetDao;



@Database(entities = {User.class, Pet.class}, version = 7, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract UserDao userDao();
    public abstract PetDao petDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "petcare_db"
                    ).fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();

        }
        return instance;
    }
}
