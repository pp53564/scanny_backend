package project.scanny.services;

import java.io.IOException;
import java.util.List;

import com.google.cloud.vision.v1.EntityAnnotation;
import org.springframework.web.multipart.MultipartFile;

public interface VisionService {
    List<EntityAnnotation> detectLabels(MultipartFile file) throws IOException;
}
