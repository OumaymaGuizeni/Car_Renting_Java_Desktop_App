package Services;

import Entite.Car;
import Utilis.Data_Source;
import Utilis.LogManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Service_Cars implements Iservices<Car> {
    private Connection con = Data_Source.getInstance().getConnection();
    private Statement stmt;
    LogManager logger = LogManager.getInstance();

    public Service_Cars() {
        try {
            if (con != null) {
                stmt = con.createStatement();
            }
        } catch (SQLException e) {
            logger.error("Error :" + e);
            // System.out.println(e);
        }
    }
    private int getLastId() throws SQLException {
        String query = "SELECT MAX(CARID) AS last_id FROM CARS";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            return rs.getInt("last_id");
        }
        return 0;  // Return 0 if there are no records, so the first ID will be 1.
    }


    public void add(Car car) throws SQLException {
        int lastId = getLastId();
        int newId = lastId + 1;

        // Check if the ID is provided
        //int carId = car.getId();  // Get the car ID directly from the Car object
        // Ensure the car ID is passed in correctly (it's expected to be valid)
        String query = "INSERT INTO CARS (CARID, TYPE, MODEL, AVALIBILITY, FUELTYPE,PLATENUM,DAILYPRICE) VALUES (?, ?, ?, ?, ?, ?,?)";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, newId);  // Use the car ID from the UI
            pstmt.setString(2, car.getBrand());
            pstmt.setString(3, car.getModel());
            pstmt.setInt(4, car.getAvailable());
            pstmt.setString(5, car.getFuelType());
            pstmt.setString(6, car.getPlateNum());
            pstmt.setDouble(7, car.getDailyPrice());
            pstmt.executeUpdate();
        }
    }

    // Delete a car by ID
    public void delete(Car car) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM CARS WHERE CARID = ?";
        String deleteQuery = "DELETE FROM CARS WHERE CARID = ?";

        try (
                PreparedStatement checkStmt = con.prepareStatement(checkQuery);
                PreparedStatement deleteStmt = con.prepareStatement(deleteQuery)
        ) {
            // Check if CARID exists
            checkStmt.setInt(1, car.getId());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // CARID exists, proceed with the deletion
                    deleteStmt.setInt(1, car.getId());
                    deleteStmt.executeUpdate();
                    logger.info("Car Deleted successfully");
                    // System.out.println("Car deleted successfully.");
                } else {
                    // CARID not found
                    throw new SQLException("Car ID not found: " + car.getId());
                }
            }
        }
    }


    // Update car parameters by ID
    public void update(Car car) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM CARS WHERE CARID = ?";
        String updateQuery = "UPDATE CARS SET AVALIBILITY = ?, DAILYPRICE = ? WHERE CARID = ?";

        try (
                PreparedStatement checkStmt = con.prepareStatement(checkQuery);
                PreparedStatement updateStmt = con.prepareStatement(updateQuery)
        ) {
            // Check if CARID exists
            checkStmt.setInt(1, car.getId());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // CARID exists, proceed with the update
                    boolean availabilityUpdated = car.getAvailable() >= 0;  // Assume valid availability if >= 0
                    boolean priceUpdated = car.getDailyPrice() >= 0;  // Assume valid price if >= 0

                    if (availabilityUpdated && priceUpdated) {
                        // If both availability and price are valid (greater than or equal to 0), update both
                        updateStmt.setInt(1, car.getAvailable());
                        updateStmt.setDouble(2, car.getDailyPrice());
                    } else if (availabilityUpdated) {
                        // If only availability is valid, update it and leave price unchanged
                        updateStmt.setInt(1, car.getAvailable());
                        updateStmt.setDouble(2, getCurrentPrice(car.getId()));  // Use current price from DB (fetch if needed)
                    } else if (priceUpdated) {
                        // If only price is valid, update it and leave availability unchanged
                        updateStmt.setInt(1, getCurrentAvailability(car.getId()));  // Use current availability from DB (fetch if needed)
                        updateStmt.setDouble(2, car.getDailyPrice());
                    } else {
                        // If neither is valid, don't perform any update
                        throw new SQLException("Both availability and price are invalid.");
                    }

                    // Set the CARID for the update
                    updateStmt.setInt(3, car.getId());

                    // Execute the update
                    updateStmt.executeUpdate();
                    logger.info("Car Updated successfully");
                    //System.out.println("Car updated successfully.");
                } else {
                    // CARID not found
                    throw new SQLException("Car ID not found: " + car.getId());
                }
            }
        }
    }

    // Helper methods to fetch the current values of price or availability (you can implement them as per your needs)
    private int getCurrentAvailability(int carId) throws SQLException {
        // Query the database to get the current availability for the car
        String query = "SELECT AVALIBILITY FROM CARS WHERE CARID = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, carId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("AVALIBILITY");
                } else {
                    throw new SQLException("Car ID not found for fetching availability.");
                }
            }
        }
    }

    private double getCurrentPrice(int carId) throws SQLException {
        // Query the database to get the current price for the car
        String query = "SELECT DAILYPRICE FROM CARS WHERE CARID = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, carId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("DAILYPRICE");
                } else {
                    throw new SQLException("Car ID not found for fetching price.");
                }
            }
        }
    }



    public List<Car> getAll() throws SQLException {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT * FROM CARS ";
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            int carid = rs.getInt("CARID");
            String brand = rs.getString("TYPE");
            String model = rs.getString("MODEL");
            int available = rs.getInt("AVALIBILITY");
            String fuelType = rs.getString("FUELTYPE");
            String plateNum = rs.getString("PLATENUM");
            double dailyPrice = rs.getDouble("DAILYPRICE");
            cars.add(new Car(carid, brand, model, available, fuelType, plateNum, dailyPrice));
        }

        return cars;
    }


    public boolean search(Car car) throws SQLException {
        String query = "SELECT * FROM CARS WHERE CARID = ?";
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            int carid = rs.getInt("CARID");
            String brand = rs.getString("TYPE");
            String model = rs.getString("MODEL");
            return true;

        }
        return false;
    }
}
