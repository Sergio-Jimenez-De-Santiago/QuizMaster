package service.controllers;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import java.net.UnknownHostException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import service.quiz.Quiz;

@RestController
public class quizController {
    private Map<Integer, Quiz> quizzes = new HashMap<>();
    @Value("${server.port}")
    private int serverPort;

    private String getHost() {
        try {
            InetAddress host = InetAddress.getLocalHost();
            return "%s:%d".formatted(host.getHostAddress(),
                    serverPort);
        } catch (UnknownHostException unknownHostException) {
            unknownHostException.printStackTrace();
        }
        return "";
    }

    @GetMapping(value = "/quizzes", produces = "application/json")
    public ResponseEntity<ArrayList<String>> getQuizzes() {
        ArrayList<String> list = new ArrayList<>();
        for (Quiz quiz : quizzes.values()) {
            list.add("http://" + getHost()
                    + "/quizzes/" + quiz.getTitle());
        }

        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping(value = "/quizzes/{id}", produces = { "application/json" })
    public ResponseEntity<Quiz> getQuiz(
            @PathVariable Integer id) {
        Quiz quiz = quizzes.get(id);
        if (quiz == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(quiz);
    }

    @DeleteMapping("/quizzes/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable int id) {
        Quiz quiz = quizzes.remove(id);
        if (quiz == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @PostMapping(value = "/quizzes", consumes = "application/json")
    public ResponseEntity<Quiz> createQuiz(
            @RequestBody  String title, Map<Integer, String> questions,double timeLeft) {
        Quiz quiz = new Quiz(title, questions, timeLeft);
        quizzes.put(quiz.getId(), quiz);
        String url = "http://" + getHost() + "/quizzes/"
                + quiz.getTitle();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", url)
                .header("Content-Location", url)
                .body(quiz);
    }
}