package academy.devdojo.springboot2.integration;


import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.service.AnimeService;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.wrapper.PageableResponse;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@Log4j2
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
public class AnimeControllerIT {
    @Autowired
    private TestRestTemplate testRestTemplate;
    @LocalServerPort
    private int port ;
    @Autowired
    private  AnimeRepository animeRepository;




//    @Test
//    @DisplayName("list returns list Anime")
//    void list_ReturnsListOfAnimes_WhenSuccessful(){
//
//        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
//        String expectedName = savedAnime.getName();
//
//        ResponseEntity<List<Anime>> response = testRestTemplate.exchange("/animes/all", HttpMethod.GET, null, new ParameterizedTypeReference<List<Anime>>() {});
//
//        log.info(response);
//        Assertions.assertNotNull(response);
//        Assertions.assertEquals(response.getBody().get(0).getName() , expectedName);
//        Assertions.assertTrue(response.getBody().size() == 1);
//
//    }

    @Test
    @DisplayName("list returns list of anime inside page object when successful")
    void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful(){

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        String expectedName = savedAnime.getName();


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<PageableResponse<Anime>> response = testRestTemplate.exchange("/animes?page=0&size=10&sort=name,asc", HttpMethod.GET, request, new ParameterizedTypeReference<PageableResponse<Anime>>() {});

        log.info(response);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertTrue(response.getBody().toList().get(0).getName().equals(expectedName) );
    }


}
