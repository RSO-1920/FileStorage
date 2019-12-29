package grpc;

import beans.UserBean;
import com.kumuluz.ee.grpc.annotations.GrpcService;
import entity.User;
import io.grpc.stub.StreamObserver;

import javax.enterprise.inject.spi.CDI;
import java.util.logging.Logger;

@GrpcService
public class S3ServiceImpl extends S3Grpc.S3ImplBase {

    private static final Logger logger = Logger.getLogger(S3ServiceImpl.class.getName());
    // private UserBean userBean;

    @Override
    public void deleteFileOnBucket(S3Service.S3DeleteFileOnBucketRequest request, StreamObserver<S3Service.S3DeleteFileOnBucketResponse> responseObserver) {

        // userBean = CDI.current().select(UserBean.class).get();
        // User user = userBean.getUser(request.getId());

        System.out.println("GRPC request");
        System.out.println(request.getBucketname());
        System.out.println(request.getFilename());

        S3Service.S3DeleteFileOnBucketResponse response;

        response = S3Service.S3DeleteFileOnBucketResponse.newBuilder()
                .setResponsemsg("success")
                .setResponsestatus(true)
                .build();
        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }
}
