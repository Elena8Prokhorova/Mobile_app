package com.example.lr1_1;

public class TaskProbaInfo {
    private String name;
    private String address;
    private String time;

    public TaskProbaInfo(String address, String name, String time) {
        //this.id = id;
        this.name = name;
        this.address = address;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCountArgs() {
        int count = 0;
        if (!(this.getName().isEmpty() || this.getName() == null)) {
            count = 1;
        }
        if (!(this.getAddress().isEmpty() || this.getAddress() == null) && count==1) {
            count = 2;
        }
        if (!(this.getTime().isEmpty() || this.getTime() == null) && count==2) {
            count = 3;
        }
        return count;
    }
}
