package pl.marzenakaa.SimpleElasticsearchApp.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SearchCriteria {

    private String id;
    private String name;
    private String surname;
    private String companyName;
    private String status;
    private Address correspondenceAddresses;
    private Address headquartersAddresses;
}
