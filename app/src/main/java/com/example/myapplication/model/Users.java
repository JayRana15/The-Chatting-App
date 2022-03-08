package com.example.myapplication.model;

public class Users {

    private String id;
    private String userName;
    private String imageURl;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageURl() {
        return imageURl;
    }

    public void setImageURl(String imageURl) {
        this.imageURl = imageURl;
    }



    public Users() {

    }

    public Users(String id, String userName, String imageURl,String status) {
        this.id=id;
        this.userName=userName;
        this.imageURl=imageURl;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}

