package be.hicham.v2_nhi_shop.models;

import java.io.Serializable;
import java.util.Date;

public class Article  implements Serializable {
    private String title;
    private String description;
    private String image;
    private String sellerUsername;
    private String localisation;
    private String id;
    private String dateTime;
    public Date dateObject;
    private double price;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }


    public String getSellerUsername() {
        return sellerUsername;
    }

    public double getPrice() {
        return price;
    }

    public String getLocalisation() {
        return localisation;
    }

    public String getDatePosted() {
        return dateTime;
    }

    public Article(String title, String description, String image, double price, String dateTime) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.price = price;
        this.dateTime = dateTime;
    }
    public Article(){}


    public void setPrice(Double price) {
        this.price = price;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setSeller(String sellerUsername) {
        this.sellerUsername = sellerUsername;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setDateObject(Date dateObject) {
        this.dateObject = dateObject;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }
}