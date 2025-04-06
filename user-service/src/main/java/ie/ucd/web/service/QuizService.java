package ie.ucd.web.service;

import ie.ucd.web.model.Quiz;
import ie.ucd.web.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizService {
    private final QuizRepository quizRepository;
    @Autowired
    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    public Quiz createQuiz(Quiz quiz) {
        if (quizRepository.findByName(quiz.getName()) != null) {
            throw new IllegalArgumentException("Quiz with name " + quiz.getName() + " already exists");
        }
        return quizRepository.save(quiz);
    }

    public Quiz updateQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public Quiz getQuizById(Long id) {
        return quizRepository.findById(id).orElse(null);
    }

    public List<Quiz> getAllQuizzes() {
        return  (List<Quiz>) quizRepository.findAll();
    }
}
