package com.example.quiz.assembler;

import com.example.quiz.controller.QuizController;
import com.example.quiz.dto.QuizDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class QuizModelAssembler implements RepresentationModelAssembler<QuizDTO, EntityModel<QuizDTO>> {

    @Override
    public EntityModel<QuizDTO> toModel(QuizDTO quiz) {
        return EntityModel.of(quiz,
                linkTo(methodOn(QuizController.class).getQuiz((int) quiz.getId())).withSelfRel(),
                linkTo(methodOn(QuizController.class).getQuizzes(null)).withRel("quizzes"),
                linkTo(methodOn(QuizController.class).getQuizzes(quiz.getCourseId())).withRel("byCourse"));
    }
}