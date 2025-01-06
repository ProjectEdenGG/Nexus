package gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams;

import gg.projecteden.api.common.utils.CompletableFutures;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Team;
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

public abstract class TeamVanillaMechanic extends TeamMechanic implements VanillaMechanic<Team> {
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
		match.getMinigamers().forEach(minigamer -> {
			minigamer.addPotionEffect(new PotionEffectBuilder(PotionEffectType.RESISTANCE).duration(TickTime.SECOND.x(20)).amplifier(10));
			minigamer.addPotionEffect(new PotionEffectBuilder(PotionEffectType.BLINDNESS).duration(TickTime.SECOND.x(5)).amplifier(10));
			minigamer.addPotionEffect(new PotionEffectBuilder(PotionEffectType.LEVITATION).duration(TickTime.SECOND.x(5)).amplifier(255));

			minigamer.getOnlinePlayer().setVelocity(new Vector(0, 0, 0));
		});
		match.getAliveTeams().forEach(team -> Tasks.async(() -> randomTeleport(match, team)));
	}

	@Override
	public @NotNull CompletableFuture<Void> onRandomTeleport(@NotNull Match match, @NotNull Team team, @NotNull Location location) {
		return CompletableFutures.joinAll(team.getMinigamers(match).stream().map(minigamer -> minigamer.teleportAsync(location)));
	}
}
