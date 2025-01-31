package com.reactivespring.moviesinfoservice.repository;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.5.5")
@ActiveProfiles("test")
class MovieInfoRespositoryIngTest {

    @Autowired
    MovieInfoRespository movieInfoRespository;

    @BeforeEach
    void setUp(){
        var moviesInfo = List.of(new MovieInfo(null, "Batman Begins",
                2005, List.of("Chirstian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "HeathLeadger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        movieInfoRespository.saveAll(moviesInfo).blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRespository.deleteAll().block();
    }

    @Test
    void findAll(){
        var moviesInfoFlux = movieInfoRespository.findAll().log();

        StepVerifier.create(moviesInfoFlux).expectNextCount(3).verifyComplete();

    }

    @Test
    void findById(){
        var moviesInfoFlux = movieInfoRespository.findById("abc").log();

       StepVerifier.create(moviesInfoFlux)
               .assertNext(movieInfo -> {
                   assertEquals("Dark Knight Rises", movieInfo.getName());
               })
               .verifyComplete();
    }

    @Test
    void saveMovieinfo(){
        //given
        var movieInfo = new MovieInfo(null, "Batman Begins1",
                2005, List.of("Chirstian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        var moviesInfoMono = movieInfoRespository.save(movieInfo).log();

        StepVerifier.create(moviesInfoMono)
                .assertNext(movieInfo1 -> {
                  assertNotNull(movieInfo.getMovieInfoId());
                  assertEquals("Batman Begins1", movieInfo1.getName());
                })
                .verifyComplete();
    }

    @Test
    void updateMovieInfo(){
        //given
        var movieInfo = movieInfoRespository.findById("abc").block();
        movieInfo.setYear(2021);

        //when
        var moviesInfoMono = movieInfoRespository.save(movieInfo).log();

        StepVerifier.create(moviesInfoMono)
                .assertNext(movieInfo1 -> {
                        assertNotNull(movieInfo1.getMovieInfoId());
                        assertEquals(2021, movieInfo1.getYear());
                }).verifyComplete();
    }


    @Test
    void deleteMovieInfo(){
        movieInfoRespository.deleteById("abc").block();

        var moviesInfoFlux = movieInfoRespository.findAll().log();

        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(2).verifyComplete();
    }

    @Test
    void findByYear(){

        var moviesInfoFlux = movieInfoRespository.findByYear(2008).log();

        StepVerifier.create(moviesInfoFlux)
                .assertNext(movieInfo -> {
                    assertEquals(2008, movieInfo.getYear());
                }).verifyComplete();



    }



}