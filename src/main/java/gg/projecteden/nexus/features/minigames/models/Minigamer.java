package gg.projecteden.nexus.features.minigames.models;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.SpeedCommand;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerScoredEvent;
import gg.projecteden.nexus.features.minigames.models.mechanics.Mechanic;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import gg.projecteden.nexus.features.minigames.models.perks.Perk;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import gg.projecteden.nexus.framework.interfaces.Colored;
import gg.projecteden.nexus.framework.interfaces.IsColoredAndNicknamed;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.FontUtils.FontChar;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Name;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.TitleBuilder;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import gg.projecteden.parchment.HasLocation;
import gg.projecteden.parchment.HasOfflinePlayer;
import gg.projecteden.parchment.OptionalPlayer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.LocationUtils.blockLocationsEqual;
import static gg.projecteden.nexus.utils.PlayerUtils.hidePlayer;
import static gg.projecteden.nexus.utils.PlayerUtils.showPlayer;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class Minigamer implements IsColoredAndNicknamed, OptionalPlayer, HasOfflinePlayer, HasLocation, HasUniqueId, Colored, ForwardingAudience.Single, Identified {
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
	private static final double INCREMENT_TELEPORTS_BY = 0.05;

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
		final Player player = getPlayer();
		if (player == null || !player.isOnline())
			throw new PlayerNotOnlineException(uuid);
		return player;
	}

	@Override
	public @Nullable Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

	@Override
	@Deprecated
	public @NotNull OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(uuid);
	}

	public @NotNull Location getLocation() {
		return getOnlinePlayer().getLocation();
	}

	@Override
	public @NotNull Identity identity() {
		return getOnlinePlayer().identity();
	}

	@Override
	public @NotNull Audience audience() {
		return getOnlinePlayer();
	}

	@Override
	public @NotNull UUID getUniqueId() {
		return uuid;
	}

	public boolean isOnline() {
		final Player player = getPlayer();
		return player != null && player.isOnline();
	}

	public boolean isDead() {
		return !isAlive;
	}

	/**
	 * Returns the Minigamer's Minecraft username.
	 *
	 * @deprecated You should probably be using {@link #getNickname()} instead.
	 */
	@Deprecated
	@NotNull
	public String getName() {
		return Objects.requireNonNull(Name.of(uuid), "Name of " + uuid + " is null");
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
		if (match != null) {
			tell("You are already in a match");
			return;
		}

		Match match = MatchManager.get(arena);

		try {
			match.checkCanJoin();
		} catch (InvalidInputException ex) {
			tell(ex.getMessage());
			return;
		}

		TitleBuilder fadeToBlack = new TitleBuilder()
			.title(FontChar.BLACK_SCREEN)
			.fade(TickTime.TICK.x(10))
			.players(getOnlinePlayer())
			.stay(TickTime.TICK.x(10));

		final Runnable join = () -> {
			try {
				if (this.match != null) {
					tell("You are already in a match");
					return;
				}

				match.checkCanJoin();
				this.match = match;
				this.match.join(this);
			} catch (InvalidInputException ex) {
				tell(ex.getMessage());
			}
		};

		if (WorldGroup.of(getOnlinePlayer()) != WorldGroup.MINIGAMES) {
			fadeToBlack
				.stay(TickTime.TICK.x(20))
				.send()
				.thenRun(() -> {
					toGamelobby();
					Tasks.wait(10, join);
				});
		} else
			fadeToBlack.send().thenRun(join);
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
		return match != null && match.getMinigamers().contains(this);
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
		return new WorldGuardUtils(getOnlinePlayer()).getRegionsAt(getOnlinePlayer().getLocation()).stream()
				.anyMatch(region -> {
					if (!isNullOrEmpty(type))
						return match.getArena().ownsRegion(region.getId(), type);
					else
						return region.getId().matches("^" + match.getArena().getRegionBaseName() + ".*");
				});
	}

	public boolean isInRegion(@NotNull String type) {
		return new WorldGuardUtils(getOnlinePlayer()).getRegionsAt(getOnlinePlayer().getLocation()).stream()
			.anyMatch(region -> match.getArena().ownsRegion(region.getId(), type));
	}

	public boolean isInGameWorld() {
		return Minigames.isMinigameWorld(getOnlinePlayer());
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
	 *
	 * @param withPrefix a message
	 */
	public void tell(@NotNull String withPrefix) {
		tell(withPrefix, true);
	}

	/**
	 * Sends a message to this minigamer in their chat with a prefix ("[Minigames]")
	 *
	 * @param withPrefix a message
	 */
	public void tell(@NotNull ComponentLike withPrefix) {
		tell(withPrefix, true);
	}

	/**
	 * Sends a message to this minigamer in their chat. If <code>prefix</code> is true, the message will be
	 * prefixed with "[Minigames]".
	 * <p>
	 * This method will automatically {@link gg.projecteden.nexus.utils.StringUtils#colorize(String)} the input.
	 *
	 * @param message a message
	 * @param prefix whether or not to display the minigames prefix
	 */
	public void tell(@NotNull String message, boolean prefix) {
		tell(new JsonBuilder(message), prefix);
	}


	/**
	 * Sends a message to this minigamer in their chat. If <code>prefix</code> is true, the message will be
	 * prefixed with "[Minigames]".
	 *
	 * @param message a message
	 * @param prefix whether or not to display the minigames prefix
	 */
	public void tell(@NotNull ComponentLike message, boolean prefix) {
		getOnlinePlayer().sendMessage(prefix ? JsonBuilder.fromPrefix("Minigames", message) : message);
	}

	public void toGamelobby() {
		if (!isOnline()) {
			new NerdService().edit(this, nerd -> nerd.setTeleportOnLogin(Minigames.getLobby()));
			return;
		}

		final Player player = getOnlinePlayer();

		player.setGameMode(GameMode.SURVIVAL);
		player.setFallDistance(0);

		teleportAsync(Minigames.getLobby()).thenRun(() -> {
			player.setAllowFlight(true);
			player.setFlying(false);
		});
	}

	public CompletableFuture<Boolean> toSpectate() {
		Location dest = match.getArena().getSpectateLocation();
		if (dest == null)
			return CompletableFuture.completedFuture(false);

		return teleportAsync(match.getArena().getSpectateLocation()).thenApply(success -> {
			clearGameModeState(true);
			match.getMinigamers().forEach(minigamer -> {
				if (minigamer.isAlive)
					minigamer.getOnlinePlayer().hidePlayer(Nexus.getInstance(), getOnlinePlayer());
				else
					getOnlinePlayer().showPlayer(Nexus.getInstance(), minigamer.getOnlinePlayer());
			});
			return success;
		});
	}

	public CompletableFuture<Boolean> teleportAsync(@NotNull Location location) {
		return teleportAsync(location, false);
	}

	public CompletableFuture<Boolean> teleportAsync(@NotNull Location location, boolean withSlowness) {
		return teleportAsync(location, withSlowness, false);
	}

	public CompletableFuture<Boolean> teleportAsync(@NotNull Location location, boolean withSlowness, boolean skipSafetyCheck) {
		Utils.notNull(location, "Tried to teleport " + getName() + " to a null location");

//		if (canTeleport)
//			return CompletableFuture.completedFuture(false); // Already teleporting
		canTeleport = true;

		final Location destination = location.clone();
		Block destinationBlock = destination.getBlock();
		if (!skipSafetyCheck && !destinationBlock.isPassable()) {
			BoundingBox blockBox = destinationBlock.getBoundingBox();
			int added = 0;
			while (blockBox.contains(destination.toVector()) && added < 1) {
				added += INCREMENT_TELEPORTS_BY;
				destination.add(0, INCREMENT_TELEPORTS_BY, 0);
			}
		}
		final Vector still = new Vector();
		getOnlinePlayer().setVelocity(still);

		final TeleportCause cause = match == null ? TeleportCause.COMMAND : TeleportCause.PLUGIN;
		return getOnlinePlayer().teleportAsync(destination, cause).thenApply(result -> {
			canTeleport = false;
			if (!result) return false;
			getOnlinePlayer().setVelocity(still);
			if (withSlowness) {
				match.getTasks().wait(1, () -> getOnlinePlayer().setVelocity(still));
				match.getTasks().wait(2, () -> getOnlinePlayer().setVelocity(still));
			}
			return true;
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
							hidePlayer(minigamer.getOnlinePlayer()).from(getOnlinePlayer());
							if (minigamer.isAlive())
								hidePlayer(getOnlinePlayer()).from(minigamer.getOnlinePlayer());
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
	 *
	 * @return player's {@link org.bukkit.Location} without yaw or pitch
	 */
	private @NotNull Location getRotationlessLocation() {
		Location location = getOnlinePlayer().getLocation();
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

		if (getOnlinePlayer().isSneaking())
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
		final Player player = getOnlinePlayer();
		player.setHealth(Math.min(player.getMaxHealth(), player.getHealth()+amount));
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
		final Player player = getOnlinePlayer();
		if (respawning)
			OnlinePlayers.getAll().forEach(_player -> {
				hidePlayer(_player).from(player);
				hidePlayer(player).from(_player);
			});
		else if (!isAlive)
			OnlinePlayers.getAll().forEach(_player -> {
				showPlayer(_player).to(player);

				Minigamer minigamer = of(_player);
				if (minigamer.isPlaying(match) && minigamer.isAlive())
					hidePlayer(_player).from(player);
			});
		 else
			unhideAll();
	}

	public void unhideAll() {
		OnlinePlayers.getAll().forEach(_player -> {
			showPlayer(getOnlinePlayer()).to(_player);
			showPlayer(_player).to(getOnlinePlayer());
		});
	}

	public void clearState() {
		clearState(false);
	}

	public void clearState(boolean forceClearInventory) {
		// TODO: Possibly edit ConditionalPerms to disallow voxel?
		getOnlinePlayer().setGameMode(match.getMechanic().getGameMode());
		clearGameModeState(forceClearInventory);
		unhideAll();
	}

	private void clearGameModeState(boolean forceClearInventory) {
		Mechanic mechanic = match.getMechanic();

		final Player player = getOnlinePlayer();
		player.setFireTicks(0);
		player.resetMaxHealth();
		player.setHealth(20);
		player.setExp(0);
		player.setTotalExperience(0);
		player.setLevel(0);
		player.getInventory().setHeldItemSlot(0);
		player.setFoodLevel(20);
		player.setFallDistance(0);
		player.setAllowFlight(mechanic.allowFly());
		player.setFlying(mechanic.allowFly());
		if (Vanish.isVanished(player))
			Vanish.unvanish(player);
		SpeedCommand.resetSpeed(player);
		player.setOp(false);

		if (mechanic.shouldClearInventory() || forceClearInventory) {
			player.getInventory().clear();
			for (PotionEffect effect : player.getActivePotionEffects())
				player.removePotionEffect(effect.getType());
		}
	}

	public boolean usesPerk(@NotNull Class<? extends Perk> perk) {
		return match.getMechanic().usesPerk(perk, this);
	}

	public boolean usesPerk(@NotNull Perk perk) {
		return match.getMechanic().usesPerk(perk, this);
	}

	public void addPotionEffect(PotionEffect potionEffect){
		getOnlinePlayer().addPotionEffect(potionEffect);
	}

	public void addPotionEffect(PotionEffectBuilder effectBuilder) {
		getOnlinePlayer().addPotionEffect(effectBuilder.build());
	}

	public void removePotionEffect(PotionEffectType type) {
		getOnlinePlayer().removePotionEffect(type);
	}

	public void clearInventory() {
		getOnlinePlayer().getInventory().setStorageContents(new ItemStack[36]);
	}

	public boolean isSpectating() {
		return false; // TODO
	}

}
