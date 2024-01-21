package gg.projecteden.nexus.features.minigames.models.mechanics;

import gg.projecteden.api.common.utils.ReflectionUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.FormatType;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.TimespanBuilder;
import gg.projecteden.api.interfaces.Named;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Match.MatchTasks.MatchTaskType;
import gg.projecteden.nexus.features.minigames.models.MinigameMessageType;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.RegenType;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchBeginEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchInitializeEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchQuitEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDamageEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.sabotage.MinigamerDisplayTimerEvent;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import gg.projecteden.nexus.features.minigames.models.modifiers.MinigameModifier;
import gg.projecteden.nexus.features.minigames.models.perks.Perk;
import gg.projecteden.nexus.features.minigames.modifiers.NoModifier;
import gg.projecteden.nexus.features.nameplates.Nameplates;
import gg.projecteden.nexus.features.nameplates.TeamAssigner;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.framework.interfaces.HasDescription;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks.Countdown;
import gg.projecteden.nexus.utils.TitleBuilder;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.NameTagVisibility;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.time.Duration;
import java.util.*;

import static gg.projecteden.nexus.utils.StringUtils.left;
import static gg.projecteden.nexus.utils.StringUtils.plural;
import static gg.projecteden.nexus.utils.Utils.getMin;

public abstract class Mechanic implements Listener, Named, HasDescription, ComponentLike {

	public Mechanic() {
		Nexus.registerListener(this);
	}

	@Nullable
	private MechanicType getMechanicType() {
		return MechanicType.of(this.getClass());
	}

	@NotNull
	public String getId() {
		return getMechanicType().name();
	}

	public @NotNull TextComponent asComponent() {
		return Component.text(getName(), NamedTextColor.YELLOW);
	}

	public @NotNull String getPrefix() {
		return StringUtils.getPrefix(this.getClass());
	}

	public abstract @NotNull ItemStack getMenuItem();

	public @NotNull GameMode getGameMode() {
		return GameMode.ADVENTURE;
	}

	public boolean canDropItem(@NotNull ItemStack item) {
		return false;
	}

	/**
	 * Returns the type of regeneration this mechanic uses.
	 *
	 * @return regeneration type
	 */
	public RegenType getRegenType() {
		return RegenType.TIER_0;
	}

	/**
	 * Heals a killer according to the mechanic's {@link #getRegenType() regeneration rules}.
	 *
	 * @param player the player to heal
	 */
	public void giveKillHeal(@NotNull Minigamer player) {
		// flat heal
		RegenType regenType = getRegenType();
		if (regenType.hasKillHeal())
			player.heal(RegenType.KILL_HEAL_AMOUNT);

		// regen potion effect
		PotionEffect baseEffect = regenType.getBaseKillRegen();
		if (baseEffect == null)
			return;
		PotionEffect currentEffect = player.getPlayer().getPotionEffect(PotionEffectType.REGENERATION);
		// add if player does not have regen, or it is of a lesser amplifier
		if (currentEffect == null || currentEffect.getAmplifier() < baseEffect.getAmplifier()) {
			player.addPotionEffect(baseEffect);
			return;
		}
		// return if player already has regen of greater amplifier
		if (currentEffect.getAmplifier() > baseEffect.getAmplifier())
			return;
		// add new regen effect which combines the duration of the base and current effects
		PotionEffect combinedEffect = baseEffect.withDuration(baseEffect.getDuration() + (currentEffect.getDuration()/2));
		player.addPotionEffect(combinedEffect);
	}

	/**
	 * Determines if a user is allowed to use a perk in a specified minigame.
	 *
	 * @param perk a user's perk
	 * @param minigamer the user
	 * @return whether to allow the perk
	 */
	public boolean usesPerk(@NotNull Class<? extends Perk> perk, @NotNull Minigamer minigamer) {
		return true;
	}

	/**
	 * Determines if a user is allowed to use a perk in a specified minigame.
	 *
	 * @param perk a user's perk
	 * @param minigamer the user
	 * @return whether to allow the perk
	 */
	public final boolean usesPerk(@NotNull Perk perk, @NotNull Minigamer minigamer) {
		return usesPerk(perk.getClass(), minigamer);
	}

