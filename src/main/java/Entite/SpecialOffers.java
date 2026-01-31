package Entite;

import javafx.beans.property.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SpecialOffers {

    private IntegerProperty id;
    private StringProperty offerName;
    private DoubleProperty discountPercentage;
    private ObjectProperty<Date> startDate;
    private ObjectProperty<Date> endDate;
    private StringProperty offerType;
    private StringProperty offerTypeId1;
    private StringProperty offerTypeId2;
    private DoubleProperty price;
    private IntegerProperty availability;

    // Constructor with properties
    public SpecialOffers(int id, String offerName, double discountPercentage, Date startDate, Date endDate, String offerType, String offerTypeId1, String offerTypeId2, double price, int availability) {
        this.id = new SimpleIntegerProperty(id);
        this.offerName = new SimpleStringProperty(offerName);
        this.discountPercentage = new SimpleDoubleProperty(discountPercentage);
        this.startDate = new SimpleObjectProperty<>(startDate);
        this.endDate = new SimpleObjectProperty<>(endDate);
        this.offerType = new SimpleStringProperty(offerType);
        this.offerTypeId1 = new SimpleStringProperty(offerTypeId1);
        this.offerTypeId2 = new SimpleStringProperty(offerTypeId2);
        this.price = new SimpleDoubleProperty(price);
        this.availability = new SimpleIntegerProperty(availability);
    }

    // Constructor without id (for creating new offers)
    public SpecialOffers(String offerName, double discountPercentage, Date startDate, Date endDate, String offerType, String offerTypeId1, String offerTypeId2, double price, int availability) {
        this.offerName = new SimpleStringProperty(offerName);
        this.discountPercentage = new SimpleDoubleProperty(discountPercentage);
        this.startDate = new SimpleObjectProperty<>(startDate);
        this.endDate = new SimpleObjectProperty<>(endDate);
        this.offerType = new SimpleStringProperty(offerType);
        this.offerTypeId1 = new SimpleStringProperty(offerTypeId1);
        this.offerTypeId2 = new SimpleStringProperty(offerTypeId2);
        this.price = new SimpleDoubleProperty(price);
        this.availability = new SimpleIntegerProperty(availability);
    }

    // Getter and setter methods
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getOfferName() {
        return offerName.get();
    }

    public void setOfferName(String offerName) {
        this.offerName.set(offerName);
    }

    public double getDiscountPercentage() {
        return discountPercentage.get();
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage.set(discountPercentage);
    }

    public Date getStartDate() {
        return startDate.get();
    }

    public void setStartDate(Date startDate) {
        this.startDate.set(startDate);
    }

    public Date getEndDate() {
        return endDate.get();
    }

    public void setEndDate(Date endDate) {
        this.endDate.set(endDate);
    }

    public String getOfferType() {
        return offerType.get();
    }

    public void setOfferType(String offerType) {
        this.offerType.set(offerType);
    }

    public String getOfferTypeId1() {
        return offerTypeId1.get();
    }

    public void setOfferTypeId1(String offerTypeId1) {
        this.offerTypeId1.set(offerTypeId1);
    }

    public String getOfferTypeId2() {
        return offerTypeId2.get();
    }

    public void setOfferTypeId2(String offerTypeId2) {
        this.offerTypeId2.set(offerTypeId2);
    }

    public double getPrice() {
        return price.get();
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public int getAvailability() {
        return availability.get();
    }

    public void setAvailability(int availability) {
        this.availability.set(availability);
    }

    // Properties for JavaFX binding
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty offerNameProperty() {
        return offerName;
    }

    public DoubleProperty discountPercentageProperty() {
        return discountPercentage;
    }

    public ObjectProperty<Date> startDateProperty() {
        return startDate;
    }

    public ObjectProperty<Date> endDateProperty() {
        return endDate;
    }

    public StringProperty offerTypeProperty() {
        return offerType;
    }

    public StringProperty offerTypeId1Property() {
        return offerTypeId1;
    }

    public StringProperty offerTypeId2Property() {
        return offerTypeId2;
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public IntegerProperty availabilityProperty() {
        return availability;
    }
    public StringProperty startDatePropertyString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return new SimpleStringProperty(sdf.format(startDate.get()));
    }

    public StringProperty endDatePropertyString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return new SimpleStringProperty(sdf.format(endDate.get()));
    }

    // Getters for the new string properties
    public String getStartDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(startDate.get());
    }

    public String getEndDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(endDate.get());
    }
    // toString method for easy printing
    @Override
    public String toString() {
        return "Offer Name: " + offerName.get() +
                " From " + getStartDateString() +
                " To " + getEndDateString() +
                " Price: " + price.get() + " DNT";
    }
}
