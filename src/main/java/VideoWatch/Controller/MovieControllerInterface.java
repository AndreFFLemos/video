package VideoWatch.Controller;

import VideoWatch.DTO.MovieDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface MovieControllerInterface {

    ResponseEntity<MovieDto> createMovie(@Valid @RequestBody MovieDto movieDto);
    ResponseEntity<MovieDto>  findMovieById(@RequestParam ("id") int id);
    ResponseEntity<MovieDto>  getMovie(@PathVariable int id);
    ResponseEntity<List<MovieDto>> findAllMovies();
    ResponseEntity<List<MovieDto>> findMoviesByReleaseYear(@Valid @RequestParam ("year") int year);
    ResponseEntity<List<MovieDto>> findMoviesByGenre(@Valid @RequestParam ("genre") String genre);
    ResponseEntity<Void>  updateMovie(@PathVariable int id,@Valid @RequestBody MovieDto movieDto);
    ResponseEntity <Void> deleteMovie(@PathVariable int id);
    ResponseEntity <MovieDto> movieBeingWatched(@PathVariable int customerId, @PathVariable int movieId);
    ResponseEntity<List<MovieDto>> findMovieByTitle(@RequestParam("title") String title);

}
