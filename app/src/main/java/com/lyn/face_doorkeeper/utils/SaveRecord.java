package com.lyn.face_doorkeeper.utils;

import android.graphics.Bitmap;
import android.os.HandlerThread;
import android.os.Process;
import android.webkit.URLUtil;

import com.lyn.face_doorkeeper.database.Person;
import com.lyn.face_doorkeeper.database.Record;
import com.lyn.face_doorkeeper.ui.App;

import java.util.UUID;

public class SaveRecord {


    private static class SaveRecordTypeClass {
        private static SaveRecord instance = new SaveRecord();
    }

    public static SaveRecord getInstance() {
        return SaveRecordTypeClass.instance;
    }


    public boolean save(Person person, Bitmap bitmap,float score, boolean isSuccess, float bodyTemperature) {
        Record record = new Record();
        record.setRecordId(UUID.randomUUID().toString());
        record.setName(person.getName());
        record.setIdCard(person.getIdCard());
        record.setIcCard(person.getIcCard());
        record.setAge(person.getAge());
        record.setSex(person.isSex());
        record.setBodyTemperature(bodyTemperature + "");
        record.setPersonId(person.getPersonId());
        record.setScore(score);
        record.setSuccess(isSuccess);
        record.setDescription(person.getDescription());
        record.setTime(System.currentTimeMillis());
        String fileName = UUID.randomUUID().toString().concat(".jpg");

        String path = FileUtils.saveBitmap(FileUtils.getRecordImagePath(App.getContext()), fileName, bitmap);
        record.setPhotoFilePath(path);
       return record.save();
    }
}
