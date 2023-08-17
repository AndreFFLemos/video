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

    @Override
    public MovieDto createMovie(MovieDto movieDto) {
        //if the movie is in the DB then the method will throw an exception
        movieRepository.findMovieByTitle(movieDto.getTitle())
                .ifPresent(movie -> {
                    throw new IllegalArgumentException("Movie already exists");
                });

        //convert the customerDto instance to a POJO instance and save the latter to the customer variable
        Movie movie= modelMapper.map(movieDto, Movie.class);
        //tell the repository to persist the customer instance and save that instance on the customer variable
        movie= movieRepository.save(movie);

        //convert that persisted instance back in to a DTO object
        MovieDto movieDto1= modelMapper.map(movie,MovieDto.class);

        return  movieDto1;
    }

    @Override
    public MovieDto findMovieById(int id) {
        return movieRepository.findById(id)
                .map(movie -> modelMapper.map(movie, MovieDto.class))
                .orElse(null);
    }

    @Override
    public List<MovieDto> findAllMovies(){

        return movieRepository.findAll()
                .stream()
                .map(movie -> modelMapper.map(movie,MovieDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public void updateMovie(int id, MovieDto movieDto) {
        Optional<Movie> optionalMovie = movieRepository.findById(id);

        if (optionalMovie.isEmpty()) {
            throw new NoSuchElementException("No movie with that Id found");
        }
        Movie movieToUpdate = optionalMovie.get();
        //convert the movieDto to movie and then repo persist it
        Movie movie = modelMapper.map(movieDto, Movie.class);
        movie.setId(movieToUpdate.getId());
        movieRepository.save(movie);

    }

    @Override
    public void deleteMovieById(int id) {
        Optional<Movie> existingMovie= movieRepository.findById(id);
        if (existingMovie.isEmpty()){
            System.out.println("Movie doesn't exist");
        }

        movieRepository.deleteById(existingMovie.get().getId());
    }

    @Override
    public List<MovieDto> findMovieByTitle(String title) {

        //if no movie is found, an empty Collection is returned
       return movieRepository.findMovieByTitle(title)
               .stream()
                .map(movie->modelMapper.map(movie,MovieDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieDto> findMoviesByYear(int year) {
        return
                movieRepository.findMoviesByReleaseYear(year).stream()
                .map(movie -> modelMapper.map(movie,MovieDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieDto> findMoviesByGenre(String genre) {
        return movieRepository.findMoviesByGenre(genre).stream()
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
