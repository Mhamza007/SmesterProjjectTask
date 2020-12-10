package com.mhamza007.videoapp.db;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

@androidx.room.Database(entities = User.class, exportSchema = false, version = 1)
public abstract class Database extends RoomDatabase {

    private static final String DB_NAME = "FINAL_PROJECT_DB";

    public abstract UserDao userDao();

    private static volatile Database instance;

    public static Database getDatabase(final Context context) {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            Database.class,
                            DB_NAME
                    ).build();
                }
            }
        }
        return instance;
    }

//    public static synchronized Database getDatabase(final Context context) {
//        if (instance == null) {
//            instance = Room.databaseBuilder(
//                    context.getApplicationContext(),
//                    Database.class,
//                    DB_NAME
//            ).build();
//        }
//        return instance;
//    }
}
