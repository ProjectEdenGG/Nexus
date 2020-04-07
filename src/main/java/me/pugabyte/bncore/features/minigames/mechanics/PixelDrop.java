package me.pugabyte.bncore.features.minigames.mechanics;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.Region;
import me.pugabyte.bncore.features.chat.events.MinecraftChatEvent;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.arenas.PixelDropArena;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchJoinEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchQuitEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.matchdata.PixelDropMatchData;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

// TODO:
//  - Clear floor animation (use blockiterator)
//  - Scoreboards

public class PixelDrop extends TeamlessMechanic {
	private final int MAX_ROUNDS = 5;
	private final int TIME_OUT = 8 * 20;
	private final int ROUND_COUNTDOWN = 45 * 20;

	@Override
	public String getName() {
		return "Pixel Drop";
	}

	@Override
	public String getDescription() {
		return "TODO";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.LIGHT_BLUE_CONCRETE);
	}

	@Override
	public void onJoin(MatchJoinEvent event) {
		super.onJoin(event);
		Match match = event.getMatch();
		PixelDropMatchData matchData = match.getMatchData();
		if (!matchData.isAnimateLobby()) {
			matchData.setAnimateLobby(true);
			matchData.startLobbyAnimation(match);
		}
	}

	@Override
	public void onQuit(MatchQuitEvent event) {
		super.onQuit(event);
		Match match = event.getMatch();
		PixelDropMatchData matchData = match.getMatchData();
		if (matchData.isAnimateLobby() && match.getMinigamers().size() == 0)
			matchData.setAnimateLobby(false);
	}

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);
		Match match = event.getMatch();
		PixelDropMatchData matchData = match.getMatchData();
		match.getTasks().cancel(matchData.getNextFrameTaskId());
		match.getTasks().cancel(matchData.getAnimateLobbyId());

		matchData.setupGame(match);
		endOfRound(match);
	}

	@Override
	public void onEnd(MatchEndEvent event) {
		Match match = event.getMatch();
		PixelDropMatchData matchData = match.getMatchData();
		matchData.clearFloor(match);
		super.onEnd(event);
	}

	public void endOfRound(Match match) {
		PixelDropMatchData matchData = match.getMatchData();
		List<Minigamer> minigamers = match.getMinigamers();
		matchData.endRound(match);

		if (matchData.getCurrentRound() != 0) {
			stopDesignTask(match);
			matchData.stopWordTask(match);

			dropRemainingBlocks(match);
			matchData.revealWord(match);

			minigamers.stream().map(Minigamer::getPlayer).forEach(player ->
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10F, 0.7F));
			match.broadcast("&c&lRound Over!&c The word was: " + matchData.getRoundWord());
		}

		matchData.resetRound();

		if (matchData.getCurrentRound() == MAX_ROUNDS) {
			match.getTasks().wait(3 * 20, () -> {
				minigamers.stream().map(Minigamer::getPlayer).forEach(player -> {
					Utils.sendActionBar(player, "&c&lGame Over!");
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10F, 1F);
				});
				match.broadcast("&c&lGame Over!");
				match.getTasks().wait(3 * 20, match::end);
			});
		} else {
			// Start countdown to new round
			match.getTasks().wait(TIME_OUT / 2, () -> {
				matchData.clearFloor(match);
				Tasks.Countdown countdown = Tasks.Countdown.builder()
						.duration(TIME_OUT)
						.onSecond(i -> minigamers.stream().map(Minigamer::getPlayer).forEach(player -> {
							if (match.isEnded())
								return;

							matchData.setTimeLeft(i);
							match.getScoreboard().update();

							Utils.sendActionBar(player, "&cNext round starts in...&c&l " + i + " second" + (i != 1 ? "s" : ""));
							if (i <= 3)
								player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10F, 0.5F);
						}))
						.onComplete(() -> newRound(match))
						.start();

				match.getTasks().register(countdown.getTaskId());
			});
		}
	}

	public void newRound(Match match) {
		if (match.isEnded()) return; // just in case
		PixelDropMatchData matchData = match.getMatchData();
		matchData.setupRound(match);
		startDesignTask(match);
	}

	public void startDesignTask(Match match) {
		PixelDropMatchData matchData = match.getMatchData();
		PixelDropArena arena = match.getArena();

		// Get Random Design
		Region designsRegion = arena.getDesignRegion();
		Region dropRegion = arena.getDropRegion();

		int designCount = matchData.getDesignCount();
		int design = Utils.randomInt(1, designCount);
		for (int i = 0; i < designCount; i++) {
			design = Utils.randomInt(1, designCount);
			if (matchData.getDesign() != design)
				break;
		}
		matchData.setDesign(design);
		matchData.startWordTask(match);

		// Get min point from current chosen design
		Vector designMin = designsRegion.getMinimumPoint().subtract(0, 1, 0).add(0, design, 0);
		// Get min point of paste region
		Vector pasteMin = dropRegion.getMinimumPoint();

		// Builds the map
		for (int x = 0; x < 15; x++) {
			for (int z = 0; z < 15; z++) {
				Block block = WGUtils.toLocation(designMin.add(x, 0, z)).getBlock();
				if (block.getType().equals(Material.BARRIER)) continue;
				String key = x + "_" + z;
				matchData.getDesignMap().put(key, block);
				matchData.getDesignKeys().add(key);
			}
		}

		// Random Paste
		int nextDesignTaskId = match.getTasks().repeat(0, 8, () -> {
			if (matchData.getDesignKeys().size() == 0) {
				stopDesignTask(match);
				return;
			}

			String key = Utils.getRandomElement(matchData.getDesignKeys());
			matchData.getDesignKeys().remove(key);
			String[] xz = key.split("_");
			int x = Integer.parseInt(xz[0]);
			int z = Integer.parseInt(xz[1]);

			Block block = matchData.getDesignMap().get(x + "_" + z);
			double blockX = x + 0.5;
			double blockZ = z + 0.5;
			Location loc = WGUtils.toLocation(pasteMin.add(blockX, 0, blockZ));

			FallingBlock fallingBlock = loc.getWorld().spawnFallingBlock(loc, block.getType().createBlockData());
			fallingBlock.setDropItem(false);
			fallingBlock.setInvulnerable(true);
			fallingBlock.setVelocity(new org.bukkit.util.Vector(0, -0.5, 0));

		});
		matchData.setDesignTaskId(nextDesignTaskId);
	}

	public void stopDesignTask(Match match) {
		PixelDropMatchData matchData = match.getMatchData();
		match.getTasks().cancel(matchData.getDesignTaskId());
	}

	public void dropRemainingBlocks(Match match) {
		PixelDropArena arena = match.getArena();
		PixelDropMatchData matchData = match.getMatchData();

		Vector pasteMin = arena.getDropRegion().getMinimumPoint();
		for (String key : matchData.getDesignKeys()) {
			Block block = matchData.getDesignMap().get(key);

			String[] xz = key.split("_");
			int x = Integer.parseInt(xz[0]);
			int z = Integer.parseInt(xz[1]);
			double blockX = x + 0.5;
			double blockZ = z + 0.5;
			Location loc = WGUtils.toLocation(pasteMin.add(blockX, 0, blockZ));

			FallingBlock fallingBlock = loc.getWorld().spawnFallingBlock(loc, block.getType().createBlockData());
			fallingBlock.setDropItem(false);
			fallingBlock.setInvulnerable(true);
			fallingBlock.setVelocity(new org.bukkit.util.Vector(0, -0.5, 0));
		}
		matchData.getDesignKeys().clear();
	}

	@EventHandler
	public void onChat(MinecraftChatEvent event) {
		Player player = event.getChatter().getPlayer();
		Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isPlaying(this)) return;

		PixelDropMatchData matchData = minigamer.getMatch().getMatchData();
		if (!matchData.canGuess()) return;

		if (player.hasPermission("group.staff"))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isPlaying(this)) return;

		// Only block actual chat, not commands
		if (!event.isAsynchronous()) return;

		Match match = minigamer.getMatch();
		PixelDropMatchData matchData = match.getMatchData();

		if (!matchData.canGuess()) return;
		event.setCancelled(true);

		Tasks.sync(() -> {
			String message = event.getMessage();
			if (matchData.getGuessed().contains(minigamer)) {
				matchData.getGuessed().forEach(recipient -> sendChat(recipient, minigamer, "&7" + message));
				return;
			}

			if (!message.equalsIgnoreCase(matchData.getRoundWord())) {
				match.getMinigamers().forEach(recipient -> sendChat(recipient, minigamer, "&f" + message));
				return;
			}

			player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 10F, 0.5F);
			match.broadcast("&a" + minigamer.getName() + " guessed the word!");
			matchData.getGuessed().add(minigamer);
			minigamer.scored(Math.max(1, 1 + (4 - matchData.getGuessed().size())));
			match.getScoreboard().update();

			if (matchData.getGuessed().size() == 1)
				startRoundCountdown(match);

			if (match.getMinigamers().size() == matchData.getGuessed().size()) {
				cancelCountdown(match);
				stopDesignTask(match);
				matchData.canGuess(false);
				match.getTasks().wait(2 * 20, () -> endOfRound(match));
			}
		});
	}

	private String getChatFormat(Minigamer sender, String message) {
		return StringUtils.getPrefix("PixelDrop") + sender.getName() + " > " + message;
	}

	private void sendChat(Minigamer recipient, Minigamer sender, String message) {
		recipient.tell(getChatFormat(sender, message), false);
	}

	public void startRoundCountdown(Match match) {
		List<Minigamer> minigamers = match.getMinigamers();
		minigamers.stream().map(Minigamer::getPlayer).forEach(player ->
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10F, 0.5F));

		PixelDropMatchData matchData = match.getMatchData();
		Tasks.Countdown countdown = Tasks.Countdown.builder()
				.duration(ROUND_COUNTDOWN)
				.onSecond(i -> minigamers.stream().map(Minigamer::getPlayer).forEach(player -> {
					if (match.isEnded()) return;
					matchData.setTimeLeft(i);
					match.getScoreboard().update();

//					Utils.sendActionBar(player, "&cRound ends in...&c&l " + i + " second" + (i != 1 ? "s" : ""));
					if (i <= 3)
						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10F, 0.5F);
				}))
				.onComplete(() -> endOfRound(match))
				.start();

		matchData.setRoundCountdownId(countdown.getTaskId());
		match.getTasks().register(countdown.getTaskId());
	}

	public void cancelCountdown(Match match) {
		PixelDropMatchData matchData = match.getMatchData();
		match.getTasks().cancel(matchData.getRoundCountdownId());
		matchData.setTimeLeft(0);
		match.getScoreboard().update();
	}
}
