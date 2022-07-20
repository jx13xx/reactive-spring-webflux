package com.reactivespring.routes;


import com.reactivespring.domain.Review;
import com.reactivespring.exceptionhandler.GlobalErrorHandler;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewReactorRepository;
import com.reactivespring.router.ReviewRouter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalErrorHandler.class})
@AutoConfigureWebTestClient
public class ReviewsUnitTest {

    @MockBean
    private ReviewReactorRepository reviewReactorRepository;

    @Autowired
    private WebTestClient webTestClient;

    static String REVIEWS_URL = "/v1/reviews";


//    @BeforeEach
//    void setUp() {
//
//        var reviewsList = List.of(
//                new Review("1", 1L, "Awesome Movie", 9.0),
//                new Review(null, 1L, "Awesome Movie1", 9.0),
//                new Review(null, 2L, "Excellent Movie", 8.0));
//
//
//        reviewReactorRepository.saveAll(reviewsList).blockLast();
//
//    }
//
//    @AfterEach
//    void tearDown(){reviewReactorRepository.deleteAll().block();}

    @Test
    void addReview(){
        var review = new Review(null, 1L, "Awesome Movie", 9.0);

        when(reviewReactorRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

        webTestClient
                .post()
                .uri(REVIEWS_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                   var savedReviewInfo = reviewEntityExchangeResult.getResponseBody();

                   assert savedReviewInfo != null;
                });
    }

    @Test
    void getAllReviews(){
        var reviewsList = List.of(
                new Review("1", 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review(null, 2L, "Excellent Movie", 8.0));

        when(reviewReactorRepository.findAll())
                .thenReturn(Flux.fromIterable(reviewsList));


        webTestClient
                .get()
                .uri(REVIEWS_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(3);

    }

    @Test
    void findReviewById(){
        var reviewId = "1";

        when(reviewReactorRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(new Review(reviewId, 1L, "Awesome Movie", 9.0)));

        when(reviewReactorRepository.findById((String) any()))
                .thenReturn(Mono.just(new Review(reviewId, 1L, "Awesome Movie", 9.0)));


        webTestClient
                .get()
                .uri(REVIEWS_URL + "/{id}", reviewId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

    }

    @Test
    void deleteReview(){
        var reviewId = "abc";

       when(reviewReactorRepository.findById((String) any()))
               .thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));
        when(reviewReactorRepository.deleteById((String) any())).thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri(REVIEWS_URL + "/{id}", reviewId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void addReview_validation(){
        var review = new Review(null, null, "Awesome Movie", -9.0);

        when(reviewReactorRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

        webTestClient
                .post()
                .uri(REVIEWS_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("rating.movieInfoId : must not be nullrating.negative : please pass a non-negative value");
    }

}
