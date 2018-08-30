package com.cpm.phillipspc.gsonGetterSetter;

/**
 * Created by neerajg on 2/19/2018.
 */

public class FeedbackQuestionData {
    private String Question_CD,Answer_CD,Customer_Id,Store_Id;

    public String getStore_Id() {
        return Store_Id;
    }

    public void setStore_Id(String store_Id) {
        Store_Id = store_Id;
    }

    public String getCustomer_Id() {
        return Customer_Id;
    }

    public void setCustomer_Id(String customer_Id) {
        Customer_Id = customer_Id;
    }

    public String getQuestion_CD() {
        return Question_CD;
    }

    public void setQuestion_CD(String question_CD) {
        Question_CD = question_CD;
    }

    public String getAnswer_CD() {
        return Answer_CD;
    }

    public void setAnswer_CD(String answer_CD) {
        Answer_CD = answer_CD;
    }
}
