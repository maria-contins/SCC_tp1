package scc.entities.House;

import scc.entities.Rental.Rental;

import java.util.*;


public class House {

	private String id;
	private String name;
	private String location;
	private String description;
	private List<String> media;
	private boolean deleted;
	private String ownerId;

	/*Associated with each house, it is also necessary to maintain the availability of the
	house (when it is available for renting) and price for each period (periods can be
			divided in days, weeks or months, as you prefer; for prices you can maintain the
	normal price and a promotion price).*/

	public House() {
	}

	public House(String id, String name, String location, String description, List<String> media, String ownerId) {
		super();
		this.id = id;
		this.name = name;
		this.location = location;
		this.description = description;
		this.media = media;
		this.ownerId = ownerId;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
    }
	
	public String getLocation() {
		return this.location;
	}

	public String setLocation(String location) {
		return this.location = location;
	}
	public String getDescription() {
		return this.description;
	}

	public String setDescription(String description) {
		return this.description = description;
	}
	
	public List<String> getMedia() {
		return this.media;
	}

	public void setMedia(List<String> media) {
		this.media = media;
	}

	public boolean isDeleted() {
		return this.deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getOwnerId() {
		return this.ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public static HouseDAO toDAO(House house) {
		return new HouseDAO(house.id, house.name, house.location, house.description, house.media, house.ownerId);
	}
	
	
}
