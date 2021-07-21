package com.lyn.face_doorkeeper.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

/**
 * 作者：龙永宁
 * @param <B>
 */
public abstract class BaseDialog<B extends ViewBinding> extends Dialog {

    private B binding;

    public BaseDialog( Context context) {
        super(context);
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        B binding = getViewBinding();
        setContentView(binding.getRoot());
        bindData(binding,this);
        bindListener(binding,this);

        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setGravity(Gravity.CENTER);
        //获取屏幕宽度
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        lp.width = wm.getDefaultDisplay().getWidth()/10*8;
        window.setAttributes(lp);
    }


    protected abstract B getViewBinding();

    protected abstract void bindData(final B binding,final Dialog dialog);

    protected abstract void bindListener(final B binding,final Dialog dialog);

}
