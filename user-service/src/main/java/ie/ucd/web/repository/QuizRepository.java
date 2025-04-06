package ie.ucd.web.repository;

import ie.ucd.web.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Quiz findByName(String name);
}
