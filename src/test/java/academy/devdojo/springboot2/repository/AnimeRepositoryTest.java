package academy.devdojo.springboot2.repository;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.util.AnimeCreator;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

@DataJpaTest // Para realizar os testes
@DisplayName("Tests for Anime Repository")
class AnimeRepositoryTest {

    @Autowired
    private AnimeRepository animeRepository;

    @Test
    @DisplayName("Save creates anime when Successful")
    void save_CreateAnime_WhenSuccessful() {
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
        Anime animeSaved = this.animeRepository.save(animeToBeSaved);

        Assertions.assertNotNull(animeSaved);
        Assertions.assertNotNull(animeSaved.getId());
        Assertions.assertEquals(animeToBeSaved.getName(), animeSaved.getName());
    }

    @Test
    @DisplayName("Update anime when Successful")
    void save_UpdateAnime_WhenSuccessful() {
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
        Anime animeSaved = this.animeRepository.save(animeToBeSaved);

        animeSaved.setName("Anime update");
        Anime animeUpdate = this.animeRepository.save(animeSaved);

        Assertions.assertNotNull(animeUpdate);
        Assertions.assertEquals(animeUpdate.getName(), animeSaved.getName());
        Assertions.assertEquals(animeUpdate.getId(), animeSaved.getId());
    }

    @Test
    @DisplayName("Delete remove anime when Successful")
    void delete_RemoveAnime_WhenSuccessful() {
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
        Anime animeSaved = this.animeRepository.save(animeToBeSaved);

        this.animeRepository.deleteById(animeSaved.getId());
        Optional<Anime> animeNotFound = this.animeRepository.findById(animeSaved.getId());

        Assertions.assertTrue(animeNotFound.isEmpty());
    }

    @Test
    @DisplayName("Find by name return a List of animes when Successful")
    void fundByName_ReturnListAnime_WhenSuccessful() {
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
        Anime animeSaved = this.animeRepository.save(animeToBeSaved);

        List<Anime> listAnime = this.animeRepository.findByName(animeSaved.getName());

        Assertions.assertNotNull(listAnime);
        Assertions.assertFalse(listAnime.isEmpty());
        Assertions.assertTrue(listAnime.contains(animeSaved));
    }

    @Test
    @DisplayName("Find by name return empty list when not found")
    void fundByName_ReturnEmptyList_WhenNotFound() {
        List<Anime> listAnime = this.animeRepository.findByName("not found");

        Assertions.assertTrue(listAnime.isEmpty());
    }

//================= TEST DE EXCEÇÔES ===============================

    @Test
    @DisplayName("Save throw ConstraintViolationException when name is empty")
    void save_ThrowsConstraintViolationException_WhenNameIsEmpty() {
        Anime anime = new Anime();
        Assertions.assertThrows( ConstraintViolationException.class,() -> this.animeRepository.save(anime));

    }

}