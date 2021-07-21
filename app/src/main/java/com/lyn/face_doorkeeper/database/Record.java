package com.lyn.face_doorkeeper.database;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.lyn.face_doorkeeper.R;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * 记录
 */
public class Record extends LitePalSupport {
    /**
     * 自增id
     */
    private long id;
    /**
     * 记录id
     */

    private String recordId;
    /**
     * 人员id
     */
    private String personId;

    /**
     * 姓名
     */

    private String name;

    /**
     * 身份证
     */

    private String idCard;
    /**
     * IC卡
     */

    private String icCard;

    /**
     * 性别
     */

    private boolean sex;
    /**
     * 年龄
     */

    private int age;
    /**
     * 体温
     */
    private String bodyTemperature;
    /**
     * 识别分数
     */
    private float score;

    /**
     * 验证状态true/false
     */
    private boolean success;

    /**
     * 上传状态true/false
     */
    private boolean upload;

    /**
     * 照片路径
     */
    private String photoFilePath;

    /**
     * 照片文件名
     */
    private String photoFileName;

    /**
     * 描述
     */
    private String description;

    private long time;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getIcCard() {
        return icCard;
    }

    public void setIcCard(String icCard) {
        this.icCard = icCard;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBodyTemperature() {
        return bodyTemperature;
    }

    public void setBodyTemperature(String bodyTemperature) {
        this.bodyTemperature = bodyTemperature;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    public String getPhotoFilePath() {
        return photoFilePath;
    }

    public void setPhotoFilePath(String photoFilePath) {
        this.photoFilePath = photoFilePath;
    }

    public String getPhotoFileName() {
        return photoFileName;
    }

    public void setPhotoFileName(String photoFileName) {
        this.photoFileName = photoFileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public static Record findByRecordId(String recordId) {
        List<Record> recordList = LitePal.where("recordId=?", recordId).find(Record.class);
        Record record = null;
        if (recordList != null && recordList.size() > 0) {
            record = recordList.get(0);
        }
        return record;
    }


    public static String getResult(Context context,boolean result ){
        if (result){
            return context.getString(R.string.str_verifiedSuccessfully);
        }
        return context.getString(R.string.str_verificationFailed);
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
