package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.chat.Censor;
import gg.projecteden.nexus.features.chat.Chat.StaticChannel;
import gg.projecteden.nexus.features.chat.events.MinecraftChatEvent;
import gg.projecteden.nexus.features.chat.events.PublicChatEvent;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.arenas.PixelDropArena;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchQuitEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.PixelDropMatchData;
import gg.projecteden.nexus.features.minigames.models.matchdata.PixelDropMatchData.Design;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import gg.projecteden.nexus.features.minigames.models.perks.Perk;
import gg.projecteden.nexus.features.minigames.models.perks.common.PlayerParticlePerk;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.ChatterService;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Tasks.Countdown;
import org.apache.commons.lang3.Range;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simmetrics.metrics.StringMetrics;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@SuppressWarnings("deprecation")
public class PixelDrop extends TeamlessMechanic {
	private static final String PREFIX = StringUtils.getPrefix("PixelDrop");
	private static final int MAX_ROUNDS = 10;
	private static final long TIME_BETWEEN_ROUNDS = TickTime.SECOND.x(8);

	@Override
	public @NotNull String getName() {
		return "Pixel Drop";
	}

	@Override
	public @NotNull String getDescription() {
		return "Try to guess the picture being drawn by the falling pixels by posting in chat";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.LIGHT_BLUE_CONCRETE);
	}

	@Override
	public void onJoin(@NotNull MatchJoinEvent event) {
		super.onJoin(event);
		Match match = event.getMatch();
		PixelDropMatchData matchData = match.getMatchData();
		if (!matchData.isAnimateLobby()) {
			matchData.setAnimateLobby(true);
			matchData.startLobbyAnimation(match);
		}
	}

	@Override
	public void onQuit(@NotNull MatchQuitEvent event) {
		super.onQuit(event);
		Match match = event.getMatch();
		PixelDropMatchData matchData = match.getMatchData();
		if (matchData.isAnimateLobby() && match.getMinigamers().isEmpty())
			matchData.setAnimateLobby(false);

		Player player = event.getMinigamer().getPlayer();
		if (player != null && matchData.getBossBar() != null) {
			player.hideBossBar(matchData.getBossBar());
		}
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);
		Match match = event.getMatch();
		PixelDropMatchData matchData = match.getMatchData();
		match.getTasks().cancel(matchData.getNextFrameTaskId());
		match.getTasks().cancel(matchData.getAnimateLobbyId());

		matchData.setupGame(match);
		endOfRound(match);
	}

	@Override
	public void kill(@NotNull Minigamer victim, @Nullable Minigamer attacker) {
		victim.spawn();
	}

	@Override
	public void onEnd(@NotNull MatchEndEvent event) {
		Match match = event.getMatch();
		PixelDropMatchData matchData = match.getMatchData();
		matchData.clearFloor(match);
		super.onEnd(event);

		for (Minigamer minigamer : match.getMinigamers()) {
			Player player = minigamer.getPlayer();
			if (player != null && matchData.getBossBar() != null) {
				player.hideBossBar(matchData.getBossBar());
			}
		}
	}

	public void endTheRound(Match match) {
		cancelCountdown(match);
		stopDesignTask(match);
		match.getTasks().wait(2 * 20, () -> endOfRound(match));
	}

	public void endOfRound(Match match) {
		PixelDropMatchData matchData = match.getMatchData();
		List<Minigamer> minigamers = match.getMinigamers();
		if (matchData.isRoundOver()) // if round has already ended
			return;

		matchData.endRound();

		if (matchData.getCurrentRound() != 0) {
			stopDesignTask(match);
			matchData.stopWordTask(match);

			dropRemainingBlocks(match);
			match.broadcastNoPrefix(PREFIX + "&c&lRound Over! &3The word was: &e" + matchData.getRoundWord());

			minigamers.stream().map(Minigamer::getOnlinePlayer).forEach(player -> {
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10F, 0.7F);
			});

			for (Minigamer minigamer : minigamers) {
				Player player = minigamer.getPlayer();
				if (player != null) {
					player.hideBossBar(matchData.getBossBar());
				}
			}
		}

		matchData.resetRound();

		if (matchData.getCurrentRound() >= MAX_ROUNDS) {
			match.getTasks().wait(3 * 20, () -> {
				minigamers.stream().map(Minigamer::getOnlinePlayer).forEach(player -> {
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10F, 1F);
				});

				match.broadcastNoPrefix(PREFIX + "&c&lGame Over!");
				match.getTasks().wait(3 * 20, match::end);
			});
		} else {
			// Start countdown to new round
			match.getTasks().wait(TIME_BETWEEN_ROUNDS / 2, () -> {
				matchData.clearFloor(match);
				match.getTasks().countdown(Countdown.builder()
						.duration(TIME_BETWEEN_ROUNDS)
						.onSecond(i -> minigamers.stream().map(Minigamer::getOnlinePlayer).forEach(player -> {
							if (match.isEnded())
								return;

							matchData.setTimeLeft(i);
							match.getScoreboard().update();

							if (Range.between(1, 3).contains(Math.toIntExact(i))) {
								player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10F, 0.5F);
							}
						}))
						.onComplete(() -> newRound(match)));
			});
		}
	}

	public void newRound(Match match) {
		if (match.isEnded()) return; // just in case
		PixelDropMatchData matchData = match.getMatchData();

		if (!matchData.isRoundOver()) return; // if a new round has already been started

		matchData.setupRound(match);
		startDesignTask(match);
		startRoundCountdown(match);
	}

	public void startDesignTask(Match match) {
		PixelDropMatchData matchData = match.getMatchData();
		if (matchData.getDesignTaskId() > 0) return;

		// Get Random Design
		Design design = matchData.getRandomDesign();

		matchData.getDesignsPlayed().add(design);

		matchData.setDesign(design);
		matchData.startWordTask(match);
		matchData.setCanGuess(true);

		// Get min point from current chosen design
		Location designMin = design.getMin();

		// Builds the map
		for (int x = 0; x < 15; x++) {
			for (int z = 0; z < 15; z++) {
				Block block = designMin.clone().add(x, 0, z).getBlock();
				if (block.getType().equals(Material.BARRIER)) continue;
				String key = x + "_" + z;
				matchData.getDesignMap().put(key, block);
				matchData.getDesignKeys().add(key);
			}
		}
		matchData.setDesignSize(matchData.getDesignKeys().size());

		// Random Paste
		matchData.setDesignTaskId(match.getTasks().repeat(0, 8, () -> {
			if (matchData.getDesignKeys().isEmpty()) {
				stopDesignTask(match);
				return;
			}

			String key = RandomUtils.randomElement(matchData.getDesignKeys());
			matchData.getDesignKeys().remove(key);

			String[] xz = key.split("_");
			Block block = matchData.getDesignMap().get(key);
			drop(match, block, Integer.parseInt(xz[0]), Integer.parseInt(xz[1]));
		}));
	}

	public void stopDesignTask(Match match) {
		PixelDropMatchData matchData = match.getMatchData();
		match.getTasks().cancel(matchData.getDesignTaskId());
		matchData.setDesignTaskId(-1);
	}

	public void dropRemainingBlocks(Match match) {
		PixelDropMatchData matchData = match.getMatchData();

		for (String key : matchData.getDesignKeys()) {
			Block block = matchData.getDesignMap().get(key);

			String[] xz = key.split("_");
			drop(match, block, Integer.parseInt(xz[0]), Integer.parseInt(xz[1]));
		}
		matchData.getDesignKeys().clear();
	}

	public void drop(Match match, Block block, int x, int z) {
		PixelDropArena arena = match.getArena();
		Location pasteMin = match.worldguard().toLocation(arena.getDropRegion().getMinimumPoint());
		Location blockCenter = LocationUtils.getBlockCenter(new Location(block.getWorld(), x, pasteMin.getY(), z));
		Location loc = pasteMin.clone().add(blockCenter.getX(), 0, blockCenter.getZ());

		FallingBlock fallingBlock = loc.getWorld().spawnFallingBlock(loc, block.getBlockData());
		fallingBlock.setDropItem(false);
		fallingBlock.setInvulnerable(true);
		fallingBlock.setVelocity(new org.bukkit.util.Vector(0, -0.5, 0));
	}

	@EventHandler
	public void onChat(MinecraftChatEvent event) {
		Minigamer minigamer = Minigamer.of(event.getChatter().getOnlinePlayer());
		if (!minigamer.isPlaying(this))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying(this)) return;

		// Only block actual chat, not commands
		if (!event.isAsynchronous()) return;

		Match match = minigamer.getMatch();
		PixelDropArena arena = match.getArena();
		PixelDropMatchData matchData = match.getMatchData();

		event.setCancelled(true);

		ChatterService chatService = new ChatterService();
		Set<Chatter> recipients = match.getMinigamers().stream()
				.map(_minigamer -> chatService.get(_minigamer.getOnlinePlayer()))
				.collect(toSet());

		PublicChatEvent publicChatEvent = new PublicChatEvent(
				chatService.get(minigamer.getOnlinePlayer()),
				StaticChannel.GLOBAL.getChannel(),
				event.getMessage(),
				event.getMessage(),
				true,
				false,
				recipients);

		Censor.process(publicChatEvent);

		if (publicChatEvent.isCancelled())
			return;

		Tasks.sync(() -> {
			String message = publicChatEvent.getMessage();
			List<Minigamer> guessed = matchData.getGuessed();
			List<Minigamer> minigamers = match.getMinigamers();

			if (!matchData.isCanGuess()) {
				minigamers.forEach(recipient -> sendChat(recipient, minigamer, "&f" + message));
				return;
			}

			if (guessed.contains(minigamer) && !matchData.isRoundOver()) {
				guessed.forEach(recipient -> sendChat(recipient, minigamer, "&7" + message));
				return;
			}

			final boolean correct = message.equalsIgnoreCase(matchData.getRoundWord());
			final float similarity = StringMetrics.levenshtein().compare(matchData.getRoundWord(), message);

			if (!correct) {
				if (similarity >= arena.getSimilarityThreshold())
					sendChat(minigamer, minigamer, "&e" + message + " &a(Close!)");
				else
					minigamers.forEach(recipient -> sendChat(recipient, minigamer, "&f" + message));
				return;
			}

			String guessTime = StringUtils.getTimeFormat(Duration.between(matchData.getRoundStart(), LocalDateTime.now()));

			player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10F, 0.5F);
			match.broadcastNoPrefix(PREFIX + "&e" + minigamer.getNickname() + " &3guessed the word in &e" + guessTime + "&3!");

			guessed.add(minigamer);
			minigamer.scored(Math.max(1, 1 + (4 - guessed.size())));
			match.getScoreboard().update();

			if (minigamers.size() == guessed.size()) {
				endTheRound(match);
			}
		});
	}

	private String getChatFormat(Minigamer sender, String message) {
		return StringUtils.getPrefix("PixelDrop") + sender.getNickname() + " > " + message;
	}

	private void sendChat(Minigamer recipient, Minigamer sender, String message) {
		recipient.tell(getChatFormat(sender, message), false);
	}

	public void startRoundCountdown(Match match) {
		PixelDropMatchData matchData = match.getMatchData();
		List<Minigamer> minigamers = match.getMinigamers();

		final int seconds = ((int) Math.ceil(matchData.getDesignSize() / 3.0)) + 10;

		matchData.setRoundCountdownId(match.getTasks().countdown(Countdown.builder()
			.duration(TickTime.SECOND.x(seconds))
			.onSecond(i -> minigamers.forEach(minigamer -> {

				if (match.isEnded())
					return;

				matchData.setTimeLeft(i);
				match.getScoreboard().update();

				if (Range.between(1, 3).contains(Math.toIntExact(i))) {
					Player player = minigamer.getOnlinePlayer();
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10F, 0.5F);
				}
			}))
			.onComplete(() -> endOfRound(match))
		));
	}

	public void cancelCountdown(Match match) {
		PixelDropMatchData matchData = match.getMatchData();
		match.getTasks().cancel(matchData.getRoundCountdownId());
		matchData.setTimeLeft(0);
		match.getScoreboard().update();
	}

	@Override
	public @NotNull LinkedHashMap<String, Integer> getScoreboardLines(@NotNull Match match) {
		LinkedHashMap<String, Integer> lines = new LinkedHashMap<>();
		PixelDropMatchData matchData = match.getMatchData();
		// During Game
		if (match.isStarted()) {

			// Inbetween Rounds
			if (matchData.isRoundOver()) {
				match.getMinigamers().stream().sorted(Comparator.comparingInt(mg -> ((Minigamer) mg).getScore()).reversed())
					.forEachOrdered(mg -> lines.put(mg.getNickname(), mg.getScore()));

				lines.put("&1", Integer.MIN_VALUE);
				lines.put("&2&fRound: &c" + matchData.getCurrentRound() + "&f/&c" + MAX_ROUNDS, Integer.MIN_VALUE);

				long timeLeft = matchData.getTimeLeft();
				if (timeLeft <= 0)
					lines.put("&3&fNext Round: &c", Integer.MIN_VALUE);
				else
					lines.put("&3&fNext Round: &c" + timeLeft, Integer.MIN_VALUE);

			// During Round
			} else {
				for (Minigamer minigamer : match.getMinigamers()) {
					if (matchData.getGuessed().contains(minigamer))
						lines.put("&1&a" + minigamer.getNickname(), Integer.MIN_VALUE);
					else
						lines.put("&1&f" + minigamer.getNickname(), Integer.MIN_VALUE);
				}

				lines.put("&2", Integer.MIN_VALUE);

				long timeLeft = matchData.getTimeLeft();
				if (timeLeft <= 0)
					lines.put("&3&fTime Left: ", Integer.MIN_VALUE);
				else
					lines.put("&4&fTime Left: &c" + timeLeft, Integer.MIN_VALUE);
			}

		// In Lobby
		} else {
			for (Minigamer minigamer : match.getMinigamers())
				lines.put(minigamer.getVanillaColoredName(), Integer.MIN_VALUE);
		}

		return lines;
	}

	@Override
	public boolean usesPerk(@NotNull Class<? extends Perk> perk, @NotNull Minigamer minigamer) {
		return !PlayerParticlePerk.class.isAssignableFrom(perk);
	}
}
