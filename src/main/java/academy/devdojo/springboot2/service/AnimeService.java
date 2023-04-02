package academy.devdojo.springboot2.service;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.exception.BadRequestException;
import academy.devdojo.springboot2.mapper.AnimeMapper;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimeService {

    private final AnimeRepository animeRepository;

    public Page<Anime> listAll(Pageable pageable) {
        return animeRepository.findAll(pageable);
    }

    public List<Anime> listAllNotPageable() {

        return animeRepository.findAll();
    }

    public Anime findByIdOrThrowBadRequestException(long id) {
        return animeRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Anime not Found"));
//        return animes.stream()
//					.filter(anime -> anime.getId().equals(id))
//					.findFirst()
//					.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Anime not Found"));
    }

    public List<Anime> findByName(String name){
        return animeRepository.findByName(name);
    }

    @Transactional //(rollbackOn = Exception.class)
    public Anime save(AnimePostRequestBody animePostRequestBody) {
   return animeRepository.save(AnimeMapper.INSTANCE.toAnime(animePostRequestBody));
    }

    public void delete(long id) {
        //findByIdOrThrowBadRequestException retornar o anime pelo o id ou 404
        animeRepository.delete(findByIdOrThrowBadRequestException(id));
    }

    public void replace(AnimePutRequestBody animePutRequestBody) {
        // Garante que o savedAnime.id é o que esta no banco de dados se não retorna 404
        Anime savedAnime = findByIdOrThrowBadRequestException(animePutRequestBody.getId());
//        Anime anime = Anime.builder()
//                .id(savedAnime.getId())
//                .name(animePutRequestBody.getName()).build();
        Anime anime = AnimeMapper.INSTANCE.toAnime(animePutRequestBody);
        anime.setId(savedAnime.getId());
        animeRepository.save(anime);


    }
}
