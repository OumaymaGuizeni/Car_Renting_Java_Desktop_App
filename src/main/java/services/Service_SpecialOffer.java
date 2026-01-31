package Services;

import Entite.SpecialOffers;
import Utilis.Data_Source;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Service_SpecialOffer implements Iservices<SpecialOffers> {
    private Connection con = Data_Source.getInstance().getConnection();

    public Service_SpecialOffer() {
        try {
            if (con != null) {
                con.createStatement();
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private int getLastId() throws SQLException {
        String query = "SELECT MAX(id) AS last_id FROM SPECIAL_OFFERS";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            return rs.getInt("last_id");
        }
        return 0;  // Return 0 if there are no records, so the first ID will be 1.
    }
    public void add(SpecialOffers offer) throws SQLException {
        String query = "INSERT INTO Special_offers (id,offer_Name, discount_Percentage, start_Date, end_Date, offer_Type, offerTypeId1, offerTypeId2, price, availability) VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int lastId = getLastId();
        int newId = lastId + 1;

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1,newId);
            pstmt.setString(2, offer.getOfferName());
            pstmt.setDouble(3, offer.getDiscountPercentage());
            pstmt.setDate(4, new java.sql.Date(offer.getStartDate().getTime()));
            pstmt.setDate(5, new java.sql.Date(offer.getEndDate().getTime()));
            pstmt.setString(6, offer.getOfferType());
            pstmt.setString(7, offer.getOfferTypeId1());
            pstmt.setString(8, offer.getOfferTypeId2());
            pstmt.setDouble(9, offer.getPrice());
            pstmt.setInt(10, offer.getAvailability());
            pstmt.executeUpdate();
        }
    }

    public void delete(SpecialOffers offer) throws SQLException {
        String query = "DELETE FROM Special_offers WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, offer.getId());
            pstmt.executeUpdate();
        }
    }

    public void update(SpecialOffers offer) throws SQLException {
        String selectQuery = "SELECT price, availability FROM Special_offers WHERE id = ?";
        String updateQuery = "UPDATE Special_offers SET price = ?, availability = ? WHERE id = ?";
        try (
                PreparedStatement selectStmt = con.prepareStatement(selectQuery);
                PreparedStatement updateStmt = con.prepareStatement(updateQuery)
        ) {
            selectStmt.setInt(1, offer.getId());
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    double currentPrice = rs.getDouble("price");
                    int currentAvailability = rs.getInt("availability");

                    double updatedPrice = offer.getPrice() > 0 ? offer.getPrice() : currentPrice;
                    int updatedAvailability = offer.getAvailability() >= 0 ? offer.getAvailability() : currentAvailability;
                    System.out.println(updatedAvailability);
                    updateStmt.setDouble(1, updatedPrice);
                    updateStmt.setInt(2, updatedAvailability);
                    updateStmt.setInt(3, offer.getId());
                    updateStmt.executeUpdate();
                    System.out.println("Upadted Seccussfeully");
                } else {
                    throw new SQLException("Special offer ID not found: " + offer.getId());
                }
            }
        }
    }


    public List<SpecialOffers> getAll() throws SQLException {
        List<SpecialOffers> offers = new ArrayList<>();
        String query = "SELECT * FROM Special_offers";
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                SpecialOffers offer = new SpecialOffers(
                        rs.getInt("id"),
                        rs.getString("offer_Name"),
                        rs.getDouble("discount_Percentage"),
                        rs.getDate("start_Date"),
                        rs.getDate("end_Date"),
                        rs.getString("offer_Type"),
                        rs.getString("offerTypeId1"),
                        rs.getString("offerTypeId2"),
                        rs.getDouble("price"),
                        rs.getInt("availability")
                );
                offers.add(offer);
            }
        }
        return offers;
    }

    public boolean search(SpecialOffers offer) throws SQLException {
        String query = "SELECT * FROM Special_offers WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, offer.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}