package pl.marzenakaa.SimpleElasticsearchApp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.marzenakaa.SimpleElasticsearchApp.document.User;
import pl.marzenakaa.SimpleElasticsearchApp.model.SearchCriteria;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final UserService userService;

    public List<User> search(SearchCriteria searchCriteria) {
        return userService.findBySearchCriteria(searchCriteria);
    }
}
