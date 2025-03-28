package project.scanny.services;

import project.scanny.dto.LectureDTO;
import project.scanny.dto.UserLectureDTO;

import java.util.List;

public interface LectureService {
    List<LectureDTO> getAllLectures();
    List<UserLectureDTO> getAllUserLectures(Long userId);
    List<UserLectureDTO> getAllUserLanguageLectures(Long userId, String selectedLangCode);
}