	/**
	 * Whether to hide the colors of team-colored perks.
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
		int lives = match.getArena().getLives();

		// announce match start
		match.broadcast("Starting match");
		match.broadcastNoPrefix("");

		for (Minigamer minigamer : match.getMinigamers()) {
			// assign bukkit teams
			TeamAssigner assigner = getTeamAssigner(match);
			if (assigner != null)
				Nameplates.get().registerTeamAssigner(minigamer, assigner);

			// set lives
			if (lives > 0)
				minigamer.setLives(lives);
			else {
				Team team = minigamer.getTeam();
				if (team != null && team.getLives() > 0)
					minigamer.setLives(team.getLives());
			}
		}

		int beginDelay = match.getArena().getBeginDelay();
		if (beginDelay > 0) {
			int taskId = match.getTasks().countdown(Countdown.builder()
					.duration(TickTime.SECOND.x(beginDelay))
					.onSecond(i -> {
						Component message = new JsonBuilder("&7Starting in &e" + plural(i + " second", i)).build();
						match.showTitle(Title.title(Component.empty(), message, Times.times(Duration.ZERO, Duration.ofSeconds(1), TickTime.TICK.duration(5))));
						if (List.of(60, 30, 15, 5, 4, 3, 2, 1).contains(Math.toIntExact(i)))
							match.broadcast(message);
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

		int taskId = match.getTasks().repeat(0, 1, () -> match.getOnlineMinigamers().forEach(Minigamer::tick));
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
		minigamer.getMatch().broadcast("&e" + minigamer.getNickname() + " &3has joined", MinigameMessageType.JOIN);
		tellMapAndMechanic(minigamer);
	}

	public void onQuit(@NotNull MatchQuitEvent event) {
		Minigames.debug("Mechanic#onQuit " + event.getMinigamer().getNickname());
		Minigamer minigamer = event.getMinigamer();
		minigamer.getMatch().broadcast("&e" + minigamer.getNickname() + " &3has quit", MinigameMessageType.QUIT);
		if (minigamer.getMatch().isStarted() && shouldBeOver(minigamer.getMatch()))
			minigamer.getMatch().end();
	}

	public void onDamage(@NotNull MinigamerDamageEvent event) {
		Minigamer minigamer = event.getMinigamer();
		minigamer.damaged();

		// reduce duration of regen by a few seconds when damaged
		PotionEffect mechanicRegenEffect = getRegenType().getBaseKillRegen();
		if (mechanicRegenEffect == null) return;
		PotionEffect playerRegenEffect = minigamer.getOnlinePlayer().getPotionEffect(PotionEffectType.REGENERATION);
		if (playerRegenEffect == null) return;
		if (mechanicRegenEffect.getAmplifier() != playerRegenEffect.getAmplifier()) return;
		minigamer.removePotionEffect(PotionEffectType.REGENERATION);
		int newDuration = playerRegenEffect.getDuration() - (mechanicRegenEffect.getDuration()/3);
		if (newDuration <= 0) return;
		minigamer.addPotionEffect(playerRegenEffect.withDuration(newDuration));
	}

	public void onDeath(@NotNull MinigamerDeathEvent event) {
		Minigamer attacker = event.getAttacker();
		if (attacker != null)
			giveKillHeal(attacker);

		event.broadcastDeathMessage();
		if (event.getMatch().getScoreboard() != null)
			event.getMatch().getScoreboard().update();
		if (shouldBeOver(event.getMatch()))
			event.getMatch().end();
	}

	public final void kill(@NotNull Minigamer minigamer) {
		Minigames.debug("Killing " + minigamer.getColoredName());
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

	public boolean useNaturalDeathMessage() {
		return false;
	}

	public void tellMapAndMechanic(@NotNull Minigamer minigamer) {
		Arena arena = minigamer.getMatch().getArena();
		String mechanicName = arena.getMechanic().getName();
		String arenaName = arena.getDisplayName();
		minigamer.tell("You are playing &e" + mechanicName + (mechanicName.equalsIgnoreCase(arenaName) ? "" : " &3on &e" + arenaName));
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

	/**
	 * Whether to render the names of teams in the default
	 * {@link gg.projecteden.nexus.features.minigames.models.scoreboards.MinigameScoreboard.Type#MATCH MATCH}
	 * scoreboard.
	 *
	 * @return whether to render the names of teams in the default scoreboard
	 */
	protected boolean renderTeamNames() {
		return true;
	}

	public boolean useScoreboardNumbers() {
		return true;
	}

	public @NotNull LinkedHashMap<String, Integer> getScoreboardLines(@NotNull Match match) {
		LinkedHashMap<String, Integer> lines = new LinkedHashMap<>();
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

		for (Minigamer minigamer : minigamers.stream().filter(Minigamer::isAlive).toList()) {
			if (lineCount == 14) {
				int minigamersLeft = minigamers.size() - minigamerCount;
				lines.put(String.format("&o+%d more %s...", minigamersLeft, StringUtils.plural("player", minigamersLeft)), Integer.MIN_VALUE);
				break;
			} else
				lines.put("&f" + minigamer.getVanillaColoredName(), minigamer.getScore());

			minigamerCount++;
			lineCount++;
		}

		for (Minigamer minigamer : minigamers.stream().filter(Minigamer::isDead).toList()) {
			if (lineCount == 14) {
				int minigamersLeft = minigamers.size() - minigamerCount;
				lines.put(String.format("&o+%d more %s...", minigamersLeft, StringUtils.plural("player", minigamersLeft)), Integer.MIN_VALUE);
				break;
			} else
				lines.put("&r&c&m" + minigamer.getNickname(), minigamer.getScore());

			minigamerCount++;
			lineCount++;
		}

		return lines;
	}

	public @NotNull LinkedHashMap<String, Integer> getScoreboardLines(@NotNull Minigamer minigamer) {
		return new LinkedHashMap<>();
	}

	public @NotNull LinkedHashMap<String, Integer> getScoreboardLines(@NotNull Match match, @NotNull Team team) {
		return new LinkedHashMap<>();
	}

