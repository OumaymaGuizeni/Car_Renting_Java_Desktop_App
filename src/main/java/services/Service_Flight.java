package Services;

import Entite.Flight;
import Utilis.Data_Source;
import Utilis.LogManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Service_Flight implements Iservices<Flight> {
    private Connection con = Data_Source.getInstance().getConnection();
    private Statement stmt;


    public Service_Flight() {
        LogManager logger = LogManager.getInstance();
        try {
        
            if (con != null) {
                stmt = con.createStatement();
            }
        } catch (SQLException e) {
            logger.error("Error : "  + e);
            // System.out.println(e);
        }
    }

    public List<Flight> getAvailableFlight() throws SQLException {
        List<Flight> flights = new ArrayList<>();
        String query = "SELECT * FROM Flights";
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            Flight flight = new Flight(
                    rs.getString("FLIGHT_NUMBER"),
                    rs.getString("DEPARTURE"),
                    rs.getString("ARRIVAL"),
                    rs.getInt("SEATS_AVAILABLE"),
                    rs.getDouble("PRICE")
            );

            flights.add(flight);
        }
        return flights;
    }

    public void add(Flight flight) throws SQLException {
        String query = "INSERT INTO FLIGHTS(FLIGHT_NUMBER, departure, arrival, seats_available, price) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, flight.getFlightId());
            pstmt.setString(2,flight.getDeparture());
            pstmt.setString(3, flight.getArrival());
            pstmt.setInt(4, flight.getSeatsAvailable());
            pstmt.setDouble(5, flight.getPrice());
            pstmt.executeUpdate();
        }
    }

    public void delete(Flight flight) throws SQLException {
        String query = "DELETE FROM FLIGHTS WHERE FLIGHT_NUMBER = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, flight.getFlightId());
            pstmt.executeUpdate();
        }
    }
    public List<Flight> Search(String searchTerm) throws SQLException {
        List<Flight> search_result = new ArrayList<>();
        String reqpr = "SELECT * FROM FLIGHTS WHERE Departure LIKE UPPER(?) OR arrival LIKE UPPER(?)";
        try (PreparedStatement pstmt = con.prepareStatement(reqpr)) {
            String searchPattern = "%" + searchTerm + "%"; // Use wildcards for partial matches
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String flightNumber = rs.getString("FLIGHT_NUMBER");
                String departure = rs.getString("DEPARTURE");
                String arrival = rs.getString("ARRIVAL");
                int seatsAvailable = rs.getInt("seats_available");
                double price = rs.getDouble("PRICE");
                Flight flight = new Flight(flightNumber, departure, arrival, seatsAvailable, price);
                search_result.add(flight);
            }
        }
        return search_result;
    }



    public void update(Flight flight) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder("UPDATE FLIGHTS SET ");
        boolean hasPreviousField = false;

        if (flight.getSeatsAvailable() != null) {
            queryBuilder.append("SEATS_AVAILABLE = ?");
            hasPreviousField = true;
        }
        if (flight.getPrice() != null) {
            if (hasPreviousField) {
                queryBuilder.append(", ");
            }
            queryBuilder.append("PRICE = ?");
        }
        queryBuilder.append(" WHERE FLIGHT_NUMBER = ?");

        try (PreparedStatement pstmt = con.prepareStatement(queryBuilder.toString())) {
            int parameterIndex = 1;

            if (flight.getSeatsAvailable() != null) {
                pstmt.setInt(parameterIndex++, flight.getSeatsAvailable());
            }
            if (flight.getPrice() != null) {
                pstmt.setDouble(parameterIndex++, flight.getPrice());
            }
            pstmt.setString(parameterIndex, flight.getFlightId());

            pstmt.executeUpdate();
        }
    }


    @Override
    public List<Flight> getAll() throws SQLException {
        List<Flight> list = new ArrayList<>();
        ResultSet rs=null;
        rs = stmt.executeQuery("SELECT * FROM FLIGHTS");
        // Processing the results
        while (rs.next()) {
            String flightNumber = rs.getString("FLIGHT_NUMBER");
            String departure = rs.getString("DEPARTURE");
            String arrival = rs.getString("ARRIVAL");
            int seatsAvailable = rs.getInt("seats_available");
            double price = rs.getDouble("PRICE");
            Flight flight = new Flight(flightNumber, departure, arrival, seatsAvailable, price);
            list.add(flight);
        }
        return list;
    }
}







