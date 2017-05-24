package com.hewj.bangumi.view;

import com.hewj.bangumi.entity.BangumiEntity;

import java.util.ArrayList;

/**
 * Created by hewj on 2017/5/10.
 */

public interface MainView {
    void setData(ArrayList<ArrayList<BangumiEntity>> entities);
}
