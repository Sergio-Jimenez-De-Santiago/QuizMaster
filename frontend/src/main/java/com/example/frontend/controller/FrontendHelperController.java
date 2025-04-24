package com.example.frontend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.frontend.model.Enrolment;

@Controller
public class FrontendHelperController {

  private final RestTemplate restTemplate = new RestTemplate();
  private final String courseServiceUrl;
  private final String enrolementServiceUrl;

  public FrontendHelperController(
      @Value("${course.service.url}") String courseServiceUrl,
      @Value("${enrolment.service.url}") String enrolementServiceUrl) {
    this.courseServiceUrl = courseServiceUrl;
    this.enrolementServiceUrl = enrolementServiceUrl;
  }

  @DeleteMapping("/courses/{id}")
  public String deleteAll(@PathVariable Integer id) {
    ResponseEntity<List<Enrolment>> response = restTemplate.exchange(
        enrolementServiceUrl + "/courses/" + id + "/enrolments",
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<Enrolment>>() {
        });

    List<Enrolment> enrolments = response.getBody();

    if (enrolments != null && !enrolments.isEmpty()) {
      for (Enrolment enrolment : enrolments) {
        try {
          restTemplate.delete(enrolementServiceUrl + "/enrolments/" + enrolment.getEnrolmentId());
        } catch (HttpClientErrorException.NotFound e) {
          System.out.println("Enrolment with ID " + enrolment.getEnrolmentId() + " already deleted.");
        } catch (Exception e) {
          System.out.println(
              "Failed to delete enrolment with ID " + enrolment.getEnrolmentId() + ". " + e.getMessage());
        }
      }
    }

    restTemplate.delete(courseServiceUrl + "/courses/" + id);
    return "redirect:/courses";
  }
}
