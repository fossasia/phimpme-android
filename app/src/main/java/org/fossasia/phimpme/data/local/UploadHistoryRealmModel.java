package org.fossasia.phimpme.data.local;

import android.os.Parcel;
import android.os.Parcelable;
import io.realm.RealmObject;

/**
 * Created by pa1pal on 16/08/17.
 */

public class UploadHistoryRealmModel extends RealmObject implements Parcelable{

    String name;
    String pathname;
    String datetime;
    String status;

    public UploadHistoryRealmModel(){}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathname() {
        return pathname;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(pathname);
        parcel.writeString(datetime);
        parcel.writeString(status);
    }

    public static final Parcelable.Creator<UploadHistoryRealmModel> creator = new Creator<UploadHistoryRealmModel>() {
        @Override public UploadHistoryRealmModel createFromParcel(Parcel parcel) {
            return new UploadHistoryRealmModel(parcel);
        }

        @Override public UploadHistoryRealmModel[] newArray(int i) {
            return new UploadHistoryRealmModel[i];
        }
    };

    private UploadHistoryRealmModel(Parcel in){
        name = in.readString();
        pathname = in.readString();
        datetime = in.readString();
        status = in.readString();
    }
}
