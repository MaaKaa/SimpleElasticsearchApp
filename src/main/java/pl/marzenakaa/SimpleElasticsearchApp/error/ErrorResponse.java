package pl.marzenakaa.SimpleElasticsearchApp.error;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
public class ErrorResponse {
    
    private HttpStatus httpStatus;
    private String errorDescription;
}
