package com.lyn.face_doorkeeper.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.lyn.face_doorkeeper.R;
import com.lyn.face_doorkeeper.databinding.ActivityFaceSettingBinding;
import com.lyn.face_doorkeeper.utils.MMKVUtils;

public class FaceSettingActivity extends BaseActivity<ActivityFaceSettingBinding> {
    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    protected ActivityFaceSettingBinding getBindView(LayoutInflater layoutInflater) {
        return ActivityFaceSettingBinding.inflate(layoutInflater);
    }

    @Override
    protected void bindData() {
        bindView.Toolbar.Title.setText(getString(R.string.str_faceRecognitionSettings));

        //人脸识别阈值
        int faceRecognitionThreshold = MMKVUtils.getInstance().getInt(MMKVUtils.FACE_RECOGNITION_THRESHOLD, MMKVUtils.defValue.FACE_RECOGNITION_THRESHOLD);
        bindView.faceRecognitionThreshold.setText(faceRecognitionThreshold + "");
        bindView.faceRecognitionThresholdBar.setProgress(faceRecognitionThreshold);

        //活体
        boolean livingBody = MMKVUtils.getInstance().getBoolean(MMKVUtils.LIVING_BODY, MMKVUtils.defValue.LIVING_BODY);
        bindView.livingBodySw.setChecked(livingBody);

        //同人识别间隔
        int fanRecognitionInterval = MMKVUtils.getInstance().getInt(MMKVUtils.FAN_RECOGNITION_INTERVAL, MMKVUtils.defValue.FAN_RECOGNITION_INTERVAL);
        bindView.repeatVerificationInterval.setText(fanRecognitionInterval + "");
        bindView.repeatVerificationIntervalSBar.setProgress(fanRecognitionInterval);

        //实名显示
        boolean isRealNameDisplay=MMKVUtils.getInstance().getBoolean(MMKVUtils.VERIFY_REAL_NAME_DISPLAY,MMKVUtils.defValue.VERIFY_REAL_NAME_DISPLAY);
        bindView.verifyRealNameDisplaySw.setChecked(isRealNameDisplay);
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

        //人脸势必阈值监听器
        bindView.faceRecognitionThresholdBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bindView.faceRecognitionThreshold.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MMKVUtils.getInstance().setInt(MMKVUtils.FACE_RECOGNITION_THRESHOLD, seekBar.getProgress());
            }
        });

        //活体
        bindView.livingBodySw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MMKVUtils.getInstance().setBoolean(MMKVUtils.LIVING_BODY, isChecked);
            }
        });

        //同人识别间隔
        bindView.repeatVerificationIntervalSBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bindView.repeatVerificationInterval.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MMKVUtils.getInstance().setInt(MMKVUtils.FAN_RECOGNITION_INTERVAL, seekBar.getProgress());
            }
        });

        //实名显示
        bindView.verifyRealNameDisplaySw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MMKVUtils.getInstance().setBoolean(MMKVUtils.VERIFY_REAL_NAME_DISPLAY,isChecked);
            }
        });
    }
}
