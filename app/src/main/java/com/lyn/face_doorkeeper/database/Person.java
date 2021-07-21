package com.lyn.face_doorkeeper.database;


import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.lyn.face_doorkeeper.R;
import com.lyn.face_doorkeeper.face.FaceData;
import com.lyn.face_doorkeeper.ui.App;
import com.lyn.face_doorkeeper.utils.FileUtils;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * 人员
 */
public class Person extends LitePalSupport {

    /**
     * 自增id
     */
    private long id;

    /**
     * 人员id
     */
    private String personId;

    /**
     * 姓名
     */

    private String name;

    /**
     * 人员类型
     */
    private int type;

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
     * 特征值
     */
    private String feature;
    /**
     * 照片路径
     */

    private String path;
    /**
     * 文件名
     */

    private String fileName;
    /**
     * 描述
     */
    private String description;

    /**
     * 开始时间
     */
    private long startTime;

    /**
     * 到期时间
     */
    private long endTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * 常客
     */
    public final static int FREQUENT_VISITOR = 0;
    /**
     * 访客
     */
    public final static int VISITOR = 1;
    /**
     * 管理员
     */
    public final static int ADMINISTRATOR = 2;
    /**
     * 访客
     */
    public final static int BLACKLIST = 3;

    /**
     * 人员类型解析1
     * @param type
     * @return
     */
    public static String getType(int type) {
        if (type == 0) {
            return App.getContext().getString(R.string.str_frequentVisitor);
        }else if (type==1){
            return App.getContext().getString(R.string.str_visitor);
        }else if (type==2){
            return App.getContext().getString(R.string.str_administrator);
        }else if (type==3){
            return App.getContext().getString(R.string.str_blacklist);
        }else {
            return App.getContext().getString(R.string.str_frequentVisitor);
        }
    }

    /**
     * 人员类型解析1
     * @param str
     * @return
     */
    public static int getType(String str) {

        if (str.equals(App.getContext().getString(R.string.str_frequentVisitor))){
            return 0;
        }else if (str.equals(App.getContext().getString(R.string.str_visitor))){
            return 1;
        }else if (str.equals(App.getContext().getString(R.string.str_administrator))){
            return 2;
        }else if (str.equals(App.getContext().getString(R.string.str_blacklist))){
            return 3;
        }else {
            return 0;
        }
    }



    public static Person findByPersonId(String personId) {
        List<Person> personList = LitePal.where("personId=?", personId).find(Person.class);
        Person person = null;
        if (personList != null && personList.size() > 0) {
            person = personList.get(0);
        }
        return person;
    }


    public static Person findByIdCard(String idCard) {
        List<Person> personList = LitePal.where("idCard=?", idCard).find(Person.class);
        Person person = null;
        if (personList != null && personList.size() > 0) {
            person = personList.get(0);
        }
        return person;
    }

    public static Person findByIcCard(String icCard) {
        List<Person> personList = LitePal.where("icCard=?", icCard).find(Person.class);
        Person person = null;
        if (personList != null && personList.size() > 0) {
            person = personList.get(0);
        }
        return person;
    }


    @Override
    public boolean save() {
        if (TextUtils.isEmpty(personId)){
            personId= UUID.randomUUID().toString();
        }
        startTime=System.currentTimeMillis();
        boolean save = super.save();
        if (save){
            FaceData.getInstance().addFaceInfo(this);
        }
        return save;
    }

    @Override
    public int update(long id) {
        startTime=System.currentTimeMillis();
        int update = super.update(id);
        if (update==1){
            FaceData.getInstance().updateFaceInfo(this);
        }
        return update;
    }

    @Override
    public int delete() {
        try {
            if (!TextUtils.isEmpty(path)){
                FileUtils.deleteFile(new File(path));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        int delete = super.delete();
        if (delete==1){
            FaceData.getInstance().deleteFaceInfo(id);
        }
        return delete;
    }


    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
