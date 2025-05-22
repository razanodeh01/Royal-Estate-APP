package com.example.realestate;

public class Property {
    private int id;
    private String title;
    private String type;
    private double price;
    private String location;
    private String area;
    private int bedrooms;
    private int bathrooms;
    private String imageUrl;
    private String description;

    public Property(int id, String title, String type, double price, String location, String area, int bedrooms, int bathrooms, String imageUrl, String description) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.price = price;
        this.location = location;
        this.area = area;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public String getLocation() { return location; }
    public String getArea() { return area; }
    public int getBedrooms() { return bedrooms; }
    public int getBathrooms() { return bathrooms; }
    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }
}