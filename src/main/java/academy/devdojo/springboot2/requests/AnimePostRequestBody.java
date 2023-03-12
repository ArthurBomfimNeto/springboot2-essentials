package academy.devdojo.springboot2.requests;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.scheduling.support.SimpleTriggerContext;

@Data
@Builder
public class AnimePostRequestBody {
    @NotEmpty(message = "The anime name cannot be empty or null") // não aceita valor nullo
    private String name;

    @URL(message = "The URL is not valid")
    private String url;

}
