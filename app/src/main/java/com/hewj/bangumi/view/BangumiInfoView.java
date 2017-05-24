package com.hewj.bangumi.view;

import com.hewj.bangumi.entity.BangumiGroupEntity;
import com.hewj.bangumi.entity.BangumiInfoEntity;

/**
 * Created by hewj on 2017/5/11.
 */

public interface BangumiInfoView {
    public void setData(BangumiInfoEntity entity);
    public void setList(BangumiGroupEntity entity);
}
