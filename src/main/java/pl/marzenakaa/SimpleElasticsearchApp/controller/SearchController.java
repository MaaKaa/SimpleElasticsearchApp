package pl.marzenakaa.SimpleElasticsearchApp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.marzenakaa.SimpleElasticsearchApp.document.User;
import pl.marzenakaa.SimpleElasticsearchApp.error.ErrorResponse;
import pl.marzenakaa.SimpleElasticsearchApp.model.SearchCriteria;
import pl.marzenakaa.SimpleElasticsearchApp.service.SearchService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchCriteria searchCriteria) {
        log.info("New request to /search endpoint...");
        List<User> results = searchService.search(searchCriteria);

        if (results == null || results.isEmpty()) {
            return new ResponseEntity<>(ErrorResponse.builder()
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .errorDescription("No record(s) found.").build(), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(results);
    }
}
