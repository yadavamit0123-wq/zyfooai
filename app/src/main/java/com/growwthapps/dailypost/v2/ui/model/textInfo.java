package com.growwthapps.dailypost.v2.ui.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.core.view.ViewCompat;

public class textInfo implements Parcelable {

    public static final Creator<textInfo> CREATOR = new Creator<textInfo>() {

        public textInfo createFromParcel(Parcel parcel) {
            return new textInfo(parcel);
        }

        public textInfo[] newArray(int i) {
            return new textInfo[i];
        }

    };
    private String name;
    private String font_family;
    private String text;
    private String text_id;
    private String txt_color;
    private String txt_height;
    private String txt_order;
    private String txt_rotation;
    private String txt_width;
    private String txt_x_pos;
    private String txt_y_pos;
    private String txt_weight;
    private String txt_justification;

    private int outLineColor = 0;
    private int outLineSize = 0;
    private int shadowColor = ViewCompat.MEASURED_STATE_MASK;
    private int shadowBlur = 0;
    private int shadowOpacity = 0;
    private int shadowTopBottom = 0;
    private int shadowLeftRight = 0;

    private int lineSpacing = 0;
    private int latterSpacing = 0;


    public textInfo() {
    }


    protected textInfo(Parcel parcel) {
        this.name = parcel.readString();
        this.txt_height = parcel.readString();
        this.txt_width = parcel.readString();
        this.text = parcel.readString();
        this.txt_x_pos = parcel.readString();
        this.font_family = parcel.readString();
        this.txt_y_pos = parcel.readString();
        this.txt_order = parcel.readString();
        this.text_id = parcel.readString();
        this.txt_rotation = parcel.readString();
        this.txt_color = parcel.readString();
        this.txt_weight = parcel.readString();
        this.txt_justification = parcel.readString();

        this.outLineColor = parcel.readInt();
        this.outLineSize = parcel.readInt();
        this.shadowColor = parcel.readInt();
        this.shadowBlur = parcel.readInt();
        this.shadowOpacity = parcel.readInt();
        this.shadowTopBottom = parcel.readInt();
        this.shadowLeftRight = parcel.readInt();
        this.lineSpacing = parcel.readInt();
        this.latterSpacing = parcel.readInt();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int describeContents() {
        return 0;
    }

    public String getTxt_height() {
        return this.txt_height;
    }

    public void setTxt_height(String str) {
        this.txt_height = str;
    }

    public String getTxt_width() {
        return this.txt_width;
    }

    public void setTxt_width(String str) {
        this.txt_width = str;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String str) {
        this.text = str;
    }

    public String getTxt_x_pos() {
        return this.txt_x_pos;
    }

    public void setTxt_x_pos(String str) {
        this.txt_x_pos = str;
    }

    public String getFont_family() {
        return this.font_family;
    }

    public void setFont_family(String str) {
        this.font_family = str;
    }

    public String getTxt_y_pos() {
        return this.txt_y_pos;
    }

    public void setTxt_y_pos(String str) {
        this.txt_y_pos = str;
    }

    public String getTxt_order() {
        return this.txt_order;
    }

    public void setTxt_order(String str) {
        this.txt_order = str;
    }

    public String getText_id() {
        return this.text_id;
    }

    public void setText_id(String str) {
        this.text_id = str;
    }

    public String getTxt_rotation() {
        return this.txt_rotation;
    }

    public void setTxt_rotation(String str) {
        this.txt_rotation = str;
    }

    public String getTxt_color() {
        return this.txt_color;
    }

    public void setTxt_color(String str) {
        this.txt_color = str;
    }

    public String getTxt_weight() {
        return txt_weight;
    }

    public void setTxt_weight(String txt_weight) {
        this.txt_weight = txt_weight;
    }

    public String getTxt_justification() {
        return txt_justification;
    }

    public void setTxt_justification(String txt_justification) {
        this.txt_justification = txt_justification;
    }

    public int getOutLineColor() {
        return outLineColor;
    }

    public void setOutLineColor(int outLineColor) {
        this.outLineColor = outLineColor;
    }

    public int getOutLineSize() {
        return outLineSize;
    }

    public void setOutLineSize(int outLineSize) {
        this.outLineSize = outLineSize;
    }

    public int getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
    }

    public int getShadowBlur() {
        return shadowBlur;
    }

    public void setShadowBlur(int shadowBlur) {
        this.shadowBlur = shadowBlur;
    }

    public int getShadowOpacity() {
        return shadowOpacity;
    }

    public void setShadowOpacity(int shadowOpacity) {
        this.shadowOpacity = shadowOpacity;
    }

    public int getShadowTopBottom() {
        return shadowTopBottom;
    }

    public void setShadowTopBottom(int shadowTopBottom) {
        this.shadowTopBottom = shadowTopBottom;
    }

    public int getShadowLeftRight() {
        return shadowLeftRight;
    }

    public void setShadowLeftRight(int shadowLeftRight) {
        this.shadowLeftRight = shadowLeftRight;
    }

    public String toString() {
        return "ClassPojo [txt_height = " + this.txt_height + ", txt_width = " + this.txt_width + ", text = " + this.text + ", txt_x_pos = " + this.txt_x_pos + ", font_family = " + this.font_family + ", txt_y_pos = " + this.txt_y_pos + ", txt_order = " + this.txt_order + ", text_id = " + this.text_id + ", txt_rotation = " + this.txt_rotation + ", txt_color = " + this.txt_color + "]";
    }

    public int getLineSpacing() {
        return lineSpacing;
    }

    public void setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public int getLatterSpacing() {
        return latterSpacing;
    }

    public void setLatterSpacing(int latterSpacing) {
        this.latterSpacing = latterSpacing;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.txt_height);
        parcel.writeString(this.txt_width);
        parcel.writeString(this.text);
        parcel.writeString(this.txt_x_pos);
        parcel.writeString(this.font_family);
        parcel.writeString(this.txt_y_pos);
        parcel.writeString(this.txt_order);
        parcel.writeString(this.text_id);
        parcel.writeString(this.txt_rotation);
        parcel.writeString(this.txt_color);
        parcel.writeString(this.txt_weight);
        parcel.writeString(this.txt_justification);
        parcel.writeInt(this.outLineColor);
        parcel.writeInt(this.outLineSize);
        parcel.writeInt(this.shadowColor);
        parcel.writeInt(this.shadowBlur);
        parcel.writeInt(this.shadowOpacity);
        parcel.writeInt(this.shadowTopBottom);
        parcel.writeInt(this.shadowLeftRight);
        parcel.writeInt(this.lineSpacing);
        parcel.writeInt(this.latterSpacing);
    }


}
