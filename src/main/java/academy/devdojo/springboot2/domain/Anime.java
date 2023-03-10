package academy.devdojo.springboot2.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Anime {

    @Id // PRIMARY KEY
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    // @JsonProperty("name")  -- mapeia o json
    private String name;
    
}
