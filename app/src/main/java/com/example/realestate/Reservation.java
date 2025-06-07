/**
 * Description:
 * The Reservation class is a data model that represents
 * a user’s reservation of a property, combining the reserved Property object with the date of reservation.
 */

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