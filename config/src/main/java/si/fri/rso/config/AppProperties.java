package si.fri.rso.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigBundle("app-properties")
public class AppProperties {

    @ConfigValue(value = "aws.s3.access-key", watch = true)
    private String awsAccessKey = "a";

    @ConfigValue(value = "aws.s3.secret-key", watch = true)
    private String awsSecretKey;

    public String getAwsSecretKey() {
        return awsSecretKey;
    }

    public void setAwsSecretKey(String AWSSecretKey) {
        this.awsSecretKey = AWSSecretKey;
    }

    public String getAwsAccessKey() {
        return awsAccessKey;
    }

    public void setAwsAccessKey(String AWSAccessKey) {
        this.awsAccessKey = AWSAccessKey;
    }
}

