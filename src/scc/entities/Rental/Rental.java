	package scc.entities.Rental;

	import scc.entities.House.House;
	import scc.entities.House.HouseDAO;

	import java.util.Map;

	public class Rental {
		private String houseId;
		private String renterId;
		private String price;
		private String discount;

		private String id;
		private String fromDate;
		private String toDate;
		private boolean free;

		private String ownerId;

		public Rental() {

		}

		//Use house name or the actual instance ??
		public Rental(String id, String houseId, String renterId, String price, String discount, String fromDate, String toDate, String ownerId, boolean free) {
			super();
			this.id = id;
			this.houseId = houseId;
			this.renterId = renterId;
			this.price = price;
			this.discount = discount;
			this.fromDate = fromDate;
			this.toDate = toDate;
			this.ownerId = ownerId;
			this.free = free;
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

		public String getOwnerId() {
			return this.ownerId;
		}

		public void setOwnerId(String ownerId) {
			this.ownerId = ownerId;
		}

		public static RentalDAO toDAO(Rental rental) {
			return new RentalDAO(rental.getId(), rental.getHouseId(), rental.getRenterId(), rental.getPrice(), rental.getDiscount(), rental.getFromDate(), rental.getToDate(), rental.getOwnerId(), rental.isFree());
		}
	}
