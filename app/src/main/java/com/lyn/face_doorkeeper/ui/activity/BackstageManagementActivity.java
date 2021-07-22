package com.lyn.face_doorkeeper.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import com.lyn.face_doorkeeper.R;
import com.lyn.face_doorkeeper.databinding.ActivityBackstageManagementBinding;
import com.lyn.face_doorkeeper.utils.MMKVUtils;

public class BackstageManagementActivity extends BaseActivity<ActivityBackstageManagementBinding>{
    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    protected ActivityBackstageManagementBinding getBindView(LayoutInflater layoutInflater) {
        return ActivityBackstageManagementBinding.inflate(layoutInflater);
    }

    @Override
    protected void bindData() {
        bindView.Toolbar.Title.setText(getString(R.string.str_backstageManagement));

        bindView.ServerAddress.setText(MMKVUtils.getInstance().getString(MMKVUtils.SERVER_ADDRESS,MMKVUtils.defValue.SERVER_ADDRESS));

        bindView.RecordTheCallbackAddress.setText(MMKVUtils.getInstance().getString(MMKVUtils.RECORD_THE_CALLBACK_ADDRESS,MMKVUtils.defValue.RECORD_THE_CALLBACK_ADDRESS));
    }

    @Override
    protected void initListener() {
        bindView.Toolbar.Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUI(MenuActivity.class);
                finish();
            }
        });

        bindView.ServerAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                MMKVUtils.getInstance().setString(MMKVUtils.SERVER_ADDRESS,s.toString());
            }
        });

        bindView.RecordTheCallbackAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                MMKVUtils.getInstance().setString(MMKVUtils.RECORD_THE_CALLBACK_ADDRESS,s.toString());
            }
        });

    }
}
