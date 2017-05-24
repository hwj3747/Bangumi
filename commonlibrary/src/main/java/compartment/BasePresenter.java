package compartment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class BasePresenter<V,BV>{
    private V view;
    public void bindView(V view) {
        this.view = view;
    }
    public void unbindView() {
        this.view = null;
    }
    protected BV baseView;
    public BV getBaseView() { return baseView; }
    public void bindBaseView(BV view) {
        this.baseView = view;
    }
    public void unbindBaseView() {
        this.baseView = null;
    }
    public V getView() {
        return view;
    }
}
