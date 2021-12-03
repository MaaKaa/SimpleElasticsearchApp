package pl.marzenakaa.SimpleElasticsearchApp.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import pl.marzenakaa.SimpleElasticsearchApp.model.Address;
import pl.marzenakaa.SimpleElasticsearchApp.model.AddressType;
import pl.marzenakaa.SimpleElasticsearchApp.model.SearchCriteria;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.elasticsearch.index.query.QueryBuilders.simpleQueryStringQuery;

@Slf4j
@Service
public class QueryBuilderService {

    private static final String NAME = "name";
    private static final String SURNAME = "lastName";
    private static final String ADDRESSES = "addresses";
    private static final String STREET = "addresses.street";
    private static final String BUILDING_NUMBER = "addresses.buildingNumber";
    private static final String FLAT_NUMBER = "addresses.flatNumber";
    private static final String CITY = "addresses.city";
    private static final String POSTAL_CODE = "addresses.postalCode";
    private static final String POST_OFFICE = "addresses.postOffice";
    private static final String COUNTRY = "addresses.country";
    private static final String ADDRESS_TYPE = "addresses.addressType";
    private static final String USER_STATUS = "status";
    private static final String USER_ACTIVE = "1";
    private static final int MIN_LENGTH = 2;

    /**
     * Method creates custom Elasticsearch query with filter - it searches for Users in status=active only.
     * @param searchCriteria - searching parameters.
     */
    public Query buildElasticsearchQuery(SearchCriteria searchCriteria) {

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(buildBoolQuery(searchCriteria))
                .withFilter(matchQuery(USER_STATUS, USER_ACTIVE));

        return nativeSearchQueryBuilder.build();
    }

    /**
     * Method creates custom query for every non null value.
     */
    protected BoolQueryBuilder buildBoolQuery(SearchCriteria searchCriteria) {
        String name = searchCriteria.getName();
        String surname = searchCriteria.getSurname();

        Address correspondenceAddress = searchCriteria.getCorrespondenceAddresses();
        Address headquartersAddress = searchCriteria.getHeadquartersAddresses();

        BoolQueryBuilder boolQuery = new BoolQueryBuilder();

        if (name != null) {
            boolQuery.must(createCustomMatchPhraseQuery(NAME, name));
        }

        if (surname != null) {
            boolQuery.must(createCustomMatchQuery(SURNAME, surname));
        }

        if (correspondenceAddress != null) {
            BoolQueryBuilder correspondenceQuery = buildAddressQuery(correspondenceAddress, AddressType.CORRESPONDENCE);
            boolQuery.must(new NestedQueryBuilder(ADDRESSES, correspondenceQuery, ScoreMode.Avg));
        }

        if (headquartersAddress != null) {
            BoolQueryBuilder headquartersQuery = buildAddressQuery(headquartersAddress, AddressType.HEADQUARTERS);
            boolQuery.must(new NestedQueryBuilder(ADDRESSES, headquartersQuery, ScoreMode.Avg));
        }

        log.info("QUERY: " + boolQuery);
        return boolQuery;
    }

    /**
     * Method creates custom query for every non null address' field.
     */
    protected BoolQueryBuilder buildAddressQuery(Address address, AddressType type) {
        String country = address.getCountry();
        String street = address.getStreet();
        String postalCode = address.getPostalCode();
        String city = address.getCity();
        String buildingNumber = address.getBuildingNumber();
        String flatNumber = address.getFlatNumber();
        String postOffice = address.getPostOffice();

        BoolQueryBuilder addressBoolQuery = new BoolQueryBuilder();

        if (country != null) {
            addressBoolQuery.must(matchQuery(COUNTRY, country));
        }
        if (street != null) {
            addressBoolQuery.must(createCustomStringQuery(STREET, street));
        }
        if (postalCode != null) {
            addressBoolQuery.must(createCustomStringQuery(POSTAL_CODE, postalCode));
        }
        if (postOffice != null) {
            addressBoolQuery.must(createCustomStringQuery(POST_OFFICE, postOffice));
        }
        if (city != null) {
            addressBoolQuery.must(createCustomStringQuery(CITY, city));
        }
        if (buildingNumber != null){
            addressBoolQuery.must(createCustomStringQueryForNumberFields(BUILDING_NUMBER, buildingNumber));
        }
        if (flatNumber != null){
            addressBoolQuery.must(createCustomStringQueryForNumberFields(FLAT_NUMBER, flatNumber));
        }

        addressBoolQuery.must(matchQuery(ADDRESS_TYPE, type));
        return addressBoolQuery;
    }

    private MatchPhraseQueryBuilder createCustomMatchPhraseQuery(String fieldName, String value) {
        return matchPhraseQuery(fieldName, value);
    }

    private MatchQueryBuilder createCustomMatchQuery(String fieldName, String value) {
        return matchQuery(fieldName, value);
    }

    private AbstractQueryBuilder<?> createCustomStringQuery(String field, String value) {
        return isValueInQuotes(value)
                ? simpleQueryStringQuery(value.replace("-", " ")).field(field)
                : queryStringQuery(prepareQueryWithPrefixAndSufix(value, true)).field(field).defaultOperator(Operator.OR);
    }

    private AbstractQueryBuilder<?> createCustomStringQueryForNumberFields(String field, String value) {
        return isValueInQuotes(value)
                ? simpleQueryStringQuery(value.replace("-", " ")).field(field)
                : queryStringQuery(prepareQueryWithPrefixAndSufix(value, false)).field(field).defaultOperator(Operator.OR);
    }

    /**
     * Helper method used to create a query in which every word is treated as it is a prefix, a sufix or a whole word.
     */
    private String prepareQueryWithPrefixAndSufix(String value, boolean checkLength) {
        List<String> words = Arrays.asList(value.split(" |-"));
        if (checkLength) {
            return words.stream()
                    .filter(this::isLengthCorrect)
                    .map(e -> "*" + e + "*")
                    .collect(Collectors.joining(" "));
        }
        return words.stream()
                .map(String::trim)
                .filter(e -> !e.isEmpty())
                .map(e -> "*" + e + "*")
                .collect(Collectors.joining(" "));
    }

    private boolean isValueInQuotes(String term) {
        return term.startsWith("\"") && term.endsWith("\"");
    }

    private boolean isLengthCorrect(String value) {
        return value.length() > MIN_LENGTH;
    }
}
