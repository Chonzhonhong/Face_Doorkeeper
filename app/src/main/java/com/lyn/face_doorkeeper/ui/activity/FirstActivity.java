package com.lyn.face_doorkeeper.ui.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.face.FaceHelper;
import com.lyn.face_doorkeeper.databinding.ActivityFirstBinding;
import com.lyn.face_doorkeeper.face.FaceData;
import com.lyn.face_doorkeeper.utils.LogUtils;


public class FirstActivity extends BaseActivity<ActivityFirstBinding> {
    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    protected ActivityFirstBinding getBindView(LayoutInflater layoutInflater) {
        return ActivityFirstBinding.inflate(layoutInflater);
    }

    @Override
    protected void bindData() {
        new initAsyncTask().execute();
    }

    @Override
    protected void initListener() {

    }

    private class initAsyncTask extends AsyncTask<String,Integer,Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                FaceHelper.getInstance().init(context);
                FaceData.getInstance().loadFaceInfoData();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            dismissSweetAlertDialog();
            if (aBoolean) {
                LogUtils.i("初始化算法成功");
                changeUI(MainActivity.class);
                finish();
            } else {
                LogUtils.i("初始化算法失败");
            }
        }
    }
}
