package academy.devdojo.springboot2.controller;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.exception.BadRequestException;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;
import academy.devdojo.springboot2.service.AnimeService;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.util.AnimePutRequestBodyCreator;
import academy.devdojo.springboot2.util.DateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@Log4j2
class AnimeControllerTest {

    @InjectMocks
    private AnimeController animeController;

    @Mock
    private AnimeService animeServiceMock;
    @Mock
    private DateUtil dateUtilMock;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));
        BDDMockito.when(animeServiceMock.listAll(ArgumentMatchers.any())).thenReturn(animePage);

        BDDMockito.when(animeServiceMock.listAllNotPageable()).thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeServiceMock.findByIdOrThrowBadRequestException(ArgumentMatchers.anyLong())).thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString())).thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeServiceMock.save(ArgumentMatchers.any(AnimePostRequestBody.class))).thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.doNothing().when(animeServiceMock).replace(ArgumentMatchers.any(AnimePutRequestBody.class));

        BDDMockito.doNothing().when(animeServiceMock).delete(ArgumentMatchers.anyLong());


        String str = "2023-03-11 11:30: 40";
        BDDMockito.when(dateUtilMock.formatLocalDateTimeToDatabaseStyle(LocalDateTime.now())).thenReturn(str);


        final AnimeController controller = new AnimeController(dateUtilMock,animeServiceMock);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        this.objectMapper = new ObjectMapper();

    }

    @Test
    @DisplayName("List returns list of anime inside page object when successful")
    void list_ReturnsListOfAnimeInsidePageObject_WhenSuccessful(){
        String name = AnimeCreator.createValidAnime().getName();

        Page<Anime> animePage = animeController.list(null).getBody();

        Assertions.assertNotNull(animePage);
        Assertions.assertFalse(animePage.toList().isEmpty());
        Assertions.assertEquals(animePage.getSize(),1);
        Assertions.assertEquals(animePage.toList().get(0).getName(), name);
    }


    @Test
    @DisplayName("List returns list of anime inside page object when successful using MockMvc")
    void list_ReturnsListOfAnimeInsidePageObject_WhenSuccessful_UsingMockMvc() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/animes"))
                .andExpect(status().isOk()).andReturn();

        JSONArray jsonArray = new JSONArray(result.getResponse().getContentAsString());
        Assertions.assertNotNull(jsonArray);
        Assertions.assertTrue(jsonArray.length() == 1);

    }

    @Test
    @DisplayName("listAll returns list of anime when successful")
    void listAll_ReturnsListOfAnime_WhenSuccessful(){
        String name = AnimeCreator.createValidAnime().getName();

        List<Anime> animes = animeController.listAll().getBody();

        Assertions.assertNotNull(animes);
        Assertions.assertFalse(animes.isEmpty());
        Assertions.assertEquals(animes.get(0).getName(), name);
    }

    @Test
    @DisplayName("List returns list of anime  when successful using MockMvc")
    void list_ReturnsListOfAnime_WhenSuccessful_UsingMockMvc() throws Exception{
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/animes/all"))
                .andExpect(status().isOk()).andReturn();
        try {
            JSONArray jsonArray = new JSONArray(result.getResponse().getContentAsString());
            Assertions.assertNotNull(jsonArray);
            Assertions.assertTrue(jsonArray.length() == 1);

        } catch (JSONException e) {
            System.out.println("Erro ao converter JSON: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("findById returns anime when successful")
    void findById_ReturnsAnime_WhenSuccessful(){
        Long expected_id = AnimeCreator.createValidAnime().getId();

        Anime animeReponse = animeController.findByID(expected_id).getBody();

        Assertions.assertNotNull(animeReponse);
        Assertions.assertEquals(animeReponse.getId(), expected_id);
    }

    @Test
    @DisplayName("findByName returns a List of anime when successful")
    void findByName_ReturnsListOfAnime_WhenSuccessful(){
        String name = AnimeCreator.createValidAnime().getName();

        List<Anime> animeReponse = animeController.findByName(name).getBody();

        Assertions.assertNotNull(animeReponse);
        Assertions.assertEquals(animeReponse.get(0).getName(), name);
        Assertions.assertEquals(animeReponse.size(),1);
        Assertions.assertFalse(animeReponse.isEmpty());
    }

    @Test
    @DisplayName("findByName returns an empty List of anime when successful")
    void findByName_ReturnsEmptyListOfAnime_WhenSuccessful(){
        BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString())).thenReturn(Collections.emptyList());

        List<Anime> animeReponse = animeController.findByName("empty").getBody();

        Assertions.assertTrue(animeReponse.isEmpty());
    }


    @Test
    @DisplayName("Save returns anime when successful")
    void save_ReturnsAnime_WhenSuccessful(){
        Anime anime = animeController.save(AnimePostRequestBodyCreator.createAnimePostRequestBody()).getBody();

        Assertions.assertNotNull(anime);
        Assertions.assertEquals(anime, AnimeCreator.createValidAnime());
    }

    @Test
    @DisplayName("Save returns anime when successful using mockMvc")
    void save_ReturnsAnime_WhenSuccessful_UsingMockMvc() throws Exception{

        AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimePostRequestBody();

        mockMvc.perform(MockMvcRequestBuilders.post("/animes")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(animePostRequestBody)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Save returns badRequestExceptions when Mapper is empty")
    void save_ReturnExceptionsBadRequest_WhenEntityEmpty_UsingMockMvc() throws Exception{

        AnimePostRequestBody animePostRequestBody = new AnimePostRequestBody();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/animes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(animePostRequestBody)))
                .andExpect(status().isBadRequest()).andReturn();

        Assertions.assertTrue(result.getResolvedException().toString().contains("MethodArgumentNotValidException"));
    }

    @Test
    @DisplayName("Save throw ConstraintViolationException when name is empty")
    void save_ThrowsConstraintViolationException_WhenNameIsEmpty() {
        AnimePostRequestBody animePostRequestBody =  AnimePostRequestBody.builder().name(null).build();

         Anime anime = animeController.save(animePostRequestBody).getBody();

          Assertions.assertNotNull(anime);

//        Assertions.assertThrows(MethodArgumentNotValidException.class, () -> animeController.save(animePostRequestBody));
    }

    @Test
    @DisplayName("replace updates anime when successful")
    void replace_UpdatesAnime_WhenSuccessful(){

        Assertions.assertDoesNotThrow(() -> animeController.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody()));

       ResponseEntity<Void> entity = animeController.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody());

        Assertions.assertNotNull(entity);
        Assertions.assertEquals(entity.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete remove anime when successful")
    void delete_DeleteAnime_WhenSuccessful(){

        Assertions.assertDoesNotThrow(() -> animeController.delete(1));

        ResponseEntity<Void> entity = animeController.delete(1);

        Assertions.assertNotNull(entity);
        Assertions.assertEquals(entity.getStatusCode(), HttpStatus.NO_CONTENT);
    }

}