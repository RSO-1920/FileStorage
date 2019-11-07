package si.fri.rso.fileStorage.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.enterprise.context.ApplicationScoped;
import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@ApplicationScoped
public class fileStorageBean {

    AWSCredentials credentials = new BasicAWSCredentials(
            "KIAZRMME2ZHWBOPRSXJ",
            "s6HGlvzFH/39QtCps2sBZ0FLIqt3YPguiIhaos+"
    );

    AmazonS3 s3client = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.EU_WEST_2)
            .build();

    public fileStorageBean() {
        System.out.println("init");
    }

    public boolean  createBucket(String bucketName) {
        if(!s3client.doesBucketExistV2(bucketName)) {
            s3client.createBucket(bucketName);
            return true;
        }
        return false;
    }

    public List<Bucket> getBuckets(){
        return s3client.listBuckets();
    }

    public boolean deleteBucket(String bucketName) {
        try {
            s3client.deleteBucket(bucketName);
            return true;
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            return false;
        }
    }

    public String storeObject(InputStream inputStream, String bucketName, String fileName) {
        s3client.putObject(
                bucketName,
                fileName,
                inputStream,
                new ObjectMetadata()
        );
        return fileName;
    }

}
