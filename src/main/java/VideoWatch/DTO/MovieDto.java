package VideoWatch.DTO;

import jakarta.validation.constraints.*;

import java.util.Objects;

public class MovieDto {

    @NotBlank
    @NotEmpty
    private String title;

    @NotEmpty
    @NotBlank
    private String genre;

    @Digits(integer = 4,fraction = 0)
    @Min(value=1900, message = "Movies didn't exist before 1900")
    @Max(value = 2023, message = "Movies after the present year not accepted")
    private int releaseYear;

    @Digits(integer = 2,fraction = 1)
    @Min(value = 1, message="movie rating goes from 1 to 10")
    @Max(value = 10,message="movie rating goes from 1 to 10")
    private float rating;

    public MovieDto(String title, String genre, int releaseYear, float rating) {
        this.title = title;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.rating = rating;
    }

    public MovieDto() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o){
        if (this==o){
            return true;
        }
        if (o==null || getClass()!=o.getClass()) return false;
        MovieDto movieDto= (MovieDto) o;
        return Objects.equals(title,movieDto.title) &&
                Objects.equals(genre, movieDto.genre) &&
                Objects.equals(releaseYear,movieDto.releaseYear)&&
                Objects.equals(rating,movieDto.rating);
    }

    @Override
    public int hashCode(){
        return Objects.hash(title,genre,releaseYear,rating);
    }

}
