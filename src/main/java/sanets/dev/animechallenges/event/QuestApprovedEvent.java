package sanets.dev.animechallenges.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import sanets.dev.animechallenges.model.quest.Quest;

import java.util.UUID;

@Getter
public class QuestApprovedEvent extends ApplicationEvent {

    private final UUID userUid;
    private final Quest quest;

    public QuestApprovedEvent(Object source, UUID userUid, Quest quest) {
        super(source);
        this.userUid = userUid;
        this.quest = quest;
    }
}
