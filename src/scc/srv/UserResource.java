package scc.srv;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.NewCookie;
import scc.data.CacheLayer;
import scc.data.DataLayer;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import scc.entities.House.House;
import scc.entities.User.Auth;
import scc.entities.User.User;
import scc.exceptions.DuplicateException;
import scc.exceptions.NotFoundException;
import scc.data.Session;

import java.util.List;
import java.util.UUID;


@Path("/users")
public class UserResource {

    //CosmosDBLayer dataLayer;
    DataLayer dataLayer;

    public static final String SCC_SESSION = "scc:session";

    //private static final boolean DEBUG = System.getenv("DEBUG").equals("1"); //retirando esta linha e a de cima funciona

    public UserResource() {
    }
    
    public UserResource(DataLayer dm) {
        this.dataLayer = dm;
    }


    // for debug purposes
    /*@GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllUsers() {
        try {
            return dataLayer.getAllUsers();
        } catch (Exception e) {
            throw new WebApplicationException(Response.ok(e.getMessage()).status(Response.Status.CONFLICT).build());
        }
    }*/

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User createUser(User user) {
        try {
            return dataLayer.createUser(user);
        } catch (DuplicateException e) { // handle not found properly
            throw new WebApplicationException(Response.Status.CONFLICT);
        }catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (Exception e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.FORBIDDEN);
        }
    }

    @DELETE
    @Path("/{id}")
    public void deleteUser(@CookieParam(SCC_SESSION) Cookie cookie, @PathParam("id") String id) {

        try {
            if (!dataLayer.matchUserToCookie(cookie, id)) throw new WebApplicationException(Response.Status.UNAUTHORIZED);

            dataLayer.deleteUser(id, cookie);
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    // for debug purposes
    /*@GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser(@PathParam("id") String id) {
        try {
            return dataLayer.getUser(id);
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }*/

    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User updateUser(@CookieParam(SCC_SESSION) Cookie cookie, @PathParam("id") String id, User user) {
        try {
            if(!dataLayer.matchUserToCookie(cookie, id)) throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            return dataLayer.updateUser(id, user);
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @POST
    @Path("/auth")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response authUser(Auth auth) {
        try {
            if (dataLayer.verifyUser(auth)) {
                String token = UUID.randomUUID().toString();
                NewCookie cookie = new NewCookie("scc:session", token, "/", null, "sessionid", 3600, false, true);

               dataLayer.bindCookie(cookie, auth.getId());
               // boolean received = s != null;


                return Response.ok().cookie(cookie).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } catch (Exception e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.FORBIDDEN);
        }
    }

    @GET
    @Path("/{id}/houses")
    @Produces(MediaType.APPLICATION_JSON)
    public List<House> getUserHouses(@PathParam("id") String id) {
        try {
            return dataLayer.getUserHouses(id);
        } catch (NotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }





}
