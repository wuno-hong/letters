package com.example.letter.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.letter.MainActivity;
import com.example.letter.constant.MyApp;
import com.example.letter.R;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.letter.constant.NetConstant.getLoginURL;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //声明UI对象
    Button bt_login = null;
    EditText et_email = null;
    EditText et_password = null;
    Button bt_register = null;
    Button bt_forget_password = null;

    private final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (MyApp.getInstance().getSharedPreferences().getString("token", "").equals("true")) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        initUI();
        setOnClickListener();
    }

    private void initUI() {
        bt_login = findViewById(R.id.bt_login);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        bt_forget_password = findViewById(R.id.bt_forget_password);
        bt_register = findViewById(R.id.bt_register);
    }

    private void setOnClickListener() {
        bt_login.setOnClickListener(this);
        bt_register.setOnClickListener(this);
        bt_forget_password.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();

        switch (v.getId()) {
            case R.id.bt_register:
                Intent it_login_to_register = new Intent(this, RegisterActivity.class);
                startActivity(it_login_to_register);
                break;
            case R.id.bt_login:
                asyncLogin(email, password);
                break;
        }
    }


    private void asyncLogin(final String email, final String password) {
        if (!isEmailValid(email)) {
            Toast.makeText(this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
        } else if (!isPasswordValid(password)) {
            Toast.makeText(this, "密码至少为六位", Toast.LENGTH_SHORT).show();
        } else {
            new Thread(() -> {
                OkHttpClient okHttpClient = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");
                JSONObject json = new JSONObject();
                try {
                    json.put("id", email);
                    json.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody requestBody = RequestBody.create(mediaType, String.valueOf(json));
                Request request = new Request.Builder()
                        .url(getLoginURL())
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        showToastInThread(LoginActivity.this, "网络连接失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseStr = response.toString();
                        if (responseStr.contains("200")) {
                            String responseBodyStr = response.body().string();
                            JsonObject responseBodyJSONObject = (JsonObject) new JsonParser().parse(responseBodyStr);
                            if (getResponseStatus(responseBodyJSONObject).equals("success")) {
                                showToastInThread(LoginActivity.this, "登陆成功");
                                SharedPreferences.Editor editor = MyApp.getInstance().getSharedPreferences().edit();
                                editor.putString("token", "true");
                                editor.putString("token_email", email);
                                editor.putString("token_password", password);
                                editor.apply();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                showToastInThread(LoginActivity.this, "账号或密码错误");
                            }
                        } else {
                            showToastInThread(LoginActivity.this, "服务器异常");
                        }
                    }
                });
            }).start();
        }
    }

    private boolean isEmailValid(String email) {
        if (email.isEmpty()) {
            return false;
        }
        String pattern = "^.*?@.*\\.com$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(email);
        return m.matches();
    }

    private boolean isPasswordValid(String password) {
        return !password.isEmpty() &&password.trim().length() > 5;
    }

    private String getResponseStatus(JsonObject responseBodyJSONObject) {
        String status = responseBodyJSONObject.get("status").getAsString();
        return status;
    }

    private void showToastInThread(final Context context, final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
