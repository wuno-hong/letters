package com.example.letter.email;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.letter.R;
import com.example.letter.constant.MyApp;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.letter.constant.NetConstant.getSendLetterURL;

public class WriteLetterActivity extends AppCompatActivity implements View.OnClickListener {

    Button bt_send_letter;
    EditText et_write_letter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_letter);
        initUI();
        et_write_letter.requestFocus();
        //OnClickListener();
    }

    private void initUI() {
        //bt_send_letter = findViewById(R.id.bt_send_letter);
        et_write_letter = findViewById(R.id.et_write_letter);
    }

    private void OnClickListener() {
        bt_send_letter.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String content = et_write_letter.getText().toString();

        if (content.isEmpty()) {
            showToastInThread(this, "标题与内容均不能为空");
        } else {
            asyncSendLetter(content);
            Log.d("content", content);
        }
    }

    private void asyncSendLetter(final String content) {
        new Thread(() -> {
            OkHttpClient okHttpClient = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            JSONObject json = new JSONObject();
            try {
                Date date = new Date();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DATE, 3);
                json.put("receiveTime", df.format(calendar.getTime()));
                json.put("sender", MyApp.getInstance().getSharedPreferences().getString("token_email", ""));
                json.put("sendTag", 0);
                json.put("addressee", "pyxxhml@126.com");
                json.put("content", content);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody requestBody = RequestBody.create(mediaType, String.valueOf(json));
            Request request = new Request.Builder()
                    .url(getSendLetterURL())
                    .post(requestBody)
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    showToastInThread(WriteLetterActivity.this, "网络连接失败");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseStr = response.toString();
                    if (responseStr.contains("200")) {
                        String responseBodyStr = response.body().string();
                        JsonObject responseBodyStrJson = (JsonObject) new JsonParser().parse(responseBodyStr);
                        if (getResponseStatus(responseBodyStrJson).equals("success")) {
                            showToastInThread(WriteLetterActivity.this, "发送成功");
                        } else {
                            showToastInThread(WriteLetterActivity.this, "发送失败");
                        }
                    } else {
                        showToastInThread(WriteLetterActivity.this, "服务器异常");
                    }

                }
            });
        }).start();
    }

    private String getResponseStatus(JsonObject jsonObject) {
        return jsonObject.get("status").getAsString();
    }

    private void showToastInThread(Context context, String msg) {
        runOnUiThread(() -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show());
    }
}
