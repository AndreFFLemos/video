package VideoWatch.Repository;

import VideoWatch.Model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie,Integer> {

    Optional<Movie> findMovieByTitle (String title);
    List <Movie> findMoviesByReleaseYear (int year);
    List <Movie> findMoviesByGenre (String genre);
    void deleteByTitle(String title);
}
