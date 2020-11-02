package com.example.letter.email;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.letter.GetLetter.GetLetterActivity;
import com.example.letter.R;


public class EmailFragment extends Fragment implements View.OnClickListener {

    private Button bt_write_letter;
    private Button bt_get_letter;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_email, container, false);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        initUI();
        setOnClickListener();
    }

    private void initUI() {
        bt_write_letter = root.findViewById(R.id.bt_write_letter);
        bt_get_letter = root.findViewById(R.id.bt_get_letter);
    }

    private void setOnClickListener() {
        bt_write_letter.setOnClickListener(this);
        bt_get_letter.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bt_write_letter:
                startActivity(new Intent(getActivity(), WriteLetterActivity.class));
                break;
            case R.id.bt_get_letter:
                startActivity(new Intent(getActivity(), GetLetterActivity.class));
                break;
        }
    }

}
