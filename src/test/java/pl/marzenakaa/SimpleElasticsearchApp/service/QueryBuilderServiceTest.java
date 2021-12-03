package pl.marzenakaa.SimpleElasticsearchApp.service;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.marzenakaa.SimpleElasticsearchApp.model.Address;
import pl.marzenakaa.SimpleElasticsearchApp.model.AddressType;
import pl.marzenakaa.SimpleElasticsearchApp.model.SearchCriteria;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class QueryBuilderServiceTest {

    private static final String MATCH_PHASE = "match_phrase";
    private static final String MATCH = "match";
    private static final String QUERY_STRING = "query_string";
    private static final String SIMPLE_QUERY_STRING = "simple_query_string";
    private static final String NAME = "name";
    private static final String NAME_VALUE = "Jan";
    private static final String LAST_NAME = "surname";
    private static final String LAST_NAME_VALUE = "Kowalski";
    private static final String COMPANY_NAME = "companyName";
    private static final String COMPANY_NAME_VALUE = "Kiosk";
    private static final String COUNTRY = "addresses.country";
    private static final String STREET = "addresses.street";
    private static final String STREET_VALUE = "Marszałkowska";
    private static final String CITY = "addresses.city";
    private static final String CITY_VALUE = "Bielsko-Biała";
    private static final String POSTAL_CODE = "addresses.postalCode";
    private static final String POSTAL_CODE_VALUE = "01-000";
    private static final String BUILDING_NUMBER = "addresses.buildingNumber";
    private static final String BUILDING_NUMBER_VALUE = "12";
    private static final String FLAT_NUMBER = "addresses.flatNumber";
    private static final String FLAT_NUMBER_VALUE = "1";
    private static final String POST_OFFICE = "addresses.postOffice";
    private static final String POST_OFFICE_VALUE = "Radom";
    private static final String ADDRESS_TYPE = "addresses.addressType";
    private static final String ADDRESS_TYPE_VALUE = "CORRESPONDENCE";
    private static final String POLSKA = "Polska";

    private QueryBuilderService queryBuilderService;

    @BeforeEach
    public void setUp() {
        queryBuilderService = new QueryBuilderService();
    }

    @Test
    public void buildElasticsearchQuery_shouldBuildQuery() {
        //given
        SearchCriteria searchCriteria = SearchCriteria.builder()
                .name(NAME_VALUE)
                .surname(LAST_NAME_VALUE)
                .build();

        //when
        Query result = queryBuilderService.buildElasticsearchQuery(searchCriteria);

        //then
        assertNotNull(result);
    }

    @Test
    public void buildBoolQuery_shouldCreateQueryForAllFieldsExcludingAddresses() {
        //given
        SearchCriteria searchCriteria = SearchCriteria.builder()
                .name(NAME_VALUE)
                .surname(LAST_NAME_VALUE)
                .correspondenceAddresses(createCorrespondenceAddress())
                .headquartersAddresses(createHeadquartersAddress())
                .build();

        //when
        BoolQueryBuilder result = queryBuilderService.buildBoolQuery(searchCriteria);

        //then
        assertTrue(result.toString().contains(createQuerySchema(MATCH_PHASE, NAME, NAME_VALUE)));
        assertTrue(result.toString().contains(createQuerySchema(MATCH, LAST_NAME, LAST_NAME_VALUE)));
        assertTrue(result.toString().contains("nested"));
        assertFalse(result.toString().contains(createQuerySchema(MATCH, COUNTRY, "PL")));
        assertFalse(result.toString().contains(createQueryStringSchema(QUERY_STRING, STREET, "*" + STREET_VALUE + "*")));
        assertFalse(result.toString().contains(createQueryStringSchema(QUERY_STRING, POSTAL_CODE, "*000*")));
        assertFalse(result.toString().contains(createQueryStringSchema(QUERY_STRING, CITY, "*Bielsko* *Biała*")));
        assertFalse(result.toString().contains(createQueryStringSchema(QUERY_STRING, BUILDING_NUMBER, "*" + BUILDING_NUMBER_VALUE + "*")));
        assertFalse(result.toString().contains(createQueryStringSchema(QUERY_STRING, FLAT_NUMBER, "*" + FLAT_NUMBER_VALUE + "*")));
        assertFalse(result.toString().contains(createQueryStringSchema(QUERY_STRING, POST_OFFICE, "*" + POST_OFFICE_VALUE + "*")));
        assertFalse(result.toString().contains(createQuerySchema(MATCH, ADDRESS_TYPE, ADDRESS_TYPE_VALUE)));
        assertFalse(result.toString().contains(createQueryStringSchema(QUERY_STRING, CITY, "*Jabłonna*")));
    }

    @Test
    public void buildBoolQuery_shouldCreateQueryStringQueryAndQueryWithPrefixAndSufix() {
        //given
        SearchCriteria esSearchCriteria = SearchCriteria.builder()
                .companyName(COMPANY_NAME_VALUE)
                .correspondenceAddresses(createCorrespondenceAddress())
                .build();

        //when
        BoolQueryBuilder result = queryBuilderService.buildBoolQuery(esSearchCriteria);

        //then
        assertTrue(result.toString().contains(createQueryStringSchema(QUERY_STRING, COMPANY_NAME, "*" + COMPANY_NAME_VALUE + "*")));
    }

    @Test
    public void buildBoolQuery_shouldCreateSimpleQueryStringWhenFieldsValuesInQuotes() {
        //given
        SearchCriteria esSearchCriteria = SearchCriteria.builder()
                .companyName("\""+ COMPANY_NAME_VALUE + "\"")
                .correspondenceAddresses(createCorrespondenceAddressWithValuesInQuotes())
                .build();

        //when
        BoolQueryBuilder result = queryBuilderService.buildBoolQuery(esSearchCriteria);

        //then
        assertTrue(result.toString().contains(createQueryStringSchema(SIMPLE_QUERY_STRING, COMPANY_NAME, "\\\"" + COMPANY_NAME_VALUE + "\\\"")));
    }

    @Test
    public void buildBoolQuery_shouldNotCreateQueryForAddressWhenNull() {
        //given
        SearchCriteria esSearchCriteria = SearchCriteria.builder()
                .name(NAME_VALUE)
                .correspondenceAddresses(null)
                .build();

        //when
        BoolQueryBuilder result = queryBuilderService.buildBoolQuery(esSearchCriteria);

        //then
        assertTrue(result.toString().contains(createQuerySchema(MATCH_PHASE, NAME, NAME_VALUE)));

        assertFalse(result.toString().contains(STREET));
        assertFalse(result.toString().contains(POSTAL_CODE));
        assertFalse(result.toString().contains(CITY));
        assertFalse(result.toString().contains(BUILDING_NUMBER));
        assertFalse(result.toString().contains(FLAT_NUMBER));
        assertFalse(result.toString().contains(POST_OFFICE));
        assertFalse(result.toString().contains(ADDRESS_TYPE));
    }

    @Test
    public void buildAddressQuery_shouldReturnAddressQueryAndQueryWithPrefixAndSufix() {
        //when
        BoolQueryBuilder result = queryBuilderService.buildAddressQuery(createCorrespondenceAddress(), AddressType.CORRESPONDENCE);

        //then
        assertFalse(result.toString().contains(createQueryStringSchema(QUERY_STRING, COMPANY_NAME, "*"+ COMPANY_NAME_VALUE +"*")));
        assertTrue(result.toString().contains(createQueryStringSchema(QUERY_STRING, STREET, "*" + STREET_VALUE + "*")));
        assertTrue(result.toString().contains(createQueryStringSchema(QUERY_STRING, POSTAL_CODE, "*000*")));
        assertTrue(result.toString().contains(createQueryStringSchema(QUERY_STRING, CITY, "*Bielsko* *Biała*")));
        assertTrue(result.toString().contains(createQueryStringSchema(QUERY_STRING, BUILDING_NUMBER, "*" + BUILDING_NUMBER_VALUE + "*")));
        assertTrue(result.toString().contains(createQueryStringSchema(QUERY_STRING, FLAT_NUMBER, "*" + FLAT_NUMBER_VALUE + "*")));
        assertTrue(result.toString().contains(createQueryStringSchema(QUERY_STRING, POST_OFFICE, "*" + POST_OFFICE_VALUE + "*")));
    }

    @Test
    public void buildAddressQuery_shouldReturnAddressQueryWhenFieldsValuesInQuotes() {
        //when
        BoolQueryBuilder result = queryBuilderService.buildAddressQuery(createCorrespondenceAddressWithValuesInQuotes(), AddressType.CORRESPONDENCE);

        //then
        assertFalse(result.toString().contains(createQueryStringSchema(SIMPLE_QUERY_STRING, COMPANY_NAME, "\\\"" + COMPANY_NAME_VALUE + "\\\"")));
        assertTrue(result.toString().contains(createQueryStringSchema(SIMPLE_QUERY_STRING, STREET, "\\\"" + STREET_VALUE + "\\\"")));
        assertTrue(result.toString().contains(createQueryStringSchema(QUERY_STRING, POSTAL_CODE, "*000*")));
        assertTrue(result.toString().contains(createQueryStringSchema(SIMPLE_QUERY_STRING, CITY, "\\\"Bielsko Biała\\\"")));
        assertTrue(result.toString().contains(createQueryStringSchema(QUERY_STRING, BUILDING_NUMBER, "*" + BUILDING_NUMBER_VALUE + "*")));
        assertTrue(result.toString().contains(createQueryStringSchema(QUERY_STRING, FLAT_NUMBER, "*" + FLAT_NUMBER_VALUE + "*")));
        assertTrue(result.toString().contains(createQuerySchema(MATCH, ADDRESS_TYPE, ADDRESS_TYPE_VALUE)));
    }

    private Address createCorrespondenceAddress() {
        return Address.builder()
                .country(POLSKA)
                .street(STREET_VALUE)
                .postalCode(POSTAL_CODE_VALUE)
                .city(CITY_VALUE)
                .buildingNumber(BUILDING_NUMBER_VALUE)
                .flatNumber(FLAT_NUMBER_VALUE)
                .postOffice(POST_OFFICE_VALUE)
                .addressType(AddressType.CORRESPONDENCE.name()).build();
    }

    private Address createCorrespondenceAddressWithValuesInQuotes() {
        return Address.builder()
                .country(POLSKA)
                .street("\"" + STREET_VALUE + "\"")
                .postalCode(POSTAL_CODE_VALUE)
                .city("\"" + CITY_VALUE + "\"")
                .buildingNumber(BUILDING_NUMBER_VALUE)
                .flatNumber(FLAT_NUMBER_VALUE)
                .postOffice(POST_OFFICE_VALUE)
                .addressType(AddressType.CORRESPONDENCE.name()).build();
    }

    private Address createHeadquartersAddress() {
        return Address.builder()
                .country(POLSKA)
                .city("Jabłonna")
                .addressType(AddressType.HEADQUARTERS.name()).build();
    }

    private String createQuerySchema(String queryType, String field, String value) {
        return "\""+ queryType + "\" : {\n" +
                "          \"" + field + "\" : {\n" +
                "            \"query\" : \"" + value + "\",";
    }

    private String createQueryStringSchema(String queryType, String field, String value) {
        return "\"" + queryType + "\" : {\n" +
                "          \"query\" : \"" + value + "\",\n" +
                "          \"fields\" : [\n" +
                "            \"" + field + "^1.0\"\n" +
                "          ],";
    }
}