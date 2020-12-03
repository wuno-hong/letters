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

import static com.example.letter.constant.NetConstant.getCodeURL;
import static com.example.letter.constant.NetConstant.getRegisterURL;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    Button bt_submit_register;
    Button bt_get_code;
    EditText et_email;
    EditText et_code;
    EditText et_password;

    private final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initUI();
        setOnClickListener();
    }

    private void initUI() {
        bt_submit_register = findViewById(R.id.bt_submit_register);
        bt_get_code = findViewById(R.id.bt_get_code);
        et_email = findViewById(R.id.et_email);
        et_code = findViewById(R.id.et_code);
        et_password = findViewById(R.id.et_password);
    }

    private void setOnClickListener() {
        bt_submit_register.setOnClickListener(this);
        bt_get_code.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String email = et_email.getText().toString();
        String code = et_code.getText().toString();
        String password = et_password.getText().toString();

        switch (v.getId()) {
            case R.id.bt_get_code:
                asyncGetCode(email);
                break;
            case R.id.bt_submit_register:
                asyncRegister(email, password, code);
                break;
        }
    }

    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }
        String pattern = "^.*?@.*\\.com$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(email);
        return m.matches();
    }

    private boolean isPasswordValid(String password) {
        return !password.isEmpty() && password.trim().length() > 5;
    }

    private boolean isCodeValid(String code) {
        return !code.isEmpty();
    }

    private void asyncGetCode(final String email) {
        if (!isEmailValid(email)) {
            Toast.makeText(this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
        } else {
            new Thread(() -> {
                OkHttpClient okHttpClient = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");
                JSONObject json = new JSONObject();
                try {
                    json.put("id", email);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody requestBody = RequestBody.create(mediaType, String.valueOf(json));
                Request request = new Request.Builder()
                        .url(getCodeURL())
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        showToastInThread(RegisterActivity.this, "网络连接失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseStr = response.toString();
                        if (responseStr.contains("200")) {
                            String responseBodyStr = response.body().string();
                            JsonObject responseBodyJSONObject = (JsonObject) new JsonParser().parse(responseBodyStr);
                            if (getResponseStatus(responseBodyJSONObject).equals("success")) {
                                showToastInThread(RegisterActivity.this, "验证码已发送");
                            } else {
                                showToastInThread(RegisterActivity.this, "邮箱已被注册");
                            }
                        } else {
                            showToastInThread(RegisterActivity.this, "服务器异常");
                        }
                    }
                });
            }).start();
        }
    }

    private void asyncRegister(final String email, final String password, final String code) {
        if (!isEmailValid(email)) {
            Toast.makeText(this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
        } else if (!isPasswordValid(password)) {
            Toast.makeText(this, "密码至少为六位", Toast.LENGTH_SHORT).show();
        } else if (code.isEmpty()) {
            Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
        } else {
            new Thread(() -> {
                OkHttpClient okHttpClient = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");
                JSONObject json = new JSONObject();
                try {
                    json.put("id", email);
                    json.put("password", password);
                    json.put("vfCode", code);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody requestBody = RequestBody.create(mediaType, String.valueOf(json));
                Request request = new Request.Builder()
                        .url(getRegisterURL())
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        showToastInThread(RegisterActivity.this, "网络连接失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseStr = response.toString();
                        if (responseStr.contains("200")) {
                            String responseBodyStr = response.body().string();
                            JsonObject responseBodyJSONObject = (JsonObject) new JsonParser().parse(responseBodyStr);
                            if (getResponseStatus(responseBodyJSONObject).equals("success")) {
                                showToastInThread(RegisterActivity.this, "注册成功");
                                SharedPreferences.Editor editor = MyApp.getInstance().getSharedPreferences().edit();
                                editor.putString("token", "true");
                                editor.putString("token_email", email);
                                editor.putString("token_password", password);
                                editor.apply();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                            } else if (getResponseStatus(responseBodyJSONObject).equals("verification code wrong")) {
                                showToastInThread(RegisterActivity.this, "验证码错误");
                            }
                        } else {
                            showToastInThread(RegisterActivity.this, "服务器异常");
                        }
                    }
                });
            }).start();
        }
    }

    private String getResponseStatus(JsonObject responseBodyJSONObject) {
        String status = responseBodyJSONObject.get("status").getAsString();
        return status;
    }

    private void showToastInThread(final Context context, final String msg) {
        runOnUiThread(() -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show());
    }
}
