package com.ormediagroup.bpo.bean;

/**
 * Created by Lau on 2018/9/6.
 */

public class CompanyBean {

    private int id;
    private String name;
    private String date;
    private String status;
    private int files;

    public CompanyBean(int id, String name, String date, String status, int files) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.status = status;
        this.files = files;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFiles() {
        return files;
    }

    public void setFiles(int files) {
        this.files = files;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
