//: spring.boot.sfg7.domain.donut.model.Donut.java

package spring.boot.sfg7.domain.donut.model;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;


public record Donut(
        @JsonView(View.Summary.class) String type,
        @JsonView(View.Public.class) Glaze glaze,
        @JsonView(View.Public.class) List<String> toppings,
        @JsonView(View.Summary.class)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "$#.##")
        BigDecimal price,
        @JsonView(View.Public.class) Boolean isVegan,
        @JsonView(View.Internal.class)Integer calories,
        @JsonView(View.Internal.class)LocalDateTime bakedAt) {

    public enum Glaze {
        CHOCOLATE,
        VANILLA,
        STRAWBERRY,
        MAPLE,
        CINNAMON_SUGAR,
        POWDERED_SUGAR,
        NONE
    }

} /// :~