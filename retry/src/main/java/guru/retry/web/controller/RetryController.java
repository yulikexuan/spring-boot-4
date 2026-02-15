//: guru.retry.web.controller.RetryController.java

package guru.retry.web.controller;


import java.util.UUID;

import guru.retry.service.RetryableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/retry")
@RequiredArgsConstructor
class RetryController {

    private final RetryableService retryableService;

    @GetMapping("{id}")
    public ResponseEntity<String> retry(@PathVariable UUID id) {
        var data = retryableService.tryFetchDataById(id);
        return ResponseEntity.ok(data);
    }

} /// :~