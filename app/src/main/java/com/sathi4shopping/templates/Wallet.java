package com.sathi4shopping.templates;

public class Wallet {
    private String name;
    private String email;
    private String image;
    private String rewards;
    private String phone;

    public Wallet() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRewards() {
        return rewards;
    }

    public void setRewards(String rewards) {
        this.rewards = rewards;
    }

    public Wallet(String name, String email, String image, String rewards,String phone) {
        this.name = name;
        this.email = email;
        this.image = image;
        this.rewards = rewards;
        this.phone=phone;
    }
}
