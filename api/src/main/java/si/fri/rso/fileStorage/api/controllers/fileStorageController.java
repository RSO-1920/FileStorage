package si.fri.rso.fileStorage.api.controllers;

import javax.ws.rs.PathParam;
//import org.glassfish.jersey.media.multipart.FormDataParam;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.glassfish.jersey.server.ResourceConfig;

import si.fri.rso.config.FileStorageConfigProperties;
import si.fri.rso.fileStorage.services.fileStorageBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
@Path("/fileTransfer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class fileStorageController {

    @Inject
    private fileStorageBean fileStorage;

    @Inject
    private FileStorageConfigProperties fileStorageConfigProperties;

    @GET
    public Response getFile() {
        List<Bucket> buckets = fileStorage.listBuckets();
        return Response.status(Response.Status.OK).entity(buckets).build();
    }

    @GET
    @Path("{bucketName}")
    public Response getFile(@PathParam("bucketName") String bucketName) {
        ObjectListing files = fileStorage.listAllFiles(bucketName);
        if(files == null)
            return Response.status(Response.Status.NOT_FOUND).entity("no bucket with the given name").build();
        else
            return Response.status(Response.Status.OK).entity(files).build();
    }

    @POST
    @Path("{bucketName}")
    public Response createBucket(@PathParam("bucketName") String bucketName) {
        Bucket createdBucket = fileStorage.createBucket(bucketName);
        return Response.status(Response.Status.OK).entity(createdBucket).build();
    }


    @DELETE
    @Path("{bucketName}")
    public Response deleteBucket(@PathParam("bucketName") String bucketName) {
        boolean bucketeDeleated = fileStorage.deleteBucket(bucketName);
        if (bucketeDeleated)
            return Response.status(Response.Status.OK).entity("Deleted bucket " + bucketName).build();

        return Response.status(Response.Status.OK).entity("Failed to delete bucket " + bucketName +"!").build();
    }

    @GET
    @Path("{bucketName}/{fileName}")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response downloadFile(@PathParam("bucketName") String bucketName, @PathParam("fileName") String fileName) {
        S3ObjectInputStream outputStream = fileStorage.downloadFile(bucketName, fileName);

        final MultiPart multiPart = new MultiPart();
        multiPart.bodyPart(new StreamDataBodyPart("fileStream", outputStream));
        return Response.status(Response.Status.OK).entity(multiPart).build();
    }

    @POST
    @Path("{bucketName}/{fileName}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@FormDataParam("fileStream") InputStream fileStream, @PathParam("bucketName") String bucketName, @PathParam("fileName") String fileName) throws InterruptedException {
        fileStorage.uploadFile(fileStream, bucketName, fileName);

        if (!this.fileStorageConfigProperties.getServiceAvailable() ) {
            TimeUnit.MINUTES.sleep(1);
        }

        return Response.status(Response.Status.OK).entity("ok").build();
    }

    @DELETE
    @Path("{bucketName}/{fileName}")
    public Response deleteFile(@PathParam("bucketName") String bucketName, @PathParam("fileName") String fileName) {
        boolean deleted = fileStorage.deleteFile(bucketName, fileName);
            return Response.status(Response.Status.OK).entity("file " + bucketName + "/" + fileName + " deleted: " + (deleted ? "successfully" : "unsuccessfully")).build();
    }

}
