package controllers.User_controllers;

public class UserSession {

    private static String email;
    private static String cin;
    private static String nom;
    private static String prenom;
    private static String address;

    // Getters and Settersg
    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        UserSession.email = email;
    }

    public static String getCin() {
        return cin;
    }

    public static void setCin(String cin) {
        UserSession.cin = cin;
    }

    public static String getNom() {
        return nom;
    }

    public static String getPrenom() {
        return prenom;
    }

    public static void setPrenom(String prenom) {
        UserSession.prenom = prenom;
    }

    public static String getAddress() {
        return address;
    }

    public static void setAddress(String address) {
        UserSession.address = address;
    }

    // Clear user data
    public static void clear() {
        email = null;
        cin = null;
        nom = null;
        prenom = null;
        address = null;
    }
}
