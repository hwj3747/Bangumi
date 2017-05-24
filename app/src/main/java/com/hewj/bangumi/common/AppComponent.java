package com.hewj.bangumi.common;


import com.hewj.bangumi.ui.bangumiinfo.BangumiInfoFragment;
import com.hewj.bangumi.ui.main.MainFragment;

import dagger.Component;
/**
 * Created by hewj on 2017/5/10.
 */
@Component(
        modules = AppModule.class
)
public interface AppComponent {
    MainFragment inject(MainFragment mainFragment);
    BangumiInfoFragment inject(BangumiInfoFragment bangumiInfoFragment);
}
