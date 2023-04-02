package academy.devdojo.springboot2.controller;




import java.time.LocalDateTime;
import java.util.List;

import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.service.AnimeService;
import academy.devdojo.springboot2.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("animes")
@Log4j2
@RequiredArgsConstructor
public class AnimeController {


	private  final DateUtil dateUtil;
	private final AnimeService animeService;

	
	//localhost:8080/anime/list
	//@RequestMapping(method = RequestMethod.GET, path = "list")
	@GetMapping
	public ResponseEntity<Page<Anime>> list(Pageable pageable){
	    log.info(dateUtil.formatLocalDateTimeToDatabaseStyle(LocalDateTime.now()));
		return ResponseEntity.ok(animeService.listAll(pageable));
	}
    @GetMapping(path = "/all")
	public ResponseEntity<List<Anime>> listAll(){
		log.info(dateUtil.formatLocalDateTimeToDatabaseStyle(LocalDateTime.now()));
		return ResponseEntity.ok(animeService.listAllNotPageable());
	}

	@GetMapping(path = "/{id}")
	public ResponseEntity<Anime> findByID(@PathVariable long id){
	    log.info(dateUtil.formatLocalDateTimeToDatabaseStyle(LocalDateTime.now()));
		return ResponseEntity.ok(animeService.findByIdOrThrowBadRequestException(id));
	}

	// required = false não retorna erro caso não forma pasado o parametro
	@GetMapping(path = "/find")
	public ResponseEntity<List<Anime>> findByName(@RequestParam(required = false) String name){
		return ResponseEntity.ok(animeService.findByName(name));
	}

	@PostMapping
	public ResponseEntity<Anime> save(@RequestBody @Valid AnimePostRequestBody animePostRequestBody){
		return new ResponseEntity<>(animeService.save(animePostRequestBody), HttpStatus.CREATED);
	}

	@DeleteMapping(path = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable long id){
		animeService.delete(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PutMapping
	public ResponseEntity<Void> replace(@RequestBody AnimePutRequestBody animePutRequestBody) {
		animeService.replace(animePutRequestBody);
		return ResponseEntity.noContent().build();
	}

}
