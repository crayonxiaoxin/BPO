package com.ormediagroup.bpo.bean;

import java.io.PipedReader;

/**
 * Created by Lau on 2018/9/25.
 */

public class CompanyFilesBean {
    private int id;
    private String name;
    private String url;
    private String fileType;
    private String date;

    public CompanyFilesBean(int id, String name, String url, String fileType, String date) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.fileType = fileType;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
