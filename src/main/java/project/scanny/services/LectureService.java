package project.scanny.services;

import project.scanny.dto.LectureDTO;
import project.scanny.models.Lecture;

import java.util.List;

public interface LectureService {
    List<LectureDTO> getAllLectures();
}
