package academy.devdojo.springboot2.client;

import academy.devdojo.springboot2.domain.Anime;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Log4j2
public class SpringClient {
    public static void main(String[] args) {

        // ================= REQUEST GET ===========================
        //Restorna em forma de response com todos os parametros
        ResponseEntity<Anime> entity = new RestTemplate().getForEntity("http://localhost:8080/animes/{id}", Anime.class, 6);
        log.info(entity);

        //Retorna o objeto de resposta
        Anime object = new RestTemplate().getForObject("http://localhost:8080/animes/6", Anime.class);
        log.info(object);

        //Retorna um array estatico de Anime
        Anime[] animes = new RestTemplate().getForObject("http://localhost:8080/animes/all", Anime[].class);
        log.info(Arrays.toString(animes));

        //Retorna uma Lista(dinamica) de Anime
        ResponseEntity<List<Anime>> exchange = new RestTemplate().exchange("http://localhost:8080/animes/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Anime>>() {
                });
        log.info(exchange.getBody());

//================= REQUEST POST ============================
//        Anime kingdom = Anime.builder().name("kingdom").build();
//        Anime Responsekingdom = new RestTemplate().postForObject("http://localhost:8080/animes", kingdom, Anime.class);
//        log.info("reposme anime {}", Responsekingdom);

        Anime samuraiChamplo = Anime.builder().name("Samurai Champloo").build();
        ResponseEntity<Anime> ResponsesamuraiChamplo = new RestTemplate().exchange("http://localhost:8080/animes",
                HttpMethod.POST,
                new HttpEntity<>(samuraiChamplo, createJsonHeader()),
                Anime.class);
        log.info("response POST anime {}", ResponsesamuraiChamplo);


//================= REQUEST PUT ============================

      Anime  samuraiChamplo2 = ResponsesamuraiChamplo.getBody();
      samuraiChamplo2.setName("Samurai Champloo 2");

      ResponseEntity<Void> ResponsesamuraiChamplo2 = new RestTemplate().exchange("http://localhost:8080/animes",
              HttpMethod.PUT,
              new HttpEntity<>(samuraiChamplo2),
              Void.class);
      log.info("response PUT anime {}", ResponsesamuraiChamplo2);



//================= REQUEST DELETE ============================


        ResponseEntity<Void> ResponseDelete = new RestTemplate().exchange("http://localhost:8080/animes/{id}",
                HttpMethod.DELETE,
               null,
                Void.class,
                23);
        log.info("response DELETE anime {}", ResponseDelete);
    }
        private static HttpHeaders createJsonHeader() {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            return httpHeaders;
        }


}
