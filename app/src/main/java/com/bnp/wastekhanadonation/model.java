package com.bnp.wastekhanadonation;

public class model{
    String name,type,description,userid,food_item,food_image;

    public model() {

    }

    public model(String name, String type, String description, String userid, String food_item, String food_image) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.userid = userid;
        this.food_item = food_item;
        this.food_image = food_image;
    }

    public String getFood_item() {
        return food_item;
    }

    public void setFood_item(String food_item) {
        this.food_item = food_item;
    }

    public String getFood_image() {
        return food_image;
    }

    public void setFood_image(String food_image) {
        this.food_image = food_image;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}