package com.example.letter.repository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.letter.model.Letter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Dao
public interface LetterDao {

    @Insert
    void insertLetters(ArrayList<Letter> letters);

    @Query("SELECT * FROM letter ORDER BY receiveTime DESC")
    List<Letter> findAll();

    @Query("SELECT content FROM letter where receiveTime = :receiveTime")
    String findByTime(Date receiveTime);

}
