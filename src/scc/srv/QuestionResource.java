package scc.srv;

import java.util.ArrayList;
import java.util.List;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
// import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobItem;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import scc.data.MongoDBLayer;
import scc.entities.Question.Question;
import scc.exceptions.DuplicateException;
import scc.exceptions.NotFoundException;
import scc.utils.Hash;

@Path("/rest/house/{id}/question")
public class QuestionResource {

    //CosmosDBLayer dataLayer;
    MongoDBLayer dataLayer;

    public QuestionResource() {
    }

    public QuestionResource(MongoDBLayer dm) {
        this.dataLayer = dm;
    }


    /**
     * Post a new question.
     */
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Question create(Question q) {
        try {
            return dataLayer.createQuestion(q);
        } catch (Exception e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.FORBIDDEN);
        }
    }

    /**
     * Replies to a specific question based on its id.
     */
    @PUT
    @Path("/{id}/reply")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String reply(@PathParam("id") String id, String reply) {
        try {
            return dataLayer.replyQuestion(id, reply);
        } catch (Exception e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.FORBIDDEN);
        }
    }

    /**
     * Return the contents of a question. Throw an appropriate error message if
     * id does not exist.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Question read(@PathParam("id") String id) {
        try {
            return dataLayer.readQuestion(id);
        } catch (Exception e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.FORBIDDEN);
        }
    }

    /**
     * Lists the ids of questions stored.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Question> list() {
        try {
            return dataLayer.listHouseQuestions();
        } catch (Exception e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.FORBIDDEN);
        }
    }

}
