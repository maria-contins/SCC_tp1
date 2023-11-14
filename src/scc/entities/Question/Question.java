package scc.entities.Question;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import scc.entities.User.User;
import scc.entities.User.UserDAO;

/*
    * Question entity
    * houses’ questions and replies. Each question must include the house it
    * refers to, the user that posed the question and the text of the message.
 */
public class Question {

    private String id;
    private String HouseId;
    private String authorId;
    private String body;
    private String repliedToId;
    private boolean answered;


    public Question() {
    }

    public Question(String id, String houseId, String authorId, String body, String repliedToId) {
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

    public boolean isReply() {
        return repliedToId != null;
    }

    public static QuestionDAO toDAO(Question question) {
        return new QuestionDAO( question.id, question.HouseId, question.authorId, question.body, question.repliedToId);
    }

    @Override
    public String toString() {
        return "Question{" +
                "id='" + id + '\'' +
                ", HouseId='" + HouseId + '\'' +
                ", authorId='" + authorId + '\'' +
                ", body='" + body + '\'' +
                ", repliedToId='" + repliedToId + '\'' +
                ", answered=" + answered +
                '}';
    }

}
