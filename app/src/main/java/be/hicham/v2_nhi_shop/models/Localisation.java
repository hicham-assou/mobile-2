package be.hicham.v2_nhi_shop.models;

public class Localisation {

    private double Longitude, Latitude;

    public Localisation(double logitude, double latitude) {
        this.Longitude = logitude;
        this.Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double logitude) {
        this.Longitude = logitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        this.Latitude = latitude;
    }
}
