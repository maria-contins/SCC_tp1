package scc.entities.House;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import scc.entities.User.User;
import scc.entities.User.UserDAO;

import java.util.List;
import java.util.Objects;

public class HouseDAO {

    private String _id;
    @BsonProperty("id")
    private String id;
    private String name;
    private String location;
    private String description;
    private List<String> media;
    private boolean deleted;
    private String ownerId;

    public HouseDAO() {
    }

    public HouseDAO(String id, String name, String location, String description, List<String> media, String ownerId) {
        this._id = id;
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.media = media;
    }

    public String get_id() {
        return this._id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public static House toHouse(HouseDAO houseDAO) {
        return new House( houseDAO.id, houseDAO.name, houseDAO.location, houseDAO.description, houseDAO.media, houseDAO.ownerId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        HouseDAO houseDAO = (HouseDAO) obj;
        return Objects.equals(id, houseDAO.id) && Objects.equals(name, houseDAO.name) && Objects.equals(location, houseDAO.location) && Objects.equals(description, houseDAO.description) && media.equals(houseDAO.media);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, id, name, description, media);
    }

    @Override
    public String toString() {
        return "HouseDAO [_id=" + _id + ", id=" + id + ", name=" + name + ", description="
                + description + ", media=" + media + "location=" + location + ", deleted=" + deleted + "]";
    }

    @BsonIgnore
    public House toHouse() {
        return new House(id, name, location, description, media, ownerId);
    }
}
