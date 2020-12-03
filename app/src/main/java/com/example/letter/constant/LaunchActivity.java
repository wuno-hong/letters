package com.example.letter.constant;

import android.content.Intent;
import android.os.Bundle;

import com.example.letter.Login.LoginActivity;
import com.example.letter.Login.RegisterActivity;
import com.example.letter.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(() -> {
           startActivity(new Intent(this, LoginActivity.class));
           this.finish();
        }).start();
    }

}
