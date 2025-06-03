package project.scanny.mappers;

import project.scanny.dto.LectureDTO;
import project.scanny.models.Lecture;
import project.scanny.models.Question;
import project.scanny.requests.lecture.CreateLectureRequest;

import java.util.List;
import java.util.stream.Collectors;

public class LectureMapper {
    public static LectureDTO toDTO(Lecture lecture) {
        return new LectureDTO(
                lecture.getId(),
                lecture.getTitle(),
                lecture.getQuestions().stream()
                        .map(QuestionMapper::toDTO)
                        .collect(Collectors.toList())
        );
    }

    public static List<LectureDTO> toDTOList(List<Lecture> lectures) {
        return lectures.stream()
                .map(LectureMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static Lecture toEntity(CreateLectureRequest request) {
        Lecture lecture = new Lecture();
        lecture.setTitle(request.lectureName());

        List<Question> questions = request.items().stream().map(
                TranslatedItemDto -> QuestionMapper.toEntity(TranslatedItemDto, lecture)).toList();

        lecture.setQuestions(questions);
        return lecture;
    }
}
