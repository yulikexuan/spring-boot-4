//: spring.boot.sfg7.web.controller.DonutController.java

package spring.boot.sfg7.web.controller;


import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.boot.sfg7.bootstrap.DataLoader;
import spring.boot.sfg7.domain.donut.model.Donut;
import spring.boot.sfg7.domain.donut.model.View;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/donuts")
class DonutController {

    private final DataLoader dataLoader;

    // Test JSON Views

    @GetMapping("/summary")
    @JsonView(View.Summary.class)
    public List<Donut> summary() {
        log.debug(">>> Fetching donuts with Summary view");
        return dataLoader.donuts();
    }

    @GetMapping("/public")
    @JsonView(View.Public.class)
    public List<Donut> publicDonuts() {
        log.debug(">>> Fetching donuts with Public view");
        return dataLoader.donuts();
    }

    @GetMapping("/internal")
    @JsonView(View.Internal.class)
    public List<Donut> internalDonuts() {
        log.debug(">>> Fetching donuts with Internal view");
        return dataLoader.donuts();
    }

    @GetMapping("/admin")
    @JsonView(View.Admin.class)
    public List<Donut> getAdmin() {
        log.debug("Fetching donuts with Admin view");
        return dataLoader.donuts();
    }

    // POST endpoint demonstrating @JsonView for Deserialization
    // Only accepts 'type' and 'price' from client (Summary view)
    // Server generates the rest (glaze, toppings, calories, bakedAt, etc.)
    @PostMapping
    @JsonView(View.Summary.class)
    public Donut createDonut(
            @RequestBody @JsonView(View.Summary.class) Donut donut) {

        log.info(">>> Received donut creation request: ");
        log.info("  Type: {}", donut.type());
        log.info("  Price: {}", donut.price());
        log.info("  Glaze: {} (should be null - not in Summary view)", donut.glaze());
        log.info("  Toppings: {} (should be null - not in Summary view)", donut.toppings());
        log.info("  IsVegan: {} (should be false - not in Summary view)", donut.isVegan());
        log.info("  Calories: {} (should be null - not in Summary view)", donut.calories());
        log.info("  BakedAt: {} (should be null - not in Summary view)", donut.bakedAt());

        // Server generates the missing fields
        Donut createdDonut = new Donut(
                donut.type(),
                Donut.Glaze.CHOCOLATE,      // Server default
                List.of("sprinkles"),   // Server default
                donut.price(),
                false,              // Server calculates
                300,                        // Server calculates
                LocalDateTime.now()         // Server sets timestamp
        );

        log.info(">>> Returning created donut with server-generated fields");

        return createdDonut;
    }

} /// :~