package VideoWatch.Service;

import VideoWatch.DTO.MovieDto;

import java.util.List;

public interface MovieServiceInterface {

    MovieDto createMovie(MovieDto movieDto);
    MovieDto findMovieById(int id);
    void updateMovie(int id,MovieDto movieDto);
    void deleteMovieById(int id);
    List<MovieDto> findMovieByTitle (String title);
    List<MovieDto> findMoviesByYear (int year);
    List<MovieDto> findMoviesByGenre (String genre);
    MovieDto movieBeingWatched (int customerId, int movieId);
    List<MovieDto> findAllMovies();


}
