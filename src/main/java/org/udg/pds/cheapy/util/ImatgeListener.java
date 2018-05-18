package org.udg.pds.cheapy.util;

import io.minio.MinioClient;
import org.udg.pds.cheapy.model.Imatge;

import javax.inject.Inject;
import javax.persistence.PostRemove;
import javax.ws.rs.WebApplicationException;

public class ImatgeListener
{
    @Inject
    private Global global;

    @PostRemove
    public void removeFile(Imatge i)
    {
        String[] splittedUrl = i.getRuta().split("/");
        String filename = splittedUrl[splittedUrl.length - 1];

        MinioClient minioClient = global.getMinioClient();
        if (minioClient == null)
            throw new WebApplicationException("Minio client not configured");

        try {
            minioClient.statObject(global.getMinioBucket(), filename);
            minioClient.removeObject(global.getMinioBucket(), filename);
        } catch (Exception e) {
            throw new WebApplicationException("Error deleting file: " + e.getMessage());
        }
    }
}
