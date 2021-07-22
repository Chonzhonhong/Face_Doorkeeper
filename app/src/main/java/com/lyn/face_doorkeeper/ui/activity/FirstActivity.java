package com.lyn.face_doorkeeper.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.face.FaceHelper;
import com.lyn.face_doorkeeper.R;
import com.lyn.face_doorkeeper.databinding.ActivityFirstBinding;
import com.lyn.face_doorkeeper.databinding.DialogConfirmOperationBinding;
import com.lyn.face_doorkeeper.face.FaceData;
import com.lyn.face_doorkeeper.ui.dialog.BaseDialog;
import com.lyn.face_doorkeeper.utils.LogUtils;

import org.jetbrains.annotations.NotNull;


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
        requestPermission();
    }

    private boolean requestPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
            return false;
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            return false;
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return false;
        }
        new initAsyncTask().execute();
        return true;
    }

    @Override
    protected void initListener() {

    }

    private class initAsyncTask extends AsyncTask<String, Integer, Boolean> {

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    //用户选择了禁止不再询问
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                        BaseDialog<DialogConfirmOperationBinding> operationBindingBaseDialog = new BaseDialog<DialogConfirmOperationBinding>(context, R.style.common_dialog) {
                            @Override
                            protected DialogConfirmOperationBinding getViewBinding() {
                                return DialogConfirmOperationBinding.bind(LayoutInflater.from(context).inflate(R.layout.dialog_confirm_operation, null, false));
                            }

                            @Override
                            protected void bindData(DialogConfirmOperationBinding binding, Dialog dialog) {
                                binding.Content.setText(getString(R.string.str_ifYouDenyPermissionYouCanTUseTheSoftware));
                                binding.Determine.setText(getString(R.string.str_manuallyOpen));
                            }

                            @Override
                            protected void bindListener(DialogConfirmOperationBinding binding, Dialog dialog) {
                                binding.Cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                                binding.Determine.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);//注意就是"package",不用改成自己的包名
                                        intent.setData(uri);
                                        startActivityForResult(intent, 1);
                                    }
                                });
                            }
                        };
                        operationBindingBaseDialog.setCanceledOnTouchOutside(false);
                        operationBindingBaseDialog.show();
                        return;
                    }
                }
            }
            requestPermission();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            requestPermission();//由于不知道是否选择了允许所以需要再次判断
        }
    }
}
