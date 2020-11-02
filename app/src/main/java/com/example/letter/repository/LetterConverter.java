package com.example.letter.repository;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class LetterConverter {
    @TypeConverter
    public static Date revert(String receiveTime) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        if (receiveTime == null) {
            return null;
        } else {
            Date newDate = null;
            try {
                newDate = df.parse(receiveTime);
            } catch(ParseException e) {
                System.out.println(e.toString());
            }
            return newDate;
        }
    }

    @TypeConverter
    public static String converter(Date receiveTime) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("Etc/GMT-8"));
        Log.d("receive_time", receiveTime.toString());
        return receiveTime==null ? null : df.format(receiveTime);
    }
}
