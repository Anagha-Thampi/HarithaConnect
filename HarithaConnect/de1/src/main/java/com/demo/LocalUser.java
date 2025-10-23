package com.demo;

import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class LocalUser extends User {
    private String name, mobile, ward, address;

    public LocalUser(String username, String name, String mobile, String ward, String address) {
        super(username, null); //
        this.name = name;
        this.mobile = mobile;
        this.ward = ward;
        this.address = address;
    }

    public LocalUser(String username, String password) {
        super(username, password);
    }
    public String getName() { return name; }
    public String getMobile() { return mobile; }
    public String getWard() { return ward; }
    public String getAddress() { return address; }


    @Override
    public String getDataFileName() {
        return "localuserdata.csv";
    }
    public String getUsername() {
        return username;
    }



}
