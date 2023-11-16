package scc.entities.Rental;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class RentalDAO {

    private String _id;
    @BsonProperty("id")
    private String id;
    private String houseId;
    private String renterId;;
    private String price;
    private String discount;
    private String fromDate;
    private String toDate;
    private boolean free;

    private String ownerId;

    public RentalDAO() {

    }

    public RentalDAO(String id, String houseId, String renterId, String price, String discount, String fromDate, String toDate, String ownerId) {
        super();
        this.id = id;
        this.houseId = houseId;
        this.renterId = renterId;
        this.price = price;
        this.discount = discount;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.ownerId = ownerId;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public String getRenterId() {
        return renterId;
    }

    public void setRenterId(String renterId) {
        this.renterId = renterId;
    }

    public String getPrice() {
        return price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getToDate() {
        return toDate;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    @BsonIgnore
    public static Rental toRental(RentalDAO rentalDAO) {
        return new Rental(rentalDAO.getId(), rentalDAO.getHouseId(), rentalDAO.getRenterId(), rentalDAO.getPrice(), rentalDAO.getDiscount(),rentalDAO.getFromDate(), rentalDAO.getToDate(), rentalDAO.getOwnerId());
    }

    @BsonIgnore
    public static RentalDAO toDAO(Rental rental) {
        return new RentalDAO(rental.getId(), rental.getHouseId(), rental.getRenterId(), rental.getPrice(), rental.getDiscount(), rental.getFromDate(), rental.getToDate(), rental.getOwnerId());
    }

    @Override
    public String toString() {
        return "RentalDAO{" +
                "id="+ id + '\''+
                "_id='" + _id + '\'' +
                ", houseId='" + houseId + '\'' +
                ", renterId='" + renterId + '\'' +
                ", price=" + price +
                ", fromDate='" + fromDate + '\'' +
                ", toDate='" + toDate + '\'' +
                '}';
    }


}
