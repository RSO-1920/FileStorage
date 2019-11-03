package si.fri.rso.fileStorage.services;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.enterprise.context.ApplicationScoped;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


@ApplicationScoped
public class fileStorageBean {
    @PostConstruct
    private void init() {
        System.out.println("Users service");
    }
}
