package academy.devdojo.springboot2.service;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.exception.BadRequestException;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.util.AnimePutRequestBodyCreator;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class AnimeServiceTest {

    @InjectMocks
    private AnimeService animeService;

    @Mock
    private AnimeRepository animeRepositoryMock;

    @BeforeEach
    void setUp() {

        PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));
        BDDMockito.when(animeRepositoryMock.findAll(ArgumentMatchers.any(PageRequest.class))).thenReturn(animePage);

        BDDMockito.when(animeRepositoryMock.findAll()).thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString())).thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepositoryMock.save(ArgumentMatchers.any(Anime.class))).thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.doNothing().when(animeRepositoryMock).delete(ArgumentMatchers.any(Anime.class));
    }

    @Test
    @DisplayName("listAll returns an page od Anime when successful")
    void listAll_ReturnsListOfAnimeInsidePageObject_WhenSuccessful() {
        String name = AnimeCreator.createValidAnime().getName();

        Page<Anime> animePage = animeService.listAll(PageRequest.of(1, 1));

        Assertions.assertNotNull(animePage);
        Assertions.assertFalse(animePage.toList().isEmpty());
        Assertions.assertEquals(animePage.toList().get(0).getName(), name);
    }

    @Test
    @DisplayName("listAllNotPageable returns an list of Anime not page when successful")
    void listAllNotPageable_ReturnsListOfAnime_WhenSuccessful() {
        String name = AnimeCreator.createValidAnime().getName();

        List<Anime> animes = animeService.listAllNotPageable();

        Assertions.assertNotNull(animes);
        Assertions.assertEquals(animes.get(0).getName(), name);
        Assertions.assertTrue(animes.contains(AnimeCreator.createValidAnime()));
        Assertions.assertFalse(animes.isEmpty());
    }

    @Test
    @DisplayName("findByIdOrThrowBadRequestException returns anime when successful")
    void findByIdOrThrowBadRequestException_ReturnsAnime_WhenSuccessful() {
        Long expected_id = AnimeCreator.createValidAnime().getId();

        Anime anime = animeService.findByIdOrThrowBadRequestException(expected_id);

        Assertions.assertEquals(anime.getId(), expected_id);
        Assertions.assertNotNull(anime);
    }

    // Exceção BadRequest ID não encontrado
    @Test
    @DisplayName("findByIdOrThrowBadRequestException throws BadResquestException when Anime not found")
    void findByIdOrThrowBadRequestException_ReturnsBadResquestException_AnimeNotFound() {
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(BadRequestException.class, () -> animeService.findByIdOrThrowBadRequestException(1));
    }

    @Test
    @DisplayName("findByName returns an List of anime when successful")
    void findByName_ReturnsListOfAnime_WhenSuccessful() {
        String name = AnimeCreator.createValidAnime().getName();

        List<Anime> animes = animeService.findByName(name);

        Assertions.assertNotNull(animes);
        Assertions.assertEquals(animes.get(0).getName(), name);
        Assertions.assertFalse(animes.isEmpty());
    }

    @Test
    @DisplayName("save returns anime when successful")
    void save_ReturnsAnime_WhenSuccessful() {
        Anime anime = animeService.save(AnimePostRequestBodyCreator.createAnimePostRequestBody());

        Assertions.assertNotNull(anime);
        Assertions.assertEquals(anime, AnimeCreator.createValidAnime());
    }

    @Test
    @DisplayName("replace updates anime when successful")
    void replace_UpdatesAnime_WhenSuccessful() {
        Assertions.assertDoesNotThrow(() -> animeService.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody()));
    }

    @Test
    @DisplayName("delete remove anime when successful")
    void delete_DeleteAnime_WhenSuccessful(){
        Assertions.assertDoesNotThrow(() -> animeService.delete(AnimeCreator.createValidAnime().getId()));
    }

    @Test
    @DisplayName("delete returns badRequest when anime not found")
    void delete_ReturnBadRequestException_AnimeNotFound(){
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(BadRequestException.class, () -> animeService.delete(AnimeCreator.createValidAnime().getId()));
    }

}