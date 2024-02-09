package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.DeathSwapMatchData;
import gg.projecteden.nexus.features.minigames.models.matchdata.DeathSwapMatchData.Swap;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessVanillaMechanic;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.TitleBuilder;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public final class DeathSwap extends TeamlessVanillaMechanic {
	@Override
	public @NotNull String getName() {
		return "Death Swap";
	}

	@Override
	public @NotNull String getDescription() {
		return "Prepare traps to kill players as you randomly swap locations every 1-2 minutes after a 4 minute grace period";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.ENDER_PEARL);
	}

	@Getter
	public final int worldDiameter = 2000;
	@Getter
	public final String worldName = "deathswap";

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);

		event.getMatch().getTasks().wait(TickTime.MINUTE.x(3), () -> delay(event.getMatch()));
	}

	@Override
	public void onDeath(@NotNull MinigamerDeathEvent event) {
		if (!event.getMinigamer().isAlive())
			return;

		event.getMinigamer().setAlive(false);

		if (event.getAttacker() != null) {
			event.getAttacker().scored();
		} else {
			DeathSwapMatchData matchData = event.getMatch().getMatchData();
			Minigamer killer = matchData.getKiller(event.getMinigamer());
			if (killer != null) {
				event.setDeathMessage(event.getMinigamer().getColoredName() + " &3was killed by " + killer.getColoredName());
				killer.scored();
			}
		}

		dropItems(event.getMinigamer());
		super.onDeath(event);
	}

	@Override
	public void onDeath(@NotNull Minigamer victim) {
		final Player player = victim.getOnlinePlayer();
		player.setGameMode(GameMode.SPECTATOR);
		player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.BLINDNESS).duration(80).amplifier(250).build());
		new TitleBuilder().players(player).subtitle("&cYou died").stay(40).send();
	}

	@Override
	public void onEnd(@NotNull MatchEndEvent event) {
		super.onEnd(event);
		event.getMatch().getMinigamers().forEach(minigamer -> minigamer.getPlayer().setGameMode(GameMode.SURVIVAL));
	}

	public void swap(Match match) {
		Minigames.debug("DeathSwap#swap");
		List<Minigamer> swappingList = match.getAliveMinigamers();
		swappingList.forEach(minigamer -> {
			minigamer.addPotionEffect(new PotionEffectBuilder(PotionEffectType.BLINDNESS).duration(30));
			ActionBarUtils.sendActionBar(minigamer.getOnlinePlayer(), "&3SWAPPING");
		});
		Collections.shuffle(swappingList);

		if (swappingList.size() % 2 != 0) {
			Swap one = new Swap(swappingList.remove(0));
			Swap two = new Swap(swappingList.remove(0));
			Swap three = new Swap(swappingList.remove(0));

			Minigames.debug("DeathSwap#swap three-way swapping %s, %s, %s".formatted(one.getSwapped().getNickname(), two.getSwapped().getNickname(), three.getSwapped().getNickname()));
			one.with(three);
			two.with(one);
			three.with(two);
		}
		while (swappingList.size() > 0) {
			Swap one = new Swap(swappingList.remove(0));
			Swap two = new Swap(swappingList.remove(0));
			Minigames.debug("DeathSwap#swap swapping %s, %s".formatted(one.getSwapped().getNickname(), two.getSwapped().getNickname()));
			one.with(two);
			two.with(one);
		}
		aggroMobs(match);
		delay(match);
	}

	public void aggroMobs(Match match) {
		match.getAliveMinigamers().forEach(player ->
				EntityUtils.getNearbyEntities(player.getLocation(), 15).keySet().stream()
						.filter(entity -> entity instanceof Mob)
						.map(entity -> (Mob) entity)
						.filter(mob -> mob.getTarget() instanceof Player)
						.forEach(mob -> mob.setTarget(player.getPlayer())));
	}

	public void delay(Match match) {
		if (match.getMinigamers().size() <= 1) {
			match.end();
			return;
		}

		match.getTasks().countdown(Tasks.Countdown.builder()
				.duration(TickTime.SECOND.x(RandomUtils.randomInt(120, 180)))
				.onSecond(i -> {
					if (i < 4)
						match.broadcast("Swapping in " + i + "...");
				}).onComplete(() -> swap(match)));
	}

}
