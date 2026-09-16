package com.pt.zyfooai.model;

import java.util.List;

/**
 * API wrapper for published frame catalog responses.
 */
public class FrameListResponse {

    public boolean success;
    public List<FrameConfig> data;
    public int version;
    public String lastUpdated;
}
