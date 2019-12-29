package grpc;
import com.kumuluz.ee.grpc.annotations.GrpcService;
import io.grpc.stub.StreamObserver;
import si.fri.rso.fileStorage.services.fileStorageBean;

import javax.enterprise.inject.spi.CDI;
import java.util.logging.Logger;

@GrpcService
public class S3ServiceImpl extends S3Grpc.S3ImplBase {

    private static final Logger logger = Logger.getLogger(S3ServiceImpl.class.getName());
    private fileStorageBean fileStorage;

    @Override
    public void deleteFileOnBucket(S3Service.S3DeleteFileOnBucketRequest request, StreamObserver<S3Service.S3DeleteFileOnBucketResponse> responseObserver) {

        System.out.println("GRPC request");
        System.out.println(request.getBucketname());
        System.out.println(request.getFilename());

        fileStorage = CDI.current().select(fileStorageBean.class).get();
        boolean isDeleted = fileStorage.deleteFile(request.getBucketname(), request.getFilename());
        S3Service.S3DeleteFileOnBucketResponse response;
        response = S3Service.S3DeleteFileOnBucketResponse.newBuilder()
                .setResponsemsg("Deletion of " + request.getBucketname() + "/" + request.getFilename())
                .setResponsestatus(isDeleted)
                .build();
        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }
}
