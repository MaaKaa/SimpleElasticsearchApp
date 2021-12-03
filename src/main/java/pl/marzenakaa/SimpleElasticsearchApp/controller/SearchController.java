package pl.marzenakaa.SimpleElasticsearchApp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.marzenakaa.SimpleElasticsearchApp.document.User;
import pl.marzenakaa.SimpleElasticsearchApp.model.SearchCriteria;
import pl.marzenakaa.SimpleElasticsearchApp.service.SearchService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/")
    public ResponseEntity<?> search(@RequestBody SearchCriteria searchCriteria) {
        List<User> results = searchService.search(searchCriteria);

        return ResponseEntity.ok(results);
    }
}
