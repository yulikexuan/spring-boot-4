//: guru.modulith.domain.notification.service.NotificationService.java

package guru.modulith.notification.domain.service;


import guru.modulith.notification.domain.dto.NotificationDto;
import guru.modulith.notification.domain.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


public interface NotificationService {

    String NOTIFICATION_TEMPLATE = ">>> Received notification by module dependency for product {} in date {} by {}.";

    // void createNotification(NotificationDto event);

}


@Slf4j
@Service
@NullMarked
@RequiredArgsConstructor
class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void createNotification(NotificationDto event) {

        final Notification notification = notificationMapper.toEntity(event);

        log.info(NOTIFICATION_TEMPLATE,
                notification.name(), notification.timestamp(), notification.format());
    }

}