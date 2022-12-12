package be.hicham.v2_nhi_shop.models;

import android.graphics.Bitmap;

import java.util.Date;

public class ChatMessage {
    public String senderId;
    public String receiverId;
    public String message;
    public String dateTime;
    public String articleTitle;



    public String articleId;
    public String conversionId;
    public String conversionName;
    public String conversionImage;
    public Date dateObject;

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setDateObject(Date dateObject) {
        this.dateObject = dateObject;
    }

    public String getImage() {
        return conversionImage;
    }

    public String getConversionName() {
        return conversionName;
    }

    public String getConversionMessage() {
        return message;
    }

    public String getConversionId() {
        return conversionId;
    }

    public void setArticleTitle(String articleTitle) { this.articleTitle = articleTitle; }

    public String getArticleTitle() { return articleTitle; }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public void setImage(String conversionImage) { this.conversionImage = conversionImage;}

    public void setConversionName(String conversionName) { this.conversionName = conversionName;}

    public void setConversionId(String conversionId) {this.conversionId = conversionId;}
}
