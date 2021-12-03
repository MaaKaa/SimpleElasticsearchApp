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
import java.util.Optional;

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

        if (results.isEmpty()) {
            return ResponseEntity.of(Optional.of(ErrorResponse.builder()
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .errorDescription("No record(s) found.").build()));
        }
        return ResponseEntity.ok(results);
    }
}
