package scc.entities.Rental;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class RentalDAO {

    private String _id;

    @BsonProperty
    private String id;
    private String houseId;
    private String renterId;;
    private String price;
    private String discount;
    private String fromDate;
    private String toDate;
    private boolean free;

    public RentalDAO() {

    }

    public RentalDAO(String houseId, String renterId, String price, String discount, String fromDate, String toDate) {
        super();
        this.houseId = houseId;
        this.renterId = renterId;
        this.price = price;
        this.discount = discount;
        this.fromDate = fromDate;
        this.toDate = toDate;
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

    @BsonIgnore
    public static Rental toRental(RentalDAO rentalDAO) {
        return new Rental(rentalDAO.id, rentalDAO.getHouseId(), rentalDAO.getRenterId(), rentalDAO.getPrice(), rentalDAO.getDiscount(),rentalDAO.getFromDate(), rentalDAO.getToDate());
    }

    @BsonIgnore
    public static RentalDAO toDAO(Rental rental) {
        return new RentalDAO(rental.getHouseId(), rental.getRenterId(), rental.getPrice(), rental.getDiscount(), rental.getFromDate(), rental.getToDate());
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
