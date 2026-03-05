package com.likhith.quizservice.feign;

import com.likhith.quizservice.model.QuestionWrapper;
import com.likhith.quizservice.model.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@FeignClient(name = "QUESTION-SERVICE")
public interface QuizInterface {

    @GetMapping("/question/generate")
    ResponseEntity<List<Integer>> getQuestionsForQuiz(
            @RequestParam String categoryName,
            @RequestParam Integer numQuestions);

    @PostMapping("/question/getQuestions")
    ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(
            @RequestBody List<Integer> questionIds);

    @PostMapping("/question/getScore")
    ResponseEntity<Integer> getScore(
            @RequestBody List<Response> responses);
}