package gg.projecteden.nexus.features.minigames.models.events.lobby;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.perks.common.GadgetPerk;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class MinigamerUseGadgetEvent extends MinigamerLobbyEvent {
	private final GadgetPerk perk;
	protected boolean cancelled;

	public MinigamerUseGadgetEvent(Minigamer minigamer, GadgetPerk perk) {
		super(minigamer);
		this.perk = perk;
	}

	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
