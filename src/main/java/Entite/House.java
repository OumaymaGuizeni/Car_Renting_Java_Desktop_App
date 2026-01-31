package Entite;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class House {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty location = new SimpleStringProperty();
    private IntegerProperty roomsAvailable = new SimpleIntegerProperty();
    private IntegerProperty pricePerNight = new SimpleIntegerProperty();
    private StringProperty fullCapacity = new SimpleStringProperty();  // Correction ici
    private StringProperty reservedByOurAgency = new SimpleStringProperty();  // Correction ici

    // Constructeur
    public House(int id, String name, String location, int roomsAvailable, double pricePerNight, String fullCapacity, String reservedByOurAgency) {
        this.id.set(id);
        this.name.set(name);
        this.location.set(location);
        this.roomsAvailable.set(roomsAvailable);
        this.pricePerNight.set((int) pricePerNight);
        this.fullCapacity.set(fullCapacity);  // Correction ici
        this.reservedByOurAgency.set(reservedByOurAgency);  // Correction ici
    }
    private String full_capacity;
    private String reserved_by_our_agency;
    
    public String getFull_capacity() {
        return full_capacity;
    }

    public String getReserved_by_our_agency() {
        return reserved_by_our_agency;
    }
    // Getters et Setters
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getLocation() {
        return location.get();
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public int getRoomsAvailable() {
        return roomsAvailable.get();
    }

    public void setRoomsAvailable(int roomsAvailable) {
        this.roomsAvailable.set(roomsAvailable);
    }

    public int getPricePerNight() {
        return pricePerNight.get();
    }

    public void setPricePerNight(int pricePerNight) {
        this.pricePerNight.set(pricePerNight);
    }

    public String getFullCapacity() {  // Correction ici
        return fullCapacity.get();  // Correction ici
    }

    public void setFullCapacity(String fullCapacity) {  // Correction ici
        this.fullCapacity.set(fullCapacity);  // Correction ici
    }

    public String getReservedByOurAgency() {  // Correction ici
        return reservedByOurAgency.get();  // Correction ici
    }

    public void setReservedByOurAgency(String reservedByOurAgency) {  // Correction ici
        this.reservedByOurAgency.set(reservedByOurAgency);  // Correction ici
    }
}



