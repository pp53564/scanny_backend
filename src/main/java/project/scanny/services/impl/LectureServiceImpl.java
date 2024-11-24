package project.scanny.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.scanny.dao.LectureRepository;
import project.scanny.dto.LectureDTO;
import project.scanny.mappers.LectureMapper;
import project.scanny.services.LectureService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LectureServiceImpl implements LectureService {
    private final LectureRepository lectureRepository;

    public LectureServiceImpl(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

    public List<LectureDTO> getAllLectures() {
        return lectureRepository.findAll().stream()
                .map(LectureMapper::toDTO)
                .collect(Collectors.toList());
    }
}
