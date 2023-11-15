	package scc.entities.Rental;

	import scc.entities.House.House;
	import scc.entities.House.HouseDAO;

	public class Rental {

	private String houseId;
	private String renterId;;
	private String price;
	private String id;
	private String fromDate;

	private String toDate;

	public Rental() {

	}
	
	//Use house name or the actual instance ??
	public Rental(String id, String houseId, String renterId, String price, String fromDate, String toDate) {
		super();
		this.id = id;
		this.houseId = houseId;
		this.renterId = renterId;
		this.price = price;
		this.fromDate = fromDate;
		this.toDate = toDate;
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

	public void setPrice(String price) {
		this.price = price;
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

	public static RentalDAO toDAO(Rental rental) {
		return new RentalDAO(rental.getHouseId(), rental.getRenterId(), rental.getPrice(), rental.getFromDate(), rental.getToDate());
	}
}
