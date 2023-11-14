package scc.entities.Question;

import org.bson.codecs.pojo.annotations.BsonIgnore;

/*
    * Question entity
    * houses’ questions and replies. Each question must include the house it
    * refers to, the user that posed the question and the text of the message.
 */
public class Question {

    private String id;

    private String Houseid;
    private String author;
    private String description;
    private String reply;


    public Question(String author, String description) {
        this.author = author;
        this.description = description;
        reply = ""; // no reply
    }

    public Question(String author, String description, String reply) {
        this.author = author;
        this.description = description;
        this.reply = reply;
    }


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public void newReply(String reply) {
        if(this.reply.equalsIgnoreCase("")) { // only works if there is no reply yet
            setReply(reply);
        }
    }

    @BsonIgnore
    public static QuestionDAO toDAO(Question q) {
        return new QuestionDAO(q.author, q.description, q.reply);
    }
    @BsonIgnore
    public QuestionDAO toDAO() {
        return new QuestionDAO(author, description, reply);
    }

    @Override
    public String toString() {
        return "Question [author=" + author + ", description=" + description + ", reply=" + reply + "]";
    }

}
