package service.controllers;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import java.net.UnknownHostException;
import java.time.LocalDate;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import service.quiz.Quiz;
import service.quiz.QuizSession;

@RestController
public class QuizController {
    private Map<Integer, Quiz> quizzes = new HashMap<>();
    private Map<String, QuizSession> activeSessions = new HashMap<>();

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
            @RequestBody String title, Map<Integer, String> questions, double timeLeft, Map<Integer, String> answers,
            LocalDate dueDate) {
        Quiz quiz = new Quiz(title, questions, timeLeft, answers, dueDate);
        quizzes.put(quiz.getId(), quiz);
        String url = "http://" + getHost() + "/quizzes/"
                + quiz.getTitle();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", url)
                .header("Content-Location", url)
                .body(quiz);
    }

    @PostMapping("/quizzes/{quizId}/start")
    public ResponseEntity<Object> startQuiz(
            @PathVariable int quizId,
            @RequestParam String studentId) {

        Quiz quiz = quizzes.get(quizId);
        if (quiz == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Quiz not found.");
        }

        String sessionKey = studentId + ":" + quizId;

        if (activeSessions.containsKey(sessionKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Quiz already started or attempted.");
        }

        QuizSession session = new QuizSession(studentId, quizId);
        activeSessions.put(sessionKey, session);

        Map<String, Object> quizStartPayload = new HashMap<>();
        quizStartPayload.put("title", quiz.getTitle());
        quizStartPayload.put("instructions", "Good luck! You have " + quiz.getTimeLeft() + " minutes.");
        quizStartPayload.put("timeLeft", quiz.getTimeLeft());
        quizStartPayload.put("questions", quiz.getQuestions()); 

        return ResponseEntity.ok(quizStartPayload);
    }
    @PostMapping("/quizzes/{quizId}/attempt")
    public ResponseEntity<Object> attemptQuiz(
            @PathVariable int quizId,
            @RequestParam String studentId,
            @RequestParam Map<Integer, String> studentAnswers) {

        String sessionKey = studentId + ":" + quizId;

        QuizSession activeSession = activeSessions.get(sessionKey);

        if (activeSession == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Quiz session not found.");
        }
            
        if (activeSession.isSubmitted()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quiz already submitted.");
        }
            
        activeSession.setStudentAnswers(studentAnswers);
        activeSession.setSubmitted();

        return ResponseEntity.ok("Quiz attempt submitted successfully.");

    }

}