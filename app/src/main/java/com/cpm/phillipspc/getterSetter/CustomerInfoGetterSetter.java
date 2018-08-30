package com.cpm.phillipspc.getterSetter;

import com.cpm.phillipspc.gsonGetterSetter.FeedbackQuestionData;

import java.util.ArrayList;

/**
 * Created by neerajg on 2/19/2018.
 */

public class CustomerInfoGetterSetter {

    public String  USER_NAME,USER_EMAIL,USER_MOBILE,VISITED_DATE,USER_ID;

    ArrayList<FeedbackQuestionData> list = new ArrayList<>();

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getVISITED_DATE() {
        return VISITED_DATE;
    }

    public void setVISITED_DATE(String VISITED_DATE) {
        this.VISITED_DATE = VISITED_DATE;
    }

    public ArrayList<FeedbackQuestionData> getList() {
        return list;
    }

    public void setList(ArrayList<FeedbackQuestionData> list) {
        this.list = list;
    }

    public String getUSER_NAME() {
        return USER_NAME;
    }

    public void setUSER_NAME(String USER_NAME) {
        this.USER_NAME = USER_NAME;
    }

    public String getUSER_EMAIL() {
        return USER_EMAIL;
    }

    public void setUSER_EMAIL(String USER_EMAIL) {
        this.USER_EMAIL = USER_EMAIL;
    }

    public String getUSER_MOBILE() {
        return USER_MOBILE;
    }

    public void setUSER_MOBILE(String USER_MOBILE) {
        this.USER_MOBILE = USER_MOBILE;
    }
}
