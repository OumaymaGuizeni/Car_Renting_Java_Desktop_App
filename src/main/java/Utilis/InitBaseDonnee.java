package Utilis;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.sun.media.jfxmedia.logging.Logger;
import Utilis.LogManager;

public class InitBaseDonnee {
    private Connection con;

    // Constructor to initialize the database connection
    public InitBaseDonnee() {
        LogManager logger = LogManager.getInstance();
        try {
            // Use Data_Source to get a connection
            this.con = Data_Source.getInstance().getConnection();
            if (con != null) {
                logger.info("Connection established successfully in InitBaseDonnee.");
                // System.out.println("Connection established successfully in InitBaseDonnee.");
            } else {
                logger.error("Failed to establish a connection in InitBaseDonnee.");
                // System.out.println("Failed to establish a connection in InitBaseDonnee.");
            }
        } catch (Exception e) {
            logger.error("Error initializing InitBaseDonnee !");
            logger.error("Error  : " + e.getMessage());
            // System.out.println("Error initializing InitBaseDonnee: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to execute table creation scripts
    private void createTable(String createTableSQL) {
        LogManager logger = LogManager.getInstance();
        if (con == null) {
            System.err.println("Cannot create table, connection is null.");
            return;
        }
        try (Statement stmt = con.createStatement()) {
            stmt.execute(createTableSQL);
            // logger.info(String.format("Table Created Successfully: %s", createTableSQL));
            System.out.println("Table created successfully.");
        } catch (SQLException e) {
            // Ignore "table already exists" error (SQLCODE -955)
            if (e.getErrorCode() == 955) {
                logger.warn("Table already exists. Skipping creation ");
                // System.out.println("Table already exists. Skipping creation.");
            } else {
                logger.error("Eroor creating table ");
                logger.error(String.format("The Error is : " + e.getMessage()));
                // System.out.println("Error creating table: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Method to check if a table exists and is empty
    private boolean isTableEmpty(String tableName) {
        String checkSQL = "SELECT COUNT(*) FROM " + tableName;
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(checkSQL)) {
            if (rs.next()) {
                return rs.getInt(1) == 0; // Table is empty if count is 0
            }
        } catch (SQLException e) {
            // Logger.getLogger().log(java.util.logging.Level.SEVERE, "Error checking table:
            // " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Method to insert data into a table if it exists and is empty
    private void insertDataIfTableEmpty(String tableName, String insertSQL) {
        LogManager logger = LogManager.getInstance();
        if (isTableEmpty(tableName)) {
            try (Statement stmt = con.createStatement()) {
                stmt.execute(insertSQL);
                logger.info("Data inserted into table: " + tableName);
            } catch (SQLException e) {
                logger.error("Error inserting data into table: " + tableName);
                logger.error("Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            logger.info("Table " + tableName + " is not empty. Skipping data insertion.");
        }
    }

    // Method to create all tables if they do not exist
    public void createTablesIfNotExist() {
        if (con == null) {
            System.err.println("Database connection is null. Skipping table creation and data insertion.");
            return;
        }
        String createCarsTableSQL = "CREATE TABLE CARS (" +
                "    CARID INTEGER PRIMARY KEY," +
                "    TYPE VARCHAR(255) ," +
                "    MODEL VARCHAR(255) ," +
                "    AVALIBILITY INTEGER ," +
                "    PLATENUM VARCHAR(255)," +
                "    FUELTYPE VARCHAR(255)," +
                "    DAILYPRICE DOUBLE NOT NULL CHECK (DAILYPRICE >= 0)" +
                ")";

        String createHotelsTableSQL = "CREATE TABLE HOTELS (" +
                "ID INTEGER NOT NULL," +
                "NAME VARCHAR(255) NOT NULL," +
                "LOCATION VARCHAR(255) NOT NULL," +
                "ROOMS_AVAILABLE INTEGER NOT NULL," +
                "PRICE_PER_NIGHT DOUBLE NOT NULL," +
                "FULL_CAPACITY INTEGER NOT NULL," +
                "RESERVED_BY_OUR_AGENCY INTEGER," +
                "IMAGE_URL VARCHAR(255)," +
                "STARS INTEGER NOT NULL" +
                ")";

        String createUserTableSQL = "CREATE TABLE USERS (" +
                "    ID INTEGER PRIMARY KEY," +
                "    EMAIL VARCHAR(255) ," +
                "    CIN TEXT," +
                "    PRENOM VARCHAR(255) ," +
                "    ADDRESS VARCHAR(255)," +
                "    NOM VARCHAR(255) " +
                ")";

        String createFlightTableSQL = "CREATE TABLE flights (" +
                "Flight_number VARCHAR(10)," +
                "departure VARCHAR(50)," +
                "arrival VARCHAR(50)," +
                "seats_available INTEGER, " + // Removed comment for safety
                "price DOUBLE)";

        String createHousesTableSQL = "CREATE TABLE HOUSES (" +
                "    ID INTEGER ," +
                "    NAME VARCHAR(255)," +
                "    LOCATION VARCHAR(255)," +
                "    ROOMS_AVAILABLE INTEGER," +
                "    PRICE_PER_NIGHT DOUBLE," +
                "    FULL_CAPACITY VARCHAR(255)," +
                "    RESERVED_BY_OUR_AGENCY VARCHAR(255)" +
                ")";

        String createReservationsTableSQL = "CREATE TABLE RESERVATIONS (" +
                "    ID INTEGER," +
                "    USER_FULL_NAME VARCHAR(255)," +
                "    USER_EMAIL VARCHAR(255)," +
                "    DATEDEBUT DATE NOT NULL," +
                "    DATEFIN DATE NOT NULL," +
                "    TYPE VARCHAR(255)," +
                "    TYPE_ID VARCHAR(255)," +
                "    RESERVATION_PRICE VARCHAR(255)," +
                "    nbrOfSeatsOrRooms INTEGER" +
                ")";

        // Insert data script for CARS
        // Insert data script for CARS
        String insertCarsDataSQL = "INSERT INTO CARS (CARID, TYPE, MODEL, AVALIBILITY, PLATENUM, FUELTYPE, DAILYPRICE) VALUES "
                +
                "(1, 'Sedan', 'Toyota Camry', 1, 'ABC123', 'Petrol', 50)," +
                "(2, 'SUV', 'Honda CR-V', 1, 'DEF456', 'Diesel', 70)," +
                "(3, 'Hatchback', 'Ford Fiesta', 1, 'GHI789', 'Petrol', 40)," +
                "(4, 'Convertible', 'BMW Z4', 1, 'JKL012', 'Petrol', 120)," +
                "(5, 'Truck', 'Ford F-150', 1, 'MNO345', 'Diesel', 90)," +
                "(6, 'Minivan', 'Toyota Sienna', 1, 'PQR678', 'Hybrid', 60)," +
                "(7, 'Sedan', 'Honda Accord', 1, 'STU901', 'Petrol', 55)," +
                "(8, 'SUV', 'Toyota RAV4', 1, 'VWX234', 'Hybrid', 75)," +
                "(9, 'Hatchback', 'Volkswagen Golf', 1, 'YZA567', 'Diesel', 45)," +
                "(10, 'Luxury', 'Mercedes-Benz E-Class', 1, 'BCD890', 'Petrol', 150)";
        // Insert data script for HOTELS
        // Insert data script for HOTELS
        String insertHotelsDataSQL = "INSERT INTO HOTELS (ID, NAME, LOCATION, ROOMS_AVAILABLE, PRICE_PER_NIGHT, FULL_CAPACITY, RESERVED_BY_OUR_AGENCY, IMAGE_URL , STARS) VALUES "
                +
                "(1, 'Movenpick', 'Sousse', 300, 450, 350, 50, 'https://cdn2.tqsan.com/booking/movenpick-resort-marine-spa/Hotel-1988-20170308-105626.jpg' , 6),"
                +
                "(2, 'Laico', 'Tunis', 200, 350, 250, 30, 'https://media-cdn.tripadvisor.com/media/photo-s/15/e9/16/d6/laico-tunis.jpg' , 4),"
                +
                "(3, 'Golden Tulip', 'Carthage', 150, 400, 200, 40, 'https://1503846100.rsc.cdn77.org/photos/17746/Hotel-Golden-Tulip-Carthage-Tunis-Entr%C3%A9e.jpg' , 5 ),"
                +
                "(4, 'Concorde', 'Hammamet', 220, 300, 270, 20, 'https://1915023314.rsc.cdn77.org/photos/17648/voyages2000-occidentalmarcopolo1.jpg' , 5),"
                +
                "(5, 'Radisson Blu', 'Djerba', 180, 500, 220, 25, 'https://static.wixstatic.com/media/60bbce_c6ac77c378ce4f3880b6fbe4c128c551~mv2.jpg/v1/fill/w_917,h_877,al_c,q_85,enc_avif,quality_auto/60bbce_c6ac77c378ce4f3880b6fbe4c128c551~mv2.jpg' , 4),"
                +
                "(6, 'El Mouradi', 'Gammarth', 160, 320, 180, 35, 'https://www.elmouradi.com/cr27.fwk/images/hotels/Hotel-586-20141125-031002.jpg' , 4),"
                +
                "(7, 'Iberostar', 'Mahdia', 250, 280, 300, 45, 'https://c.otcdn.com/imglib/hotelfotos/8/177/hotel-iberostar-selection-royal-el-mansour-mahdia-20231210212132502200.jpg' , 3),"
                +
                "(8, 'Hasdrubal', 'Djerba', 200, 550, 250, 50, 'https://promohotel.os-travel.com/file_manager/source/GALLERY/DJERBA/Hasdrubal%20Prestige/booking-promohotel-tn-hotel-hasdrubal-prestige-thalasso-spa-djerba-exterieure3.png' , 5),"
                +
                "(9, 'Vincci', 'Hammamet', 180, 310, 230, 20, 'https://cdn2.tqsan.com/booking/vincci-marillia/Section-1421-20130610-112623.JPG' , 4),"
                +
                "(10, 'Royal Victoria', 'Sousse', 140, 350, 180, 30, 'https://dd5bn4ev8odbe.cloudfront.net/photos/4186/d7e0a5fa_z.jpg' , 5),"
                +
                "(11, 'LTI Bellevue', 'Tunis', 230, 380, 260, 40, 'https://www.resabo.com/cr.fwk/images/hotels/Hotel-1772-20230906-092431.jpg' , 4),"
                +
                "(12, 'Four Seasons', 'Carthage', 160, 600, 190, 60, 'https://cf.bstatic.com/xdata/images/hotel/max1024x768/122990841.jpg?k=a676629f629653fec3c42f1d374344ede5c29bf930f8a40da2472d43fa5f10a8&o=&hp=1' , 5),"
                +
                "(13, 'Sheraton', 'Tunis', 200, 420, 240, 25, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS4vNppDw1UYOzL1NJVOGSj8qY0CsXeYNMiEg&s' , 4),"
                +
                "(14, 'Hilton', 'Gammarth', 150, 550, 180, 20, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSEbByPW91C8WaA2Q_c7VptUm5ySxx0XG0gwg&s' , 5),"
                +
                "(15, 'Magic Life', 'Monastir', 270, 300, 310, 50, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS4RLCczO1zZcE-5UmXYrRWaDHDIQtc79gMng&s' , 4),"
                +
                "(16, 'Seabel Rym Beach', 'Djerba', 240, 450, 280, 40, 'https://travelbird-images.imgix.net/65/ec/65ecee215b5c1d15e16363f686151ee1?auto=compress%2Cformat&crop=faces%2Cedges%2Ccenter&fit=crop&h=720&upscale=true&w=1080' , 5),"
                +
                "(17, 'Club Med', 'Hammamet', 220, 350, 260, 30, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS1skK887wusnheYTCwLWM-vDL69hFDrLIspQ&s' , 4),"
                +
                "(18, 'The Residence', 'Carthage', 180, 500, 210, 50, 'https://dynamic-media-cdn.tripadvisor.com/media/photo-o/07/79/e1/30/the-residence-tunis.jpg?w=700&h=-1&s=1' , 5),"
                +
                "(19, 'Dar Ismail', 'Tabarka', 130, 280, 160, 15, 'https://www.resabo.com/cr.fwk/images/hotels/Hotel-727-20220127-093353.jpg' , 5),"
                +
                "(20, 'Royal Azur', 'Hammamet', 200, 400, 240, 35, 'https://dynamic-media-cdn.tripadvisor.com/media/photo-o/26/2a/a1/6f/hotel-royal-azur-thalassa.jpg?w=700&h=-1&s=1' , 4)";

        // Insert data script for FLIGHTS
        // Insert data script for FLIGHTS
        String insertFlightDataSQL = "INSERT INTO FLIGHTS (Flight_number, departure, arrival, seats_available, price) VALUES "
                +
                "('FL001', 'TUNIS', 'PARIS', 200, 450)," +
                "('FL002', 'TUNIS', 'LONDON', 150, 500)," +
                "('FL003', 'TUNIS', 'NEW YORK', 100, 600)," +
                "('FL004', 'TUNIS', 'BERLIN', 180, 550)," +
                "('FL005', 'TUNIS', 'MADRID', 120, 480)," +
                "('FL006', 'TUNIS', 'ROME', 160, 520)," +
                "('FL007', 'TUNIS', 'DUBAI', 90, 700)," +
                "('FL008', 'TUNIS', 'CAIRO', 140, 430)," +
                "('FL009', 'TUNIS', 'BRUSSELS', 110, 490)," +
                "('FL010', 'TUNIS', 'ATHENS', 130, 460)," +
                "('FL011', 'TOKYO', 'LOS ANGELES', 200, 800)," +
                "('FL012', 'SYDNEY', 'MELBOURNE', 180, 300)," +
                "('FL013', 'CAPE TOWN', 'JOHANNESBURG', 150, 200)," +
                "('FL014', 'LONDON', 'PARIS', 120, 150)," +
                "('FL015', 'NEW DELHI', 'BANGALORE', 160, 100)," +
                "('FL016', 'SINGAPORE', 'HONG KONG', 140, 250)," +
                "('FL017', 'RIO DE JANEIRO', 'SAO PAULO', 130, 180)," +
                "('FL018', 'MOSCOW', 'ST. PETERSBURG', 110, 220)," +
                "('FL019', 'ISTANBUL', 'ATHENS', 90, 200)," +
                "('FL020', 'DUBAI', 'ABU DHABI', 200, 100)," +
                "('FL021', 'LOS ANGELES', 'SAN FRANCISCO', 150, 120)," +
                "('FL022', 'PARIS', 'ROME', 180, 300)," +
                "('FL023', 'LONDON', 'DUBLIN', 200, 130)," +
                "('FL024', 'BANGKOK', 'BALI', 160, 400)," +
                "('FL025', 'HOUSTON', 'CHICAGO', 140, 250)," +
                "('FL026', 'MUMBAI', 'DELHI', 200, 90)," +
                "('FL027', 'SANTIAGO', 'LIMA', 130, 150)," +
                "('FL028', 'TORONTO', 'VANCOUVER', 160, 220)," +
                "('FL029', 'SEOUL', 'TOKYO', 180, 300)," +
                "('FL030', 'CAIRO', 'DOHA', 150, 200)";

        // Create tables
        createTable(createCarsTableSQL);
        createTable(createHotelsTableSQL);
        createTable(createUserTableSQL);
        createTable(createHousesTableSQL);
        createTable(createFlightTableSQL);
        createTable(createReservationsTableSQL);

        // insert data if empty
        insertDataIfTableEmpty("CARS", insertCarsDataSQL);
        insertDataIfTableEmpty("HOTELS", insertHotelsDataSQL);
        insertDataIfTableEmpty("flights", insertFlightDataSQL);

    }

    public static void main(String[] args) {
        // Initialize InitBaseDonnee and create tables
        InitBaseDonnee initBaseDonnee = new InitBaseDonnee();
        initBaseDonnee.createTablesIfNotExist();
    }
}
