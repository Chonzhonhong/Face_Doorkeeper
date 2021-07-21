package com.lyn.face_doorkeeper.ui.activity;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.face.FaceHelper;
import com.lyn.face_doorkeeper.R;
import com.lyn.face_doorkeeper.database.Person;
import com.lyn.face_doorkeeper.databinding.ActivityMainBinding;
import com.lyn.face_doorkeeper.databinding.DialogShowPersonInfoBinding;
import com.lyn.face_doorkeeper.entity.FaceInfo;
import com.lyn.face_doorkeeper.entity.FaceScore;
import com.lyn.face_doorkeeper.face.FaceData;
import com.lyn.face_doorkeeper.ui.adapter.MyBaseAdapter;
import com.lyn.face_doorkeeper.ui.adapter.MyPagerAdapter;
import com.lyn.face_doorkeeper.ui.dialog.BaseDialog;
import com.lyn.face_doorkeeper.ui.fragment.FaceDetectorFragment;
import com.lyn.face_doorkeeper.utils.LogUtils;
import com.lyn.face_doorkeeper.utils.MMKVUtils;
import com.lyn.face_doorkeeper.utils.SaveRecord;
import com.lyn.face_doorkeeper.utils.StringUtils;
import com.lyn.face_doorkeeper.utils.SystemUtils;
import com.lyn.face_doorkeeper.utils.TTS_Tool;
import com.mvp.PresenterImpl;
import com.seeta.sdk.Nv21Data;
import com.utils.NV21ToBitmap;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends BaseActivity<ActivityMainBinding> implements FaceDetectorFragment.Callback {

    private FaceDetectorFragment faceDetectorFragment;

    private HandlerThread faceDetectHandlerThread;

    private HandlerThread faceHandlerThread;

    private NV21ToBitmap nv21ToBitmap;
    private BaseDialog<DialogShowPersonInfoBinding> showPersonInfoBindingBaseDialog;
    private volatile long dismissDialogTime;

    {
        faceDetectHandlerThread = new HandlerThread("faceDetectHandlerThread", Process.THREAD_PRIORITY_MORE_FAVORABLE);
        faceDetectHandlerThread.start();

        faceHandlerThread = new HandlerThread("faceHandlerThread", Process.THREAD_PRIORITY_MORE_FAVORABLE);
        faceHandlerThread.start();
    }

    private MyPagerAdapter myPagerAdapter;
    private ImageView oneImage, twoImage, threeImage;

    /**
     * 人脸识别阈值
     */
    private float faceRecognitionThreshold;
    /**
     * 同人识别间隔
     */
    private int fanRecognitionInterval;
    /**
     * 实名显示
     */
    private boolean isRealNameDisplay;

    @Override

    protected void init(Bundle savedInstanceState) {

    }

    @Override
    protected ActivityMainBinding getBindView(LayoutInflater layoutInflater) {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void bindData() {
        getConfig();
        nv21ToBitmap = new NV21ToBitmap(context);
        faceDetectorFragment = new FaceDetectorFragment();
        faceDetectorFragment.setCallback(this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(bindView.FaceDetectorFrameLayout.getId(), faceDetectorFragment)
                .commitNow();

        List<View> viewList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            viewList.add(new ImageView(context));
        }
        myPagerAdapter = new MyPagerAdapter(viewList);
        bindView.HeaderViewPager.setScrollBarFadeDuration(1000);
        bindView.HeaderViewPager.setOffscreenPageLimit(3);
        bindView.HeaderViewPager.setPageMargin(40);
        bindView.HeaderViewPager.setAdapter(myPagerAdapter);


    }

    @Override
    protected void initListener() {
        bindView.FaceDetectorFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (faceDetectorFragment != null) {
                    faceDetectorFragment.Focus();
                }

            }
        });
        bindView.Hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUI(MenuActivity.class);
                finish();
            }
        });
        bindView.HeaderViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /**
     * 获取配置参数数据
     */
    private void getConfig() {
        faceRecognitionThreshold = MMKVUtils.getInstance().getInt(MMKVUtils.FACE_RECOGNITION_THRESHOLD, MMKVUtils.defValue.FACE_RECOGNITION_THRESHOLD) / 100;
        fanRecognitionInterval = MMKVUtils.getInstance().getInt(MMKVUtils.FAN_RECOGNITION_INTERVAL, MMKVUtils.defValue.FAN_RECOGNITION_INTERVAL);
        isRealNameDisplay = MMKVUtils.getInstance().getBoolean(MMKVUtils.VERIFY_REAL_NAME_DISPLAY, MMKVUtils.defValue.VERIFY_REAL_NAME_DISPLAY);
    }

    @Override
    public void toNv21Data(Nv21Data nv21Data) {
        TimedPolling();
        if (isFinishing()) {
            return;
        }
        FaceHelper.getInstance().setPreviewScaleX((float) bindView.faceTracking.getWidth() / nv21Data.getHeight());
        FaceHelper.getInstance().setPreviewScaleY((float) bindView.faceTracking.getHeight() / nv21Data.getWidth());
        faceDetectHandler.removeMessages(1);
        faceDetectHandler.obtainMessage(1, nv21Data).sendToTarget();
    }

    /**
     * 人脸检测handler
     */
    private Handler faceDetectHandler = new Handler(faceDetectHandlerThread.getLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            try {
                Nv21Data nv21Data = (Nv21Data) msg.obj;
//                LogUtils.i("width:" + nv21Data.getWidth() + "height:" + nv21Data.getHeight());
                final PresenterImpl.TrackingInfo trackingInfo = FaceHelper.getInstance().detect(nv21Data.getData(), nv21Data.getWidth(), nv21Data.getHeight());
                if (trackingInfo == null) {
                    return;
                }
                if (!trackingInfo.isFace) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bindView.faceTracking.setRectF(null);
                            bindView.Hint.setText(getString(R.string.str_pleaseAimAtTheCameraAndBrushYourFace));
                        }
                    });
                    return;
                }
                bindView.faceTracking.setRectF(trackingInfo.faceRectF);
                faceHandler.removeMessages(1);
                faceHandler.obtainMessage(1, trackingInfo).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private List<FaceScore> faceScoreList = new ArrayList<>();
    /**
     * 人脸识别handler
     */
    private Handler faceHandler = new Handler(faceHandlerThread.getLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            PresenterImpl.TrackingInfo trackingInfo = (PresenterImpl.TrackingInfo) msg.obj;
            float[] feature = FaceHelper.getInstance().getFeature(trackingInfo);
            if (feature == null) {
                LogUtils.i("feature==null");
                return;
            }
            HashSet<FaceInfo> faceInfoHashSet = FaceData.getInstance().getFaceInfoHashSet();
            if (faceInfoHashSet == null) {
                LogUtils.i("faceInfoHashSet==null");
                return;
            }
            int count = 0;
            faceScoreList.clear();
            Iterator<FaceInfo> faceInfoIterator = faceInfoHashSet.iterator();
            while (faceInfoIterator.hasNext()) {
                FaceInfo faceInfo = faceInfoIterator.next();
                float score = FaceHelper.getInstance().FeatureComparison(feature, faceInfo.getFeature());
                LogUtils.i(faceInfo.getId() + "比对分数是:" + score);
                if (score > 0.0f && score > faceRecognitionThreshold) {
                    if (count >= 10) {
                        break;
                    }
                    FaceScore faceScore = new FaceScore();
                    faceScore.setId(faceInfo.getId());
                    faceScore.setScore(score);
                    faceScoreList.add(faceScore);
                    count++;
                }
            }
            if (count == 0) {
                LogUtils.i("count==0");
                return;
            }
            FaceScore faceScore = null;
            for (int i = 0; i < faceScoreList.size(); i++) {
                if (faceScore == null) {
                    faceScore = faceScoreList.get(i);
                } else {
                    if (faceScore.getScore() < faceScoreList.get(i).getScore()) {
                        faceScore = faceScoreList.get(i);
                    }
                }
            }
            if (faceScore == null) {
                LogUtils.i("faceScore==null");
                return;
            }

            Person person = LitePal.find(Person.class, faceScore.getId());
            if (person == null) {
                return;
            }
            recognitionResult(person, trackingInfo, faceScore);
        }
    };

    private volatile long pid;
    private volatile long RepeatVerificationTime;
    private volatile boolean isRecognitionResult = true;

    private void recognitionResult(final Person person, final PresenterImpl.TrackingInfo trackingInfo, final FaceScore faceScore) {
        if (!isRecognitionResult) {
            return;
        }
        isRecognitionResult = false;
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (pid == person.getId()) {
                        if (System.currentTimeMillis() - RepeatVerificationTime <= fanRecognitionInterval * 1000) {
                            bindView.Hint.setText(realNameDisplay(person.getName()) + space(1) + getString(R.string.str_pleaseRepeatVerificationByMistake));
                            return;
                        }
                    }
                    pid = person.getId();
                    RepeatVerificationTime = System.currentTimeMillis();
                    Bitmap bitmap = FaceHelper.getInstance().MatToBitmap(trackingInfo, false);
                    showPersonInfoDialog(person, bitmap, faceScore.getScore(),true);
                }
            });
        } finally {
            isRecognitionResult = true;
        }

    }

    /**
     * 实名显示
     * @param str
     * @return
     */
    private String realNameDisplay(String str) {
        if (!isRealNameDisplay) {
            String realNameDisplay = StringUtils.realNameDisplay(str);
            return realNameDisplay;
        }
        return str;
    }

    /**
     * 空格
     *
     * @param count
     * @return
     */
    private String space(int count) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < count; i++) {
            stringBuffer.append("\t");
        }
        return stringBuffer.toString();
    }

    /**
     * 显示验证信息
     *
     * @param person
     * @param bitmap
     * @param isSuccess
     */
    private void showPersonInfoDialog(final Person person, Bitmap bitmap,final float score,final boolean isSuccess) {
        if (isFinishing()) {
            return;
        }
        if (person == null || bitmap == null) {
            return;
        }
        if (showPersonInfoBindingBaseDialog != null) {
            showPersonInfoBindingBaseDialog.dismiss();
            showPersonInfoBindingBaseDialog = null;
        }
        showPersonInfoBindingBaseDialog = new BaseDialog<DialogShowPersonInfoBinding>(context, R.style.common_dialog) {
            @Override
            protected DialogShowPersonInfoBinding getViewBinding() {
                return DialogShowPersonInfoBinding.inflate(getLayoutInflater());
            }

            @Override
            protected void bindData(DialogShowPersonInfoBinding binding, Dialog dialog) {
                binding.Name.setText(realNameDisplay(person.getName()));
                bindView.Hint.setText(isSuccess ? getString(R.string.str_verifiedSuccessfully) : getString(R.string.str_verificationFailed));
                binding.HeaderPhoto.setImageBitmap(bitmap);
                TTS_Tool.getInstance().Pronounce(binding.Name.getText().toString() + bindView.Hint.getText().toString());
            }

            @Override
            protected void bindListener(DialogShowPersonInfoBinding binding, Dialog dialog) {

            }
        };
        showPersonInfoBindingBaseDialog.show();
        if (showPersonInfoBindingBaseDialog.isShowing()) {
            dismissDialogTime = System.currentTimeMillis();
        }
        boolean save = SaveRecord.getInstance().save(person, bitmap,score, isSuccess, 0f);
        if (save) {
            LogUtils.i("保存记录成功");
        } else {
            LogUtils.i("保存记录失败");
        }
    }

    /**
     * 定时轮询
     */
    private void TimedPolling() {
        if (System.currentTimeMillis() - dismissDialogTime >= 5000) {
            dismissDialogTime = System.currentTimeMillis();
            if (showPersonInfoBindingBaseDialog != null) {
                showPersonInfoBindingBaseDialog.dismiss();
                showPersonInfoBindingBaseDialog = null;
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            faceDetectHandlerThread.quitSafely();
            faceHandlerThread.quitSafely();
        }
    }


}
