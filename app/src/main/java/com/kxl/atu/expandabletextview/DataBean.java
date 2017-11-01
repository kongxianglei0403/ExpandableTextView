package com.kxl.atu.expandabletextview;

/**
 * Description:
 * Author:ATU
 * Date:2017/10/31  16:58
 */
public class DataBean {
    private String text;
    private boolean isCollpased = true;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCollpased() {
        return isCollpased;
    }

    public void setCollpased(boolean collpased) {
        isCollpased = collpased;
    }
}
