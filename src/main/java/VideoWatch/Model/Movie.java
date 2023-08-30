package VideoWatch.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @Column(name="title",nullable = false)
    private String title;
    @Column(name="genre",nullable = false)
    private String genre;
    @Column(name="releaseyear")
    private int releaseYear;
    @Column (name="rating")
    private float rating;
    //the mappedBy attribute indicates that the relationship is managed by the customer side
    @ManyToMany(mappedBy = "watchedMovies")
    @Builder.Default //this guarantees that the instances are created via builder and values are always present
    private List<Customer> viewers= new LinkedList<>();

}