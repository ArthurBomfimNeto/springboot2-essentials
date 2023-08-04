package academy.devdojo.springboot2.integration;


import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.domain.DevDojoUser;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.repository.DevDojoUserRepository;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.wrapper.PageableResponse;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

@Log4j2
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
// deleta tudo e recria a cada metodo é lento se tratar da para tirar
public class AnimeControllerIT {
    @Autowired
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate testRestTemplateRoleUser;

    @Autowired
    @Qualifier(value = "testRestTemplateRoleAdmin")
    private TestRestTemplate testRestTemplateRoleAdmin;

    @Autowired
    private AnimeRepository animeRepository;


    @Autowired
    private DevDojoUserRepository devDojoUserRepository;
    private static final DevDojoUser USER = DevDojoUser.builder()
            .name("Gabriel Bomfim")
            .password("$2a$10$GEyerjBzGbuYXoW1iaVYUeOQlmCi76C5ya/Wm3x9a3jNXSTKM8nES")
            .username("biel")
            .authorities("ROLE_USER")
            .build();

    private static final DevDojoUser ADMIN = DevDojoUser.builder()
            .name("Arthur Bomfim")
            .password("$2a$10$GEyerjBzGbuYXoW1iaVYUeOQlmCi76C5ya/Wm3x9a3jNXSTKM8nES")
            .username("turbas")
            .authorities("ROLE_USER,ROLE_ADMIN")
            .build();

    @TestConfiguration
    @Lazy
    static class Config {
        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("biel", "academy");
            return new TestRestTemplate(restTemplateBuilder);
        }
        @Bean(name = "testRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("turbas", "academy");
            return new TestRestTemplate(restTemplateBuilder);
        }
    }

    @Test
    @DisplayName("list returns list of anime inside page object when successful")
    //list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful ...
    void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        devDojoUserRepository.save(USER);

        String expectedName = savedAnime.getName();


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<PageableResponse<Anime>> response = testRestTemplateRoleUser.exchange("/animes?page=0&size=10&sort=name,asc", HttpMethod.GET, request, new ParameterizedTypeReference<PageableResponse<Anime>>() {
        });

        log.info(response);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertTrue(response.getBody().toList().get(0).getName().equals(expectedName));
    }

    @Test
    @DisplayName("list returns list Anime when successful")
    void list_ReturnsListOfAnimes_WhenSuccessful() {

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        devDojoUserRepository.save(USER);

        String expectedName = savedAnime.getName();

        ResponseEntity<List<Anime>> response = testRestTemplateRoleUser.exchange("/animes/all", HttpMethod.GET, null, new ParameterizedTypeReference<List<Anime>>() {
        });

        log.info(response);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getBody().get(0).getName(), expectedName);
    }

    @Test
    @DisplayName("findById returns anime when successful")
    void findById_ReturnsAnime_WhenSuccessful() {

        Anime savedanime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        devDojoUserRepository.save(USER);

        Long expected_id = savedanime.getId();

        Anime animeReponse = testRestTemplateRoleUser.getForObject("/animes/{id}", Anime.class, expected_id);

        Assertions.assertNotNull(animeReponse);
        Assertions.assertEquals(animeReponse.getId(), expected_id);
    }

    @Test
    @DisplayName("findByName returns a List of anime when successful")
    void findByName_ReturnsListOfAnime_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        devDojoUserRepository.save(USER);

        String expectedName = savedAnime.getName();

        String url = String.format("/animes/find?name=%s", expectedName);

        List<Anime> animeReponse = testRestTemplateRoleUser.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Anime>>() {
        }).getBody();

        Assertions.assertNotNull(animeReponse);
        Assertions.assertEquals(animeReponse.get(0).getName(), expectedName);
        Assertions.assertFalse(animeReponse.isEmpty());
    }

    @Test
    @DisplayName("findByName returns an empty List of anime when successful")
    void findByName_ReturnsEmptyListOfAnime_WhenSuccessful() {
        devDojoUserRepository.save(USER);
        ResponseEntity<List<Anime>> animeResponse = testRestTemplateRoleUser.exchange("/animes/find?name=abc", HttpMethod.GET, null, new ParameterizedTypeReference<List<Anime>>() {
        });

        Assertions.assertTrue(animeResponse.getBody().isEmpty());
    }


    @Test
    @DisplayName("Save returns anime when successful")
    void save_ReturnsAnime_WhenSuccessful() {

        AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimePostRequestBody();
        devDojoUserRepository.save(USER);

        ResponseEntity<Anime> animeResponseEntity = testRestTemplateRoleUser.postForEntity("/animes", animePostRequestBody, Anime.class);

        Assertions.assertNotNull(animeResponseEntity);
        Assertions.assertEquals(animeResponseEntity.getStatusCode(), HttpStatus.CREATED);
    }


    @Test
    @DisplayName("Save returns badRequestExceptions when Mapper is empty")
    void save_ReturnExceptionsBadRequest_WhenEntityEmpty() throws Exception {

        AnimePostRequestBody animePostRequestBody = new AnimePostRequestBody();
        devDojoUserRepository.save(USER);
        ResponseEntity<Optional> animeResponseEntity = testRestTemplateRoleUser.postForEntity("/animes", animePostRequestBody, Optional.class);


        Assertions.assertNotNull(animeResponseEntity);
        Assertions.assertEquals(animeResponseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
    }


    @Test
    @DisplayName("replace updates anime when successful")
    void replace_UpdatesAnime_WhenSuccessful() {


        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        devDojoUserRepository.save(USER);

        savedAnime.setName("replace");


        ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange("/animes", HttpMethod.PUT, new HttpEntity<>(savedAnime), Void.class);

        Assertions.assertNotNull(animeResponseEntity);
        Assertions.assertEquals(animeResponseEntity.getStatusCode(), HttpStatus.NO_CONTENT);

    }

    @Test
    @DisplayName("delete remove anime when successful")
    void delete_DeleteAnime_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        devDojoUserRepository.save(ADMIN);

        ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleAdmin.exchange("/animes/admin/{id}", HttpMethod.DELETE, null, Void.class, savedAnime.getId());

        Assertions.assertNotNull(animeResponseEntity);
        Assertions.assertEquals(animeResponseEntity.getStatusCode(), HttpStatus.NO_CONTENT);

    }

    @Test
    @DisplayName("delete returns 403 when user is not admin")
    void delete_Returns403_WhenUserIsNotAdmin() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        devDojoUserRepository.save(USER);

        ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange("/animes/admin/{id}", HttpMethod.DELETE, null, Void.class, savedAnime.getId());

        Assertions.assertNotNull(animeResponseEntity);
        Assertions.assertEquals(animeResponseEntity.getStatusCode(), HttpStatus.FORBIDDEN);

    }



}
