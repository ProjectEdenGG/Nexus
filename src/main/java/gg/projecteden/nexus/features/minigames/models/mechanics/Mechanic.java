package gg.projecteden.nexus.features.minigames.models.mechanics;

import gg.projecteden.interfaces.Named;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Match.MatchTasks.MatchTaskType;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchBeginEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchInitializeEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MinigamerQuitEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDamageEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.sabotage.MinigamerDisplayTimerEvent;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import gg.projecteden.nexus.features.minigames.models.modifiers.MinigameModifier;
import gg.projecteden.nexus.features.minigames.models.perks.Perk;
import gg.projecteden.nexus.features.minigames.models.perks.common.PlayerParticlePerk;
import gg.projecteden.nexus.features.minigames.modifiers.NoModifier;
import gg.projecteden.nexus.framework.interfaces.HasDescription;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks.Countdown;
import gg.projecteden.nexus.utils.TitleBuilder;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.utils.TimeUtils.TickTime;
import gg.projecteden.utils.TimeUtils.Timespan;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gg.projecteden.nexus.utils.ActionBarUtils.sendActionBar;
import static gg.projecteden.nexus.utils.StringUtils.left;
import static gg.projecteden.nexus.utils.StringUtils.plural;
import static gg.projecteden.nexus.utils.Utils.getMin;

public abstract class Mechanic implements Listener, Named, HasDescription, ComponentLike {

	public Mechanic() {
		Nexus.registerListener(this);
	}

	public @NotNull TextComponent asComponent() {
		return Component.text(getName(), NamedTextColor.YELLOW);
	}

	public @NotNull String getPrefix() {
		return StringUtils.getPrefix(this.getClass());
	}

	public abstract @NotNull ItemStack getMenuItem();

	public boolean isTeamGame() {
		return false;
	}

	public @NotNull GameMode getGameMode() {
		return GameMode.ADVENTURE;
	}

	public boolean canDropItem(@NotNull ItemStack item) {
		return false;
	}

	public boolean usesAlternativeRegen() {
		return false;
	}

	/**
	 * Determines if a user is allowed to use a perk in a specified minigame.
	 * @param perk a user's perk
	 * @param minigamer the user
	 * @return whether or not to allow the perk
	 */
	public boolean usesPerk(@NotNull Class<? extends Perk> perk, @NotNull Minigamer minigamer) {
		return !PlayerParticlePerk.class.isAssignableFrom(perk);
	}

	/**
	 * Determines if a user is allowed to use a perk in a specified minigame.
	 * @param perk a user's perk
	 * @param minigamer the user
	 * @return whether or not to allow the perk
	 */
	public final boolean usesPerk(@NotNull Perk perk, @NotNull Minigamer minigamer) {
		return usesPerk(perk.getClass(), minigamer);
	}

	/**
	 * Whether or not to hide the colors of team-colored perks.
	 */
	public boolean hideTeamLoadoutColors() {
		return false;
	}

	public void onInitialize(@NotNull MatchInitializeEvent event) {
		Match match = event.getMatch();
		int taskId = match.getTasks().repeat(1, TickTime.SECOND, () -> {
			if (match.getScoreboard() != null)
				match.getScoreboard().update();
		});

		match.getTasks().register(MatchTaskType.SCOREBOARD, taskId);
	}

	public void onStart(@NotNull MatchStartEvent event) {
		Match match = event.getMatch();
		match.broadcast("Starting match");
		match.broadcastNoPrefix("");
		int lives = match.getArena().getLives();
		if (lives > 0)
			match.getMinigamers().forEach(minigamer -> minigamer.setLives(lives));
		else
			match.getMinigamers().forEach(minigamer -> {
				if (minigamer.getTeam().getLives() > 0)
					minigamer.setLives(minigamer.getTeam().getLives());
			});

		int beginDelay = match.getArena().getBeginDelay();
		if (beginDelay > 0) {
			int taskId = match.getTasks().countdown(Countdown.builder()
					.duration(TickTime.SECOND.x(beginDelay))
					.onSecond(i -> {
						if (Arrays.asList(60, 30, 15, 5, 4, 3, 2, 1).contains(i))
							match.broadcast("&7Starting in &e" + plural(i + " second", i) + "...");
					})
					.onComplete(() -> {
						MatchBeginEvent beginEvent = new MatchBeginEvent(match);
						if (beginEvent.callEvent())
							onBegin(beginEvent);
					}));

			match.getTasks().register(MatchTaskType.BEGIN_DELAY, taskId);
		} else {
			MatchBeginEvent beginEvent = new MatchBeginEvent(match);
			if (beginEvent.callEvent())
				onBegin(beginEvent);
		}

		int taskId = match.getTasks().repeat(0, 1, () -> match.getMinigamers().forEach(Minigamer::tick));
		match.getTasks().register(MatchTaskType.TICK, taskId);
	}

