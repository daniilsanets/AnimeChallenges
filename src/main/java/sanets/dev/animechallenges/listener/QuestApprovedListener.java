package sanets.dev.animechallenges.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import sanets.dev.animechallenges.event.QuestApprovedEvent;
import sanets.dev.animechallenges.service.processing.BadgeProcessorService;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuestApprovedListener {

    private final BadgeProcessorService badgeProcessor;

    @EventListener
    public void handleQuestApprovedEvent(QuestApprovedEvent questApprovedEvent) {
        log.info("QuestApprovedEvent received {}", questApprovedEvent);
        badgeProcessor.processQuestCompletion(questApprovedEvent.getUserUid(), questApprovedEvent.getQuest());
    }
}
