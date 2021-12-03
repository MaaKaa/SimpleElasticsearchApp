package pl.marzenakaa.SimpleElasticsearchApp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import pl.marzenakaa.SimpleElasticsearchApp.document.User;
import pl.marzenakaa.SimpleElasticsearchApp.model.SearchCriteria;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    private final QueryBuilderService queryBuilderService;

    private static final String INDEX_NAME = "user";

    /**
     * Method searches for User data on the basis of search criteria.
     */
    public List<User> findBySearchCriteria(SearchCriteria searchCriteria) {
        List<User> users = new ArrayList<>();

        Query searchQuery = queryBuilderService.buildElasticsearchQuery(searchCriteria);
        SearchHits<User> userSearchHits = elasticsearchRestTemplate.search(searchQuery, User.class, IndexCoordinates.of(INDEX_NAME));

        if (userSearchHits != null && !userSearchHits.isEmpty()) {
            userSearchHits.getSearchHits().forEach(p -> users.add(p.getContent()));
        }

        log.info("Searching by User: {} result(s) found.", users.size());
        return users;
    }

}
