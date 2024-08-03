package gg.projecteden.nexus.features.quests.events;

import gg.projecteden.nexus.models.quests.Quest;
import gg.projecteden.nexus.models.quests.Quester;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class QuestCompletedEvent extends QuestEvent {

	public QuestCompletedEvent(@NotNull Quester quester, @NotNull Quest quest) {
		super(quester, quest);
	}

	@Getter
	private static final HandlerList handlerList = new HandlerList();

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}

}
