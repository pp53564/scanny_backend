package project.scanny.requests.lecture;

import project.scanny.dto.TranslatedItemDto;

import java.util.List;

public record CreateLectureRequest(
        String lectureName,
        List<TranslatedItemDto> items
) { }

