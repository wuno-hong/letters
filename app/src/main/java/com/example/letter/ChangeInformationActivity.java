package com.example.letter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.letter.constant.MyApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.letter.constant.NetConstant.getChangeInformationURL;

public class ChangeInformationActivity extends AppCompatActivity implements View.OnClickListener {
    private Button bt_commit_change;
    private EditText et_nickname;
    private EditText et_description;
    String nickname;
    String description;
    SharedPreferences sp = MyApp.getInstance().getSharedPreferences();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_information);
        initUI();
        setOnClickListener();
    }

    private void initUI() {
        bt_commit_change = findViewById(R.id.bt_commit_change);
        et_nickname = findViewById(R.id.et_nickname);
        et_description = findViewById(R.id.et_description);
        et_nickname.setText(sp.getString("user_nickname", ""));
        et_description.setText(sp.getString("user_description", ""));
    }

    private void setOnClickListener() {
        bt_commit_change.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (netConnect()) {
            nickname = et_nickname.getText().toString();
            description = et_description.getText().toString();
            asyncChangeInformation();
            finish();
        } else {
            Toast.makeText(this, "网络未连接", Toast.LENGTH_SHORT).show();
        }
    }

    private void asyncChangeInformation() {
        if (nickname.isEmpty()) {
            Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
        } else if (description.isEmpty()) {
            Toast.makeText(this, "简介不能为空", Toast.LENGTH_SHORT).show();
        } else {
            new Thread(() -> {
                OkHttpClient okHttpClient = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");
                JSONObject json = new JSONObject();
                try {
                    json.put("id", sp.getString("token_email", ""));
                    json.put("username", nickname);
                    json.put("description", description);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody requestBody = RequestBody.create(mediaType, String.valueOf(json));
                Request request = new Request.Builder()
                        .url(getChangeInformationURL())
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("user_nickname", nickname);
                        editor.putString("user_description", description);
                        editor.apply();
                        showToastInThread(ChangeInformationActivity.this, "提交成功");
                    }
                });
            }).start();
        }
    }

    private boolean netConnect() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable()) {
            return false;
        }
        return true;
    }

    private void showToastInThread(final Context context, final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
