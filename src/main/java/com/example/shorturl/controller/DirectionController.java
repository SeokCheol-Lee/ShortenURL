package com.example.shorturl.controller;

import com.example.shorturl.dto.ShortenUrlForm;
import com.example.shorturl.service.UrlService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DirectionController {

    private final UrlService urlService;
    private final String BASE_URL = "http://localhost:8080/";

    @PostMapping("/shorten")
    public ResponseEntity<String> getShortenUrl(@RequestBody ShortenUrlForm form){
        return ResponseEntity.ok(BASE_URL+urlService.generateShortenUrl(form.getUrl()));
    }

    @GetMapping("/{encodedId}")
    public ResponseEntity<?> redirectUrl(@PathVariable String encodedId){
        String directionUrl = urlService.redirectUrl(encodedId);
        log.info("encoded : "+encodedId);
        log.info("direction : "+directionUrl);
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
            .location(URI.create(directionUrl)).build();
    }


}
