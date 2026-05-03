//: spring.boot.sfg7.DonutClientApp.java

package spring.boot.sfg7;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.client.RestClient;
import spring.boot.sfg7.domain.donut.model.Donut;
import spring.boot.sfg7.domain.donut.model.View;


@Slf4j
class DonutClientApp implements ApplicationRunner {

    private final RestClient client;

    public DonutClientApp() {
        this.client = RestClient.builder()
                .baseUrl("http://localhost:8080")
                .build();
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(DonutClientApp.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // Create a Donut with ALL fields populated
        Donut clientDonut = new Donut(
                "Maple Bar",
                Donut.Glaze.MAPLE,
                List.of("pecans", "bacon"),
                new BigDecimal("3.99"),
                false,
                450,  // Client tries to set calories
                LocalDateTime.now().minusHours(2)  // Client tries to set bakedAt
        );

        log.info("\nUsing hint() with Views.Summary.class - only type and price will be sent");

        // POST with hint() - only Summary fields (type, price) are serialized and sent
        var newDonut = this.client.post()
                .uri("/api/donuts")
                .hint(JsonView.class.getName(), View.Summary.class)
                .body(clientDonut)
                .retrieve()
                .body(Donut.class);

        log.info(">>> New donut: {}", newDonut);
    }

} /// :~