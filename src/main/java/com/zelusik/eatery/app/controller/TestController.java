package com.zelusik.eatery.app.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/test")
@RestController
public class TestController {

    @GetMapping
    public ResponseEntity<TestResponse> testGet(
            @NotBlank @RequestParam String param1,
            @Length(min = 3, max = 10) @RequestParam String param2
    ) throws Exception {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new TestResponse(param1, param2, null));
    }

    @PostMapping
    public ResponseEntity<TestResponse> testPost(
            @RequestParam String param1,
            @Valid @RequestBody TestRequest testRequest
    ) throws Exception {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new TestResponse(param1, testRequest.getParam2(), testRequest.getParam3()));
    }

    @GetMapping("/no-content")
    public ResponseEntity<Void> testReturnNoContent() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    private static class TestRequest {
        @NotNull
        private String param2;
        @NotBlank
        private String param3;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    private static class TestResponse {
        private String res1;
        private String res2;
        private String res3;
    }
}
