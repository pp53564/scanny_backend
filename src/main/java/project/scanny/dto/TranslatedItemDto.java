package project.scanny.dto;

import java.util.Map;

public record TranslatedItemDto(
        String base,
        Map<String,String> translations
) { }
