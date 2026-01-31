package Services;

import Entite.Hotel;
import Utilis.Data_Source;
import Utilis.LogManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class Service_Hotels implements Iservices<Hotel> {
    private Connection con = Data_Source.getInstance().getConnection();
    private Statement stmt;

    public Service_Hotels() {
        LogManager logger = LogManager.getInstance();
        try {
            if (con != null) {
                stmt = con.createStatement();
            }
        } catch (SQLException e) {
            logger.error("Error :" + e);
            //System.out.println(e);
        }
    }

    public List<Hotel> getAll() throws SQLException {
        List<Hotel> hotels = new ArrayList<>();
        String query = "SELECT * FROM HOTELS WHERE ROOMS_AVAILABLE > 0";
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            int id = rs.getInt("ID");
            String name = rs.getString("NAME");
            String location = rs.getString("LOCATION");
            int roomsAvailable = rs.getInt("ROOMS_AVAILABLE");
            double pricePerNight = rs.getDouble("PRICE_PER_NIGHT");
            int full_capacity = rs .getInt("FULL_CAPACITY");
            int reserved_by_our_agency = rs.getInt("RESERVED_BY_OUR_AGENCY");
            String imageUrl = rs.getString("IMAGE_URL");
            int stars = rs.getInt("STARS");
            hotels.add(new Hotel(id, name, location, roomsAvailable, pricePerNight, full_capacity , reserved_by_our_agency , imageUrl,stars));
        }

        return hotels;
    }



    public void add(Hotel hotel) throws SQLException {
        String query = "INSERT INTO HOTELS (ID, NAME, LOCATION, ROOMS_AVAILABLE, PRICE_PER_NIGHT , FULL_CAPACITY ,RESERVED_BY_OUR_AGENCY , IMAGE_URL , STARS ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        LogManager logger = LogManager.getInstance();
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, hotel.getId());
            pstmt.setString(2, hotel.getName());
            pstmt.setString(3, hotel.getLocation());
            pstmt.setInt(4, hotel.getRoomsAvailable());
            pstmt.setDouble(5, hotel.getPricePerNight());
            pstmt.setInt(6, hotel.getFull_capacity());
            pstmt.setInt(7, hotel.getReserved_by_our_agency());
            pstmt.setString(8, hotel.getImageUrl());
            pstmt.setInt(9, hotel.getStars());
            pstmt.executeUpdate();
        }
        catch (Exception e){
            logger.error("Error :" + e);
            // System.out.println(e);
        }
    }

    public void delete(Hotel hotel) throws SQLException {
        if (hotel == null || hotel.getId() == 0) {
            throw new IllegalArgumentException("Hotel object is invalid or missing ID.");
        }
        String query = "DELETE FROM hotels WHERE id = ?";
        LogManager logger = LogManager.getInstance();
        logger.info("Executing delete query: " + query + " with ID: " + hotel.getId());
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, hotel.getId());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No rows affected. Hotel ID might not exist: " + hotel.getId());
            }
            logger.info("Successfully deleted hotel with ID: " + hotel.getId());
        } catch (SQLException e) {
            logger.error("SQL error during deletion: " + e.getMessage());
            throw e; // Rethrow to ensure it's captured by the caller
        }
    }
    

    public void update(Hotel hotel) throws SQLException {
        String query = "UPDATE HOTELS SET NAME = ?, LOCATION = ?, ROOMS_AVAILABLE = ?, PRICE_PER_NIGHT = ?, FULL_CAPACITY = ?, RESERVED_BY_OUR_AGENCY = ? WHERE ID = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, hotel.getName());         // Update the NAME field
            pstmt.setString(2, hotel.getLocation());     // Update the LOCATION field
            pstmt.setInt(3, hotel.getRoomsAvailable());  // Update the ROOMS_AVAILABLE field
            pstmt.setDouble(4, hotel.getPricePerNight()); // Update the PRICE_PER_NIGHT field
            pstmt.setInt(5,hotel.getFull_capacity());
            pstmt.setInt(6,hotel.getReserved_by_our_agency());
            pstmt.setInt(7, hotel.getId());              // Set the WHERE clause with the hotel ID
    
            int rowsUpdated = pstmt.executeUpdate();      // Execute the update query
            if (rowsUpdated == 0) {
                throw new SQLException("No hotel found with the given ID.");
            }
        } catch (SQLException e) {
            throw new SQLException("Error updating hotel data: " + e.getMessage(), e);
        }
    }
    
    
    public boolean search(Hotel hotel) throws SQLException {
        String query = "SELECT * FROM HOTELS WHERE ID = ?";
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            String name = rs.getString("NAME");
            String location = rs.getString("LOCATION");
            int getroomsavailable = rs.getInt("ROOMS_AVAILABLE");
            return true;

        }
        return false;
    }
}
