package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public enum Pugmas25DeathCause {
	UNKNOWN("<player> died", "<player> stopped existing for unknown reasons", "<player> has left this plane of existence"),
	GEYSER("<player> was boiled alive", "<player> tried taking a lava bath", "<player> was cooked medium rare", "<player> took a steam bath… permanently"),
	INSTANT_DEATH("<player> had really bad luck", "<player> experienced instant regret", "<player> rolled a natural 1"),
	FALL("<player> forgot fall damage existed", "<player> misjudged that jump", "<player> tested gravity’s loyalty"),
	STARVATION("<player> forgot to eat", "<player> starved to death", "<player> thought hunger was optional", "<player> should’ve packed snacks", "<player> learned you can’t live on Christmas spirit alone"),
	ELYTRA("<player> needs more practice with an elytra", "<player> became one with the wall"),
	TRAIN("<player> found out what happens when unstoppable meets squishable", "<player> became train-shaped paste", "<player> got flattened by holiday cheer", "<player> mistook the tracks for a crosswalk"),
	MONSTER("<player> died fighting a monster");

	final List<String> messages;

	Pugmas25DeathCause(String... messages) {
		this.messages = new ArrayList<>(List.of(messages));
	}

	public static @NotNull Pugmas25DeathCause from(PlayerDeathEvent event) {
		EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();

		Pugmas25DeathCause deathCause = Pugmas25DeathCause.UNKNOWN;
		if (damageEvent != null) {
			deathCause = switch (damageEvent.getCause()) {
				case FALL -> Pugmas25DeathCause.FALL;
				case STARVATION -> Pugmas25DeathCause.STARVATION;
				case FLY_INTO_WALL -> Pugmas25DeathCause.ELYTRA;
				case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK, ENTITY_EXPLOSION, PROJECTILE, MAGIC ->
					Pugmas25DeathCause.MONSTER;
				default -> Pugmas25DeathCause.UNKNOWN;
			};
		}
		return deathCause;
	}

	public String getMessage(Player player) {
		String message = RandomUtils.randomElement(messages);
		return message.replaceAll("<player>", Nickname.of(player));
	}
}
