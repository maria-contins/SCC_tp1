package scc.entities.User;

import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Arrays;
import java.util.List;

public class User {
    private String id;

    private String nickname;

    private String password;

    private String photoId;

    private boolean deleted;

    private List<String> houseIds;

    public User() {
    }

    public User(String id, String nickname, boolean deleted, String password, String photoId, List<String> houseIds) {
        super();
        this.id = id;
        this.nickname = nickname;
        this.deleted = deleted;
        this.password = password;
        this.photoId = photoId;
        this.houseIds = houseIds;
    }

    public String getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
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

    public String getPhotoId() {
        return photoId;
    }

    public List<String> getHouseIds() {
        return houseIds;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public void setHouseIds(List<String> houseIds) {
        this.houseIds = houseIds;
    }

    public static UserDAO toDAO(User user) {
        return new UserDAO( user.id, user.nickname, user.deleted, user.password, user.photoId, user.houseIds);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                ", photoId='" + photoId + '\'' +
                ", isDeleted=" + deleted +
                ", houseIds=" + houseIds +
                '}';
    }

}
