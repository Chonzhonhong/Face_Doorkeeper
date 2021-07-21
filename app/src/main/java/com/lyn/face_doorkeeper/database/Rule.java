package com.lyn.face_doorkeeper.database;

import android.text.TextUtils;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.UUID;

/**
 * 规则
 */
public class Rule extends LitePalSupport {
    /**
     * 自增id
     */
    private long id;

    /**
     * uuid
     */
    @Column(nullable = false, unique = true)
    private String ruleId;
    /**
     * 姓名
     */
    private String name;
    /**
     * 开始时间
     */
    private long startTime;
    /**
     * 介绍时间
     */
    private long endTime;
    /**
     * 星期一
     */
    private boolean monday;

    /**
     * 星期二
     */
    private boolean tuesday;

    /**
     * 星期三
     */
    private boolean wednesday;

    /**
     * 星期四
     */
    private boolean thursday;

    /**
     * 星期五
     */
    private boolean friday;

    /**
     * 星期六
     */
    private boolean saturday;

    /**
     * 星期天
     */
    private boolean sunday;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isMonday() {
        return monday;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    @Override
    public boolean save() {
        if (TextUtils.isEmpty(ruleId)) {
            ruleId = UUID.randomUUID().toString();
        }
        boolean save = super.save();
        return save;
    }
}
