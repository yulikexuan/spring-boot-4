//: guru.modulith.domain.notification.service.NotificationMapper.java

package guru.modulith.notification.domain.service;


import guru.modulith.notification.domain.dto.NotificationDto;
import guru.modulith.notification.domain.model.Notification;
import guru.modulith.notification.domain.model.NotificationType;
import org.jspecify.annotations.NullMarked;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@NullMarked
@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "timestamp", target = "timestamp")
    @Mapping(source = "format", target = "format")
    @Mapping(source = "name", target = "name")
    NotificationDto toDto(Notification notification);

    @Mapping(source = "timestamp", target = "timestamp")
    @Mapping(source = "format", target = "format")
    @Mapping(source = "name", target = "name")
    Notification toEntity(NotificationDto dto);

    default String mapNotificationTypeToString(NotificationType notificationType) {
        return notificationType.name();
    }

    default NotificationType mapStringToNotificationType(String format) {
        try {
            return NotificationType.valueOf(format);
        } catch (IllegalArgumentException e) {
            return NotificationType.UNKNOWN;
        }
    }

}
