package com.lyn.face_doorkeeper.ui.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.lyn.face_doorkeeper.R;


/**
 * activity 基类
 *
 * @param <T> BindViewing
 */
public abstract class BaseActivity<T extends ViewBinding> extends AppCompatActivity {
    protected T bindView;
    protected Context context;
    protected ProgressDialog progressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        init(savedInstanceState);
        context = this;
        bindView = getBindView(getLayoutInflater());
        setContentView(bindView.getRoot());
        bindData();
        initListener();

        ActivityManager.getAppManager().addActivity(this);
    }

    /**
     * activity创建回调，这里不能在这里做控件的设置操作，会报空指针，因为这里是在绑定视图前面执行
     */
    protected abstract void init(Bundle savedInstanceState);

    protected abstract T getBindView(LayoutInflater layoutInflater);

    /**
     * 绑定数据
     */
    protected abstract void bindData();

    /**
     * 初始化监听
     */
    protected abstract void initListener();


    /**
     * 土司显示
     *
     * @param message
     */
    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 浮动通知显示
     *
     * @param message
     */
    @SuppressLint("WrongConstant")
    protected void showSnackBar(String message) {
       /* if (bindView != null) {
            TopSnackbar.make(bindView.getRoot(), message, TopSnackbar.LENGTH_SHORT).show();
        } else {
            LogUtils.i("showSnackBar bindView==null");
        }*/
    }

    /**
     * 进度条
     */
    protected void showProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(getString(R.string.str_prompt));
        progressDialog.setMessage(getString(R.string.str_pleaseWait));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    /**
     * @param message
     */
    protected void setProgressDialogMessage(String message) {
        if (progressDialog != null) {
            progressDialog.setMessage(message);
        }
    }


    /**
     * 关闭进度对话框dialog
     */
    protected void dismissSweetAlertDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /**
     * 跳转新得页面
     *
     * @param newActivity 新的activity
     */
    protected void changeUI(Class newActivity) {
        startActivity(new Intent(getApplicationContext(), newActivity));
    }

    /**
     * 跳转网页
     *
     * @param url
     */
    protected void startToInternet(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    /**
     * 跳转传递值
     *
     * @param newActivity 新的activity
     * @param intent
     */
    protected void changeUiWithText(Class newActivity, Intent intent) {
        intent.setClass(getApplicationContext(), newActivity);
        startActivity(intent);
    }


    /**
     * 返回键拦截
     */
    @Override
    public void onBackPressed() {
        return;
    }


    /**
     * 销毁
     */
    @Override
    protected void onDestroy() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        super.onDestroy();
        ActivityManager.getAppManager().finishActivity(this);
    }
}
