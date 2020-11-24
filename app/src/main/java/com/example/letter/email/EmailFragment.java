package com.example.letter.email;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
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
        createFile();
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

    // Request code for creating a PDF document.
    private static final int CREATE_FILE = 1;

    private void createFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/doc");
        intent.putExtra(Intent.EXTRA_TITLE, "invoice.doc");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, "content://sdcard");

        startActivityForResult(intent, CREATE_FILE);
    }

    private static final int PICK_PDF_FILE = 2;

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");

        startActivityForResult(intent, PICK_PDF_FILE);
    }
}
