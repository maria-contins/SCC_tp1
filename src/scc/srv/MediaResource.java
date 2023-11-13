package scc.srv;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import scc.utils.Hash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.core.MediaType;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;

/**
 * Resource for managing media files, such as images.
 */
@Path("/media")
public class MediaResource
{
    String storageConnectionString = System.getenv("storageAccountConnectionString");

    BlobContainerClient containerClient = new BlobContainerClientBuilder()
            .connectionString(storageConnectionString)
            .containerName("media")
            .buildClient();

    /**
     * Post a new image.The id of the image is its hash.
     */
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_JSON)
    public String upload(byte[] contents) {
        String key = Hash.of(contents);
        try {
            if (!containerClient.getBlobClient(key).exists()) {
                BlobClient blob = containerClient.getBlobClient(key);
                blob.upload(BinaryData.fromBytes(contents));
            }
            return key;
        } catch (Exception e) {
            throw new WebApplicationException(Response.Status.CONFLICT);
        }
    }

    /**
     * Return the contents of an image. Throw an appropriate error message if
     * id does not exist.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public byte[] download(@PathParam("id") String id) {
        try {
            BlobClient blob = containerClient.getBlobClient(id);
            return blob.downloadContent().toBytes();
        } catch (Exception e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    /**
     * Lists the ids of images stored.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> list() {
        ArrayList<String> blobs = new ArrayList<>();
        for (BlobItem item : containerClient.listBlobs()) {
            blobs.add(item.getName());
        }
        return blobs;
    }


}
