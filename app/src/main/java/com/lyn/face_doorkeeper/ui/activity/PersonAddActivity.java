package com.lyn.face_doorkeeper.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.entity.FaceResult;
import com.face.FaceHelper;
import com.lyn.face_doorkeeper.R;
import com.lyn.face_doorkeeper.database.Person;
import com.lyn.face_doorkeeper.databinding.ActivityAddPersonBinding;
import com.lyn.face_doorkeeper.ui.fragment.FaceDetectorFragment;
import com.lyn.face_doorkeeper.utils.FileUtils;
import com.lyn.face_doorkeeper.utils.LogUtils;
import com.mvp.PresenterImpl;
import com.utils.NV21ToBitmap;

import org.litepal.LitePal;

import java.io.File;
import java.util.UUID;


public class PersonAddActivity extends BaseActivity<ActivityAddPersonBinding> {
    private Bitmap bitmap;
    private float[] feature;
    private long pid;

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    protected ActivityAddPersonBinding getBindView(LayoutInflater layoutInflater) {
        return ActivityAddPersonBinding.inflate(layoutInflater);
    }

    @Override
    protected void bindData() {
        bindView.Toolbar.Title.setText(getString(R.string.str_add_person));
        bindView.Toolbar.Menu.setVisibility(View.VISIBLE);
        bindView.Toolbar.MenuIcon.setBackground(getDrawable(R.drawable.save));
        pid=getIntent().getLongExtra("pid",0);
        if (pid!=0){
            new showPersonDataAsyncTask().execute(pid);
        }
    }

    @Override
    protected void initListener() {
        bindView.Toolbar.Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUI(PersonManagementActivity.class);
                finish();
            }
        });

        bindView.Toolbar.Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SavePersonAsyncTask().execute();
            }
        });

        bindView.TakePictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
    }


    /**
     * 调起相机拍照
     */
    private void openCamera() {
        startActivityForResult(new Intent(context, TakePicturesActivity.class), 1);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (requestCode == 1) {
                new extractFaceAsyncTask().execute(data);
            }

        }
    }


    private class extractFaceAsyncTask extends AsyncTask<Intent, Integer, FaceResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected FaceResult doInBackground(Intent... intents) {
            if (intents == null && intents[0] == null) {
                LogUtils.d("bytes==null&&bytes[0]==null");
                return null;
            }
            Intent intent = intents[0];
            byte[] data = intent.getByteArrayExtra("data");
            int width = intent.getIntExtra("width", 640);
            int height = intent.getIntExtra("height", 480);
            FaceResult faceResult =
                    FaceHelper.getInstance().ExtractBitmapFaceInfo(data, width, height, false);

            return faceResult;
        }

        @Override
        protected void onPostExecute(FaceResult faceResult) {
            super.onPostExecute(faceResult);
            dismissSweetAlertDialog();
            if (faceResult == null) {
                bitmap = null;
                feature = null;
                Toast.makeText(context, getString(R.string.str_failedToExtractFacialFeatures), Toast.LENGTH_LONG).show();
                bindView.Photo.setImageDrawable(getDrawable(R.drawable.photo));
                return;
            }
            feature = faceResult.getFeature();
            bitmap = faceResult.getBitmap();
            bindView.Photo.setImageBitmap(faceResult.getBitmap());
        }
    }

    private class SavePersonAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected String doInBackground(String... strings) {
            Person person=LitePal.find(Person.class,pid);
            return sava(person);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dismissSweetAlertDialog();
            if (s.equals("success")) {
                changeUI(PersonManagementActivity.class);
                finish();
            } else {
                Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            }
        }
    }

    private String sava(Person person) {
        boolean isUpdate = false;
        String oldPath=null;
        if (person == null) {
            person = new Person();
        } else {
            oldPath=person.getPath();
            isUpdate = true;
        }
        int personType = bindView.PersonType.getSelectedItemPosition();
        String name = bindView.Name.getText().toString().trim();
        boolean sex = bindView.PersonSex.getSelectedItemPosition() == 0;
        String age = bindView.Age.getText().toString().trim();
        String idCard = bindView.IDCard.getText().toString().trim();
        String icCard = bindView.ICCard.getText().toString().trim();
        String description = bindView.Description.getText().toString().trim();
        person.setType(personType);
        if (TextUtils.isEmpty(name)) {
            return getString(R.string.str_nameCannotBeEmpty);
        }
        person.setName(name);
        person.setSex(sex);
        person.setAge(Integer.parseInt(TextUtils.isEmpty(age) ? "0" : age));
        if (!TextUtils.isEmpty(idCard)) {
            Person byIdCardPerson = Person.findByIdCard(idCard);
            if (byIdCardPerson != null) {
                if (isUpdate){
                    if (byIdCardPerson.getId()!=person.getId()){
                        return getString(R.string.str_theIDCardAlreadyExists);
                    }
                }else {
                    return getString(R.string.str_theIDCardAlreadyExists);
                }
            }
            person.setIdCard(idCard);
        }

        if (!TextUtils.isEmpty(icCard)) {
            Person byIcCardPerson = Person.findByIcCard(icCard);
            if (byIcCardPerson != null) {
                if (byIcCardPerson.getId()!=person.getId()){
                    return getString(R.string.str_theICCardAlreadyExists);
                }else {
                    return getString(R.string.str_theICCardAlreadyExists);
                }

            }
            person.setIcCard(icCard);
        }
        person.setDescription(description);

        if (bitmap == null) {
            return getString(R.string.str_pleaseTakeAPicture);
        }
        if (feature == null) {
            return getString(R.string.str_noFaceDetected);
        }
        person.setFeature(JSON.toJSONString(feature));
        String fileName = UUID.randomUUID().toString().concat(".jpg");
        String path = FileUtils.saveBitmap(FileUtils.getPersonImagePath(context), fileName, bitmap);
        if (TextUtils.isEmpty(path)) {
            return getString(R.string.str_failedToSavePhoto);
        }
        person.setFileName(fileName);
        person.setPath(path);
        boolean save = false;
        if (isUpdate) {
            save = person.update(person.getId()) == 1;
        } else {
            save = person.save();
        }
        if (save) {
            if (isUpdate){
                if (!TextUtils.isEmpty(oldPath)){
                    FileUtils.deleteFile(new File(oldPath));
                }
            }
            return "success";
        }
        return getString(R.string.str_failedToSaveStaff);
    }


    private class showPersonDataAsyncTask extends AsyncTask<Long,Integer,Person>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Person doInBackground(Long... longs) {
            long pid=longs[0];
            return  LitePal.find(Person.class,pid);
        }

        @Override
        protected void onPostExecute(Person person) {
            super.onPostExecute(person);
            dismissSweetAlertDialog();
            if (person==null){
                return;
            }
            if (!TextUtils.isEmpty(person.getPath())){
                bitmap=BitmapFactory.decodeFile(person.getPath());
                bindView.Photo.setImageBitmap(bitmap);
            }
            feature=JSONObject.parseObject(person.getFeature(),float[].class);
            bindView.PersonSex.setSelection(person.getType());
            bindView.Name.setText(person.getName());
            bindView.PersonSex.setSelection(person.isSex()?0:1);
            bindView.Age.setText(person.getAge()+"");
            bindView.IDCard.setText(person.getIdCard());
            bindView.ICCard.setText(person.getIcCard());
            bindView.Description.setText(person.getDescription());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

}
