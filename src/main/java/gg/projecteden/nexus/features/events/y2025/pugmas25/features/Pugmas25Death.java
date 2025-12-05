package gg.projecteden.nexus.features.events.y2025.pugmas25.features;

import gg.projecteden.nexus.features.commands.DeathMessagesCommand;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25DeathCause;
import gg.projecteden.nexus.models.deathmessages.DeathMessages;
import gg.projecteden.nexus.models.deathmessages.DeathMessagesService;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class Pugmas25Death implements Listener {
	private static final Location BLACK_BOX = Pugmas25.get().location(-637.5, 66.0, -3260.5, 180, 0);

	public static void onDeath(Player player, @NotNull Pugmas25DeathCause deathType) {
		onDeath(player, deathType, null);
	}

	public static void onDeath(Player player, @NotNull Pugmas25DeathCause deathType, String defaultMessage) {
		player.teleportAsync(BLACK_BOX);
		// Health
		player.setHealth(5);
		player.setAbsorptionAmount(5);

		// Hunger
		player.setFoodLevel(20);
		player.setSaturation(5);
		player.setExhaustion(5);

		// EXP
		int expToDrop = getExpToDrop(player);
		player.setTotalExperience(0);
		player.setLevel(0);
		player.setExp(0);
		player.giveExp(expToDrop);

		// Effects
		player.clearActivePotionEffects();

		// Status Resets
		player.setFireTicks(0);
		player.setFreezeTicks(0);
		player.setRemainingAir(player.getMaximumAir());
		player.setArrowsInBody(0);

		// Motion
		player.setVelocity(new Vector(0, 0, 0));
		player.setFallDistance(0);

		// Vanilla Invulnerability
		player.setNoDamageTicks(20);

		String message =
			deathType == Pugmas25DeathCause.MONSTER && defaultMessage != null ?
				defaultMessage : deathType.getMessage(player);

		Pugmas25.get().broadcast(message);
		Pugmas25.get().fadeToBlack(player, "&c&lYou died.", 30)
			.thenRun(() -> player.teleportAsync(Pugmas25.get().getRespawnLocation(player)));
	}

	public static int getExpToDrop(Player player) {
		int level = player.getLevel();
		int total = player.getTotalExperience();
		int xpForNext = getExpForNextLevel(level);

		return Math.min(total, xpForNext / 2);
	}

	private static int getExpForNextLevel(int level) {
		if (level >= 30)
			return 112 + (level - 30) * 9;
		else if (level >= 15)
			return 37 + (level - 15) * 5;
		else
			return 7 + level * 2;
	}

	public static void onPlayerDeath(PlayerDeathEvent event) {
		event.setCancelled(true);
		Pugmas25DeathCause deathCause = Pugmas25DeathCause.from(event);

		String defaultMessage = null;
		Component deathMessageRaw = event.deathMessage();
		if (deathCause == Pugmas25DeathCause.MONSTER && deathMessageRaw instanceof TranslatableComponent deathMessage) {
			DeathMessages deathMessages = new DeathMessagesService().get(event.getPlayer());
			JsonBuilder json = new JsonBuilder(deathMessage.args(deathMessage.args().stream().map(arg -> DeathMessagesCommand.handleArgument(deathMessages, arg)).toList()));
			defaultMessage = AdventureUtils.asPlainText(json.build());
		}

		onDeath(event.getPlayer(), deathCause, defaultMessage);
	}

}
