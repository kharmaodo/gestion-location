package com.location.shared.storage;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetBucketPolicyArgs;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MinioStorageService {
    private static final Logger log = LoggerFactory.getLogger(MinioStorageService.class);
    private final MinioProperties props;
    private final MinioClient client;

    public MinioStorageService(MinioProperties props) {
        this.props = props;
        this.client = MinioClient.builder()
                .endpoint(props.getEndpoint())
                .credentials(props.getAccessKey(), props.getSecretKey())
                .build();
    }

    @PostConstruct
    void ensureBucket() {
        try {
            boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(props.getBucket()).build());
            if (!exists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(props.getBucket()).build());
            }
            String policy = """
                    {"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"AWS":["*"]},"Action":["s3:GetObject"],"Resource":["arn:aws:s3:::%s/*"]}]}
                    """.formatted(props.getBucket());
            client.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(props.getBucket()).config(policy).build());
        } catch (Exception e) {
            log.warn("MinIO non joignable ({}). Lancer: docker compose -f docker-compose.dev.yml up -d minio", props.getEndpoint());
        }
    }

    public String upload(String objectName, InputStream stream, long size, String contentType) {
        try {
            client.putObject(PutObjectArgs.builder()
                    .bucket(props.getBucket())
                    .object(objectName)
                    .stream(stream, size, -1)
                    .contentType(contentType == null ? "application/octet-stream" : contentType)
                    .build());
            return props.getPublicUrl().replaceAll("/$", "") + "/" + props.getBucket() + "/" + objectName;
        } catch (Exception e) {
            throw new IllegalStateException("upload MinIO impossible — docker compose up -d minio", e);
        }
    }
}
