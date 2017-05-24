package com.hewj.bangumi.entity;

import java.util.ArrayList;

/**
 * Created by hewj on 2017/5/11.
 */

public class BangumiGroupEntity {
    ArrayList<String> group;//视频来源
    ArrayList<ArrayList<BangumiEpisodeEntity>> child;//具体每一集

    public ArrayList<String> getGroup() {
        return group;
    }

    public void setGroup(ArrayList<String> group) {
        this.group = group;
    }

    public ArrayList<ArrayList<BangumiEpisodeEntity>> getChild() {
        return child;
    }

    public void setChild(ArrayList<ArrayList<BangumiEpisodeEntity>> child) {
        this.child = child;
    }
}
