package Services;

import Entite.Personne;
import Utilis.Data_Source;
import Utilis.LogManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Service_Personnes implements Iservices<Personne> {
    private Connection con = Data_Source.getInstance().getConnection();
    private Statement stmt;

    public Service_Personnes() {
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

    // Méthode pour hacher le CIN avec SHA-256
    public String hashSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hachage SHA-256", e);
        }
    }

    // Get the last ID and increment by 1
    private int getLastId() throws SQLException {
        String query = "SELECT MAX(ID) AS last_id FROM USERS";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            return rs.getInt("last_id");
        }
        return 0;  // Return 0 if there are no records, so the first ID will be 1.
    }

    @Override
    public void add(Personne p) throws SQLException {
        // Get the last ID and increment by 1
        int lastId = getLastId();
        int newId = lastId + 1;

        String reqpr = "INSERT INTO USERS (ID, NOM, PRENOM, CIN, ADDRESS, EMAIL) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = con.prepareStatement(reqpr);

        // Hacher le CIN avant de l'ajouter à la base de données
        String cinHashed = hashSHA256(p.getCin());

        // Use the new ID
        pstmt.setInt(1, newId);
        pstmt.setString(2, p.getNom());
        pstmt.setString(3, p.getPrenom());
        pstmt.setString(4, cinHashed); // Utiliser le CIN haché
        pstmt.setString(5, p.getAddress());
        pstmt.setString(6, p.getEmail());
        pstmt.executeUpdate();
    }

    @Override
    public void update(Personne p) throws SQLException {
        String reqpr = "UPDATE USERS SET NOM = ?, PRENOM = ?, CIN = ?, ADDRESS = ?, EMAIL = ? WHERE ID = ?";
        try (PreparedStatement pstmt = con.prepareStatement(reqpr)) {
            pstmt.setInt(1, p.getId());
            pstmt.setString(2, p.getNom());
            pstmt.setString(3, hashSHA256(p.getCin())); // Hacher le CIN avant de le mettre à jour
            pstmt.setString(4, p.getAddress());
            pstmt.setString(5, p.getEmail());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(Personne p) throws SQLException {
        String reqpr = "DELETE FROM USERS WHERE ID = ?";
        try (PreparedStatement pstmt = con.prepareStatement(reqpr)) {
            pstmt.setInt(1, p.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Personne> getAll() throws SQLException {
        List<Personne> list = new ArrayList<>();
        ResultSet rs = stmt.executeQuery("SELECT * FROM USERS");

        while (rs.next()) {
            int id = rs.getInt("ID");
            String nom = rs.getString("NOM");
            String prenom = rs.getString("PRENOM");

            // Récupérer et traiter le CIN haché
            String cinHashed = rs.getString("CIN");

            // Ici, le CIN est stocké sous forme de hachage, il n'est pas possible de le récupérer d'origine

            String address = rs.getString("ADDRESS");
            String email = rs.getString("EMAIL");

            // Créer un objet Personne avec les données récupérées
            Personne personne = new Personne(id, nom, prenom, cinHashed, address, email);
            list.add(personne);
        }

        return list;
    }

    public List<Personne> Search(String searchTerm) throws SQLException {
        return List.of();
    }

    public boolean search(Personne p) throws SQLException {
        String reqpr = "SELECT * FROM USERS WHERE NOM = ?";
        PreparedStatement pstmt = con.prepareStatement(reqpr);
        pstmt.setString(1, p.getNom());
        ResultSet rs = pstmt.executeQuery();
        return rs.next();
    }
    public boolean isEmailExists(String email) throws SQLException {
        String query = "SELECT 1 FROM USERS WHERE EMAIL = ?";
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Returns true if a record is found
            }
        }
    }

}
