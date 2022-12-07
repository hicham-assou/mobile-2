package be.hicham.v2_nhi_shop.models;

import java.io.Serializable;

public class User implements Serializable {
    private String email, username, password, token, image, id;

    public User() {}

    public User(String username, String password){}

    public User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    // SETTERS
    public void setUsername(String username) { this.username = username;}
    public void setId(String id) {
        this.id = id;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    // GETTERS
    public String getEmail() {
        return email;
    }
    public String getUsername() {
        return username;
    }
    public String getImage() { return image; }
    public String getId() { return id; }
    public String getPassword() { return password; }
}
