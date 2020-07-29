package com.example.abhivriddi20;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.util.List;
import java.util.Locale;

public class complaint_class {

    String cid, image, byuname, problem;
    String status;
    String updated_date,remarks;
    String regDate,dept;
    String longitude,latitude,address;

    public complaint_class(String cid, String image, String byuname, String problem, String status, String regDate, String updated_date,String remarks,String dept,String longitude,String latitude,String address) {
        this.cid = cid;
        this.image = image;
        this.byuname = byuname;
        this.problem = problem;
        this.status = status;
        this.updated_date = updated_date;
        this.regDate = regDate;
        this.remarks = remarks;
        this.dept=dept;
        this.longitude=longitude;
        this.latitude=latitude;
        this.address=address;


    }

    public String getName() {
        return cid;
    }

    public String getImage() {
        return image;
    }

    public String getByuname() {
        return byuname;
    }

    public String getproblem() {
        return problem;
    }

    public String getstatus() {
        return status;
    }

    public String getupdatedDate() {
        return updated_date;
    }

    public String getregDate() {
        return regDate;
    }
    public String getremarks() {
        return remarks;
    }
    public String getDept(){return dept;}
    public String getLat(){return latitude;}
    public String getLong(){return longitude;}
    public String getAddress() {
        return address;
    }
}