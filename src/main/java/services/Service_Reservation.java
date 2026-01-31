package Services;

import Entite.Reservation;
import Utilis.Data_Source;
import Utilis.LogManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Service_Reservation implements Iservices<Reservation> {
    private final Connection con = Data_Source.getInstance().getConnection();
    LogManager logger = LogManager.getInstance();

    public Service_Reservation() {
        try {
            if (con == null) {
                throw new SQLException("Database connection is not initialized.");
            }
        } catch (SQLException e) {
            logger.error("Error : " + e);
            // System.out.println(e);
        }
    }

    // Get the last ID and increment by 1
    private int getLastId() throws SQLException {
        String query = "SELECT MAX(ID) AS last_id FROM RESERVATIONS";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("last_id");
            }
        }
        return 0;  // Return 0 if there are no records, so the first ID will be 1.
    }

    public void add(Reservation reservation) throws SQLException {
        int lastId = getLastId();
        int newId = lastId + 1;

        String query = "INSERT INTO RESERVATIONS (ID, USER_FULL_NAME, USER_EMAIL, DATEDEBUT, DATEFIN, TYPE, TYPE_ID, RESERVATION_PRICE,nbrOfSeatsOrRooms) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, newId); // Set the ID explicitly
            pstmt.setString(2, reservation.getUserFullName());
            pstmt.setString(3, reservation.getUserEmail());
            pstmt.setDate(4, java.sql.Date.valueOf(reservation.getDateDebut()));
            pstmt.setDate(5, java.sql.Date.valueOf(reservation.getDateFin()));
            pstmt.setString(6, reservation.getType());
            pstmt.setString(7, reservation.getTypeId());
            pstmt.setDouble(8, reservation.getReservationPrice());
            pstmt.setInt(9, reservation.getNbrOfSeatsOrRooms());
            pstmt.executeUpdate();
        }
    }

    public void delete(Reservation reservation) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM RESERVATIONS WHERE ID = ?";
        String deleteQuery = "DELETE FROM RESERVATIONS WHERE ID = ?";

        try (PreparedStatement checkStmt = con.prepareStatement(checkQuery);
             PreparedStatement deleteStmt = con.prepareStatement(deleteQuery)) {
            checkStmt.setInt(1, reservation.getId());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    deleteStmt.setInt(1, reservation.getId());
                    deleteStmt.executeUpdate();
                    logger.info("Reservation deleted successfully");
                    // System.out.println("Reservation deleted successfully.");
                } else {
                    throw new SQLException("Reservation ID not found: " + reservation.getId());
                }
            }
        }
    }

    public void update(Reservation reservation) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM RESERVATIONS WHERE ID = ?";
        String updateQuery = "UPDATE RESERVATIONS SET DATEDEBUT = ?, DATEFIN = ?, nbrOfSeatsOrRooms=?,RESERVATION_PRICE = ? WHERE ID = ?";

        try (PreparedStatement checkStmt = con.prepareStatement(checkQuery);
             PreparedStatement updateStmt = con.prepareStatement(updateQuery)) {
            // Check if the reservation exists
            checkStmt.setInt(1, reservation.getId());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // If the reservation exists, update the record
                    updateStmt.setDate(1, java.sql.Date.valueOf(reservation.getDateDebut()));
                    updateStmt.setDate(2, java.sql.Date.valueOf(reservation.getDateFin()));
                    updateStmt.setInt(3, reservation.getNbrOfSeatsOrRooms());
                    updateStmt.setDouble(4, reservation.getReservationPrice());
                    updateStmt.setInt(5, reservation.getId());
                    updateStmt.executeUpdate();
                    logger.info("Reservation updated successfully");
                    // System.out.println("Reservation updated successfully.");
                } else {
                    throw new SQLException("Reservation ID not found: " + reservation.getId());
                }
            }
        }
    }

    public List<Reservation> getAll() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM RESERVATIONS";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("ID");
                String userFullName = rs.getString("USER_FULL_NAME");
                String userMail = rs.getString("USER_EMAIL");
                LocalDate dateDebut = rs.getDate("DATEDEBUT").toLocalDate();
                LocalDate dateFin = rs.getDate("DATEFIN").toLocalDate();
                String type = rs.getString("TYPE");
                String typeId = rs.getString("TYPE_ID");
                double reservationPrice = rs.getDouble("RESERVATION_PRICE");
                int nombreOfseatsOrRooms = rs.getInt("nbrOfSeatsOrRooms");

                reservations.add(new Reservation(id, userFullName, userMail, dateDebut, dateFin, type, typeId, reservationPrice,nombreOfseatsOrRooms));

            }
        }

        return reservations;
    }

    public List<Reservation> getReservationsByUser(String email) throws SQLException {
        String query = "SELECT * FROM RESERVATIONS WHERE USER_EMAIL = ?";
        List<Reservation> reservations = new ArrayList<>();

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = new Reservation(
                            rs.getString("USER_FULL_NAME"),
                            rs.getString("USER_EMAIL"),
                            rs.getDate("DATEDEBUT").toLocalDate(),
                            rs.getDate("DATEFIN").toLocalDate(),
                            rs.getString("TYPE"),
                            rs.getString("TYPE_ID"),
                            rs.getDouble("RESERVATION_PRICE"),
                            rs.getInt("NBROfSeatsOrRooms")
                    );
                    reservation.setId(rs.getInt("ID"));
                    reservations.add(reservation);
                }
            }
        }
        return reservations;
    }
}
