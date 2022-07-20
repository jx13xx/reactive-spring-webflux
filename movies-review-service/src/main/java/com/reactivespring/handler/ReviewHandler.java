package com.reactivespring.handler;


import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.exception.ReviewNotFoundException;
import com.reactivespring.repository.ReviewReactorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ReviewHandler {

    private ReviewReactorRepository reviewReactorRepository;

    @Autowired
    private Validator validator;

    public ReviewHandler(ReviewReactorRepository reviewReactorRepository){
        this.reviewReactorRepository = reviewReactorRepository;
    }


    public Mono<ServerResponse> addReview(ServerRequest request) {
       return request.bodyToMono(Review.class)
               .doOnNext(this::validate)
                .flatMap(reviewReactorRepository::save)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }

    private void validate(Review review) {
        var constraintViolations = validator.validate(review);
        log.info("constraintViolations: {}", constraintViolations);

        if(constraintViolations.size() > 0){
            var errorMessage =  constraintViolations
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.joining(""));

            throw new ReviewDataException(errorMessage);
        }



    }

    public Mono<ServerResponse> getReview(ServerRequest request) {
        var reviewsFlux = reviewReactorRepository.findAll().log();
        return ServerResponse.ok().body(reviewsFlux, Review.class);
    }

    public Mono<ServerResponse> updateReview(ServerRequest request) {

        var reviewId = request.pathVariable("id");

        var existingReview = reviewReactorRepository.findById(reviewId)
                .switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found for for given id")));

        return existingReview
                .flatMap(review -> request.bodyToMono(Review.class)
                        .map(reqReview -> {
                            review.setComment(reqReview.getComment());
                            review.setRating(reqReview.getRating());

                            return  review;
                        })
                        .flatMap(reviewReactorRepository::save)
                        .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview))
                );
    }

    public Mono<ServerResponse> deleteReview(ServerRequest request) {
        var reviewId = request.pathVariable("id");

        var existingReview = reviewReactorRepository.findById(reviewId);

        return existingReview
                .flatMap(review -> reviewReactorRepository.deleteById(reviewId)
                        .then(ServerResponse.noContent().build()));
    }

    public Mono<ServerResponse> getReviews(ServerRequest request){

        var movieInfoId = request.queryParam("movieInfoId");

        if(movieInfoId.isPresent()){
          var reviewsFlux = reviewReactorRepository
                  .findReviewByMovieInfoId(Long.valueOf(movieInfoId.get()));

          return getBody(reviewsFlux);
          
        }else {
            var reviewsFlux = reviewReactorRepository.findAll();
            return getBody(reviewsFlux);
        }
    }

    private Mono<ServerResponse> getBody(Flux<Review> reviewsFlux) {
        return ServerResponse.ok().body(reviewsFlux, Review.class);
    }

    public Mono<ServerResponse> getReviewById(ServerRequest request) {
        var reviewId = request.pathVariable("id");

        var existingReview = reviewReactorRepository.findById(reviewId);

        return existingReview
                .flatMap(review -> reviewReactorRepository.findById(reviewId)
                        .then(ServerResponse.ok().bodyValue(existingReview))).log();

    }
}
