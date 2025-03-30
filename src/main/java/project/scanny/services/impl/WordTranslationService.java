package project.scanny.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WordTranslationService {
    private final Map<String, Map<String, String>> translations = new HashMap<>();

    @PostConstruct
    public void loadTranslations() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getClass().getResourceAsStream("/data/word_translations.json");
            TypeReference<Map<String, Map<String, String>>> typeRef = new TypeReference<>() {};
            Map<String, Map<String, String>> loadedData = mapper.readValue(is, typeRef);

            translations.putAll(loadedData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load translations from JSON", e);
        }
    }

    public String getTranslation(String baseEnglish, String langCode) {
        if ("en".equalsIgnoreCase(langCode)) {
            return baseEnglish;
        }
        return translations
                .getOrDefault(baseEnglish, Map.of())
                .getOrDefault(langCode, baseEnglish);
    }
}
