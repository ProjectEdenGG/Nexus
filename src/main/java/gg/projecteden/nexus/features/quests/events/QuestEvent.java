package gg.projecteden.nexus.features.quests.events;

import gg.projecteden.nexus.models.quests.Quest;
import gg.projecteden.nexus.models.quests.Quester;
import lombok.Data;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Data
public class QuestEvent extends Event {
	@Getter
	private static final HandlerList handlerList = new HandlerList();
	private final Quester quester;
	private final Quest quest;

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}
}
