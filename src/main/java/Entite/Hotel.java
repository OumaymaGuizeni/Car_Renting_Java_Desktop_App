package Entite;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class Hotel {
    // Factory Pattern 
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty location = new SimpleStringProperty();
    private IntegerProperty roomsAvailable = new SimpleIntegerProperty();
    private IntegerProperty pricePerNight = new SimpleIntegerProperty();
    private IntegerProperty full_capacity = new SimpleIntegerProperty();
    private IntegerProperty reserved_by_our_agency = new SimpleIntegerProperty();
    private StringProperty imageUrl = new SimpleStringProperty(); // New field for image URL
    private IntegerProperty stars = new SimpleIntegerProperty();

    // Constructor
    public Hotel(int id, String name, String location, int roomsAvailable, double pricePerNight , int full_capacity , int reserved_by_our_agency , String imageUrl , int stars) {
        this.id.set(id);
        this.name.set(name);
        this.location.set(location);
        this.roomsAvailable.set(roomsAvailable);
        this.pricePerNight.set((int) pricePerNight);  // Assumes price is an integer, modify if needed
        this.full_capacity.set((int)full_capacity);
        this.reserved_by_our_agency.set((int)reserved_by_our_agency);
        this.imageUrl.set(imageUrl); // Set image URL in the constructor
        this.stars.set(stars);
    }

    public int getStars() {
        return stars.get();
    }

    public IntegerProperty starsProperty() {
        return stars;
    }

    public void setStars(int  stars) {
        this.stars.set(stars);
    }

    public String getImageUrl() {
        return imageUrl.get();
    }

    public StringProperty imageUrlProperty() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl.set(imageUrl);
    }
    
    // Getters and setters
    public int getFull_capacity(){
        return full_capacity.get();
    }

    public IntegerProperty full_capacityProperty(){
        return full_capacity;
    }

    public void setfull_capacity(int full_capacity){
        this.full_capacity.set(full_capacity);
    }

    public int getReserved_by_our_agency(){
        return reserved_by_our_agency.get();
    }

    public IntegerProperty reserved_by_our_agencyProperty(){
        return reserved_by_our_agency;
    }

    public void setreserved_by_our_agency(int reserved_by_our_agency){
        this.reserved_by_our_agency.set(reserved_by_our_agency);
    }


    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getLocation() {
        return location.get();
    }

    public StringProperty locationProperty() {
        return location;
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public int getRoomsAvailable() {
        return roomsAvailable.get();
    }

    public IntegerProperty roomsAvailableProperty() {
        return roomsAvailable;
    }

    public void setRoomsAvailable(int roomsAvailable) {
        this.roomsAvailable.set(roomsAvailable);
    }

    public int getPricePerNight() {
        return pricePerNight.get();
    }

    public IntegerProperty pricePerNightProperty() {
        return pricePerNight;
    }

    public void setPricePerNight(int pricePerNight) {
        this.pricePerNight.set(pricePerNight);
    }

    @Override
    public String toString() {
        return "Hotel{id=" + id + ", name='" + name + "'}";
    }

}
