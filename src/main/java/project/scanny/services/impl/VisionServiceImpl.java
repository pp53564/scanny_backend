package project.scanny.services.impl;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project.scanny.services.VisionService;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class VisionServiceImpl implements VisionService {

    private final ImageAnnotatorSettings visionSettings;

//    public VisionServiceImpl() throws IOException {
//        String keyPath = "src/main/resources/credentials/vision-service-account.json";
//
//        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(keyPath))
//                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
//
//        this.visionSettings = ImageAnnotatorSettings.newBuilder()
//                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
//                .build();
//    }
public VisionServiceImpl() throws IOException {
    GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
            .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

    this.visionSettings = ImageAnnotatorSettings.newBuilder()
            .setCredentialsProvider(() -> credentials)
            .build();
}

    public List<EntityAnnotation> detectLabels(MultipartFile file) throws IOException {
        List<EntityAnnotation> labels = new ArrayList<>();
        byte[] imageBytes = file.getBytes();

        try (ImageAnnotatorClient visionClient = ImageAnnotatorClient.create(visionSettings)) {
            Image img = Image.newBuilder()
                    .setContent(ByteString.copyFrom(imageBytes))
                    .build();

            Feature feature = Feature.newBuilder()
                    .setType(Feature.Type.LABEL_DETECTION)
                    .build();

            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feature)
                    .setImage(img)
                    .build();

            List<AnnotateImageRequest> requests = List.of(request);
            BatchAnnotateImagesResponse response = visionClient.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            if (!responses.isEmpty()) {
                AnnotateImageResponse res = responses.getFirst();
                if (res.hasError()) {
                    throw new IOException("Vision API Error: " + res.getError().getMessage());
                }
                labels = res.getLabelAnnotationsList();
            }
        }
        return labels;
    }
}
