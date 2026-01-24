package com.novashen.riverside.entity;

import java.util.List;

/**
 * author: sca_tl
 * date: 2020/5/21 13:26
 * description:
 */
public class DayQuestionBean {
    public String formHash;
    public String description;
    public String checkPoint;
    public Integer questionNum;
    public String questionTitle;
    public List<Options> options;

    public static class Options{
        public String answerValue;//答案值，提交表单的answer
        public String dsp;
        public boolean answerChecked;
    }
}
