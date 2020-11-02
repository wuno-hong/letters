package com.example.letter.repository;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.letter.model.Letter;
import com.example.letter.repository.LetterConverter;
import com.example.letter.repository.LetterDao;

@Database(entities = {Letter.class}, version = 1)
@TypeConverters({LetterConverter.class})
public abstract class LetterDatebase extends RoomDatabase {
    public abstract LetterDao letterDao();
}
