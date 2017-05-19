package com.xinlan.imageeditlibrary.editimage.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by panyi on 2015/8/11.
 */
public class StickerBean {
    private String coverPath;//封面路径
    private List<String> pathList;

    public StickerBean(){
        pathList = new ArrayList<String>();
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public List<String> getPathList() {
        return pathList;
    }

    public void setPathList(List<String> pathList) {
        this.pathList = pathList;
    }
}//end class
