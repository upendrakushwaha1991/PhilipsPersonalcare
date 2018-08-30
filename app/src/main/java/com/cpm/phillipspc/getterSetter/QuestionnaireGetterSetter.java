package com.cpm.phillipspc.getterSetter;

import java.util.ArrayList;

/**
 * Created by deepakp on 15-01-2018.
 */

public class QuestionnaireGetterSetter {

    public String getTable_QUESTIONNAIRE() {
        return table_QUESTIONNAIRE;
    }

    public void setTable_QUESTIONNAIRE(String table_QUESTIONNAIRE) {
        this.table_QUESTIONNAIRE = table_QUESTIONNAIRE;
    }

    public ArrayList<String> getQUESTION_ID() {
        return QUESTION_ID;
    }

    public void setQUESTION_ID(String QUESTION_ID) {
        this.QUESTION_ID.add(QUESTION_ID);
    }

    public ArrayList<String> getQUESTION() {
        return QUESTION;
    }

    public void setQUESTION(String QUESTION) {
        this.QUESTION.add(QUESTION);
    }

    public ArrayList<String> getANSWER_ID() {
        return ANSWER_ID;
    }

    public void setANSWER_ID(String ANSWER_ID) {
        this.ANSWER_ID.add(ANSWER_ID);
    }

    public ArrayList<String> getANSWER() {
        return ANSWER;
    }

    public void setANSWER(String ANSWER) {
        this.ANSWER.add(ANSWER);
    }

    public ArrayList<String> getQUESTION_GROUP_ID() {
        return QUESTION_GROUP_ID;
    }

    public void setQUESTION_GROUP_ID(String QUESTION_GROUP_ID) {
        this.QUESTION_GROUP_ID.add(QUESTION_GROUP_ID);
    }

    public ArrayList<String> getQUESTION_GROUP() {
        return QUESTION_GROUP;
    }

    public void setQUESTION_GROUP(String QUESTION_GROUP) {
        this.QUESTION_GROUP.add(QUESTION_GROUP);
    }

    public ArrayList<String> getQUESTION_TYPE() {
        return QUESTION_TYPE;
    }

    public void setQUESTION_TYPE(String QUESTION_TYPE) {
        this.QUESTION_TYPE.add(QUESTION_TYPE);
    }

    public String getSp_AnswerId() {
        return Sp_AnswerId;
    }

    public void setSp_AnswerId(String sp_AnswerId) {
        Sp_AnswerId = sp_AnswerId;
    }

    public String getSp_Answer() {
        return Sp_Answer;
    }

    public void setSp_Answer(String sp_Answer) {
        Sp_Answer = sp_Answer;
    }

    String Sp_Answer;
    String Sp_AnswerId;
    String table_QUESTIONNAIRE;
    ArrayList<String> QUESTION_ID = new ArrayList<>();
    ArrayList<String> QUESTION = new ArrayList<>();
    ArrayList<String> ANSWER_ID = new ArrayList<>();
    ArrayList<String> ANSWER = new ArrayList<>();
    ArrayList<String> QUESTION_GROUP_ID = new ArrayList<>();
    ArrayList<String> QUESTION_GROUP = new ArrayList<>();
    ArrayList<String> QUESTION_TYPE = new ArrayList<>();
}
