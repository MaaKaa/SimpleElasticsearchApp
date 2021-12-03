package pl.marzenakaa.SimpleElasticsearchApp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import pl.marzenakaa.SimpleElasticsearchApp.document.User;
import pl.marzenakaa.SimpleElasticsearchApp.model.Address;
import pl.marzenakaa.SimpleElasticsearchApp.model.AddressType;
import pl.marzenakaa.SimpleElasticsearchApp.model.SearchCriteria;
import pl.marzenakaa.SimpleElasticsearchApp.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
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

        log.info("{} result(s) found.", users.size());
        return users;
    }

    /**
     * Method adds some test data do Elasticsearch.
     */
    public void loadTestData() {
        Address correspondence1 = Address.builder()
                .addressType(AddressType.CORRESPONDENCE.name())
                .street("Marszałkowska")
                .buildingNumber("1")
                .flatNumber("1")
                .city("Warszawa")
                .postalCode("00-001")
                .postOffice("Warszwa")
                .country("Polska")
                .build();
        Address correspondence2 = Address.builder()
                .addressType(AddressType.CORRESPONDENCE.name())
                .street("Marszałkiniowa")
                .buildingNumber("10")
                .flatNumber("11")
                .city("Warszawa")
                .postalCode("00-002")
                .postOffice("Warszwa")
                .country("Polska")
                .build();
        Address headquarters = Address.builder()
                .addressType(AddressType.HEADQUARTERS.name())
                .street("Karolkowa")
                .buildingNumber("20")
                .flatNumber("44")
                .city("Warszawa")
                .postalCode("00-202")
                .postOffice("Gózd")
                .country("Polska")
                .build();

        User activeUser1 = User.builder()
                .name("Jan")
                .surname("Kowalski")
                .status("1")
                .addresses(List.of(correspondence1))
                .build();
        User activeUser2 = User.builder()
                .name("Grażyna")
                .surname("Kowalska")
                .status("1")
                .addresses(List.of(headquarters))
                .build();
        User activeUser3 = User.builder()
                .name("Bożena Anna")
                .surname("Barszcz Żółkiewska")
                .status("1")
                .addresses(List.of(correspondence2))
                .build();
        User inactiveUser1 = User.builder()
                .name("Janusz")
                .surname("Nowak")
                .status("0")
                .addresses(null)
                .build();

        userRepository.saveAll(List.of(activeUser1, inactiveUser1, activeUser2, activeUser3));
    }

    public void deleteTestData() {
        userRepository.deleteAll();
    }
}
