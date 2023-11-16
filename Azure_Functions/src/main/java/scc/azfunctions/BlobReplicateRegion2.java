package src.main.java.scc.serverless;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.BlobTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;

public class BlobReplicateRegion2 {
    String connectionString = System.getenv("storageAccountReg1");
    BlobContainerClient containerClient = new BlobContainerClientBuilder().connectionString(connectionString)
            .containerName("images").buildClient();

    @FunctionName("blobReplicateRegion2To1")
    public void setLastBlobInfo(@BlobTrigger(name = "BlobReplicateRegion2",
            dataType = "binary",
            path = "images/{name}",
            connection = "storageAccountReg2")
                                        byte[] content,
                                @BindingName("name") String blobname,
                                final ExecutionContext context) {

        String key = src.main.java.scc.utils.Hash.of(content);
        BlobClient blob = containerClient.getBlobClient(key);
        blob.upload(BinaryData.fromBytes(content));
    }
}
