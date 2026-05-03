//: spring.boot.sfg7.bootstrap.DataLoader.java

package spring.boot.sfg7.bootstrap;


import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import spring.boot.sfg7.domain.donut.model.Donut;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;


@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    static final String DONUTS_JSON_PATH = "classpath:/data/donuts-menu.json";

    private final JsonMapper jsonMapper;
    private final ResourceLoader resourceLoader;

    private List<Donut> donuts;

    @Override
    public void run(String... args) throws Exception {

        log.info(">>> Loading Donuts \uD83C\uDF69");

        var res = resourceLoader.getResource(DONUTS_JSON_PATH);

        if(!res.exists()) {
            log.error(">>> Donut menu file not found at: {}", DONUTS_JSON_PATH);
            return;
        }

        this.donuts = jsonMapper.readValue(
                res.getInputStream(),
                new TypeReference<>() {});

        donuts.forEach(System.out::println);

        validateSerialization(donuts);
    }

    private void validateSerialization(List<Donut> donuts) {

        log.info(">>> Serializing Donuts \uD83C\uDF69");

        if(!donuts.isEmpty()) {
            String json = jsonMapper.writeValueAsString(donuts.getFirst());
            // NOTE: How are properties in the JSON sorted?
            log.info("\n{}", json);
        }
    }

    public List<Donut> donuts() {
        return List.copyOf(donuts);
    }

} /// :~