package compartment;

import android.content.Context;
import android.view.View;

/**
 * Author:  leo
 * Email:   95253575@qq.com | leohak2010@gmail.com
 * Date:    2015/11/10.
 * Description:
 */
public interface BaseView {
    Context getApplicationContext();

    Context getBaseContext();

    void showLoading(String msg);

    void hideLoading();

    /**
     * show error message
     */
    void showError(String msg, View.OnClickListener onClickListener);

    /**
     * show exception message
     */
    void showException(String msg, View.OnClickListener onClickListener);

    /**
     * show net error
     */
    void showNetError(View.OnClickListener onClickListener);

    /**
     * show empty
     */
    void showEmpty(View.OnClickListener onClickListener);
}
