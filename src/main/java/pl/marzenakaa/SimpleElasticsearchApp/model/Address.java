package pl.marzenakaa.SimpleElasticsearchApp.model;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private Long id;
    private String street;
    private String buildingNumber;
    private String flatNumber;
    private String city;
    private String postalCode;
    private String postOffice;
    private String country;
    private String addressType;
}
