# Simple Elasticsearch App

## Requirements
* Java 11
* Gradle 6.
* Running Elasticsearch engine 7.6.2 (version must be compatible with Spring Data Elasticsearch). Set up the Elasticsearch engine's hostname in **application.yml** in the **elasticsearch.host** section (e.g. localhost:9200).

##Set up
### Set up Elasticsearch
1. Install Elasticsearch.
   You can follow this tutorial: https://www.baeldung.com/spring-data-elasticsearch-tutorial:
````
docker run -d --name es762 -p 9200:9200 -e "discovery.type=single-node" elasticsearch:7.6.2
````
2. Start Elasticsearch: 
````
docker run elasticsearch:7.6.2
````
3. Configure Elasticsearch:
````
curl -XPUT -H "Content-Type: application/json" http://localhost:9200/_cluster/settings -d '{ "transient": { "cluster.routing.allocation.disk.threshold_enabled": false }'
curl -XPUT -H "Content-Type: application/json" http://localhost:9200/_all/_settings -d '{ "index.blocks.read_only_allow_delete": null}'
````
4. Check if it works:
````
http://localhost:9200/
````

5. Add Elasticsearch host to **application.yml**:
````
elasticsearch:
  host: localhost:9200 
````
6. One you run the app, it automatically creates an index in Elasticsearch. 
   To check if the index is created, run:
````
curl http://localhost:9200/user/_settings – displays index settings.
curl http://localhost:9200/user/_mapping – displays index mappings.
````

### Set up the app
1 Build the project:
````
./gradlew clean build
````
2 Run the app:
````
./gradlew bootRun
````
The application starts on port 8080.

###Load test data
1. Set the "test" profile in **application.yml**:
````
spring:
  profiles:
    active: test
````
2. Run the app:
````
./gradlew bootRun
````
3. Load test data:
````
curl http://localhost:8080/test/loadData
````
...or do it via Swagger:
http://localhost:8080/swagger-ui.html

4. Display Elasticsearch content:
````
curl -XPOST -H "Content-type: application/json" 'http://localhost:9200/_search?pretty=true'
````
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