package com.example.lr1_1;

public class Services {
    private String nameOfService;
    private String price;

    public Services() {}

    public Services(String nameOfService, String price) {
        this.nameOfService = nameOfService;
        this.price = price;
    }

    public String serviceToString(Services service) {
        return service.getNameOfService()+":\t"+service.getPrice()+" руб.";
    }

    public String getNameOfService() {
        return nameOfService;
    }

    public void setNameOfService(String nameOfService) {
        this.nameOfService = nameOfService;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
