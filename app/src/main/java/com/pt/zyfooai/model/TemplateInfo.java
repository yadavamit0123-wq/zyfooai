package com.pt.zyfooai.model;


import com.pt.zyfooai.View.text.AutofitTextInfo;
import com.pt.zyfooai.ui.model.ElementInfo;

import java.util.ArrayList;

public class TemplateInfo {

    ArrayList<ElementInfo> elementInfoArrayList;

    ArrayList<AutofitTextInfo> autofitTextInfoArrayList;

    private String FRAME_NAME;

    private String BACKGROUND_PATH;

    private String OVERLAY_NAME = "";

    private String PROFILE_TYPE;

    private String RATIO;

    private int TEMPLATE_ID;

    public TemplateInfo() {
    }

    public String getBACKGROUND_PATH() {
        return BACKGROUND_PATH;
    }

    public void setBACKGROUND_PATH(String BACKGROUND_PATH) {
        this.BACKGROUND_PATH = BACKGROUND_PATH;
    }

    public int getTEMPLATE_ID() {
        return this.TEMPLATE_ID;
    }

    public void setTEMPLATE_ID(int i) {
        this.TEMPLATE_ID = i;
    }

    public String getFRAME_NAME() {
        return this.FRAME_NAME;
    }

    public void setFRAME_NAME(String str) {
        this.FRAME_NAME = str;
    }

    public String getRATIO() {
        return this.RATIO;
    }

    public void setRATIO(String str) {
        this.RATIO = str;
    }

    public String getPROFILE_TYPE() {
        return this.PROFILE_TYPE;
    }

    public void setPROFILE_TYPE(String str) {
        this.PROFILE_TYPE = str;
    }


    public String getOVERLAY_NAME() {
        return this.OVERLAY_NAME;
    }

    public void setOVERLAY_NAME(String str) {
        this.OVERLAY_NAME = str;
    }

    public ArrayList<AutofitTextInfo> getTextInfoArrayList() {
        return this.autofitTextInfoArrayList;
    }

    public void setTextInfoArrayList(ArrayList<AutofitTextInfo> arrayList) {
        this.autofitTextInfoArrayList = arrayList;
    }

    public ArrayList<ElementInfo> getElementInfoArrayList() {
        return this.elementInfoArrayList;
    }

    public void setElementInfoArrayList(ArrayList<ElementInfo> arrayList) {
        this.elementInfoArrayList = arrayList;
    }
}
