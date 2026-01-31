package Services;

import Entite.House;
import Utilis.Data_Source;
import Utilis.LogManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class Service_Houses implements Iservices<House> {
    private Connection con = Data_Source.getInstance().getConnection();
    private Statement stmt;

    public Service_Houses() {
        LogManager logger = LogManager.getInstance();
        try {
            if (con != null) {
                stmt = con.createStatement();
            }
        } catch (SQLException e) {
            logger.error("Error : " + e);
            // System.out.println(e);
        }
    }

    public List<House> getAll() throws SQLException {
        List<House> Houses = new ArrayList<>();
        String query = "SELECT * FROM Houses WHERE ROOMS_AVAILABLE > 0";
        ResultSet rs = stmt.executeQuery(query);
    
        while (rs.next()) {
            int id = rs.getInt("ID");
            String name = rs.getString("NAME");
            String location = rs.getString("LOCATION");
            int roomsAvailable = rs.getInt("ROOMS_AVAILABLE");
            double pricePerNight = rs.getDouble("PRICE_PER_NIGHT");
            String full_capacity = rs .getString("FULL_CAPACITY");
            String reserved_by_our_agency = rs.getString("RESERVED_BY_OUR_AGENCY");
            Houses.add(new House(id, name, location, roomsAvailable, pricePerNight, full_capacity , reserved_by_our_agency));
        }
        return Houses;
    }


    public void add(House House) throws SQLException {
        String query = "INSERT INTO HOUSES (ID, NAME, LOCATION, ROOMS_AVAILABLE, PRICE_PER_NIGHT , FULL_CAPACITY ,RESERVED_BY_OUR_AGENCY ) VALUES (?, ?, ?, ?, ?, ?, ?)";
        LogManager logger = LogManager.getInstance();
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, House.getId());
            pstmt.setString(2, House.getName());
            pstmt.setString(3, House.getLocation());
            pstmt.setInt(4, House.getRoomsAvailable());
            pstmt.setDouble(5, House.getPricePerNight());
            pstmt.setString(6, House.getFullCapacity());
            pstmt.setString(7, House.getReservedByOurAgency());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error during insertion " + e.getMessage());
            // System.out.println("Error during insertion: " + e.getMessage());
            e.printStackTrace();
        }
    }
    

    public void delete(House House) throws SQLException {
        String query = "DELETE FROM HOUSES WHERE ID = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, House.getId());
            pstmt.executeUpdate();
        }
    }

    public void update(House House) throws SQLException {
        String query = "UPDATE HOUSES SET NAME = ?, LOCATION = ?, ROOMS_AVAILABLE = ?, PRICE_PER_NIGHT = ?, FULL_CAPACITY = ?, RESERVED_BY_OUR_AGENCY = ? WHERE ID = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, House.getName());             // Update NAME
            pstmt.setString(2, House.getLocation());         // Update LOCATION
            pstmt.setInt(3, House.getRoomsAvailable());      // Update ROOMS_AVAILABLE
            pstmt.setDouble(4, House.getPricePerNight());    // Update PRICE_PER_NIGHT
            pstmt.setString(5, House.getFullCapacity());     // Update FULL_CAPACITY
            pstmt.setString(6, House.getReservedByOurAgency());  // Update RESERVED_BY_OUR_AGENCY
            pstmt.setInt(7, House.getId());                  // Set WHERE clause with House ID
    
            int rowsUpdated = pstmt.executeUpdate();         // Execute update
            if (rowsUpdated == 0) {
                throw new SQLException("No House found with the given ID.");
            }
        } catch (SQLException e) {
            throw new SQLException("Error updating House data: " + e.getMessage(), e);
        }
    }
    
    
    
    public boolean search(House House) throws SQLException {
        String query = "SELECT * FROM HOUSES WHERE ID = ?";
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
