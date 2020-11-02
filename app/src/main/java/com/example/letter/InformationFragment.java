package com.example.letter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.letter.constant.MyApp;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.letter.constant.NetConstant.getPenPalInfoURL;


public class InformationFragment extends Fragment implements View.OnClickListener {

    private Button bt_change;
    private View root;
    private SharedPreferences.Editor editor = MyApp.getInstance().getSharedPreferences().edit();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_information, container, false);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        initUI();
        setOnClickListener();
    }

    private void initUI() {
        TextView tv_nickname = root.findViewById(R.id.tv_nickname);
        TextView tv_description = root.findViewById(R.id.tv_description);
        bt_change = root.findViewById(R.id.bt_change);
        SharedPreferences sp = MyApp.getInstance().getSharedPreferences();
        String nickname = sp.getString("user_nickname", "");
        String description = sp.getString("user_description", "");
        tv_nickname.setText(nickname);
        tv_description.setText(description);
        if (sp.getString("have_pen_pal", "").isEmpty()) {
            replaceFragment(new FindPenPalFragment());
            asyncGetPenPalInfo();
        } else {
            replaceFragment(new PenPalFragment());
            asyncGetPenPalInfo();
        }
    }

    private void setOnClickListener() {
        bt_change.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(getActivity(), ChangeInformationActivity.class));
    }

    private void asyncGetPenPalInfo() {
        new Thread(() -> {
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .get()
                    .url(getPenPalInfoURL())
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseStr = response.toString();
                    if (responseStr.contains("200")) {
                        String responseBodyStr = response.body().string();
                        if (!responseBodyStr.isEmpty()) {
                            JsonObject responseBodyJSONObject = (JsonObject) new JsonParser().parse(responseBodyStr);
                            editor.putString("pen_pal_id", responseBodyJSONObject.get("id").getAsString());
                            editor.putString("pen_pal_nickname", responseBodyJSONObject.get("username").getAsString());
                            editor.putString("pen_pal_description", responseBodyJSONObject.get("description").getAsString());
                            editor.putString("have_pen_pal", "true");
                            editor.apply();
                            replaceFragment(new PenPalFragment());
                        } else {
                            Log.d("response", "test");
                            editor.putString("have_pen_pal", "");
                            editor.apply();
                            replaceFragment(new FindPenPalFragment());
                        }
                    }
                }
            });
        }).start();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.pen_pal_fragment, fragment);
        transaction.commit();
    }
    /*private void alertDialog(Context context) {
        final AlertDialog.Builder alertDialog =3 new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.change_information, null);
        alertDialog
                .setTitle("")
                .setMessage("请确认是否解除笔友关系")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "yes", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "no", Toast.LENGTH_SHORT).show();
                    }
                })
                .create()
                .show();
    }*/


}
