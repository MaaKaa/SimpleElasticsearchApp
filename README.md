# Simple Elasticsearch App

## Requirements
* Java 11
* Gradle 6.
* Running Elasticsearch engine 7.6.2 (version must be compatible with Spring Data Elasticsearch). Set up the Elasticsearch engine's hostname in **application.yml** in the **elasticsearch.host** section (e.g. localhost:9200).

## Set up
1 Building the project:
````
./gradlew clean build
````
2 Running the application:
````
./gradlew bootRun
````
The application starts on port 8080.

## Elasticsearch
The application uses [Spring Data Elasticsearch](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#preface)

There are several ways to create queries:
- creating queries based on method names [[read more]](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#elasticsearch.query-methods.criterions)
- using the @Query [[read more]](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#elasticsearch.query-methods.at-query) annotation. You can test queries by sending requests in json to Elasticsearch engine's /_search?pretty=true (e.g. GET http://172.31.254.14:9200/_search?pretty=true)
- for more complex queries: using [Queries interface](https://docs.spring.io/spring-data/elasticsearch/docs/current-SNAPSHOT/reference/html/#elasticsearch.operations.searchresulttypes) and the [ElasticsearchRestTemplate](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#elasticsearch.operations.resttemplate)

(Note: basic CRUD methods are available through the ElasticsearchRepository extension)

## Testing
Testing endpoint **/api/search**

Using Postman:
- ensure that the Elasticsearch engine is up and running.
- run the application.
- create a new POST request http://localhost:9002/api/search
- set the headers:
    - Content-type: application/json,
- Set body, e.g:

````
{
    "name": "Jan",
    "surname": "Kowalski",
    "address":
      {
        "street": "Mickiewicza",
        "buildingNumber": "9",
        "flatNumber": "5",
        "city": "Warszawa",
        "postalCode": "00-100",
        "postOffice": "Warszawa",
        "country": "POLSKA"
      }
}
````