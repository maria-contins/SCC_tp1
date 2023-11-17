package scc.azfunctions;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.BlobTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import src.main.java.scc.utils.Hash;

public class BlobReplicateRegion1 {
    String connectionString = System.getenv("storageAccountReg2");
    BlobContainerClient containerClient = new BlobContainerClientBuilder().connectionString(connectionString)
            .containerName("images").buildClient();

    @FunctionName("blobReplicateRegion1To2")
    public void setLastBlobInfo(@BlobTrigger(name = "BlobReplicateRegion1",
            dataType = "binary",
            path = "images/{name}",
            connection = "storageAccountReg1")
                                        byte[] content,
                                @BindingName("name") String blobname,
                                final ExecutionContext context) {

        String key = Hash.of(content);
        BlobClient blob = containerClient.getBlobClient(key);
        blob.upload(BinaryData.fromBytes(content));
    }
}