	public void begin(@NotNull Match match) {
		MatchBeginEvent beginEvent = new MatchBeginEvent(match);
		if (beginEvent.callEvent())
			onBegin(beginEvent);
	}

	public void onBegin(@NotNull MatchBeginEvent event) {
		event.getMatch().setBegun(true);
	}

	public void end(@NotNull Match match) {
		match.end();
	}

	public void onEnd(@NotNull MatchEndEvent event) {

		if (event.getMatch().isStarted())
			announceWinners(event.getMatch());
	}

	public abstract void processJoin(@NotNull Minigamer minigamer);

	public void onJoin(@NotNull MatchJoinEvent event) {
		Minigamer minigamer = event.getMinigamer();
		minigamer.getMatch().broadcast("&e" + minigamer.getNickname() + " &3has joined");
		tellMapAndMechanic(minigamer);
	}

	public void onQuit(@NotNull MinigamerQuitEvent event) {
		Minigamer minigamer = event.getMinigamer();
		minigamer.getMatch().broadcast("&e" + minigamer.getNickname() + " &3has quit");
		if (minigamer.getMatch().isStarted() && shouldBeOver(minigamer.getMatch()))
			minigamer.getMatch().end();
	}

	public void onDamage(@NotNull MinigamerDamageEvent event) {
		Minigamer minigamer = event.getMinigamer();
		minigamer.damaged();
	}

	public void onDeath(@NotNull MinigamerDeathEvent event) {
		if (event.getAttacker() != null && usesAlternativeRegen())
			event.getAttacker().heal(2);

		event.broadcastDeathMessage();
		if (event.getMatch().getScoreboard() != null)
			event.getMatch().getScoreboard().update();
		if (shouldBeOver(event.getMatch()))
			event.getMatch().end();
	}

	public final void kill(@NotNull Minigamer minigamer) {
		kill(minigamer, null);
	}

	public void kill(@NotNull Minigamer victim, @Nullable Minigamer attacker) {
		MinigamerDeathEvent event = new MinigamerDeathEvent(victim, attacker);
		if (!event.callEvent()) return;

		onDeath(event);
	}

	public boolean shouldClearInventory() {
		return true;
	}

	public boolean allowFly() {
		return false;
	}

	public void tellMapAndMechanic(@NotNull Minigamer minigamer) {
		Arena arena = minigamer.getMatch().getArena();
		String mechanicName = arena.getMechanic().getName();
		String arenaName = arena.getDisplayName();
		minigamer.tell("You are playing &e" + mechanicName + (mechanicName.equals(arenaName) ? "" : " &3on &e" + arenaName));
		tellDescriptionAndModifier(minigamer);
	}

	protected final void tellDescriptionAndModifier(@NotNull Minigamer minigamer) {
		String description = minigamer.getMatch().getArena().getMechanic().getDescription();
		if (!description.isEmpty() && !description.toLowerCase().startsWith("todo"))
			minigamer.tell("Objective: &e" + description);
		MinigameModifier modifier = Minigames.getModifier();
		if (modifier.getClass() != NoModifier.class) {
			new TitleBuilder().players(minigamer).title("&3Modifier: &e" + modifier.getName()).subtitle("&6" + modifier.getDescription()).fadeIn(5).stay(TickTime.SECOND.x(5)).fadeOut(10).send();
		}
	}

	public abstract void announceWinners(@NotNull Match match);

	public int getWinningScore(@NotNull Collection<Integer> scores) {
		if (scores.size() == 0)
			return 0;
		return Collections.max(scores);
	}

	public final void balance(@NotNull Minigamer minigamer) {
		balance(Collections.singletonList(minigamer));
	}

	public abstract void balance(@NotNull List<Minigamer> minigamers);

	public @NotNull String getScoreboardTitle(@NotNull Match match) {
		return left(match.getArena().getName(), 16);
	}