	/**
	 * Gets the name tag of a {@code target} as viewed by a {@code viewer}.
	 *
	 * @param target the target player (who is expected to be playing this mechanic)
	 * @param viewer the viewer player (who may or may not be playing on the same match)
	 * @return the name tag of the target player or null if the player(s) are not in this mechanic
	 */
	public @Nullable JsonBuilder getNameplate(@NotNull Minigamer target, @NotNull Minigamer viewer) {
		if (target.getMatch() != viewer.getMatch())
			return null;

		if (!target.getMatch().isStarted())
			return null;

		return new JsonBuilder(target.getColoredName());
	}

	/**
	 * Whether the given {@code viewer} should be able to see {@code target}'s name tag.
	 *
	 * @param target the target player (who is expected to be playing this mechanic)
	 * @param viewer the viewer player (who may or may not be playing on the same match)
	 * @return whether the viewer should be able to see the target's name tag
	 */
	public boolean shouldShowNameplate(@NotNull Minigamer target, @NotNull Minigamer viewer) {
		// ensure player has a minigame nameplate
		if (getNameplate(target, viewer) == null)
			return true;

		// handle respawning
		if (target.isRespawning())
			return false;
		if (viewer.isRespawning())
			return false;

		// handle spectators/dead players
		if (!target.isAlive() && viewer.isAlive())
			return false;

		// handle name tag visibility
		if (target.isAlive() && viewer.isAlive()) {
			Team targetTeam = target.getTeam();
			Team viewerTeam = viewer.getTeam();
			NameTagVisibility targetVisibility = targetTeam == null ? NameTagVisibility.ALWAYS : targetTeam.getNameTagVisibility();
			if (targetVisibility == NameTagVisibility.NEVER)
				return false;
			if (targetVisibility == NameTagVisibility.HIDE_FOR_OTHER_TEAMS && targetTeam != viewerTeam)
				return false;
			if (targetVisibility == NameTagVisibility.HIDE_FOR_OWN_TEAM && targetTeam == viewerTeam)
				return false;
		}

		// name tag is visible :)
		return true;
	}

	public boolean shouldShowHealthInNameplate() {
		return true;
	}

	/**
	 * Whether to allow chat messages of the provided {@code type}.
	 *
	 * @param type the type of chat message
	 * @return whether to allow chat messages
	 */
	public boolean allowChat(MinigameMessageType type) {
		return true;
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
		return ReflectionUtils.superclassesOf(this.getClass());
	}

	@Nullable
	@Contract("null -> null; !null -> _")
	public final <T> T getAnnotation(@Nullable Class<? extends Annotation> annotation) {
		return (T) Utils.getAnnotation(getClass(), annotation);
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

	public static void sendActionBarWithTimer(@NotNull Minigamer minigamer, @NotNull String message) {
		sendActionBarWithTimer(minigamer, new JsonBuilder(message));
	}

	public static void sendActionBarWithTimer(@NotNull Minigamer minigamer, @NotNull ComponentLike message) {
		minigamer.sendActionBar(new JsonBuilder(Timespan.ofSeconds(minigamer.getMatch().getTimer().getTime()).format()).next(" | ").next(message));
	}

	public boolean showTeamOnDeath() {
		return true;
	}

	public void onDisplayTimer(MinigamerDisplayTimerEvent event) {}

	public @Nullable TeamAssigner getTeamAssigner(Match match) {
		return null;
	}

	private ItemBuilder displayImage;
	private ItemBuilder menuImage;

	public ItemBuilder getDisplayImage() {
		if (displayImage == null)
			findImages();
		if (displayImage == null)
			return null;
		return displayImage.clone();
	}

	public ItemBuilder getMenuImage() {
		if (menuImage == null)
			findImages();
		if (menuImage == null)
			return null;
		return menuImage.clone();
	}

	public void findImages() {
		final List<CustomModel> matches = new ArrayList<>();

		for (CustomModel value : ResourcePack.getModels().values())
			if (value.getFolder().getPath().contains("ui/images/gamelobby"))
				if (value.getFileName().equalsIgnoreCase(getId()))
					matches.add(value);

		if (matches.isEmpty())
			return;

		final Comparator<CustomModel> comparator = Comparator.comparing(model -> model.getFolder().getPath().length());
		final CustomModel shallowestMatch = Collections.min(matches, comparator);
		final CustomModel deepestMatch = Collections.max(matches, comparator);
		this.displayImage = new ItemBuilder(shallowestMatch);
		this.menuImage = new ItemBuilder(deepestMatch);
	}

	public boolean isTestMode() {
		return false;
	}

	public boolean shouldAutoEndOnZeroTimeLeft() {
		return true;
	}

	public boolean shouldBroadcastTimeLeft() {
		return true;
	}

	public void broadcastTimeLeft(Match match, int time) {
		match.broadcast("&e" + TimespanBuilder.ofSeconds(time).format(FormatType.LONG) + " &7left...");
	}

	public boolean shouldTickParticlePerks() {
		return true;
	}

}
