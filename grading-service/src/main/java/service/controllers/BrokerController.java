package service.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import service.core.ClientInfo;
import service.core.Offer;
import service.core.Quotation;

@RestController
public class BrokerController {
    private Map<Integer, Offer> offers = new TreeMap<>();
    private List<String> quotationServiceUrls = List.of(
        "http://localhost:8081/quotations",
        "http://localhost:8082/quotations",
        "http://localhost:8083/quotations");
    
    @Value("${server.port}")
    private int serverPort;
    
    private String getHost() {
        return "localhost:" + serverPort;
    }
    
    @PostMapping(value = "/offers", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Offer> createOffer(@RequestBody ClientInfo info) {
        Offer offer = new Offer(info);
        
        RestTemplate restTemplate = new RestTemplate();
        for (String serviceUrl : quotationServiceUrls) {
            try {
                ResponseEntity<Quotation> response = restTemplate.postForEntity(serviceUrl, info, Quotation.class);
                if (response.getStatusCode() == HttpStatus.CREATED) {
                    Quotation quotation = response.getBody();
                    offer.quotations.add(quotation);
                }
            } catch (Exception e) {
                System.out.println("Error with service " + serviceUrl + ": " + e.getMessage());
            }
        }
        
        offers.put(offer.id, offer);
        String offerUrl = "http://" + getHost() + "/offers/" + offer.id;
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", offerUrl)
                .header("Content-Location", offerUrl)
                .body(offer);
    }
    
    @GetMapping(value = "/offers", produces = "application/json")
    public ResponseEntity<List<String>> getOffers() {
        List<String> offerUrls = new ArrayList<>();
        for (Integer id : offers.keySet()) {
            offerUrls.add("http://" + getHost() + "/offers/" + id);
        }
        return ResponseEntity.status(HttpStatus.OK).body(offerUrls);
    }
    
    @GetMapping(value = "/offers/{id}", produces = "application/json")
    public ResponseEntity<Offer> getOffer(@PathVariable int id) {
        Offer offer = offers.get(id);
        if (offer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(offer);
    }
}
