package gg.projecteden.nexus.features.minigames.models;

import com.google.common.base.Strings;
import de.myzelyam.api.vanish.VanishAPI;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.SpeedCommand;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerScoredEvent;
import gg.projecteden.nexus.features.minigames.models.mechanics.Mechanic;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import gg.projecteden.nexus.features.minigames.models.perks.Perk;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import gg.projecteden.nexus.framework.interfaces.Colored;
import gg.projecteden.nexus.framework.interfaces.IsColoredAndNicknamed;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.Name;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.TitleBuilder;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.WorldGroup;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.parchment.PlayerLike;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import me.lexikiq.HasUniqueId;
import me.lexikiq.PlayerLike;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static gg.projecteden.nexus.utils.LocationUtils.blockLocationsEqual;
import static gg.projecteden.nexus.utils.PlayerUtils.hidePlayer;
import static gg.projecteden.nexus.utils.PlayerUtils.showPlayer;
import static gg.projecteden.nexus.utils.StringUtils.colorize;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class Minigamer implements IsColoredAndNicknamed, PlayerLike, Colored {
	@NotNull
	@EqualsAndHashCode.Include
	private UUID uuid;
	@ToString.Exclude
	private Match match;
	@Nullable
	private Team team;
	private int score = 0;
	/**
	 * Number that represents the player's participation in the game. Should be used to track events like kills,
	 * capturing flags, etc. Mostly used for team games where participation is more complex than just kill counts.
	 */
	private int contributionScore = 0;
	@Accessors(fluent = true)
	private boolean canTeleport;
	private boolean respawning = false;
	private boolean isAlive = true;
	private int lives;
	private int immobileTicks = 0;
	private int lastStruckTicks = 0;
	@MonotonicNonNull
	private Location lastLocation = null;
	// 1/2 = half a heart, /2s = half a heart every 2 sec, /4.5 = half a heart at max multiplier every 2s
	private static final double HEALTH_PER_TICK = (1d/2d)/ TickTime.SECOND.x(2);
	private static final long IMMOBILE_SECONDS = TickTime.SECOND.x(3);

	@NotNull
	public static Minigamer of(@NotNull UUID uuid) throws PlayerNotOnlineException {
		for (Match match : MatchManager.getAll())
			for (Minigamer minigamer : match.getMinigamers())
				if (minigamer.getUniqueId().equals(uuid))
					return minigamer;

		Player onlinePlayer = Bukkit.getPlayer(uuid);
		if (onlinePlayer == null)
			throw new PlayerNotOnlineException(uuid);

		return new Minigamer(uuid);
	}

	@Contract("null -> null; !null -> !null")
	public static Minigamer of(@Nullable HasUniqueId player) {
		if (player == null)
			return null;

		if (player instanceof Minigamer minigamer)
			return minigamer;

		try {
			return of(player.getUniqueId());
		} catch (PlayerNotOnlineException exc) {
			// fake player (NPC), this should probably return null but to avoid breaking changes we create a fake minigamer as well
			if (player instanceof Player player1)
				return new Minigamer(player1.getUniqueId());
			throw exc;
		}
	}

	public @NotNull Player getOnlinePlayer() {
		final Player player = Bukkit.getPlayer(uuid);
		if (player == null || !player.isOnline())
			throw new PlayerNotOnlineException(uuid);
		return player;
	}

	@Override
	public @NotNull Player getPlayer() {
		return getOnlinePlayer();
	}

	@Override
	@Deprecated
	public @NotNull OfflinePlayer getOfflinePlayer() {
		return getPlayer();
	}

	@Override
	public @NotNull UUID getUniqueId() {
		return getPlayer().getUniqueId();
	}

	/**
	 * Returns the Minigamer's Minecraft username.
	 * @deprecated You should probably be using {@link #getNickname()} instead.
	 */
	@Deprecated
	@NotNull
	public String getName() {
		return Name.of(uuid);
	}

	public @NotNull String getNickname() {
		return Nickname.of(uuid);
	}

	public @NotNull Color getColor() {
		if (team == null)
			return Color.WHITE;
		return team.getColor();
	}

	public void join(@NotNull String name) {
		join(ArenaManager.find(name));
	}

	public void join(@NotNull Arena arena) {
		if (!WorldGroup.MINIGAMES.equals(WorldGroup.of(getPlayer().getWorld()))) {
			toGamelobby();
			Tasks.wait(10, () -> join(arena));
			return;
		}

		if (match == null) {
			match = MatchManager.get(arena);
			if (!match.join(this))
				match = null;
		} else {
			tell("You are already in a match");
		}
	}

	public void quit() {
		if (match != null) {
			match.quit(this);
			match = null;
		} else {
			if (isInGameWorld())
				toGamelobby();
			else
				tell("You are not in a match");
		}
	}

	public boolean isPlaying() {
		return match != null;
	}

	public boolean isIn(@NotNull Match match) {
		return isIn(match.getMechanic()) && match.equals(this.match);
	}

	public boolean isInLobby(@NotNull Match match) {
		return isInLobby(match.getMechanic()) && match.equals(this.match);
	}

	public boolean isPlaying(@NotNull Match match) {
		return isPlaying(match.getMechanic()) && match.equals(this.match);
	}

	public boolean isIn(@NotNull Mechanic mechanic) {
		return isIn(mechanic.getClass());
	}

	public boolean isPlaying(@NotNull Mechanic mechanic) {
		return isPlaying(mechanic.getClass());
	}

	public boolean isInLobby(@NotNull Mechanic mechanic) {
		return isInLobby(mechanic.getClass());
	}

	public boolean isIn(@NotNull Class<? extends Mechanic> mechanic) {
		if (match != null)
			return mechanic.isInstance(match.getMechanic());
		return false;
	}

	public boolean isPlaying(@NotNull Class<? extends Mechanic> mechanic) {
		if (match != null)
			return mechanic.isInstance(match.getMechanic()) && match.isStarted() && isAlive();
		return false;
	}

	public boolean isInLobby(@NotNull Class<? extends Mechanic> mechanic) {
		if (match != null)
			return mechanic.isInstance(match.getMechanic()) && !match.isStarted();
		return false;
	}

	public boolean isInMatchRegion() {
		return isInMatchRegion(null);
	}

	public boolean isInMatchRegion(@Nullable String type) {
		return new WorldGuardUtils(getPlayer()).getRegionsAt(getPlayer().getLocation()).stream()
				.anyMatch(region -> {
					if (!Strings.isNullOrEmpty(type))
						return match.getArena().ownsRegion(region.getId(), type);
					else
						return region.getId().matches("^" + match.getArena().getRegionBaseName() + ".*");
				});
	}

	public boolean isInRegion(@NotNull String type) {
		return new WorldGuardUtils(getPlayer()).getRegionsAt(getPlayer().getLocation()).stream()
			.anyMatch(region -> match.getArena().ownsRegion(region.getId(), type));
	}

	public boolean isInGameWorld() {
		return Minigames.isMinigameWorld(getPlayer().getWorld());
	}

	/**
	 * Sends a message to this minigamer in their chat with no prefix ("[Minigames]")
	 * <p>
	 * This method will automatically {@link gg.projecteden.nexus.utils.StringUtils#colorize(String)} the input.
	 *
	 * @param noPrefix a message
	 */
	public void sendMessage(@NotNull String noPrefix) {
		tell(noPrefix, false);
	}

	/**
	 * Sends a message to this minigamer in their chat with a prefix ("[Minigames]")
	 * <p>
	 * This method will automatically {@link gg.projecteden.nexus.utils.StringUtils#colorize(String)} the input.
	 * @param withPrefix a message
	 */
	public void tell(@NotNull String withPrefix) {
		tell(withPrefix, true);
	}

	/**
	 * Sends a message to this minigamer in their chat. If <code>prefix</code> is true, the message will be
	 * prefixed with "[Minigames]".
	 * <p>
	 * This method will automatically {@link gg.projecteden.nexus.utils.StringUtils#colorize(String)} the input.
	 * @param message a message
	 * @param prefix whether or not to display the minigames prefix
	 */
	public void tell(@NotNull String message, boolean prefix) {
		getPlayer().sendMessage((prefix ? Minigames.PREFIX : "") + colorize(message));
	}

	public void toGamelobby() {
		boolean staff = Rank.of(getPlayer()).isStaff();

		getPlayer().setGameMode(GameMode.SURVIVAL);
		getPlayer().setFallDistance(0);
		getPlayer().setAllowFlight(staff);
		getPlayer().setFlying(staff);

		teleportAsync(Minigames.getLobby());
	}

	public void toSpectate() {
		teleportAsync(match.getArena().getSpectateLocation());
		match.getMinigamers().forEach(minigamer -> {
			if (minigamer.isAlive)
				minigamer.getPlayer().hidePlayer(Nexus.getInstance(), getPlayer());
			else
				getPlayer().showPlayer(Nexus.getInstance(), minigamer.getPlayer());
		});
	}

	public CompletableFuture<Void> teleportAsync(@NotNull Location location) {
		return teleportAsync(location, false);
	}

	public CompletableFuture<Void> teleportAsync(@NotNull Location location, boolean withSlowness) {
		Utils.notNull(location, "Tried to teleport " + getName() + " to a null location");

		final Location up = location.clone().add(0, .5, 0);
		final Vector still = new Vector(0, 0, 0);

		return location.getWorld().getChunkAtAsyncUrgently(up)
			.thenRun(() -> {
				getPlayer().setVelocity(still);
				canTeleport = true;
			}).thenCompose($ -> {
				final TeleportCause cause = match == null ? TeleportCause.COMMAND : TeleportCause.PLUGIN;
				return getPlayer().teleportAsync(up, cause);
			}).thenRun(() -> {
				canTeleport = false;
				getPlayer().setVelocity(still);
				if (withSlowness) {
					match.getTasks().wait(1, () -> getPlayer().setVelocity(still));
					match.getTasks().wait(2, () -> getPlayer().setVelocity(still));
				}
			});
	}

	public void setTeam(@Nullable Team team) {
		assert match != null;
		match.handleGlow(this.team);
		this.team = team;
		match.handleGlow(team);

		// join new team channel
		if (match.getMechanic() instanceof TeamMechanic && team != null) {
			((TeamMechanic) match.getMechanic()).joinTeamChannel(this);
			if (team.getObjective() != null && !team.getObjective().isEmpty()) {
				sendMessage("&6Team Objective: &e" + team.getObjective());
				new TitleBuilder().players(this).title("&6Team Objective").subtitle("&e" + team.getObjective()).fadeIn(10).stay(TickTime.SECOND.x(4)).fadeOut(20).send();
			}
		}
	}

	public void scored() {
		scored(1);
	}

	public void scored(int scored) {
		setScore(score + scored);
	}

	public void contributionScored(int amount) {
		setContributionScore(contributionScore + amount);
	}

	public void setScore(int score) {
		int diff = score - this.score;
		contributionScored(diff);

		MinigamerScoredEvent event = new MinigamerScoredEvent(this, diff);
		if (!event.callEvent()) return;

		this.score += event.getAmount();
		if (match.getScoreboard() != null)
			match.getScoreboard().update();
	}

	public void died() {
		--lives;
		lastStruckTicks = 0;
	}

	public void damaged() {
		lastStruckTicks = 0;
	}

	public void spawn() {
		if (!match.isEnded())
			team.spawn(this);
	}

	public void respawn() {
		if (match.getArena().getRespawnLocation() == null)
			spawn();
		else {
			respawning = true;
			clearState();
			hideAll();
			teleportAsync(match.getArena().getRespawnLocation(), true);
			addPotionEffect(new PotionEffectBuilder(PotionEffectType.BLINDNESS).duration(TickTime.SECOND.x(2)).amplifier(2));
			Runnable respawn = () -> {
				if (!match.isEnded()) {
					spawn();
					unhideAll();
					respawning = false;
					// hides players who are still respawning (as unhideAll unhides them)
					match.getMinigamers().forEach(minigamer -> {
						if (!minigamer.equals(this) && (minigamer.isRespawning() || !minigamer.isAlive())) {
							hidePlayer(minigamer).from(this);
							if (minigamer.isAlive())
								hidePlayer(this).from(minigamer);
						}
					});
				}
			};
			int respawnIn = match.getArena().getRespawnSeconds() * 20;
			if (respawnIn == 0)
				respawn.run();
			else
				match.getTasks().wait(respawnIn, respawn);
		}
	}

	/**
	 * Calculates the current player's location without yaw or pitch
	 * @return player's {@link org.bukkit.Location} without yaw or pitch
	 */
	private @NotNull Location getRotationlessLocation() {
		Location location = getPlayer().getLocation();
		location.setYaw(0);
		location.setPitch(0);
		return location;
	}

	public void tick() {
		Location playerLocation = getRotationlessLocation();
		if (lastLocation == null || !blockLocationsEqual(playerLocation, lastLocation))
			immobileTicks = 0;
		else
			immobileTicks++;
		lastLocation = playerLocation;

		if (getMatch().getMechanic().getRegenType().hasCustomRegen())
			regen();
	}

	public void regen() {
		double multiplier = 1;
		double sneakMultiplier = 1; // calculated independently as it is variable

		if (getPlayer().isSneaking())
			sneakMultiplier = 1.5d;

		if (immobileTicks >= IMMOBILE_SECONDS) {
			multiplier *= immobileTicks/(double)IMMOBILE_SECONDS;
		}

		multiplier *= sneakMultiplier;

		if (lastStruckTicks <= TickTime.SECOND.x(2))
			multiplier *= 1d/3d;

		// this skips making the hearts do the little regeneration bobbing but idk how to fix that
		heal(HEALTH_PER_TICK * multiplier);

		lastStruckTicks++;
	}

	public void heal(double amount) {
		getPlayer().setHealth(Math.min(getPlayer().getMaxHealth(), getPlayer().getHealth()+amount));
	}

	// respawning
	//     you see alive players = false;
	//     you see dead players = false;
	//     alive players see you = false;
	// spectating
	//     you see alive players = true;
	//     you see dead players = true;
	//     alive players see you = false;

	private void hideAll() {
		if (respawning)
			OnlinePlayers.getAll().forEach(_player -> {
				hidePlayer(_player).from(this);
				hidePlayer(this).from(_player);
			});
		else if (!isAlive)
			OnlinePlayers.getAll().forEach(_player -> {
				showPlayer(_player).to(this);

				Minigamer minigamer = of(_player);
				if (minigamer.isPlaying(match) && minigamer.isAlive())
					hidePlayer(_player).from(this);
			});
		 else
			unhideAll();
	}

	public void unhideAll() {
		OnlinePlayers.getAll().forEach(_player -> {
			showPlayer(getPlayer()).to(_player);
			showPlayer(_player).to(getPlayer());
		});
	}

	public void clearState() {
		clearState(false);
	}

	public void clearState(boolean forceClearInventory) {
		// TODO: Possibly edit ConditionalPerms to disallow voxel?
		getPlayer().setGameMode(match.getMechanic().getGameMode());
		clearGameModeState(forceClearInventory);

		unhideAll();
	}

	private void clearGameModeState(boolean forceClearInventory) {
		Mechanic mechanic = match.getMechanic();

		getPlayer().setFireTicks(0);
		getPlayer().resetMaxHealth();
		getPlayer().setHealth(20);
		getPlayer().setExp(0);
		getPlayer().setTotalExperience(0);
		getPlayer().setLevel(0);
		getPlayer().getInventory().setHeldItemSlot(0);
		getPlayer().setFoodLevel(20);
		getPlayer().setFallDistance(0);
		getPlayer().setAllowFlight(mechanic.allowFly());
		getPlayer().setFlying(mechanic.allowFly());
		if (VanishAPI.isInvisible(getPlayer()))
			VanishAPI.showPlayer(getPlayer());
		SpeedCommand.resetSpeed(getPlayer());
		getPlayer().setOp(false);

		if (mechanic.shouldClearInventory() || forceClearInventory)
			getPlayer().getInventory().clear();

		for (PotionEffect effect : getPlayer().getActivePotionEffects())
			getPlayer().removePotionEffect(effect.getType());
	}

	public boolean usesPerk(@NotNull Class<? extends Perk> perk) {
		return match.getMechanic().usesPerk(perk, this);
	}

	public boolean usesPerk(@NotNull Perk perk) {
		return match.getMechanic().usesPerk(perk, this);
	}

	public void addPotionEffect(PotionEffect potionEffect){
		getPlayer().addPotionEffect(potionEffect);
	}

	public void addPotionEffect(PotionEffectBuilder effectBuilder){
		getPlayer().addPotionEffect(effectBuilder.build());
	}

	public void removePotionEffect(PotionEffectType type){
		getPlayer().removePotionEffect(type);
	}

}
