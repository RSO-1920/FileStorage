package si.fri.rso.fileStorage.services;
import si.fri.rso.config.AppProperties;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import si.fri.rso.config.FileStorageConfigProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@ApplicationScoped
public class fileStorageBean {

    @Inject
    private AppProperties appProperties;

    AWSCredentials credentials;
    AmazonS3 s3client;

    @PostConstruct
    void init() {
        try{
            credentials = new BasicAWSCredentials(
                appProperties.getAwsAccessKey(),
                appProperties.getAwsSecretKey()
            );

            s3client = AmazonS3ClientBuilder
                    .standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion(Regions.EU_WEST_1)
                    .build();

        } catch (Exception e) {
            throw new AmazonClientException("Cannot initialize credentials.", e);
        }
    }

    public fileStorageBean() {
    }

    Random randomNumberGenerator = new Random();

    public Bucket createBucket(String bucketName) {
        bucketName = bucketName.toLowerCase();
        while(s3client.doesBucketExistV2(bucketName)){
            bucketName += Integer.toString(randomNumberGenerator.nextInt(9));
        }
        Bucket createdBucket = s3client.createBucket(bucketName);
        return createdBucket;
    }

    public List<Bucket> listBuckets(){
        return s3client.listBuckets();
    }

    public boolean deleteBucket(String bucketName) {
        if(!s3client.doesBucketExistV2(bucketName))
            return false;
        try {
            s3client.deleteBucket(bucketName);
            return true;
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            return false;
        }
    }

    public boolean deleteFile(String bucketName, String fileName) {
        if(!s3client.doesBucketExistV2(bucketName))
            return false;
        s3client.deleteObject(bucketName, fileName);
        return true;
    }

    public String uploadFile(InputStream inputStream, String bucketName, String fileName) {
        s3client.putObject(
                bucketName,
                fileName,
                inputStream,
                new ObjectMetadata()
        );
        return fileName;
    }

    public S3ObjectInputStream downloadFile(String bucketName, String fileName) {
        S3Object s3object = s3client.getObject(bucketName, fileName);
        S3ObjectInputStream outputStream = s3object.getObjectContent();
        return outputStream;
    }

    public ObjectListing listAllFiles(String bucketName){
        if(s3client.doesBucketExistV2(bucketName))
            System.out.println("listAllFiles in bucket " + bucketName);
        else{
            System.out.println("no bucket named " + bucketName);
            return null;
        }
        ObjectListing objectListing = s3client.listObjects(bucketName);
        for(S3ObjectSummary os : objectListing.getObjectSummaries()) {
            System.out.println(os.getKey());
        }
        return objectListing;
    }

}
