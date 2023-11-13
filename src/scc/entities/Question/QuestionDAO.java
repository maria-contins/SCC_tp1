package scc.entities.Question;

import java.util.Objects;

import org.bson.codecs.pojo.annotations.BsonIgnore;

public class QuestionDAO {

    private String _id;
    private String author;
    private String description;
    private String reply;


    public QuestionDAO() {
    }

    public QuestionDAO(Question q) {
        this(q.getAuthor(), q.getDescription(), q.getReply());
    }

    public QuestionDAO(String author, String description) {
        this.author = author;
        this.description = description;
        reply = ""; // no reply
    }

    public QuestionDAO(String author, String description, String reply) {
        this.author = author;
        this.description = description;
        this.reply = reply;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    private void setReply(String reply) {
        this.reply = reply;
    }

    public void newReply(String reply) {
        if(this.reply.equalsIgnoreCase("")) { // only works if there is no reply yet
            setReply(reply);
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        QuestionDAO other = (QuestionDAO) obj;
        if (_id == null) {
            if (other._id != null)
                return false;
        } else if (!_id.equals(other._id))
            return false;
        if (author == null) {
            if (other.author != null)
                return false;
        } else if (!author.equals(other.author))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (reply == null) {
            if (other.reply != null)
                return false;
        } else if (!reply.equals(other.reply))
            return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(_id, author, description, reply);
    }

    @BsonIgnore
    public Question toQuestion() {
        return new Question(author, description, reply);
    }

    @Override
    public String toString() {
        return "QuestionDAO [_id=" + _id + ", author=" + author + ", description=" + description + ", reply=" + reply
                + "]";
    }

    
}
