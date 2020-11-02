package com.example.letter.GetLetter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.letter.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GetLetterAdapter extends RecyclerView.Adapter<GetLetterAdapter.MyViewHolder> {

    private List<Date> mTimeList;
    private Context mContext;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        View dateView;

        public MyViewHolder(View v) {
            super(v);
            dateView = v;
            tv_title = v.findViewById(R.id.tv_title);
        }
    }

    public GetLetterAdapter(Context context, List<Date> timeList) {
        mContext = context;
        mTimeList = timeList;
    }

    @Override
    public GetLetterAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.title_items, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        vh.dateView.setOnClickListener((View view) -> {
            int position = vh.getAdapterPosition();
            String time = mTimeList.get(position).toString();
            Intent intent = new Intent(mContext, ShowLetterActivity.class);
            intent.putExtra("time", time);
            mContext.startActivity(intent);
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat( "送达时间：yyyy年MM月dd日 HH:mm:ss");
        String time = df.format(mTimeList.get(position));
        holder.tv_title.setText(time);
    }

    @Override
    public int getItemCount() {
        return mTimeList.size();
    }
}
