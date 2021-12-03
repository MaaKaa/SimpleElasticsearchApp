package pl.marzenakaa.SimpleElasticsearchApp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.marzenakaa.SimpleElasticsearchApp.document.User;
import pl.marzenakaa.SimpleElasticsearchApp.model.SearchCriteria;
import pl.marzenakaa.SimpleElasticsearchApp.service.SearchService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

public class SearchControllerTest {

    @Mock
    private SearchService searchService;

    private SearchController controller;

    private SearchCriteria searchCriteria;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = new SearchController(searchService);
        searchCriteria = SearchCriteria.builder().build();
    }

    @Test
    public void search_shouldReturn200_whenSearchResultsGiven() {
        //given
        given(searchService.search(searchCriteria)).willReturn(List.of(User.builder().build()));
        //when
        ResponseEntity<?> result = controller.search(searchCriteria);
        //then
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void search_shouldReturn404_whenSearchResultIsNull() {
        //given
        given(searchService.search(searchCriteria)).willReturn(null);
        //when
        ResponseEntity<?> result = controller.search(searchCriteria);
        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void search_shouldReturn404_whenSearchResultsIsEmpty() {
        //given
        given(searchService.search(searchCriteria)).willReturn(List.of());
        //when
        ResponseEntity<?> result = controller.search(searchCriteria);
        //then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }
}