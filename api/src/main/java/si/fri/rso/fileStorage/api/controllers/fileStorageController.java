package si.fri.rso.fileStorage.api.controllers;

import javax.ws.rs.PathParam;
//import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

import si.fri.rso.fileStorage.services.fileStorageBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

@ApplicationScoped
@Path("/fileTransfer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class fileStorageController {

    @Inject
    private fileStorageBean fileStorage;

    @GET
    @Path("getFile")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response getFile(@FormDataParam("fileIdentifier") String fileIdentifier) {
        return Response.status(Response.Status.OK).entity("returning: " + fileIdentifier).build();
    }

    @POST
    @Path("storeFile")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response storeFile(@FormDataParam("fileStream") InputStream fileStream) {
        return Response.status(Response.Status.OK).entity(fileStream).build();
    }

    @DELETE
    @Path("deleteFile")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response storeFile(@FormDataParam("fileIdentifier") String fileIdentifier) {
        return Response.status(Response.Status.OK).entity("Delted file " + fileIdentifier).build();
    }
}
