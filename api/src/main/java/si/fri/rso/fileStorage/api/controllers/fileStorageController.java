package si.fri.rso.fileStorage.api.controllers;

import javax.ws.rs.PathParam;
//import org.glassfish.jersey.media.multipart.FormDataParam;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
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

    @Context
    protected UriInfo uriInfo;

    @Operation(description = "Returns list all AWS S3 buckets.", summary = "Bucket list", tags = "buckets", responses = {
            @ApiResponse(responseCode = "200",
                    description = "List of buckets",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = Bucket.class)))
            )
    })
    @GET
    public Response getBuckets() {
        List<Bucket> buckets = fileStorage.listBuckets();
        return Response.status(Response.Status.OK).entity(buckets).build();
    }

    @Operation(description = "Returns list all files in the given AWS S3 bucket.", summary = "File list", tags = "files", responses = {
            @ApiResponse(responseCode = "200",
                    description = "List of files",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ObjectListing.class)))
            ),
            @ApiResponse(responseCode = "404",
                    description = "Bucket not found",
                    content = @Content(schema = @Schema(implementation = Error.class))
            )
    })
    @GET
    @Path("{bucketName}")
    public Response getFile(@PathParam("bucketName") String bucketName) {
        ObjectListing files = fileStorage.listAllFiles(bucketName);
        if(files == null)
            return Response.status(Response.Status.NOT_FOUND).entity("no bucket with the given name").build();
        else
            return Response.status(Response.Status.OK).entity(files).build();
    }

    @Operation(description = "Creates an AWS S3 bucket with the given name.", summary = "Create bucket", tags = "create, bucket", responses = {
            @ApiResponse(responseCode = "200",
                    description = "Created bucket",
                    content = @Content(schema = @Schema(implementation = Bucket.class))
            )
    })
    @POST
    @Path("{bucketName}")
    public Response createBucket(@PathParam("bucketName") String bucketName) {
        Bucket createdBucket = fileStorage.createBucket(bucketName);
        return Response.status(Response.Status.OK).entity(createdBucket).build();
    }

    @Operation(description = "Deletes the specified AWS S3 bucket.", summary = "Delete bucket", tags = "delete, bucket", responses = {
            @ApiResponse(responseCode = "200",
                    description = "Deleted file",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(responseCode = "404",
                    description = "File not found",
                    content = @Content(schema = @Schema(implementation = Error.class))
            )
    })
    @DELETE
    @Path("{bucketName}")
    public Response deleteBucket(@PathParam("bucketName") String bucketName) {
        boolean bucketeDeleated = fileStorage.deleteBucket(bucketName);
        if (bucketeDeleated)
            return Response.status(Response.Status.OK).entity("Deleted bucket " + bucketName).build();

        return Response.status(Response.Status.NOT_FOUND).entity("Failed to delete bucket " + bucketName +"!").build();
    }

    @Operation(description = "Get the specified file from AWS S3 bucket.", summary = "Get file", tags = "get, file", responses = {
            @ApiResponse(responseCode = "200",
                    description = "File",
                    content = @Content(schema = @Schema(implementation = S3ObjectInputStream.class))
            ),
            @ApiResponse(responseCode = "404",
                    description = "File not found",
                    content = @Content(schema = @Schema(implementation = Error.class))
            )
    })
    @GET
    @Path("{bucketName}/{fileName}")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response downloadFile(@PathParam("bucketName") String bucketName, @PathParam("fileName") String fileName) {
        S3ObjectInputStream outputStream = fileStorage.downloadFile(bucketName, fileName);
        return Response.status(outputStream == null ? Response.Status.NOT_FOUND : Response.Status.OK).entity(outputStream == null ? "Error: File " + bucketName + "/" + fileName + " not found" : outputStream).build();
    }

    @Operation(description = "Upload file to AWS S3 bucket.", summary = "Upload file", tags = "upload, file", responses = {
            @ApiResponse(responseCode = "200",
                    description = "Confirmation",
                    content = @Content(schema = @Schema(implementation = String.class))
            )
    })
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

    @Operation(description = "Delete the specified file from AWS S3 bucket.", summary = "Delete file", tags = "delete, file", responses = {
            @ApiResponse(responseCode = "200",
                    description = "Confirmation",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(responseCode = "404",
                    description = "File not found",
                    content = @Content(schema = @Schema(implementation = Error.class))
            )
    })
    @DELETE
    @Path("{bucketName}/{fileName}")
    public Response deleteFile(@PathParam("bucketName") String bucketName, @PathParam("fileName") String fileName) {
        boolean deleted = fileStorage.deleteFile(bucketName, fileName);
        return Response.status(deleted ? Response.Status.OK : Response.Status.NOT_FOUND).entity("file " + bucketName + "/" + fileName + " deleted: " + (deleted ? "successfully" : "unsuccessfully")).build();
    }

}
