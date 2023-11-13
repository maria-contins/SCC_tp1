package scc.entities.User;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.List;
import java.util.Objects;

public class UserDAO {

    private String _id;
    @BsonProperty("id")
    private String id;

    private String nickname;

    private boolean deleted;

    private String password;

    private String photoId;

    private List<String> houseIds;

    public UserDAO() {
    }

    public UserDAO(String id, String nickname, boolean deleted, String password, String photoId, List<String> houseIds) {
        this.id = id;
        this._id = id;
        this.nickname = nickname;
        this.deleted = deleted;
        this.password = password;
        this.photoId = photoId;
        this.houseIds = houseIds;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public List<String> getHouseIds() {
        return houseIds;
    }

    public void setHouseIds(List<String> houseIds) {
        this.houseIds = houseIds;
    }

    @BsonIgnore
    public static User toUser(UserDAO userDAO) {
        return new User( userDAO.id, userDAO.nickname, userDAO.deleted, userDAO.password, userDAO.photoId, userDAO.houseIds);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserDAO userDAO = (UserDAO) obj;
        return Objects.equals(id, userDAO.id) && Objects.equals(nickname, userDAO.nickname) && Objects.equals(password, userDAO.password) && Objects.equals(photoId, userDAO.photoId) && houseIds.equals(userDAO.houseIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, id, nickname, password, photoId, houseIds);
    }

    @Override
    public String toString() {
        return "UserDAO [_id=" + _id + ", id=" + id + ", nickname=" + nickname + ", password="
                + password + ", photoId=" + photoId + ", houseIds=" + houseIds + "]";
    }

    @BsonIgnore
    public User toUser() {
        return new User(id, nickname, deleted, password, photoId, houseIds);
    }


}