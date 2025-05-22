package com.example.realestate;

public class Reservation {
    private Property property;
    private String reservationDate;

    public Reservation(Property property, String reservationDate) {
        this.property = property;
        this.reservationDate = reservationDate;
    }

    public Property getProperty() {
        return property;
    }

    public String getReservationDate() {
        return reservationDate;
    }
}