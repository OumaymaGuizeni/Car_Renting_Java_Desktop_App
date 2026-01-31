package Entite;

import java.time.LocalDate;

public class Reservation {
    private int id; // Maps to ID column
    private String userFullName; // Combines userName and userLastName, maps to USER_FULL_NAME
    private String userEmail; // Maps to USER_EMAIL
    private LocalDate dateDebut; // Maps to DATEDEBUT
    private LocalDate dateFin; // Maps to DATEFIN
    private String type; // Maps to TYPE
    private String typeId; // Maps to TYPE_ID (e.g., specific ID for cars, flights, etc.)
    private double reservationPrice; // Maps to RESERVATION_PRICE
    private int nbrOfSeatsOrRooms;

    public Reservation() {}

    public Reservation(String userFullName, String userEmail, LocalDate dateDebut, LocalDate dateFin, String type, String typeId, double reservationPrice, int nbrOfSeatsOrRooms) {
        this.userFullName = userFullName;
        this.userEmail = userEmail;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.type = type;
        this.typeId = typeId;
        this.reservationPrice = reservationPrice;
        this.nbrOfSeatsOrRooms = nbrOfSeatsOrRooms;
    }

    public Reservation(int id, String userFullName, String userEmail, LocalDate dateDebut, LocalDate dateFin, String type, String typeId, double reservationPrice,int nbrOfSeatsOrRooms) {
        this.id = id;
        this.userFullName = userFullName;
        this.userEmail = userEmail;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.type = type;
        this.typeId = typeId;
        this.reservationPrice = reservationPrice;
        this.nbrOfSeatsOrRooms=nbrOfSeatsOrRooms;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", userFullName='" + userFullName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", type='" + type + '\'' +
                ", typeId='" + typeId + '\'' +
                ", reservationPrice=" + reservationPrice +
                "' nbrOfSeatsOrRooms=" + nbrOfSeatsOrRooms +
                '}';
    }

    public int getNbrOfSeatsOrRooms() {
        return nbrOfSeatsOrRooms;
    }

    public void setNbrOfSeatsOrRooms(int nbrOfSeatsOrRooms) {
        this.nbrOfSeatsOrRooms = nbrOfSeatsOrRooms;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public double getReservationPrice() {
        return reservationPrice;
    }

    public void setReservationPrice(double reservationPrice) {
        this.reservationPrice = reservationPrice;
    }
}
