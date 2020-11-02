package com.example.letter.GetLetter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.example.letter.repository.LetterDao;
import com.example.letter.constant.MyApp;
import com.example.letter.R;
import com.example.letter.model.Letter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.wang.avi.AVLoadingIndicatorView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.letter.constant.NetConstant.getLetterURL;

public class GetLetterActivity extends AppCompatActivity {

    private AVLoadingIndicatorView avi;
    private RecyclerView rv_get_letter;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);;
    private LetterDao letterDao = MyApp.getInstance().getLetterDatebase().letterDao();
    private List<Letter> mLetters = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_letter);
        rv_get_letter = findViewById(R.id.rv_get_letter);
        avi = findViewById(R.id.avi);
        avi.show();
        rv_get_letter.setLayoutManager(layoutManager);
        mLetters = letterDao.findAll();
        List<Date> timeList = new ArrayList<>();
        for (Letter letter : mLetters) {
            timeList.add(letter.receiveTime);
        }
        mAdapter = new GetLetterAdapter(this, timeList);
        rv_get_letter.setAdapter(mAdapter);
        asyncGetLetter();
    }

    private void asyncGetLetter() {
        new Thread(() -> {
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .get()
                    .url(getLetterURL())
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    showToastInThread(GetLetterActivity.this, "网络连接失败");
                    runOnUiThread(() -> avi.hide());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseStr = response.toString();
                    if (responseStr.contains("200")) {
                        String responseBodyStr = response.body().string();
                        JsonArray jsonArray = new JsonParser().parse(responseBodyStr).getAsJsonArray();
                        if (jsonArray.size() > 0) {
                            Gson gson = new Gson();
                            ArrayList<Letter> letters = new ArrayList<>();
                            for (JsonElement jLetter : jsonArray) {
                                Letter letter = gson.fromJson(jLetter, Letter.class);
                                letters.add(letter);
                            }
                            runOnUiThread(() -> {
                                letterDao.insertLetters(letters);
                                rv_get_letter.setLayoutManager(layoutManager);
                                mLetters = letterDao.findAll();
                                List<Date> timeList = new ArrayList<>();
                                for (Letter letter : mLetters) {
                                    timeList.add(letter.receiveTime);
                                }
                                mAdapter = new GetLetterAdapter(GetLetterActivity.this, timeList);
                                rv_get_letter.setAdapter(mAdapter);
                            });
                        }
                        runOnUiThread(() -> avi.hide());
                    } else {
                        showToastInThread(GetLetterActivity.this, "服务器异常");
                        runOnUiThread(() -> avi.hide());
                    }
                }
            });
        }).start();
    }

    private void showToastInThread(Context context, String msg) {
        runOnUiThread(() -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show());
    }

}
