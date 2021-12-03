package pl.marzenakaa.SimpleElasticsearchApp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.marzenakaa.SimpleElasticsearchApp.document.User;
import pl.marzenakaa.SimpleElasticsearchApp.model.SearchCriteria;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final UserService userService;

    public List<User> search(SearchCriteria searchCriteria) {
        log.info("Request: " + convertToJson(searchCriteria));
        return userService.findBySearchCriteria(searchCriteria);
    }

    private String convertToJson(SearchCriteria searchCriteria) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.writeValueAsString(searchCriteria);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
