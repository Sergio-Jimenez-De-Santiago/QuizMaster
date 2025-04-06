package ie.ucd.web;

import ie.ucd.web.model.Quiz;
import ie.ucd.web.repository.QuizRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitialiser implements CommandLineRunner {
        @Autowired
        private QuizRepository quizRepository;

        @Override
        @Transactional
        public void run(String... args) {
                if (quizRepository.findAll().isEmpty()) {
                        Quiz quiz1 = new Quiz(
                                        "Maths quiz",
                                        60,
                                        "Algebra maths quiz.",
                                        true);

                        quizRepository.save(quiz1);
                }
        }
}
