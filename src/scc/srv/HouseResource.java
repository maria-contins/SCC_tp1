package scc.srv;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import scc.data.MongoDBLayer;
import scc.entities.House.Availability.Availability;
import scc.entities.House.House;
import scc.exceptions.DuplicateException;
import scc.exceptions.NotFoundException;

@Path("/houses")
public class HouseResource {
	
    private MongoDBLayer dataLayer;

    public HouseResource(MongoDBLayer dataLayer) {
        this.dataLayer = dataLayer;
    }
    
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public House createHouse(House house) {
        try {
            return dataLayer.createHouse(house);
        } catch (DuplicateException e) {
            throw new WebApplicationException(Response.Status.CONFLICT);
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public House getHouse(@PathParam("id") String id) {
        try {
            return dataLayer.getHouse(id);
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public House updateHouse(@PathParam("id") String id, House house) {
        try {
            return dataLayer.updateHouse(id, house);
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public House deleteHouse(@PathParam("id") String id) {
        try {
            return dataLayer.deleteHouse(id);
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
	
	
}
