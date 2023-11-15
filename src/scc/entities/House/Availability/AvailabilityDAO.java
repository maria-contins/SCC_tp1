package scc.entities.House.Availability;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Objects;

public class AvailabilityDAO {


    private String _id;

    @BsonProperty
    private String id;
    private String houseId;

    private String fromData;

    private String toData;

    private String pricePerNight;

    private String discountedPricePerNight;

    public AvailabilityDAO() {

    }

    public AvailabilityDAO(String id, String houseId, String fromData, String toData, String pricePerNight, String discountedPricePerNight) {
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

    public void set_Id(String _id) {
        this._id = _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_id() {
        return _id;
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

    @Override
    public String toString() {
        return "Availability [ houseId=" + houseId + ", fromData=" + fromData + ", toData=" + toData
                + ", pricePerNight=" + pricePerNight + ", discountedPricePerNight=" + discountedPricePerNight + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, houseId, fromData, toData, pricePerNight, discountedPricePerNight);
    }

    @BsonIgnore
    public static Availability toAvailability(AvailabilityDAO a) {
        return new Availability(a.id, a.houseId, a.fromData, a.toData, a.pricePerNight, a.discountedPricePerNight);
    }

    @BsonIgnore
    public Availability toAvailability() {
        return new Availability(id, houseId, fromData, toData, pricePerNight, discountedPricePerNight);
    }


}
