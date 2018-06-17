package com.horizonshd.www.barrageclassstudent;

import java.io.Serializable;

public class Question implements Serializable {
    private String questionid;
    private String description;
    private String optiona;
    private String optionb;
    private String optionc;
    private String optiond;
    private String answer;

    public Question(String questionid,String description,String optiona,String optionb,String optionc,String optiond,String answer){
        this.questionid = questionid;
        this.description = description;
        this.optiona = optiona;
        this.optionb = optionb;
        this.optionc = optionc;
        this.optiond = optiond;
        this.answer = answer;
    }

    public String getQuestionid() {
        return questionid;
    }

    public String getDescription() {
        return description;
    }

    public String getOptiona() {
        return optiona;
    }

    public String getOptionb() {
        return optionb;
    }

    public String getOptionc() {
        return optionc;
    }

    public String getOptiond() {
        return optiond;
    }

    public String getAnswer() {
        return answer;
    }
}