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

    public UserDAO() {
    }

    public UserDAO(String id, String nickname, String password, String photoId) {
        this.id = id;
        this._id = id;
        this.nickname = nickname;
        this.password = password;
        this.photoId = photoId;
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


    @BsonIgnore
    public static User toUser(UserDAO userDAO) {
        return new User( userDAO.id, userDAO.nickname, userDAO.password, userDAO.photoId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserDAO userDAO = (UserDAO) obj;
        return Objects.equals(id, userDAO.id) && Objects.equals(nickname, userDAO.nickname) && Objects.equals(password, userDAO.password) && Objects.equals(photoId, userDAO.photoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, id, nickname, password, photoId);
    }

    @Override
    public String toString() {
        return "UserDAO [_id=" + _id + ", id=" + id + ", nickname=" + nickname + ", password="
                + password + ", photoId=" + photoId + "]";
    }

    @BsonIgnore
    public User toUser() {
        return new User(id, nickname, password, photoId);
    }


}