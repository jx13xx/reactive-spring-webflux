package com.reactivespring.moviesinfoservice.controller;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.repository.MovieInfoRespository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.5.5")
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MoviesInfoControllerIntTest {

    @Autowired
    MovieInfoRespository movieInfoRespository;

    @Autowired
    WebTestClient webTestClient;

    static String MOVIES_INFO_URL ="/v1/movieinfos";

    @BeforeEach
    void setUp() {
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
    void addMovieInfo() {
        var movieInfo = new MovieInfo(null, "",
                2005, List.of("Chirstian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient
                .post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {

                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert savedMovieInfo != null;
                    assert savedMovieInfo.getMovieInfoId() != null;
                    assertEquals(savedMovieInfo.getName(), "Batman Begins");

                });

    }

    @Test
    void getAllMovieInfo(){

        webTestClient.
                get()
                .uri(MOVIES_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getById(){

        webTestClient
                .get()
                .uri(MOVIES_INFO_URL  +"/{id}", "abc")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var result = movieInfoEntityExchangeResult.getResponseBody();
                    assertEquals(result.getMovieInfoId(), "abc");

                });

    }

    @Test
    void getById_notFound(){
        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/{id}", "def")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void updateMovieInfo(){

        var movieInfo = new MovieInfo(null, "Dark Knight Rises1",
                2005, List.of("Chirstian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient
                .put()
                .uri(MOVIES_INFO_URL+"/{id}", "abc")
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var updatedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert updatedMovieInfo != null;
                    assert updatedMovieInfo.getMovieInfoId() != null;
                });
    }

    @Test
    void deleteMovieInfo(){

        webTestClient
                .delete()
                .uri(MOVIES_INFO_URL+ "/{id}", "abc")
                .exchange()
                .expectStatus()
                .isNoContent();

    }

    @Test
    void updateMovieInfo_notFound() {

        var movieInfo = new MovieInfo(null, "Dark Knight Rises1",
                2005, List.of("Chirstian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient
                .put()
                .uri(MOVIES_INFO_URL + "/{id}", "def")
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

}