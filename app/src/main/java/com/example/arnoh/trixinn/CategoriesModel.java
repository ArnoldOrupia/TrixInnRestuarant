package com.example.arnoh.trixinn;

/**
 * Created by ARNOH on 3/30/2018.
 */

public class CategoriesModel {

    private String title, image, key;

    public CategoriesModel() {
    }

    public CategoriesModel(String title, String image, String key) {
        this.title = title;
        this.image = image;
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
