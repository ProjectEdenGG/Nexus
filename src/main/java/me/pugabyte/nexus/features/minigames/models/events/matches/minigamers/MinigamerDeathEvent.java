package me.pugabyte.nexus.features.minigames.models.events.matches.minigamers;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Getter
public class MinigamerDeathEvent extends MinigamerEvent {
	@Nullable
	private final Minigamer attacker;
	@Nullable
	private final Event originalEvent;
	@Setter
	private String deathMessage = "";

	public MinigamerDeathEvent(@NonNull Minigamer victim) {
		super(victim);
		attacker = null;
		originalEvent = null;
	}

	public MinigamerDeathEvent(@NonNull Minigamer victim, @Nullable Minigamer attacker) {
		super(victim);
		this.attacker = attacker;
		originalEvent = null;
	}

	public MinigamerDeathEvent(@NonNull Minigamer victim, @Nullable Event originalEvent) {
		super(victim);
		attacker = null;
		this.originalEvent = originalEvent;
	}

	public MinigamerDeathEvent(@NonNull Minigamer victim, @Nullable Minigamer attacker, @Nullable Event originalEvent) {
		super(victim);
		this.attacker = attacker;
		this.originalEvent = originalEvent;
	}

	public void broadcastDeathMessage() {
		if (deathMessage == null) return;
		if (deathMessage.length() == 0)
			if (attacker == null)
				getMatch().broadcast(minigamer.getColoredName() + " &3died");
			else
				getMatch().broadcast(minigamer.getColoredName() + " &3was killed by " + attacker.getColoredName());
		else
			getMatch().broadcast(deathMessage);
	}

}
