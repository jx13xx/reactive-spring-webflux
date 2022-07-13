package com.reactivespring.moviesinfoservice.controller;


import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
public class MoviesInfoControllerUnitTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MovieInfoService moviesInfoServiceMock;

    static String MOVIES_INFO_URL ="/v1/movieinfos";
    @Test
    void getAllMoviesInfo(){
        var moviesInfo = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Chirstian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "HeathLeadger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));


        when(moviesInfoServiceMock.getAllMovieInfos()).thenReturn(Flux.fromIterable(moviesInfo));

        webTestClient
                .get()
                .uri(MOVIES_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void addMovieInfo(){
        var movieInfo = new MovieInfo(null, "Batman Begins1",
                2005, List.of("Chirstian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

      when(moviesInfoServiceMock.addMovieInfo(isA(MovieInfo.class))).thenReturn(
              Mono.just(new MovieInfo("mockId", "Batman Begins1",
                      2005, List.of("Chirstian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")))
      );

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
                    assertEquals(savedMovieInfo.getName(), "Batman Begins1");
                    assertEquals("mockId", savedMovieInfo.getMovieInfoId());

                });
    }


    @Test
    void updateMovieInfo(){

        var movieInfo = new MovieInfo(null, "Dark Knight Rises1",
                2005, List.of("Chirstian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));


        when(moviesInfoServiceMock.updateMovieInfo(isA(MovieInfo.class), isA(String.class))).thenReturn(
                Mono.just(new MovieInfo("abc", "Dark Knight Rises1",
                        2005, List.of("Chirstian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")))
        );

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

        when(moviesInfoServiceMock.deleteMovieInfo(isA(String.class))).thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri(MOVIES_INFO_URL+ "/{id}", "mockId")
                .exchange()
                .expectStatus()
                .isNoContent();

    }
}
