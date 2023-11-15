package scc.entities.House.Availability;

import java.util.Objects;

public class Availability {

    private String id;
    private String houseId;

    private String fromData;

    private String toData;

    private String pricePerNight;

    private String discountedPricePerNight;

    public Availability() {

    }

    public Availability(String id, String houseId, String fromData, String toData, String pricePerNight, String discountedPricePerNight) {
        super();
        this.id = id;
        this.houseId = houseId;
        this.fromData = fromData;
        this.toData = toData;
        this.pricePerNight = pricePerNight;
        this.discountedPricePerNight = discountedPricePerNight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public String getFromData() {
        return fromData;
    }

    public void setFromData(String fromData) {
        this.fromData = fromData;
    }

    public String getToData() {
        return toData;
    }

    public void setToData(String toData) {
        this.toData = toData;
    }

    public String getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(String pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public String getDiscountedPricePerNight() {
        return discountedPricePerNight;
    }

    public void setDiscountedPricePerNight(String discountedPricePerNight) {
        this.discountedPricePerNight = discountedPricePerNight;
    }

    public String toString() {
        return "Availability [Id ="+id+" houseId=" + houseId + ", fromData=" + fromData + ", toData=" + toData + ", pricePerNight=" + pricePerNight + ", discountedPricePerNight=" + discountedPricePerNight + "]";
    }

    public int hashCode() {
        return Objects.hash(houseId, fromData, toData, pricePerNight, discountedPricePerNight);
    }

    public AvailabilityDAO toDAO() {
        return new AvailabilityDAO(id, houseId, fromData, toData, pricePerNight, discountedPricePerNight);
    }


}
