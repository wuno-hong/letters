package com.example.letter.constant;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.room.Room;

import com.example.letter.model.User;
import com.example.letter.repository.LetterDatebase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.letter.constant.NetConstant.getPenPalInfoURL;

public class MyApp extends Application {
    private static MyApp mInstance;
    private SharedPreferences sharedPreferences;
    private LetterDatebase letterDatebase;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        letterDatebase = Room.databaseBuilder(this, LetterDatebase.class, "letters")
                .allowMainThreadQueries()
                .addMigrations()
                .build();
    }

    public static MyApp getInstance() {
        return mInstance;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public LetterDatebase getLetterDatebase() {
        return letterDatebase;
    }


}

