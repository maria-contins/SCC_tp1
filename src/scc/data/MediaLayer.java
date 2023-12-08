package scc.data;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;

import java.io.File;
import java.nio.file.Files;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

public class MediaLayer {
    public enum StorageType{
        BLOBSTORE,
        FILESYSTEM
    }

    private final StorageType storageType;
    private BlobContainerClient containerClient;
    private String path;

    public MediaLayer(StorageType storageType) {
        this.storageType = storageType;
        switch (this.storageType){
            case BLOBSTORE:
                this.initBlobStore();
            case FILESYSTEM:
                this.initFileSystem();
                break;
            default:
                throw new IllegalStateException("Unsupported storage type");
        }
    }



    //Filename can only contain alphanumeric characters and dashes, avoid users being able to access other files by using path as a filename (ex: ../../etc/passwd)
    private String sanitizeId(String id){
        return id.replaceAll("[^a-zA-Z0-9\\-]", "_");
    }

    private void initBlobStore() {
        String storageConnectionString = System.getenv("storageAccountConnectionString");
        String containerName = System.getenv("storageAccountContainerName");
        if (storageConnectionString == null || containerName == null)
            throw new IllegalStateException("Missing storage account configuration");
        this.containerClient = new BlobContainerClientBuilder()
                .connectionString(storageConnectionString)
                .containerName(containerName)
                .buildClient();
    }

    private void initFileSystem() {
        String path = System.getenv("storagePath");
        if (path == null)
            throw new IllegalStateException("Missing filesystem configuration");
        this.path = path;
    }

    private String getFSPath(String id){

        return this.path + (this.path.endsWith("/") ? id :  "/" + id);
    }


    /**
     * Writes media contents to storage
     * @param id media id
     * @param media media contens as byte array
     * @return true if successful, false otherwise
     */
    public boolean writeMedia(String id, byte[] media){
        String sanitizedId = this.sanitizeId(id);
        switch (storageType){
            case BLOBSTORE:
                try{
                    containerClient.getBlobClient(sanitizedId).upload(BinaryData.fromBytes(media));
                    return true;
                }catch (Exception e){
                    System.err.println(e.getMessage());
                    return false;
                }
            case FILESYSTEM:
                    try{
                        //As per documentation, if a file with the same name already exists, an exception is thrown
                        Files.write(new File(getFSPath(sanitizedId)).toPath(), media, CREATE_NEW);
                        return true;
                    } catch (Exception e){
                        System.err.println(e.getMessage());
                        return false;
                    }
            default:
                throw new IllegalStateException("Unsupported storage type");
        }
    }

    /**
     * Returns media contents as byte array, or <b>null</b> if it does not exist
     * @param id media id
     * @return media contents as byte array, or <b>null</b> if it does not exist
     */
    public byte[] readMedia(String id){
        String sanitizedId = this.sanitizeId(id);
        switch (storageType){
            case BLOBSTORE:
                try{
                    return this.containerClient.getBlobClient(sanitizedId).downloadContent().toBytes();
                } catch (Exception e){
                    return null;
                }
            case FILESYSTEM:
                try{
                    return Files.readAllBytes(new File(getFSPath(sanitizedId)).toPath());
                } catch (Exception e){
                    return null;
                }
            default:
                throw new IllegalStateException("Unsupported storage type");
        }
    }


    /**
     * Checks if media exists, without reading it
     * @param id media id
     * @return true if media exists, false otherwise
     */
    public boolean mediaExists(String id){
        String sanitizedId = this.sanitizeId(id);
        switch (storageType){
            case BLOBSTORE:
                return this.containerClient.getBlobClient(sanitizedId).exists();
            case FILESYSTEM:
                File f = new File(getFSPath(sanitizedId));
                return f.exists() && !f.isDirectory();
            default:
                throw new IllegalStateException("Unsupported storage type");
        }
    }








}
