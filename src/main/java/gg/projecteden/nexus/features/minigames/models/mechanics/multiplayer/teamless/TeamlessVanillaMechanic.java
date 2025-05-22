package gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless;

import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchInitializeEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.VanillaMechanic;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public abstract class TeamlessVanillaMechanic extends TeamlessMechanic implements VanillaMechanic<Minigamer> {
	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);
		VanillaMechanic.super.onStart(event);
	}

	@Override
	public void onInitialize(@NotNull MatchInitializeEvent event) {
		super.onInitialize(event);
		resetBorder();
	}

	@Override
	public void onEnd(@NotNull MatchEndEvent event) {
		super.onEnd(event);
		resetBorder();
	}

	@Override
	public @NotNull GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@Override
	public boolean canOpenInventoryBlocks() {
		return true;
	}

	@Override
	public boolean canDropItem(@NotNull ItemStack item) {
		return true;
	}

	@Override
	public boolean useNaturalDeathMessage() {
		return true;
	}

	@Override
	public void spreadPlayers(@NotNull Match match) {
		for (Minigamer minigamer : match.getMinigamers()) {
			minigamer.addPotionEffect(new PotionEffectBuilder(PotionEffectType.BLINDNESS).duration(TimeUtils.TickTime.SECOND.x(5)).amplifier(10));
			minigamer.addPotionEffect(new PotionEffectBuilder(PotionEffectType.RESISTANCE).infinite().maxAmplifier());

			minigamer.getOnlinePlayer().setVelocity(new Vector(0, 0, 0));
			Tasks.async(() -> randomTeleport(match, minigamer));
		}
	}

	@Override
	public @NotNull CompletableFuture<Boolean> onRandomTeleport(@NotNull Match match, @NotNull Minigamer minigamer, @NotNull Location location) {
		return minigamer.teleportAsync(location).thenApply(result -> {
			minigamer.removePotionEffect(PotionEffectType.RESISTANCE);
			minigamer.addPotionEffect(new PotionEffectBuilder(PotionEffectType.RESISTANCE).duration(400).maxAmplifier());
			return result;
		});
	}
}
