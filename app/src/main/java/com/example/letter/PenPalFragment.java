package com.example.letter;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.letter.constant.MyApp;


public class PenPalFragment extends Fragment implements View.OnClickListener{

    private TextView tv_nickname;
    private TextView tv_description;
    private Button bt_delete_pen_pal;
    private View root;
    private SharedPreferences sp = MyApp.getInstance().getSharedPreferences();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstance) {
        root = inflater.inflate(R.layout.pen_pal_fragment, container, false);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        initUI();
        setOnClickListener();
    }

    private void initUI() {
        tv_nickname = root.findViewById(R.id.tv_nickname);
        tv_description = root.findViewById(R.id.tv_description);
        bt_delete_pen_pal = root.findViewById(R.id.bt_delete_pen_pal);
        tv_nickname.setText(sp.getString("pen_pal_nickname", ""));
        tv_description.setText(sp.getString("pen_pal_description", ""));
    }

    private void setOnClickListener() {
        bt_delete_pen_pal.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}
