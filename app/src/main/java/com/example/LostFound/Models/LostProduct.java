package com.example.LostFound.Models;

public class LostProduct {
    private String imageKey;
    private String imageUrl;
    private String ProductName;
    private String description;
    private String color;
    private String usability;
    private String finder;
    private String productOwner;
    private String status;

    public LostProduct() {
        // Default constructor required for Firebase database
    }
    

    public LostProduct(String imageKey, String imageUrl, String ProductName, String description,
                     String color,String usability,String finder,String productOwner,String status) {
        this.imageKey = imageKey;
        this.imageUrl = imageUrl;
        this.ProductName = ProductName;
        this.description=description;
        this.color=color;
        this.usability=usability;
        this.finder=finder;
        this.productOwner=productOwner;
        this.status=status;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String ProductName) {
        this.ProductName = ProductName;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getFinder() {
        return finder;
    }

    public void setFinder(String finder) {
        this.finder=finder;
    }
    public String getProductOwner() {
        return productOwner;
    }

    public void setProductOwner(String productOwner) {
        this.productOwner=productOwner;
    }
    public String getUsability() {
        return usability;
    }

    public void setUsability(String usability) {
        this.usability=usability;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
