package com.reactivespring.routes;


import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.5.5")
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class ReviewsIntgTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReviewReactorRepository reviewReactorRepository;

    static String REVIEWS_URL = "/v1/reviews";

    @BeforeEach
    void setUp() {

        var reviewsList = List.of(
                new Review("1", 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review(null, 2L, "Excellent Movie", 8.0));


        reviewReactorRepository.saveAll(reviewsList).blockLast();

    }

    @AfterEach
    void tearDown() {
        reviewReactorRepository.deleteAll().block();
    }

    @Test
    void addReview(){

        var review = new Review(null, 1L, "Awesome Movie", 9.0);

        webTestClient
                .post()
                .uri(REVIEWS_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {

                    var savedReview = reviewEntityExchangeResult.getResponseBody();
                    System.out.println(savedReview);
                    assert  savedReview!= null;
                    assert savedReview.getReviewId()!=null;
                });


    }

    @Test
    void getReview(){

        webTestClient
                .get()
                .uri(REVIEWS_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var getReview = reviewEntityExchangeResult.getResponseBody();
                    System.out.println(getReview);


                });
    }

    @Test
    void updateReview(){
        var review = new Review("1", 1L, "Awesome Movie Update", 9.0);

        webTestClient
                .put()
                .uri(REVIEWS_URL+"/{id}", "1")
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var updatedReviewInfo = reviewEntityExchangeResult.getResponseBody();
                    System.out.println(updatedReviewInfo);

                    assert updatedReviewInfo != null;
                    assert  updatedReviewInfo.getReviewId() != null;
                });


    }

    @Test
    void deleteReview(){
        webTestClient
                .delete()
                .uri(REVIEWS_URL+ "/{id}", "1")
                .exchange()
                .expectStatus()
                .isNoContent();

    }

    @Test
    void getReviewById(){

        webTestClient
                .get()
                .uri(uriBuilder -> {
                    return uriBuilder.path(REVIEWS_URL)
                            .queryParam("movieInfoId", "1")
                            .build();
                })
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .value(reviewList -> {
                    System.out.println("reviewsList : " + reviewList);

                });

    }

    @Test
    void updatereviewNotFound(){

        webTestClient
                .put()
                .uri(REVIEWS_URL+ "/{id}", "5")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void reviewIdNotFound(){
       webTestClient
               .get()
                .uri(uriBuilder -> {
                    return uriBuilder.path(REVIEWS_URL)
                            .queryParam("movieInfoId", "8")
                            .build();
                })
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}
