package com.example.letter.email;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.letter.GetLetter.GetLetterAdapter;
import com.example.letter.R;
import com.example.letter.constant.MyApp;
import com.example.letter.model.Letter;
import com.example.letter.repository.LetterDao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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


public class EmailFragment extends Fragment implements View.OnClickListener {

    private FloatingActionButton bt_write_letter;
    private View root;
    private AVLoadingIndicatorView avi;
    private RecyclerView rv_get_letter;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());;
    private LetterDao letterDao = MyApp.getInstance().getLetterDatebase().letterDao();
    private List<Letter> mLetters = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_email, container, false);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        initUI();
        setOnClickListener();
        avi.show();
        rv_get_letter.setLayoutManager(layoutManager);
        mLetters = letterDao.findAll();
        List<Date> timeList = new ArrayList<>();
        for (Letter letter : mLetters) {
            timeList.add(letter.receiveTime);
        }
        mAdapter = new GetLetterAdapter(getActivity(), timeList);
        rv_get_letter.setAdapter(mAdapter);
        asyncGetLetter();
    }

    private void initUI() {
        bt_write_letter = root.findViewById(R.id.bt_write_letter);
        rv_get_letter = root.findViewById(R.id.rv_get_letter);
        avi = root.findViewById(R.id.avi);
    }

    private void setOnClickListener() {
        bt_write_letter.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bt_write_letter:
                startActivity(new Intent(getActivity(), WriteLetterActivity.class));
                break;

        }
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
                    showToastInThread(getActivity(), "网络连接失败");
                    getActivity().runOnUiThread(() -> avi.hide());
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
                            getActivity().runOnUiThread(() -> {
                                letterDao.insertLetters(letters);
                                rv_get_letter.setLayoutManager(layoutManager);
                                mLetters = letterDao.findAll();
                                List<Date> timeList = new ArrayList<>();
                                for (Letter letter : mLetters) {
                                    timeList.add(letter.receiveTime);
                                }
                                mAdapter = new GetLetterAdapter(getActivity(), timeList);
                                rv_get_letter.setAdapter(mAdapter);
                            });
                        }
                        getActivity().runOnUiThread(() -> avi.hide());
                    } else {
                        showToastInThread(getActivity(), "服务器异常");
                        getActivity().runOnUiThread(() -> avi.hide());
                    }
                }
            });
        }).start();
    }

    private void showToastInThread(Context context, String msg) {
        getActivity().runOnUiThread(() -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show());
    }
}
