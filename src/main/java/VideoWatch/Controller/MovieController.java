package VideoWatch.Controller;

import VideoWatch.DTO.MovieDto;
import VideoWatch.Service.MovieServiceInterface;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class MovieController implements MovieControllerInterface {

    private MovieServiceInterface movieServiceInterface;

    @Autowired
    public MovieController(MovieServiceInterface movieServiceInterface) {
        this.movieServiceInterface = movieServiceInterface;
    }

    @Override
    @PostMapping(value = "/movie/new")
    public ResponseEntity<MovieDto> createMovie(@Valid @RequestBody MovieDto movieDto) {
        return new ResponseEntity<>(movieServiceInterface.createMovie(movieDto),HttpStatus.CREATED);
    }

    @Override
    @GetMapping(value = "movie/byid")
    public ResponseEntity<MovieDto> findMovieById(@RequestParam("id") int id) {
        return ResponseEntity.ok(movieServiceInterface.findMovieById(id));
    }
    @Override
    @GetMapping(value = "movie/bytitle")
    public ResponseEntity<MovieDto> findMovieByTitle(@RequestParam("title") String title) {
        return ResponseEntity.ok(movieServiceInterface.findMovieByTitle(title));
    }

    @Override
    @GetMapping(value = "/movie/{id}")
    public ResponseEntity<MovieDto> getMovie(@PathVariable int id) {
        return ResponseEntity.ok(movieServiceInterface.findMovieById(id));
    }

    @Override
    @GetMapping(value = "/movie/all")
    public ResponseEntity<List<MovieDto>> findAllMovies() {
        return ResponseEntity.ok(movieServiceInterface.findAllMovies());
    }

    @Override
    @GetMapping(value = "/movie/findbyyear")
    public ResponseEntity<List<MovieDto>> findMoviesByReleaseYear(@Valid @RequestParam("year") int year) {
        return ResponseEntity.ok(movieServiceInterface.findMoviesByYear(year));
    }

    @Override
    @GetMapping(value = "/movie/findbygenre")
    public ResponseEntity<List<MovieDto>> findMoviesByGenre(@Valid @RequestParam("genre") String genre) {
        return ResponseEntity.ok(movieServiceInterface.findMoviesByGenre(genre));
    }

    @Override
    @PutMapping(value = "/movie/{id}")
    public ResponseEntity<Void> updateMovie(@PathVariable int id, @Valid @RequestBody MovieDto movieDto) {
        movieServiceInterface.updateMovie(id,movieDto);

        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping(value = "/movie/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable int id) {

        movieServiceInterface.deleteMovieById(id);

        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping(value = "/{customerId}/{movieId}")
    public ResponseEntity<MovieDto> movieBeingWatched(@PathVariable int customerId, @PathVariable int movieId) {

        MovieDto movieBeingWatched = movieServiceInterface.movieBeingWatched(customerId, movieId);

        if (movieBeingWatched == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(movieBeingWatched);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleAnyException(Exception e) {
        e.printStackTrace(); // This will print the stack trace to the console
        // Log the error, notify someone, etc.
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}