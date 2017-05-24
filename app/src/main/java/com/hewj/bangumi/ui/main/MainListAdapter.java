package com.hewj.bangumi.ui.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hewj.bangumi.R;
import com.hewj.bangumi.entity.BangumiEntity;

import java.util.ArrayList;

/**
 * Created by hewj on 2017/5/10.
 * 番剧列表适配器
 */

public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.MyViewHolder> implements View.OnClickListener {

    private Context mContext;
    private ArrayList<BangumiEntity> datas;//数据
    private MyItemClickListener mListener;

    public MainListAdapter(Context context,ArrayList<BangumiEntity> datas,MyItemClickListener mListener) {
        this.mContext=context;
        this.datas=datas;
        this.mListener=mListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_cardview, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        holder.bangumiName.setText(datas.get(position).getTitle());
        holder.bangumiNum.setText(datas.get(position).getNumber());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount()
    {
        return datas.size();
    }

    @Override
    public void onClick(View v) {
        if(mListener != null){
            mListener.onItemClick(v,(int)v.getTag());
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {

        TextView bangumiName;
        TextView bangumiNum;
        public MyViewHolder(View view)
        {
            super(view);
            bangumiName=(TextView)view.findViewById(R.id.bangumi_name);
            bangumiNum=(TextView)view.findViewById(R.id.bangumi_num);
        }
    }
}
