package com.mzs.guaji.entity;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

/**
 * Created by wlanjie on 14-1-17.
 */
public class Banners {
    @Expose
    private String img;
    @Expose
    private String title;
    @Expose
    private String type;
    @Expose
    private JsonElement target;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonElement getTarget() {
        return target;
    }

    public void setTarget(JsonElement target) {
        this.target = target;
    }
}
