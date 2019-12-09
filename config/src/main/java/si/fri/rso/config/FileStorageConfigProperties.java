package si.fri.rso.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigBundle("rest-config")
public class FileStorageConfigProperties {
    @ConfigValue(value = "service-available", watch = true)
    private Boolean serviceAvailable;

    public void setServiceAvailable(Boolean serviceAvailable) {
        this.serviceAvailable = serviceAvailable;
    }

    public Boolean getServiceAvailable() {
        return serviceAvailable;
    }
}
