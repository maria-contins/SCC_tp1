package scc.entities.Question;

import java.util.Objects;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import scc.entities.User.User;
import scc.entities.User.UserDAO;

public class QuestionDAO {

    @BsonProperty("id")
    private String id;
    private String HouseId;
    private String authorId;
    private String body;
    private String repliedToId;
    private boolean answered;


    public QuestionDAO() {
    }

    public QuestionDAO(String id, String houseId, String authorId, String body, String repliedToId) {
        super();
        this.id = id;
        HouseId = houseId;
        this.authorId = authorId;
        this.body = body;
        this.repliedToId = repliedToId;
        this.answered = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public  String getHouseId() {
        return HouseId;
    }

    public void setHouseId(String houseId) {
        HouseId = houseId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public String getRepliedToId() {
        return repliedToId;
    }

    public void setRepliedToId(String repliedToId) {
        this.repliedToId = repliedToId;
    }

    @BsonIgnore
    public static Question toQuestion(QuestionDAO questionDAO) {
        return new Question(questionDAO.id, questionDAO.HouseId, questionDAO.authorId, questionDAO.body, questionDAO.repliedToId);
    }

    @Override
    public String toString() {
        return "QuestionDAO{" +
                "id='" + id + '\'' +
                ", HouseId='" + HouseId + '\'' +
                ", authorId='" + authorId + '\'' +
                ", body='" + body + '\'' +
                ", repliedToId='" + repliedToId + '\'' +
                ", answered=" + answered +
                '}';
    }

    @BsonIgnore
    public Question toQuestion() {
        return new Question(id, HouseId, authorId, body, repliedToId);
    }



}
