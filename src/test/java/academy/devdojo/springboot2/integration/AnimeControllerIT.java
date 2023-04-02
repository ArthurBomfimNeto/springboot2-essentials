package academy.devdojo.springboot2.integration;


import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.wrapper.PageableResponse;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

@Log4j2
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode =  DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // deleta tudo e recria a cada metodo é lento se tratar da para tirar
public class AnimeControllerIT {
    @Autowired
    private TestRestTemplate testRestTemplate;
    @LocalServerPort
    private int port ;
    @Autowired
    private  AnimeRepository animeRepository;


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

    @Test
    @DisplayName("list returns list Anime when successful")
    void list_ReturnsListOfAnimes_WhenSuccessful(){

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        String expectedName = savedAnime.getName();

        ResponseEntity<List<Anime>> response = testRestTemplate.exchange("/animes/all", HttpMethod.GET, null, new ParameterizedTypeReference<List<Anime>>() {});

        log.info(response);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getBody().get(0).getName() , expectedName);
    }

    @Test
    @DisplayName("findById returns anime when successful")
    void findById_ReturnsAnime_WhenSuccessful(){

        Anime savedanime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        Long expected_id = savedanime.getId();

        Anime animeReponse = testRestTemplate.getForObject("/animes/{id}", Anime.class, expected_id);

        Assertions.assertNotNull(animeReponse);
        Assertions.assertEquals(animeReponse.getId(), expected_id);
    }

    @Test
    @DisplayName("findByName returns a List of anime when successful")
    void findByName_ReturnsListOfAnime_WhenSuccessful(){
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        String expectedName = savedAnime.getName();

        String url = String.format("/animes/find?name=%s",expectedName);

        List<Anime> animeReponse = testRestTemplate.exchange(url,HttpMethod.GET,null,  new ParameterizedTypeReference<List<Anime>>(){}).getBody();

        Assertions.assertNotNull(animeReponse);
        Assertions.assertEquals(animeReponse.get(0).getName(), expectedName);
        Assertions.assertFalse(animeReponse.isEmpty());
    }

    @Test
    @DisplayName("findByName returns an empty List of anime when successful")
    void findByName_ReturnsEmptyListOfAnime_WhenSuccessful(){

        ResponseEntity<List<Anime>> animeResponse = testRestTemplate.exchange("/animes/find?name=abc", HttpMethod.GET, null, new ParameterizedTypeReference<List<Anime>>() {});

        Assertions.assertTrue(animeResponse.getBody().isEmpty());
    }


    @Test
    @DisplayName("Save returns anime when successful")
    void save_ReturnsAnime_WhenSuccessful(){

        AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimePostRequestBody();

        ResponseEntity<Anime> animeResponseEntity = testRestTemplate.postForEntity("/animes",animePostRequestBody, Anime.class);

        Assertions.assertNotNull(animeResponseEntity);
        Assertions.assertEquals(animeResponseEntity.getStatusCode(), HttpStatus.CREATED);
    }



    @Test
    @DisplayName("Save returns badRequestExceptions when Mapper is empty")
    void save_ReturnExceptionsBadRequest_WhenEntityEmpty() throws Exception{

        AnimePostRequestBody animePostRequestBody = new AnimePostRequestBody();

        ResponseEntity<Optional> animeResponseEntity = testRestTemplate.postForEntity("/animes",animePostRequestBody, Optional.class);


        Assertions.assertNotNull(animeResponseEntity);
        Assertions.assertEquals(animeResponseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
    }


    @Test
    @DisplayName("replace updates anime when successful")
    void replace_UpdatesAnime_WhenSuccessful(){


        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        savedAnime.setName("replace");

        ResponseEntity<Void> animeResponseEntity = testRestTemplate.exchange("/animes", HttpMethod.PUT, new HttpEntity<>(savedAnime), Void.class);

        Assertions.assertNotNull(animeResponseEntity);
        Assertions.assertEquals(animeResponseEntity.getStatusCode(), HttpStatus.NO_CONTENT);

    }

    @Test
    @DisplayName("delete remove anime when successful")
    void delete_DeleteAnime_WhenSuccessful(){
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        ResponseEntity<Void> animeResponseEntity = testRestTemplate.exchange("/animes/{id}", HttpMethod.DELETE, null, Void.class, savedAnime.getId());

        Assertions.assertNotNull(animeResponseEntity);
        Assertions.assertEquals(animeResponseEntity.getStatusCode(), HttpStatus.NO_CONTENT);

    }



}
