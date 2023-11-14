package scc.entities.House.Availability;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class AvailabilityDAO {


    private String _id;

    

    private String houseId;

    private String fromData;

    private String toData;

    private double pricePerNight;

    private double discountedPricePerNight;

    public AvailabilityDAO() {

    }

    public AvailabilityDAO(String houseId, String fromData, String toData, double pricePerNight, double discountedPricePerNight) {
        super();
        this._id = houseId;
        this.houseId = houseId;
        this.fromData = fromData;
        this.toData = toData;
        this.pricePerNight = pricePerNight;
        this.discountedPricePerNight = discountedPricePerNight;
    }
}
