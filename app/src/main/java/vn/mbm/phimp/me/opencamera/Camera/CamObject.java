package vn.mbm.phimp.me.opencamera.Camera;

/**
 * Created by manuja on 30/5/17.
 */

public class CamObject {

    public String getString() {
        return string;
    }

    public Boolean getaBoolean() {
        return aBoolean;
    }

    public String[] getStringArray() {
        return stringArray;
    }

    public int[] getIntArray() {
        return intArray;
    }

    String string;

    public void setString(String string) {
        this.string = string;
    }

    public void setaBoolean(Boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    public void setStringArray(String[] stringArray) {
        this.stringArray = stringArray;
    }

    public void setIntArray(int[] intArray) {
        this.intArray = intArray;
    }

    Boolean aBoolean;
    String[] stringArray;
    int [] intArray;

}
