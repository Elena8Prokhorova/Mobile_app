package com.example.lr1_1;

public class TaskInfo {
    private String name;
    private String address;
    private String time;
    private String telephone;
    private String email;
    private String description;
    private String status;

    public TaskInfo() {}

    public TaskInfo(String name, String address, String time, String telephone, String email,
                    String description, String status) {
        this.name = name;
        this.address = address;
        this.time = time;
        this.telephone = telephone;
        this.email = email;
        this.description = description;
        this.status = status;
    }

    public String taskToString(TaskInfo taskInfo) {
        return "name: "+taskInfo.getName()+", address: "+taskInfo.getAddress()+", time: "+taskInfo.getTime()+".";
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
