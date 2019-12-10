package si.fri.rso.fileStorage.api;

import com.kumuluz.ee.discovery.annotations.RegisterService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RegisterService(value = "rso1920-fileStorage")
@OpenAPIDefinition(info = @Info(title = "FileStorage Rest API", version = "v1", contact = @Contact(), license = @License(), description = "File storage API that comunicates with AWS S3"), servers = @Server(url ="http://localhost:8081/v1"))
@ApplicationPath("/v1")
public class fileStorageApi extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> resources = new HashSet<Class<?>>();
        resources.add(MultiPartFeature.class);
        resources.add(si.fri.rso.fileStorage.api.controllers.fileStorageController.class);
        return resources;
    }
}
