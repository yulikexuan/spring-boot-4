//: guru.modulith.domain.notification.internal.model.Notification.java

package guru.modulith.notification.domain.model;


import java.time.Instant;

import org.jspecify.annotations.NullMarked;


@NullMarked
public record Notification(
        Instant timestamp, NotificationType format, String name) {

}
