//: guru.modulith.domain.notification.service.NotificationDto.java

package guru.modulith.notification.domain.dto;


import java.time.Instant;

import org.jspecify.annotations.NullMarked;


@NullMarked
public record NotificationDto(Instant timestamp, String format, String name) {

}
