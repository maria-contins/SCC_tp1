package scc.entities.House.Availability;

import java.util.Objects;

public class Availability {

    private String houseId;

    private String fromData;

    private String toData;

    private double pricePerNight;

    private double discountedPricePerNight;

    public Availability() {

    }

    public Availability(String houseId, String fromData, String toData, double pricePerNight, double discountedPricePerNight) {
        super();
        this.houseId = houseId;
        this.fromData = fromData;
        this.toData = toData;
        this.pricePerNight = pricePerNight;
        this.discountedPricePerNight = discountedPricePerNight;
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

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public double getDiscountedPricePerNight() {
        return discountedPricePerNight;
    }

    public void setDiscountedPricePerNight(double discountedPricePerNight) {
        this.discountedPricePerNight = discountedPricePerNight;
    }

    public String toString() {
        return "Availability [houseId=" + houseId + ", fromData=" + fromData + ", toData=" + toData + ", pricePerNight=" + pricePerNight + ", discountedPricePerNight=" + discountedPricePerNight + "]";
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!Availability.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Availability other = (Availability) obj;
        if (!Objects.equals(this.houseId, other.houseId)) {
            return false;
        }
        if (!Objects.equals(this.fromData, other.fromData)) {
            return false;
        }
        if (!Objects.equals(this.toData, other.toData)) {
            return false;
        }
        if (this.pricePerNight != other.pricePerNight) {
            return false;
        }
        return this.discountedPricePerNight == other.discountedPricePerNight;
    }

    public int hashCode() {
        return Objects.hash(houseId, fromData, toData, pricePerNight, discountedPricePerNight);
    }

    public AvailabilityDAO toDAO() {
        return new AvailabilityDAO(houseId, fromData, toData, pricePerNight, discountedPricePerNight);
    }


}
