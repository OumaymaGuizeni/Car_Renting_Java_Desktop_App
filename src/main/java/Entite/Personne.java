package Entite;

public class Personne {
    private int id;
    private String nom;
    private String prenom;
    private String cin;
    private String address;
    private String email;

    public Personne(int id, String nom, String prenom, String cin, String address, String email) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.cin = cin;
        this.address = address;
        this.email = email;
    }
    public Personne(String nom, String prenom, String cin, String address, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.cin = cin;
        this.address = address;
        this.email = email;
    }

    @Override
    public String toString() {
        return "Personne{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", cin='" + cin + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public Personne() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setCin (String cin){
        this.cin = cin ;
    }

    public String getCin (){
        return cin ;
    }

    public void setAddress (String address){
        this.address = address ; 
    }

    public String getAddress (){
        return address ;
    }

    public void setEmail (String email){
        this.email = email ;
    }

    public String getEmail (){
        return email ;
    }
}
