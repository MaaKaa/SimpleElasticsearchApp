# Simple Elasticsearch App
It a asimple project created to test Spring Data Elasticsearch in practice.

## Table of contents
* [Requirements](#requirements)
* [Setup](#setup)
* [Setup](#setup)
* [Elasticsearch](#elasticsearch)
* [Testing](#testing)
* [Status](#status)
* [Contact](#contact)

## Requirements
* Java 11
* Gradle 6.
* Running Elasticsearch engine 7.6.2 (version must be compatible with Spring Data Elasticsearch). Set up the Elasticsearch engine's hostname in **application.yml** in the **elasticsearch.host** section (e.g. localhost:9200).

## Setup
### Setup Elasticsearch
1. Install Elasticsearch.
   You can follow this tutorial: https://www.baeldung.com/spring-data-elasticsearch-tutorial:
````
docker run -d --name es762 -p 9200:9200 -e "discovery.type=single-node" elasticsearch:7.6.2
````
2. Start Elasticsearch: 
````
docker run elasticsearch:7.6.2
````
3. Check, if it works:
````
http://localhost:9200/
````
4. Configure Elasticsearch:
````
curl -XPUT -H "Content-Type: application/json" http://localhost:9200/_cluster/settings -d '{ "transient": { "cluster.routing.allocation.disk.threshold_enabled": false }'
curl -XPUT -H "Content-Type: application/json" http://localhost:9200/_all/_settings -d '{ "index.blocks.read_only_allow_delete": null}'
````
5. Add Elasticsearch host to **application.yml**:
````
elasticsearch:
  host: localhost:9200 
````
6. One you run the app, it automatically creates an index in Elasticsearch. 
   To check, if the index is created, run:
````
curl http://localhost:9200/user/_settings – displays index settings.
curl http://localhost:9200/user/_mapping – displays index mappings.
````

### Setup the app
1 Build the project:
````
./gradlew clean build
````
2 Run the app:
````
./gradlew bootRun
````
The application runs on port 8080.

### Load test data
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
- create a new POST request to: http://localhost:8080/api/search
- set the headers:
    - Content-type: application/json,
- Set body, e.g:

This is a correct request to find Jan Kowalski:
````
curl -XPOST -H "Content-type: application/json" -d '{"name": "Jan","surname": "Kowalski","correspondenceAddresses":{ "street":"Marszałkowska","buildingNumber":"1","flatNumber":"1","city":"Warszawa","postalCode":"00-001","postOffice":"Warszwa","country":"Polska"}
}' 'http://localhost:8080/api/search'
````

This will not work, because it searches for headquartes address (while Jan has only correspondence):
````
curl -XPOST -H "Content-type: application/json" -d '{"name": "Jan","surname": "Kowalski","headquartersAddresses":{ "street":"Marszałkowska","buildingNumber":"1","flatNumber":"1","city":"Warszawa","postalCode":"00-001","postOffice":"Warszwa","country":"Polska"}
}' 'http://localhost:8080/api/search'
````

This is a correct request to find Grażyna Kowalska:
````
curl -XPOST -H "Content-type: application/json" -d '{"name": "Grażyna","surname": "Kowalska","headquartersAddresses":{ "street":"Karolkowa","buildingNumber":"20","flatNumber":"44","city":"Warszawa","postalCode":"00-202","postOffice":"Gózd","country":"Polska"}
}' 'http://localhost:8080/api/search'
````

This way you can also find Grażyna, even if you use latin "z" instead of Polish character "ż" (thanks to ascii analyzer):
````
curl -XPOST -H "Content-type: application/json" -d '{"name": "Grazyna","surname": "Kowalska","headquartersAddresses":{ "street":"Karolkowa","buildingNumber":"20","flatNumber":"44","city":"Warszawa","postalCode":"00-202","postOffice":"Gózd","country":"Polska"}
}' 'http://localhost:8080/api/search'
````

This way you won't find Grażyna - there is a typo in her name:
````
curl -XPOST -H "Content-type: application/json" -d '{"name": "Grasyna","surname": "Kowalska","headquartersAddresses":{ "street":"Karolkowa","buildingNumber":"20","flatNumber":"44","city":"Warszawa","postalCode":"00-202","postOffice":"Gózd","country":"Polska"}
}' 'http://localhost:8080/api/search'
````

This way you will not find Janusz Nowak, because it is an inactive user (thus, will not pass the query filter):
````
curl -XPOST -H "Content-type: application/json" -d '{"name":"Janusz", "surname": "Nowak"}' 'http://localhost:8080/api/search'
````
This will return both Jan Kowalski and Bożena Barszcz - there is a full-text search, so search param "Marszałk" will find both "Marszałkowska" and "Marszałkini" street:
````
curl -XPOST -H "Content-type: application/json" -d '{"correspondenceAddresses":{ "street":"Marszałk","city":"Warszawa","postOffice":"Warszwa","country":"Polska"}
}' 'http://localhost:8080/api/search'
````

## Status
Project is: _finished_

## Contact
Created by [Marzena Kacprowicz](http://zrobtowinternecie.pl/) - feel free to contact me!