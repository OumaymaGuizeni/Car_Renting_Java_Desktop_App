package Entite;

public class Car {
    private int id;
    private String brand;
    private String model;
    private int available;
    private String fuelType;
    private String plateNum;
    private double dailyPrice;

    public Car(int id, String brand, String model, int available, String fuelType, String plateNum, double dailyPrice) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.available = available;
        this.fuelType = fuelType;
        this.plateNum = plateNum;
        this.dailyPrice = dailyPrice;
    }
    public Car(String brand, String model, int available, String fuelType, String plateNum, double dailyPrice) {
        this.brand = brand;
        this.model = model;
        this.available = available;
        this.fuelType = fuelType;
        this.plateNum = plateNum;
        this.dailyPrice = dailyPrice;
    }


    @Override
    public String toString() {
        return "Brand: "+brand + " Model : " + model + " Plat Number : " + plateNum + " Daily Price " + dailyPrice;
    }
    // Getters
    public int getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getAvailable() {
        return available;
    }

    public String getFuelType() {
        return fuelType;
    }

    public String getPlateNum() {
        return plateNum;
    }

    public double getDailyPrice() {
        return dailyPrice;
    }

    // Setters (optional, for updates)
    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public void setDailyPrice(double dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    public void setAvailable(int available) {
        this.available = available;
    }
}
