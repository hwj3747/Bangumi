package com.hewj.bangumi.ui.bangumiinfo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hewj.bangumi.R;
import com.hewj.bangumi.entity.BangumiEpisodeEntity;
import com.hewj.bangumi.ui.webview.WebViewActivity;

import java.util.ArrayList;

/**
 * Created by hewj on 2017/5/10.
 * 番剧剧集适配器
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private LayoutInflater mLayoutInflater;
    protected Context context;
    private ArrayList<String> groupArray;
    private ArrayList<ArrayList<BangumiEpisodeEntity>> childArray;

    public ExpandableListAdapter(Context context,  ArrayList<String> groupArray, ArrayList<ArrayList<BangumiEpisodeEntity>> childArray){
        this.context=context;
        this.groupArray = groupArray;
        this.childArray = childArray;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public ExpandableListAdapter(Context context){
        this.context=context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return groupArray.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childArray.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            groupViewHolder = new GroupViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_group_view, null);
            groupViewHolder.groupName=(TextView)convertView.findViewById(R.id.group_name);
            groupViewHolder.arrow=(ImageView)convertView.findViewById(R.id.ico_arrow);
            groupViewHolder.arrowDown=(ImageView)convertView.findViewById(R.id.ico_arrow_down);
            convertView.setTag(groupViewHolder);
        }else{
            groupViewHolder = (GroupViewHolder)convertView.getTag();
        }
        groupViewHolder.groupName.setText(groupArray.get(groupPosition));
        if (isExpanded) {
            groupViewHolder.arrow.setVisibility(View.GONE);
            groupViewHolder.arrowDown.setVisibility(View.VISIBLE);
        }else {
            groupViewHolder.arrowDown.setVisibility(View.GONE);
            groupViewHolder.arrow.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        View view = convertView;
        if (convertView == null) {
            childViewHolder = new ChildViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_child_view, null);
            childViewHolder.num=(TextView)convertView.findViewById(R.id.num);
            childViewHolder.linearLayout= (LinearLayout) convertView.findViewById(R.id.layout);
            convertView.setTag(childViewHolder);
        }else {
            childViewHolder = (ChildViewHolder)convertView.getTag();
        }
//        http://static.hdslb.com/miniloader.swf?aid=10053077&page=1
        childViewHolder.num.setText(childArray.get(groupPosition).get(childPosition).getNum());
        childViewHolder.linearLayout.setOnClickListener(v->{
            WebViewActivity.launch(context,childArray.get(groupPosition).get(childPosition).getUrl(),childArray.get(groupPosition).get(childPosition).getNum());
            //VideoPlayActivity.launch(context,childArray.get(groupPosition).get(childPosition).getUrl());
            Log.i("url1",childArray.get(groupPosition).get(childPosition).getUrl());
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class GroupViewHolder{
        ImageView arrow;
        ImageView arrowDown;
        TextView groupName;
    }

    class ChildViewHolder{
        LinearLayout linearLayout;
        TextView num;
    }
}
