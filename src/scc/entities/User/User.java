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

    public User() {
    }

    public User(String id, String nickname, String password, String photoId) {
        super();
        this.id = id;
        this.nickname = nickname;
        this.password = password;
        this.photoId = photoId;
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


    public static UserDAO toDAO(User user) {
        return new UserDAO( user.id, user.nickname, user.password, user.photoId);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                ", photoId='" + photoId + '\'' +
                ", isDeleted=" + deleted +
                '}';
    }

}
