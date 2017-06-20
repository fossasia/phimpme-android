package vn.mbm.phimp.me.utils;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by dynamitechetan on 14/03/2017.
 */
public class Image implements Parcelable{

    public long _id;
    public Uri uri;
    public String imagePath;
    public boolean isPortraitImage;

    public Image(long _id, Uri uri, String imagePath, boolean isPortraitImage) {
        this._id = _id;
        this.uri = uri;
        this.imagePath = imagePath;
        this.isPortraitImage = isPortraitImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this._id);
        dest.writeParcelable(this.uri, flags);
        dest.writeString(this.imagePath);
        dest.writeByte(this.isPortraitImage ? (byte) 1 : (byte) 0);
    }

    protected Image(Parcel in) {
        this._id = in.readLong();
        this.uri = in.readParcelable(Uri.class.getClassLoader());
        this.imagePath = in.readString();
        this.isPortraitImage = in.readByte() != 0;
    }

    public String getImageHash() throws IOException, NoSuchAlgorithmException {
        File imageFile = new File(imagePath);
        return FileUtils.getHash(imageFile);
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
