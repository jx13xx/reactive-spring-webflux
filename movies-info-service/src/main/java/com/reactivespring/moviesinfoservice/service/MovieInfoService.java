package com.reactivespring.moviesinfoservice.service;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.repository.MovieInfoRespository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieInfoService {

    private final MovieInfoRespository movieInfoRespository;

    public MovieInfoService(MovieInfoRespository movieInfoRespository) {
        this.movieInfoRespository = movieInfoRespository;
    }

    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
        return movieInfoRespository.save(movieInfo);
    }

    public Flux<MovieInfo> getAllMovieInfos() {
        return movieInfoRespository.findAll();
    }

    public Mono<MovieInfo> getMovieInfoById(String id) {

        var moviesInfo2 = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Chirstian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "HeathLeadger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));


        moviesInfo2
                .stream()
                .filter(str -> str.getYear() >= 2008)
                .collect(Collectors.toList());

        System.out.println(moviesInfo2);

        return  movieInfoRespository.findById(id);
    }

    public Mono<MovieInfo> updateMovieInfo(MovieInfo updatedMovieInfo, String id) {


       return movieInfoRespository.findById(id)
                .flatMap(movieInfo -> {
                   movieInfo.setCast(updatedMovieInfo.getCast());
                   movieInfo.setYear(updatedMovieInfo.getYear());
                   movieInfo.setRelease_date(updatedMovieInfo.getRelease_date());
                   return movieInfoRespository.save(movieInfo);
                });
    }

    public Mono<Void> deleteMovieInfo(String id) {
        return movieInfoRespository
                .findById(id).then(movieInfoRespository.deleteById(id));



    }
}
