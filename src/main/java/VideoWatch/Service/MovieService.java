package VideoWatch.Service;

import VideoWatch.DTO.MovieDto;
import VideoWatch.Model.Customer;
import VideoWatch.Model.Movie;
import VideoWatch.Repository.CustomerRepository;
import VideoWatch.Repository.MovieRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class MovieService implements MovieServiceInterface{

    private final MovieRepository movieRepository;
    private final CustomerRepository customerRepository;
    ModelMapper modelMapper;

    @Autowired
    public MovieService(MovieRepository movieRepository, CustomerRepository customerRepository, ModelMapper modelMapper) {
        this.movieRepository = movieRepository;
        this.customerRepository=customerRepository;
        this.modelMapper = modelMapper;
    }

    public MovieDto createMovie(MovieDto movieDto) {
        //if the movie is in the DB then the method will return an empty container meaning no saved movie
        Optional <Movie> existingMovie= movieRepository.findMovieByTitle(movieDto.getTitle());

        if (existingMovie.isPresent()) {
            throw new IllegalArgumentException("Movie already is present");
        }

        //convert the customerDto instance to a POJO instance and save the latter to the customer variable
        Movie movie= modelMapper.map(movieDto, Movie.class);
        //tell the repository to persist the customer instance and save that instance on the customer variable
        movie= movieRepository.save(movie);

        //convert that persisted instance back in to a DTO object
        MovieDto movieDto1= modelMapper.map(movie,MovieDto.class);

        return  movieDto1;
    }

    public MovieDto findMovieById(int id) {
        Optional <Movie> movie= movieRepository.findById(id);

        if (movie.isEmpty()){
            return null;
        }
        MovieDto movieDto= modelMapper.map(movie,MovieDto.class);
        return movieDto;
    }

    public List<MovieDto> findAllMovies(){
        List <Movie>movies = movieRepository.findAll();
        List<MovieDto> moviesDto=new LinkedList<>();


        for (Movie movie:movies){
            MovieDto movieDto= modelMapper.map(movie,MovieDto.class);
            moviesDto.add(movieDto);
        }
        return moviesDto;
    }

    public void updateMovie(int id, MovieDto movieDto) {
           Optional <Movie> optionalMovie= movieRepository.findById(id);

           if (optionalMovie.isEmpty()){
               throw new NoSuchElementException("No movie with that Id found");
           }
            Movie movieToUpdate= optionalMovie.get();
           //convert the movieDto to movie and then repo persist it
            Movie movie= modelMapper.map(movieDto,Movie.class);
            movie.setId(movieToUpdate.getId());
            movieRepository.save(movie);
    }

    public void deleteMovieById(int id) {
        Optional<Movie> existingMovie= movieRepository.findById(id);
        if (existingMovie.isEmpty()){
            System.out.println("Movie doesn't exist");
        }

        movieRepository.deleteById(existingMovie.get().getId());
    }

    @Override
    public MovieDto findMovieByTitle(String title) {
        Optional <Movie> movie= movieRepository.findMovieByTitle(title);
        if (movie.isEmpty()) {
            return null;
        }

        MovieDto movieDto= modelMapper.map(movie,MovieDto.class);

        return movieDto;
    }

    @Override
    public List<MovieDto> findMoviesByYear(int year) {
        List <Movie> movies= movieRepository.findMoviesByReleaseYear(year);
        if (movies.isEmpty()){
            return Collections.emptyList();
        }

        return movies.stream()
                .map(movie -> modelMapper.map(movie,MovieDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieDto> findMoviesByGenre(String genre) {
        List <Movie> movies= movieRepository.findMoviesByGenre(genre);
        if (movies.isEmpty()){
            return Collections.emptyList();
        }
        return movies.stream()
                .map(movie -> modelMapper.map(movie,MovieDto.class))
                .collect(Collectors.toList());    }

    @Override
    public MovieDto movieBeingWatched(int customerId,int movieId) {

            Optional<Movie> existingMovie = movieRepository.findById(movieId);
            Optional<Customer> existingCustomer = customerRepository.findById(customerId);

            if (existingMovie.isPresent()) {

                Movie movie = existingMovie.get(); //get the movie out of the optional
                Customer customer = existingCustomer.get(); //get the customer out of the optional

                // Add movie to the list of movies watched by a customer
                customer.getWatchedMovies().add(movie);

                // Add the customer to the list of customers that watched the movie
                movie.getViewers().add(customer);

                // Save the updated entities (their lists) back to the database
                movieRepository.save(movie);
                customerRepository.save(customer);

                MovieDto movieToMovieDto= modelMapper.map(movie, MovieDto.class);
                return movieToMovieDto;
            } else {
                return null;
            }
    }

}
