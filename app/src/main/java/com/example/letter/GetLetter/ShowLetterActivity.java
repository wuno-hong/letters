package com.example.letter.GetLetter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.example.letter.repository.LetterDao;
import com.example.letter.constant.MyApp;
import com.example.letter.R;

import java.util.Date;

public class ShowLetterActivity extends AppCompatActivity {

    private TextView tv_show_letter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_letter);

        tv_show_letter = findViewById(R.id.tv_show_letter);

        Intent intent = getIntent();
        String time = intent.getStringExtra("time");
        LetterDao letterDao = MyApp.getInstance().getLetterDatebase().letterDao();
        String content = letterDao.findByTime(new Date(time));
        tv_show_letter.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv_show_letter.setText(content);

    }
}
