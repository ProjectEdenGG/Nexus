package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.api.common.utils.TimeUtils.Timespan.FormatType;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.TimespanBuilder;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.Loadout;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchBeginEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchTimerTickEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDamageEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.TNTTagMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import gg.projecteden.nexus.features.nameplates.Nameplates;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.RandomUtils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public final class TNTTag extends TeamMechanic {

	@Override
	public @NotNull String getName() {
		return "TNT Tag";
	}

	@Override
	public @NotNull String getDescription() {
		return "TODO";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.TNT);
	}

	@Override
	public boolean isTestMode() {
		return true;
	}

	@Override
	public boolean shouldAutoEndOnZeroTimeLeft() {
		return false;
	}

	@Override
	public void broadcastTimeLeft(Match match, int time) {
		// do nothing
	}

	@Override
	public boolean usesAutoBalancing() {
		return false;
	}

	@Override
	public boolean allowFriendlyFire() {
		return true;
	}

	@Override
	public boolean shouldShowHealthInNameplate() {
		return false;
	}

	@Override
	public boolean useScoreboardNumbers(Match match) {
		return false;
	}

	@Override
	public @NotNull LinkedHashMap<String, Integer> getScoreboardLines(@NotNull Match match) {
		if (!match.isStarted())
			return super.getScoreboardLines(match);

		final TNTTagMatchData matchData = match.getMatchData();
		return new LinkedHashMap<>() {{
			put("&7Round #" + matchData.getRound(), 5);
			put("&f", 4);

			final int tagged = match.getAliveMinigamers(matchData.getTaggedTeam()).size();
			final String time = TimespanBuilder.ofSeconds(match.getTimer().getTime()).format(FormatType.SHORT);
			if (tagged > 0)
				put("&eExplosion in &c" + time, 3);
			else
				put("&eNext round in &c" + time, 3);

			put("&f&f", 2);
			put("&ePrimed TNT: &c" + tagged, 1);
		}};
	}

	@Override
	public void onDamage(@NotNull MinigamerDamageEvent event) {
		final Match match = event.getMatch();
		final TNTTagMatchData matchData = match.getMatchData();
		if (!(event.getOriginalEvent() instanceof EntityDamageEvent damageEvent))
			return;

		final Minigamer minigamer = event.getMinigamer();
		minigamer.damaged();
		damageEvent.setDamage(0.000001);

		if (minigamer.getTeam().equals(matchData.getTaggedTeam()))
			return;

		final Minigamer attacker = event.getAttacker();
		if (!attacker.getTeam().equals(matchData.getTaggedTeam()))
			return;

		tagged(match, minigamer, attacker);
	}

	private static void tagged(Match match, Minigamer tagged, Minigamer tagger) {
		final TNTTagMatchData matchData = match.getMatchData();

		match.broadcast(tagged.getColoredName() + " &3was tagged by " + tagger.getColoredName());

		setTeam(tagged, matchData.getTaggedTeam());
		setTeam(tagger, matchData.getEvadingTeam());
	}

	private static void setTeam(Minigamer tagged, Team team) {
		tagged.setTeam(team);
		Loadout taggedLoadout = team.getLoadout();
		taggedLoadout.apply(tagged);
		tagged.getOnlinePlayer().getInventory().setHeldItemSlot(0);
		Nameplates.get().getNameplateManager().update(tagged.getOnlinePlayer());
	}

	@Override
	public void onBegin(@NotNull MatchBeginEvent event) {
		final Match match = event.getMatch();
		nextRound(match);

		final TNTTagMatchData matchData = match.getMatchData();

		match.getTasks().repeat(2, 5, () -> {
			match.getAliveMinigamers(matchData.getTaggedTeam()).forEach(tagged -> {
				Minigamer target = Collections.min(match.getMinigamers(), Comparator.comparingDouble(minigamer -> {
					if (minigamer == tagged || !minigamer.isAlive() || minigamer.getTeam().equals(matchData.getTaggedTeam()))
						return Double.MAX_VALUE;

					return Distance.distance(minigamer, minigamer).get();
				}));

				if (target != null)
					tagged.getOnlinePlayer().setCompassTarget(target.getLocation());
			});
		});
	}

	@Override
	public boolean shouldBeOver(@NotNull Match match) {
		final boolean onePlayerLeft = match.getAlivePlayers().size() == 1;
		if (onePlayerLeft)
			Nexus.log("Match has only one player, ending");
		return onePlayerLeft;
	}

	private void nextRound(Match match) {
		final TNTTagMatchData matchData = match.getMatchData();

		if (match.isEnded() || matchData.isEnding())
			return;

		if (shouldBeOver(match)) {
			match.end();
			return;
		}

		final List<Minigamer> minigamers = match.getAliveMinigamers();
		int time = Math.min(60, 30 + ((minigamers.size() / 2) * 5));
		int tnt = Math.max(1, (int) Math.round(minigamers.size() * .2));

		matchData.setRound(matchData.getRound() + 1);

		Collections.shuffle(minigamers);
		while (match.getAliveMinigamers(matchData.getTaggedTeam()).size() < tnt)
			setTeam(RandomUtils.randomElement(match.getAliveMinigamers(matchData.getEvadingTeam())), matchData.getTaggedTeam());

		// TODO Messages & sounds https://i.imgur.com/NeaFdOj.png

		match.getTimer().setTime(time);
		match.getScoreboard().update();
	}

	@Override
	public void announceWinners(@NotNull Match match) {
		final List<Minigamer> minigamers = match.getAliveMinigamers();
		if (minigamers.isEmpty()) {
			Minigames.broadcast("&3No one won in &e" + match.getArena().getDisplayName());
		} else if (minigamers.size() == 1) {
			final Minigamer minigamer = minigamers.getFirst();
			Minigames.broadcast(minigamer.getColoredName() + " &3has won &e" + match.getArena().getDisplayName());
		} else {
			Nexus.severe("Multiple players won TNT Tag: " + minigamers.stream().map(Minigamer::getColoredName).collect(Collectors.joining("&3, ")));
		}
	}

	@EventHandler
	public void on(MatchTimerTickEvent event) {
		final Match match = event.getMatch();
		if (!(match.getMatchData() instanceof TNTTagMatchData matchData))
			return;

		if (event.getTime() != 0)
			return;

		final List<Minigamer> tagged = match.getAliveMinigamers(matchData.getTaggedTeam());
		if (tagged.isEmpty()) {
			nextRound(match);
			return;
		}

		tagged.forEach(minigamer -> {
			if (match.getAliveMinigamers().size() > 2) // Dont deal proximity damage on last round
				minigamer.getLocation().getNearbyPlayers(2).forEach(player -> {
					final Minigamer nearby = Minigamer.of(player);
					if (!nearby.isPlaying(this))
						return;

					if (minigamer.getTeam().equals(nearby.getTeam()))
						return;

					kill(nearby);
				});

			kill(minigamer);
		});

		if (shouldBeOver(match)) {
			match.end();
			return;
		}

		match.getTimer().setTime(10).start();
	}

	@Override
	public void kill(@NotNull Minigamer victim, @Nullable Minigamer attacker) {
		victim.getOnlinePlayer().getWorld().spawnParticle(Particle.EXPLOSION_LARGE, victim.getLocation().add(0, 1, 0), 1);
		// TODO sound?
		super.kill(victim, attacker);
	}

	@Override
	public void onDeath(@NotNull MinigamerDeathEvent event) {
		event.setDeathMessage(event.getMinigamer().getColoredName() + " &eblew up!");
		super.onDeath(event);
	}

}
