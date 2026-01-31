package Services;

import Entite.LocationInfo;

public class LocationService {

    public LocationInfo getLocationInfo() {
        // Méthode pour obtenir des informations de localisation.
        // Vous pouvez ajouter la logique ici, par exemple, en utilisant une API pour obtenir des informations de géolocalisation.
        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setTimezone("UTC");
        locationInfo.setLat(52.5200);
        locationInfo.setLon(13.4050);
        return locationInfo;
    }
}