	protected boolean renderTeamNames() {
		return true;
	}

	public @NotNull Map<String, Integer> getScoreboardLines(@NotNull Match match) {
		Map<String, Integer> lines = new HashMap<>();
		int lineCount = 0;

		if (match.getWinningScore() > 0) {
			lines.put("&3&lScore to Win", match.getWinningScore());
			lineCount++;
		}

		if (renderTeamNames() && match.getMechanic() instanceof TeamMechanic) {
			for (Team team : match.getAliveTeams()) {
				lines.put("- " + team.getVanillaColoredName(), team.getScore(match));

				lineCount++;
				if (lineCount == 15) return lines;
			}
		}

		List<Minigamer> minigamers = new ArrayList<>(match.getMinigamers());
		minigamers.sort((minigamer, t1) -> t1.getScore() - minigamer.getScore()); // sorts by descending (i think lol)
		int minigamerCount = 0;

		for (Minigamer minigamer : minigamers) {
			if (lineCount == 14) {
				int minigamersLeft = minigamers.size() - minigamerCount;
				int minScore = getMin(lines.values(), Integer::intValue).getObject();
				lines.put(String.format("&o+%d more %s...", minigamersLeft, StringUtils.plural("player", minigamersLeft)), minScore-1);
				break;
			} else if (minigamer.isAlive())
				lines.put("&f" + minigamer.getVanillaColoredName(), minigamer.getScore());
			else
				// &r to force last
				lines.put("&r&c&m" + minigamer.getNickname(), minigamer.getScore());

			minigamerCount++;
			lineCount++;
		}

		return lines;
	}

	public @NotNull Map<String, Integer> getScoreboardLines(@NotNull Minigamer minigamer) {
		return new HashMap<>();
	}

	public @NotNull Map<String, Integer> getScoreboardLines(@NotNull Match match, @NotNull Team team) {
		return new HashMap<>();
	}

	public void onPlayerInteract(Minigamer minigamer, PlayerInteractEvent event) {
		if (ActionGroup.CLICK_BLOCK.applies(event))
			if (event.getClickedBlock() != null)
				if (!canUseBlock(minigamer, event.getClickedBlock())) {
					Nexus.debug("Cancelling interact");
					event.setCancelled(true);
					return;
				}
	}

	public boolean canUseBlock(@NotNull Minigamer minigamer, @NotNull Block block) {
		return minigamer.getMatch().getArena().canUseBlock(block.getType());
	}

	public abstract boolean shouldBeOver(@NotNull Match match);

	public boolean shuffleSpawnpoints() {
		return true;
	}

	// Reflection utils

	public @NotNull final List<Class<? extends Mechanic>> getSuperclasses() {
		return Utils.getSuperclasses(this.getClass());
	}

	@Contract("null -> null; !null -> _") @Nullable
	public final <T> T getAnnotation(@Nullable Class<? extends Annotation> annotation) {
		if (annotation == null) return null;
		for (Class<? extends Mechanic> mechanic : getSuperclasses()) {
			Annotation result = mechanic.getAnnotation(annotation);
			if (result != null) {
				return (T) result;
			}
		}

		return null;
	}

	public boolean canOpenInventoryBlocks() {
		return false;
	}

	public boolean canMoveArmor() {
		return true;
	}

	public static boolean isInRegion(@NotNull Match match, @NotNull Block block, @NotNull String region) {
		return match.getArena().isInRegion(block, region);
	}

	public static void error(@NotNull String message, @NotNull Match match) {
		Nexus.severe(message);
		match.broadcast("&c" + message);
		match.end();
	}

	public static void sendBarWithTimer(@NotNull Minigamer minigamer, @NotNull String message) {
		sendActionBar(minigamer.getPlayer(), message + "&r (" + Timespan.ofSeconds(minigamer.getMatch().getTimer().getTime()).format() + ")");
	}

	public static void sendBarWithTimer(@NotNull Minigamer minigamer, @NotNull ComponentLike message) {
		minigamer.sendActionBar(new JsonBuilder(Timespan.ofSeconds(minigamer.getMatch().getTimer().getTime()).format()).next(" | ").next(message));
	}

	public boolean showTeamOnDeath() {
		return true;
	}

	public void onDisplayTimer(MinigamerDisplayTimerEvent event) {}

}
