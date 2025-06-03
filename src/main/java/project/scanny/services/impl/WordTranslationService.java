package project.scanny.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import project.scanny.dto.TranslatedItemDto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@Slf4j
public class WordTranslationService {

    @Value("${word-translations.path}")
    private String jsonFilePath;

    private final Map<String, Map<String, String>> translations = new HashMap<>();

    @PostConstruct
    public void loadTranslations() {
        try {
            Path path = Path.of(jsonFilePath);
            File file = path.toFile();

            if (!file.exists()) {
                file.getParentFile().mkdirs();
                boolean created = file.createNewFile();
                if (created) {
                    new ObjectMapper()
                            .writerWithDefaultPrettyPrinter()
                            .writeValue(file, Map.of());
                    log.info("Created new empty dictionary at {}", path);
                }
            }
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<Map<String, Map<String, String>>> typeRef = new TypeReference<>() {};
            Map<String, Map<String, String>> loadedData = mapper.readValue(file, typeRef);

            translations.putAll(loadedData);
            log.info("Loaded {} words from {}", translations.size(), path);
        } catch (IOException e) {
            log.error("Failed to load translations from '{}': {}", jsonFilePath, e.getMessage(), e);
            throw new RuntimeException("Cannot read word_translations.json", e);
        }
    }

    public String getTranslation(String baseEnglish, String langCode) {
        if ("en".equalsIgnoreCase(langCode)) {
            return baseEnglish;
        }
        Map<String, String> innerMap = translations.get(baseEnglish.toLowerCase(Locale.ROOT));
        return (innerMap != null && innerMap.containsKey(langCode))
                ? innerMap.get(langCode)
                : baseEnglish;
    }

    public synchronized void upsertTranslation(TranslatedItemDto item) {
        String key = item.translations().get("en").trim().toLowerCase(Locale.ROOT);

        translations.compute(key, (k, existing) -> {
            Map<String,String> merged = (existing == null)
                    ? new HashMap<>()
                    : new HashMap<>(existing);
            merged.putAll(item.translations());
            return merged;
        });

        try {
            Path path = Path.of(jsonFilePath);
            File file = path.toFile();
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, translations);
            log.info("Upserted '{}' → {} at {} ({}).",
                    key,
                    item.translations(),
                    file.getAbsolutePath(),
                    Instant.now());
        } catch (IOException e) {
            log.error("Failed to write translations to '{}': {}", jsonFilePath, e.getMessage(), e);
        }
    }
}
