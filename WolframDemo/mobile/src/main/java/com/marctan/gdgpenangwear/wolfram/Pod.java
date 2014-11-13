package com.marctan.gdgpenangwear.wolfram;

import android.os.Parcel;
import android.os.Parcelable;

public class Pod implements Parcelable {
    private String id;
    private String title;
    private String data;
    private String image;
    private String imageData;

    public Pod() {

    }

    public Pod(String id, String title, String data, String image) {
        this.id = id;
        this.title = title;
        this.data = data;
        this.image = image;
    }

    public static final Parcelable.Creator<Pod> CREATOR
            = new Parcelable.Creator<Pod>() {

        @Override
        public Pod createFromParcel(Parcel source) {
            Pod pod = new Pod();
            pod.setId(source.readString());
            pod.setTitle(source.readString());
            pod.setData(source.readString());
            pod.setImageData(source.readString());

            return pod;
        }

        @Override
        public Pod[] newArray(int size) {
            return new Pod[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(data);
        parcel.writeString(imageData);

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }
}
