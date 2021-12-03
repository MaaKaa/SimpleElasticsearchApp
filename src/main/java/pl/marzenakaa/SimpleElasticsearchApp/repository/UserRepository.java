package pl.marzenakaa.SimpleElasticsearchApp.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import pl.marzenakaa.SimpleElasticsearchApp.document.User;

public interface UserRepository extends ElasticsearchRepository<User, String> {

}
