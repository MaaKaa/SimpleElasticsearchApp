package pl.marzenakaa.SimpleElasticsearchApp.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import pl.marzenakaa.SimpleElasticsearchApp.model.Address;

import java.util.List;

import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

/**
 * Data model used by the Elasticsearch engine to create "User" index.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "user", createIndex = true)
@Setting(settingPath = "/asciiAnalyzer.json")
public class User {

    @Id
    @Field(type = Text)
    private String id;

    @Field(type = Text, analyzer = "asciifolding_analyzer")
    private String name;

    @Field(type = Text, analyzer = "asciifolding_analyzer")
    private String surname;

    @Field(type = Text)
    private String status;

    @Field(type = FieldType.Nested, includeInParent = true)
    private List<Address> addresses;
}


