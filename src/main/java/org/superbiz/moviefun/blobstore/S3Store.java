package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.superbiz.moviefun.blobstore.BlobStore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

public class S3Store implements BlobStore {

    private AmazonS3Client s3Client;
    private String photoStorageBucket;
    private final Tika tika = new Tika();


    public S3Store(AmazonS3Client s3Client, String photoStorageBucket) {
        this.s3Client = s3Client;
        this.photoStorageBucket = photoStorageBucket;
    }

    @Override
    public void put(Blob blob) throws IOException {
        s3Client.putObject(photoStorageBucket, blob.name, blob.inputStream, new ObjectMetadata());
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        if (!s3Client.doesObjectExist(photoStorageBucket, name)) {
            return Optional.empty();
        }

        S3Object s3Object = s3Client.getObject(photoStorageBucket, name);
        S3ObjectInputStream content = s3Object.getObjectContent();

        byte[] bytes = IOUtils.toByteArray(content);

        return Optional.of(new Blob(
                name,
                new ByteArrayInputStream(bytes),
                tika.detect(bytes)
        ));
    }

    @Override
    public void deleteAll() {
        System.out.println("Not Yet Implemented");
    }
}
