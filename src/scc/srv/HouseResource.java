package scc.srv;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import scc.data.DataLayer;
import scc.entities.House.House;
import scc.entities.Question.Question;
import scc.entities.Rental.Rental;
import scc.entities.User.Renter;
import scc.exceptions.*;
import scc.exceptions.ForbiddenException;
import scc.exceptions.NotFoundException;

import java.util.List;

@Path("/houses")
public class HouseResource {
	
    private DataLayer dataLayer;

    public HouseResource(DataLayer dataLayer) {
        this.dataLayer = dataLayer;
    }
    
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public House createHouse(@CookieParam("scc:session") Cookie cookie, House house) {
        try {
            if (!dataLayer.matchUserToCookie(cookie, house.getOwnerId())) {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }
            return dataLayer.createHouse(house);
        } catch (DuplicateException e) {
            throw new WebApplicationException(Response.Status.CONFLICT);
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
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

    //TODO {id}/param loc = etv
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<House> getLocationHouses(@QueryParam("location") String location) {
        if (location != null) {
            try {
                return dataLayer.getLocationHouses(location);
            } catch (Exception e) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
        } else
            return dataLayer.getDiscountHouses();

    }

    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public House updateHouse(@CookieParam("scc:session") Cookie cookie, @PathParam("id") String id, House house) {
        try {
            if (dataLayer.matchUserToCookie(cookie, house.getOwnerId())) {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }
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
            // TODO how verify if user is owner
            return dataLayer.deleteHouse(id);
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    // QUESTIONS

    @POST
    @Path("/{id}/questions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Question createQuestion(@CookieParam("scc:session") Cookie cookie, @PathParam("id") String id, Question question) {
        try {
            if (dataLayer.matchUserToCookie(cookie, question.getAuthorId())) {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }
            return dataLayer.createQuestion(id, question);
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (ForbiddenException e) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        } catch (DuplicateException e) {
            throw new WebApplicationException(Response.Status.CONFLICT);
        }
    }

    @GET
    @Path("/{id}/questions")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Question> listQuestion(@PathParam("id") String id) {
        try {
            return dataLayer.listQuestions(id);
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
	
	// RENTALS

    @GET
    @Path("/{id}/rentals")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Rental> listRentals(@PathParam("id") String id) {
        try {
            return dataLayer.listRentals(id);
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @POST
    @Path("/{id}/rentals/availability") // availability post
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Rental createRental(@PathParam("id") String id, Rental rental) {
        try {

            return dataLayer.createAvailable(id, rental);
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (ForbiddenException e) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
    }


    @PATCH
    @Path("/{id}/rentals/{rentalId}") // any rental
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Rental updateRental(@PathParam("id") String id, @PathParam("rentalId") String rentalId, Rental rental) {
        try {
            return dataLayer.updateRental(id, rentalId, rental);
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (ForbiddenException e) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
    }


    //TODO post /{id}/rentals/{rentalId}/renter vou arrendar recebe rental ID
    @POST
    @Path("/{id}/rentals/{rentalId}/renter") // any rental
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Rental createRenter(@PathParam("id") String id, @PathParam("rentalId") String rentalId, Renter renter) {
        try {
            return dataLayer.createRent(id, rentalId, renter);
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

}
