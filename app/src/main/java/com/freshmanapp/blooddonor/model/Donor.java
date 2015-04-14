package com.freshmanapp.blooddonor.model;

/**
 * Created by Ramkumar on 06/04/15.
 */
public class Donor {
    private String header, thumbnailUrl;
    private String subline1;
    private String subline2;
    private String caption;
    private String mobile;
    private String geotag;

    public Donor() {
    }

    public Donor(String header, String thumbnailUrl, String subline1,String subline2, String caption,String mobile,String geotag) {
        this.header = header;
        this.thumbnailUrl = thumbnailUrl;
        this.subline1 = subline1;
        this.subline2 = subline2;
        this.caption = caption;
        this.mobile = mobile;
        this.geotag = geotag;

    }

    public String getHeader() {
        return header;
    }

    public void setName(String header) {
        this.header = header;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getSubline1() {
        return subline1;
    }

    public void setSubline1(String subline1) {
        this.subline1 = subline1;
    }

    public void setSubline2(String subline2) {
        this.subline2 = subline2;
    }

    public String getSubline2() {
        return subline2;
    }

    public String getCaption() {
        return caption;
    }
    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getMobile() {return mobile; }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getGeotag() {
        return geotag;
    }
    public void setGeotag(String geotag) {
        this.geotag = geotag;
    }

}
