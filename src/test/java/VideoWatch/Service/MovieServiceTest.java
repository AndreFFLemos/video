package VideoWatch.Service;

import VideoWatch.DTO.CustomerDto;
import VideoWatch.DTO.MovieDto;
import VideoWatch.Model.Customer;
import VideoWatch.Model.Movie;
import VideoWatch.Repository.CustomerRepository;
import VideoWatch.Repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    private Movie movie;
    private Customer customer;
    private CustomerDto customerDto;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private CustomerRepository customerRepository;
    @InjectMocks
    private MovieService ms;
    private List <Movie> movies;
    private MovieDto movieDto;
    private MovieDto movieDtoSaved;
    private ModelMapper modelMapper;
    private List<Customer> customers;


    @BeforeEach
    void setup (){
        modelMapper=new ModelMapper();
        movies= new LinkedList<>();
        ms= new MovieService(movieRepository,customerRepository,modelMapper);
        //Collections.emptylist is an immutable list
        movie= new Movie(0,"Rambo","Action",1982,7,new LinkedList<>());
        movieDto=new MovieDto("Rambo","Action",1982,7);

        movies.add(movie);
        movieDtoSaved= modelMapper.map(movie, MovieDto.class);
        customer= new Customer(0,"Ana", "Lemos","AL","ola","a@l",movies);
        customers=new LinkedList<>();
        customers.add(customer);
        customerDto= new CustomerDto("Ana", "Lemos","AL","a@l");
    }

    @Test
    void createMovieTest() {

        Movie newMovie= new Movie(0,"ET","Adventure",1980,8,Collections.emptyList());
        MovieDto newMovieDto= new MovieDto("ET","Adventure",1980,8);

        //test when movie doesn't exist in the DB
        when (movieRepository.findMovieByTitle("ET")).thenReturn(Optional.empty());
        when(movieRepository.save(any(Movie.class))).thenReturn(newMovie);
        MovieDto mockedMovie= ms.createMovie(newMovieDto);

        assertEquals(newMovieDto,mockedMovie);

        //when movie exists
        when (movieRepository.findMovieByTitle("Rambo")).thenReturn(Optional.of(movie));
        assertThrows(IllegalArgumentException.class,()-> ms.createMovie(movieDto));

        verify(movieRepository).save(newMovie);
        verify(movieRepository).findMovieByTitle("ET");
        verify(movieRepository).findMovieByTitle("Rambo");
    }

    @Test
    void findMovieByIdTest() {
        //when movie exists
        when(movieRepository.findById(0)).thenReturn(Optional.of(movie));
        MovieDto mockedM= ms.findMovieById(0);

        assertEquals(movieDtoSaved,mockedM);

        //when movie doesn't exist
        when (movieRepository.findById(5)).thenReturn(Optional.empty());
        MovieDto movieNotFound= ms.findMovieById(5);
        assertNull(movieNotFound);

        verify(movieRepository).findById(0);
        verify(movieRepository).findById(5);
    }

    @Test
    void findAllMoviesTest(){
        Movie movie1=new Movie();
        Movie movie2=new Movie();

        movies.add(movie1);
        movies.add(movie2);

        when(movieRepository.findAll()).thenReturn(movies);
        List <MovieDto> mockedList= ms.findAllMovies();

        assertEquals(mockedList.size(),3);
        verify(movieRepository).findAll();
    }

    @Test
    void updateMovieTest() {
        Movie updatedMovie= new Movie(1,"Matrix","Action",1999,8,Collections.emptyList());
        MovieDto updatedMovieDto= new MovieDto("Matrix","Action",1999,8);

        //when the movie exists
        when(movieRepository.findById(1)).thenReturn(Optional.of(updatedMovie)); //when findById gets used then it returns
        when(movieRepository.save(updatedMovie)).thenReturn(updatedMovie); //when a movie is saved by the repo then return that movie
        ms.updateMovie(1,updatedMovieDto);

        //when the movie doesn't exist
        when (movieRepository.findById(0)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,()->
                ms.updateMovie(0,
                        new MovieDto("ET","adventure",1980,8)));

        verify(movieRepository).save(updatedMovie);
        verify(movieRepository).findById(1);
        verify(movieRepository).findById(0);

    }

    @Test
    void deleteMovieByIdTest() {
        when(movieRepository.findById(0)).thenReturn(Optional.of(movie));
        doNothing().when(movieRepository).deleteById(0);

        ms.deleteMovieById(0);
        verify(movieRepository).deleteById(0);
    }

    @Test
    void findMovieByTitleTest (){

        //check when movie is present
        when(movieRepository.findMovieByTitle("Rambo")).thenReturn(Optional.of(movie));
        MovieDto mockedM= ms.findMovieByTitle("Rambo");
        assertEquals("Rambo",mockedM.getTitle());

        //check when movie is not present
        when(movieRepository.findMovieByTitle("Matrix")).thenReturn(Optional.empty());
        MovieDto movieNotFound= ms.findMovieByTitle("Matrix");
        assertNull(movieNotFound);

        verify(movieRepository).findMovieByTitle("Rambo");
        verify(movieRepository).findMovieByTitle("Matrix");
    }

    @Test
    void findMoviesByGenre(){
        Movie m2=new Movie();
        m2.setGenre("Action");

        movies.add(m2);

        //check when movie is present
        when(movieRepository.findMoviesByGenre("Action")).thenReturn(movies);
        List<MovieDto> genreFound= ms.findMoviesByGenre("Action");

        assertEquals(2,genreFound.size());

        //check when movie is not present
        when(movieRepository.findMoviesByGenre("Drama")).thenReturn(Collections.emptyList());
        List<MovieDto> genreNotFound= ms.findMoviesByGenre("Drama");
        assertTrue(genreNotFound.isEmpty());

        verify(movieRepository).findMoviesByGenre("Action");
        verify(movieRepository).findMoviesByGenre("Drama");
    }

    @Test
    void findMovieByYear(){
        movie.setReleaseYear(1988);

        //check when movie is present
        when(movieRepository.findMoviesByReleaseYear(1988)).thenReturn(movies);
        List<MovieDto> yearFound= ms.findMoviesByYear(1988);
        assertEquals(1,yearFound.size());

        //check when movie is not present
        when(movieRepository.findMoviesByReleaseYear(1950)).thenReturn(Collections.emptyList());
        List<MovieDto> yearNotFound= ms.findMoviesByYear(1950);
        assertTrue(yearNotFound.isEmpty());

        verify(movieRepository).findMoviesByReleaseYear(1988);
        verify(movieRepository).findMoviesByReleaseYear(1950);
    }

    @Test
    public void watchMovieTest(){
        //if movie exists
        when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        MovieDto watchedMovie= ms.movieBeingWatched(1,1);
        assertEquals(movieDto,watchedMovie);

        //if movie doesn't exist

        when(movieRepository.findById(1)).thenReturn(Optional.empty());
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        MovieDto nonExistentMovie= ms.movieBeingWatched(1,1);
        assertNull(nonExistentMovie);

        verify(customerRepository, times(2)).findById(1);
        verify(movieRepository,times(2)).findById(1);
        verify(movieRepository).save(movie);
        verify(customerRepository).save(customer);
    }
}