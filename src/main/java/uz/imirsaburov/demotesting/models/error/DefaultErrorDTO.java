package uz.imirsaburov.demotesting.models.error;

import lombok.Data;
import org.springframework.http.HttpMethod;

@Data
public class DefaultErrorDTO {
    private String path;
    private String httpMethod;
    private String httpStatus;
    private Integer httpStatusCode;
    private Long timestamp;
    private String errorMessage;
    private String errorCode;
}
