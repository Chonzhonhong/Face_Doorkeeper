package com.lyn.face_doorkeeper.face;

import com.alibaba.fastjson.JSONObject;
import com.lyn.face_doorkeeper.database.Person;
import com.lyn.face_doorkeeper.entity.FaceInfo;

import org.litepal.LitePal;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class FaceData {

    private static class FaceDataTypeClass {
        public static FaceData instance = new FaceData();
    }

    public static FaceData getInstance() {
        return FaceDataTypeClass.instance;
    }

    private static HashSet<FaceInfo> faceInfoHashSet = new HashSet<>();


    public void loadFaceInfoData() {
        List<Person> personList = LitePal.findAll(Person.class);
        if (personList != null) {
            faceInfoHashSet.clear();
            for (int i = 0; i < personList.size(); i++) {
                Person person = personList.get(i);
                FaceInfo faceInfo = new FaceInfo();
                faceInfo.setId(person.getId());
                float[] feature = JSONObject.parseObject(person.getFeature(), float[].class);
                faceInfo.setFeature(feature);
                faceInfoHashSet.add(faceInfo);
            }
        }
    }

    public boolean addFaceInfo(Person person) {
        if (person == null) {
            return false;
        }
        FaceInfo faceInfo = new FaceInfo();
        faceInfo.setId(person.getId());
        float[] feature = JSONObject.parseObject(person.getFeature(), float[].class);
        faceInfo.setFeature(feature);
        return faceInfoHashSet.add(faceInfo);
    }

    public boolean deleteFaceInfo(long id) {
        Iterator<FaceInfo> iterator = faceInfoHashSet.iterator();
        while (iterator.hasNext()) {
            FaceInfo faceInfo = iterator.next();
            if (faceInfo.getId() == id) {
                return faceInfoHashSet.remove(faceInfo);
            }
        }
        return false;
    }


    public boolean updateFaceInfo(Person person) {
        if (person == null) {
            return false;
        }
        Iterator<FaceInfo> iterator = faceInfoHashSet.iterator();
        while (iterator.hasNext()) {
            FaceInfo faceInfo = iterator.next();
            if (faceInfo.getId() == person.getId()) {
                faceInfoHashSet.remove(faceInfo);
                break;
            }
        }
        FaceInfo faceInfo = new FaceInfo();
        faceInfo.setId(person.getId());
        float[] feature = JSONObject.parseObject(person.getFeature(), float[].class);
        faceInfo.setFeature(feature);
        return faceInfoHashSet.add(faceInfo);
    }


    public FaceInfo queryFaceInfo(long id) {
        Iterator<FaceInfo> iterator = faceInfoHashSet.iterator();
        while (iterator.hasNext()) {
            FaceInfo faceInfo = iterator.next();
            if (faceInfo.getId() == id) {
                return faceInfo;
            }
        }
        return null;
    }

    public HashSet<FaceInfo> getFaceInfoHashSet() {
        return faceInfoHashSet;
    }
}
