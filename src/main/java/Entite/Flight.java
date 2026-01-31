package Entite;

public class Flight {
    private String flightId;
    private String departure;
    private String arrival;
    private Integer seats_available;
    private Double price;

    public Flight(String flightNumber, String departure, String arrival, int seats_available, double price) {
        this.flightId = flightNumber;
        this.departure = departure;
        this.arrival = arrival;
        this.seats_available = seats_available;
        this.price = price;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public Integer getSeatsAvailable() {
        return seats_available;
    }

    public void setSeats_available(Integer seats_available) {
        this.seats_available = seats_available;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "departure=' " + departure +
                " --> '" + arrival +
                " seats_available : " + seats_available +
                " seat price : " + price+
                " DNT";
    }
}

