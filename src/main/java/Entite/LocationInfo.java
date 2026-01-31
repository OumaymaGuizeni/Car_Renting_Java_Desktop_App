package Entite;


public class LocationInfo {
    private String timezone;
    private double lat;
    private double lon;

    // Getters and Setters
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }

    @Override
    public String toString() {
        return "Timezone: " + timezone +
               "\nLatitude: " + lat +
               "\nLongitude: " + lon;
    }
}

