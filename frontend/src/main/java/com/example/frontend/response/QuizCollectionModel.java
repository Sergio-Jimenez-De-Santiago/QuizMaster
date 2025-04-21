package com.example.frontend.response;

import java.util.List;

import org.springframework.hateoas.EntityModel;

import com.example.frontend.dto.QuizDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

    public class QuizCollectionModel {
    @JsonProperty("_embedded")
    private Embedded embedded;

    public List<EntityModel<QuizDTO>> getQuizzes() {
        return embedded.quizDTOList;
    }

    static class Embedded {
        @JsonProperty("quizDTOList") 
        private List<EntityModel<QuizDTO>> quizDTOList;
    }
}


