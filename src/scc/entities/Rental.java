	package scc.entities;

public class Rental {
	
	private String houseName;
	private String renter;
	private int period;
	private int price;
	
	//Use house name or the actual instance ??
	public Rental(String houseName, String userName, int period, int price) {
		this.houseName = houseName;
		this.renter = userName;
		this.period = period;
		this.price = price;
	}
	
	
	//GETTERS
	
	public String getHouseName() {
		return this.houseName;
	}
	
	public String getRenter() {
		return this.renter;
	}
	
	public int getPeriod() {
		return this.period;
	}
	
	public int getPrice() {
		return this.price;
	}
	
	//Setters
	
	public void setHouseName(String newName) {
		this.houseName = newName;
	}
	
	public void setRenter(String newRenter) {
		this.renter = newRenter;
	}
	
	public void setPeriod(int newPeriod) {
		this.period = newPeriod;
	}
	
	public void setPrice(int newPrice) {
		this.price = newPrice;
	}
}
