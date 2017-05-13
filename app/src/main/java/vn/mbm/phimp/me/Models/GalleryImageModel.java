package vn.mbm.phimp.me.Models;

/**
 * Created by vinay on 13/5/17.
 */

public class GalleryImageModel {

    private String path,name;
    private boolean isSelected;

    public GalleryImageModel(){

    }

    public GalleryImageModel(String path, String name, boolean isSelected){
        this.path = path;
        this.name = name;
        this.isSelected = isSelected;
    }

    public String getPath(){
        return path;
    }

    public void setPath(String path){
        this.path = path;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public boolean getSelected(){
        return isSelected;
    }

    public void setSelected(boolean isSelected){
        this.isSelected = isSelected;
    }

}
