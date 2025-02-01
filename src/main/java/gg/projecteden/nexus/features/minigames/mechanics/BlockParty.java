package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.api.BlockPartyWebSocketServer;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchBeginEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchInitializeEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchQuitEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.BlockPartyMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlockParty extends TeamlessMechanic {

	static List<BlockPartySong> songList = new ArrayList<>();
	private static final String FOLDER = "plugins/Nexus/minigames/blockpartymusic/";

	// region minigame framework
	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemBuilder(Material.MUSIC_DISC_CAT).build();
	}

	@Override
	public @NotNull String getName() {
		return "Block Party";
	}

	@Override
	public @NotNull String getDescription() {
		return "Dance to music, and stand on the correct block";
	}

	@Override
	public void onInitialize(@NotNull MatchInitializeEvent event) {
		super.onInitialize(event);
		pasteLogoFloor(event.getMatch());
		selectSongsForVoting(event.getMatch());
	}

	@Override
	public void onJoin(@NotNull MatchJoinEvent event) {
		super.onJoin(event);
		Minigamer minigamer = event.getMinigamer();
		Player player = minigamer.getOnlinePlayer();

		if (!event.getMatch().isStarted()) {
			ItemStack menuItem = new ItemBuilder(Material.MUSIC_DISC_CAT).name("Vote for a Song!").build();
			player.getInventory().setItem(0, menuItem);
		}

		minigamer.getMatch().getTasks().wait(30, () ->
			minigamer.tell(new JsonBuilder("&e&lClick Here &3to listen to the music in your browser!")
				.url("https://projecteden.gg/blockparty")
				.hover("&3Open in your browser")
				.build()));
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);
		selectSong(event.getMatch());
		setSong(event.getMatch());
	}

	@Override
	public void onBegin(@NotNull MatchBeginEvent event) {
		super.onBegin(event);
		playSong(event.getMatch());
		waitWithMusic(event.getMatch());
	}

	@Override
	public boolean shouldBeOver(@NotNull Match match) {
		if (match.getAliveMinigamers().isEmpty()) {
			checkWin(match);
			return true;
		}
		return false;
	}

	@Override
	public void onQuit(@NotNull MatchQuitEvent event) {
		super.onQuit(event);

		BlockPartyWebSocketServer.broadcast(
			BlockPartyWebSocketServer.BlockPartyClientMessage.to(event.getMinigamer().getUniqueId()).stop().block("")
		);
	}

	@Override
	public void onEnd(@NotNull MatchEndEvent event) {
		super.onEnd(event);

		List<UUID> uuids = event.getMatch().getMinigamersAndSpectators().stream().map(Minigamer::getUuid).toList();
		BlockPartyWebSocketServer.broadcast(
			BlockPartyWebSocketServer.BlockPartyClientMessage.to(uuids).stop().block("")
		);
	}
	// endregion

	// region floor pasting
	private void pasteLogoFloor(Match match) {
		pasteFloor(match, 0);
	}

	private void pasteNewFloor(Match match) {
		BlockPartyMatchData matchData = match.getMatchData();
		matchData.incRound();
		if (matchData.getRound() % 2 == 1)
			match.broadcast("");
		pasteFloor(match, RandomUtils.randomInt(1, matchData.countFloors()));
		playSong(match);
		waitWithMusic(match);
	}

	private void pasteFloor(Match match, int index) {
		BlockPartyMatchData matchData = match.getMatchData();
		match.getArena().worldedit().paster("Block Party")
			.clipboard(matchData.getFloorInStack(index))
			.at(matchData.getPasteRegion().getMinimumPoint())
			.build();

		BlockPartyWebSocketServer.broadcast(
			BlockPartyWebSocketServer.BlockPartyClientMessage.to(getListenerUUIDs(match)).block("")
		);
	}

	private void waitWithMusic(Match match) {
		match.getTasks().wait(TimeUtils.TickTime.SECOND.x(3), () -> {
			selectColor(match);
			doCountdown(match);
		});
	}

	private void selectColor(Match match) {
		BlockPartyMatchData matchData = match.getMatchData();
		List<Material> types = match.getArena().worldedit().getBlocks(matchData.getPasteRegion())
			.stream().map(Block::getType).distinct().toList();
		matchData.setBlock(RandomUtils.randomElement(types));

		BlockPartyWebSocketServer.broadcast(
			BlockPartyWebSocketServer.BlockPartyClientMessage.to(getListenerUUIDs(match)).block(matchData.getBlock().name())
		);
	}

	private void doCountdown(Match match) {
		BlockPartyMatchData matchData = match.getMatchData();
		double countdownTime = getRoundSpeed(matchData.getRound());
		Tasks.Countdown.builder()
				.duration(TimeUtils.TickTime.SECOND.x(countdownTime))
				.onStart(() -> sendCountdownActionBar(match, countdownTime))
				.onSecond(i -> sendCountdownActionBar(match, i))
				.onComplete(() -> {
					pauseSong(match);
					clearFloor(match);
				}).start();
	}

	private void sendCountdownActionBar(Match match, double time) {
		BlockPartyMatchData matchData = match.getMatchData();
		ColorType color = ColorType.of(matchData.getBlock());

		JsonBuilder builder = new JsonBuilder();
		for (int i = 0; i < (int) time; i++)
			builder.next(color.getChatColor() + "■");
		builder.next(" &f&l" + color.name().replace("_", " ") + " ");
		for (int i = 0; i < (int) time; i++)
			builder.next(color.getChatColor() + "■");

		match.sendActionBar(builder);

		if (time == 3.0)
			pling(match, .7f);
		if (time == 2.0)
			pling(match, .6f);
		if (time == 1.0)
			pling(match, .5f);
	}

	private void pling(Match match, float pitch) {
		match.getMinigamersAndSpectators().forEach(minigamer -> minigamer.getPlayer().playSound(minigamer.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, pitch));
	}

	private double getRoundSpeed(int round) {
		return Math.max(6 - 0.5 * ((round + 1) / 2), 0.5);
	}

	private void clearFloor(Match match) {
		BlockPartyMatchData matchData = match.getMatchData();

		matchData.setAliveAtStartOfRound(match.getAliveMinigamers());

		matchData.getPasteRegion().forEach(vec3 -> {
			Block block = match.getArena().worldedit().toLocation(vec3).getBlock();
			if (block.getType() != matchData.getBlock())
				block.setType(Material.AIR);
		});

		waitAfterClear(match);
	}

	private void waitAfterClear(Match match) {
		match.getTasks().wait(TimeUtils.TickTime.SECOND.x(3), () -> {
			if (checkWin(match)) {
				match.end();
				return;
			}
			pasteNewFloor(match);
		});
	}

	private boolean checkWin(Match match) {
		BlockPartyMatchData matchData = match.getMatchData();
		if (match.getAliveMinigamers().isEmpty()) {
			matchData.getAliveAtStartOfRound().forEach(Minigamer::scored);
			announceTeamlessWinners(match);
			return true;
		}
		if (match.getAliveMinigamers().size() == 1 && match.getAllMinigamers().size() != 1) {
			match.getAliveMinigamers().getFirst().scored();
			announceTeamlessWinners(match);
			return true;
		}
		return false;
	}
	// endregion

	//region Song playing/selection
	private List<UUID> getListenerUUIDs(Match match) {
		return match.getMinigamersAndSpectators().stream().map(Minigamer::getUniqueId).collect(Collectors.toList());
	}

	public static BlockPartySong getSong(String id) {
		return songList.stream().filter(song -> song.fileName.equalsIgnoreCase(id)).findFirst().orElse(null);
	}

	private void selectSongsForVoting(Match match) {
		int songCount = Math.min(5, songList.size());

		List<BlockPartySong> selectedSongs = new ArrayList<>();

		for (int i = 0; i < songCount; i++) {
			List<BlockPartySong> random = new ArrayList<>(songList);
			Collections.shuffle(random);
			selectedSongs.add(random.getFirst());
		}

		((BlockPartyMatchData) match.getMatchData()).setPossibleSongs(selectedSongs);
	}

	private void selectSong(Match match) {
		BlockPartyMatchData matchData = match.getMatchData();
		String id = matchData.getVotes().values().stream()
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
			.entrySet().stream()
			.max(Map.Entry.comparingByValue())
			.map(Map.Entry::getKey)
			.orElse(RandomUtils.randomElement(matchData.getPossibleSongs()).fileName);

		BlockPartySong song = getSong(id);
		if (song == null)
			song = RandomUtils.randomElement(songList);
		matchData.setSong(song);
		match.broadcast("&3Now playing: &e&l" + song.title + " &3by &e&l" + song.artist);
	}

	private void setSong(Match match) {
		BlockPartySong song = ((BlockPartyMatchData) match.getMatchData()).getSong();

		BlockPartyWebSocketServer.broadcast(
			BlockPartyWebSocketServer.BlockPartyClientMessage.to(getListenerUUIDs(match))
				.song(new BlockPartyWebSocketServer.Song(
					song.title,
					song.artist,
					0,
					song.getUrl()
				))
				.play()
		);
	}

	private void playSong(Match match) {
		match.sendActionBar(new JsonBuilder("&a♫ &f&lDANCE &a♫"));

		BlockPartyMatchData matchData = match.getMatchData();
		matchData.setPlayTime(LocalDateTime.now());

		BlockPartyWebSocketServer.broadcast(
			BlockPartyWebSocketServer.BlockPartyClientMessage.to(getListenerUUIDs(match)).play()
		);
	}

	private void pauseSong(Match match) {
		match.sendActionBar(new JsonBuilder("&c&l✖ &f&lSTOP &c&l✖"));

		BlockPartyMatchData matchData = match.getMatchData();
		double seconds = Duration.between(matchData.getPlayTime(), LocalDateTime.now()).toSeconds();
		matchData.setSongTimeInSeconds(matchData.getSongTimeInSeconds() + seconds);

		BlockPartyWebSocketServer.broadcast(
			BlockPartyWebSocketServer.BlockPartyClientMessage.to(getListenerUUIDs(match)).pause()
		);
	}
	//endregion

	@EventHandler
	public void setPlayerBlock(PlayerInteractEvent event) {
		if (event.getItem() == null) return;
		if (event.getItem().getType() != Material.MUSIC_DISC_CAT) return;
		if (!Utils.ActionGroup.RIGHT_CLICK.applies(event)) return;

		Player player = event.getPlayer();
		if (!player.getWorld().equals(Minigames.getWorld())) return;

		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isInLobby(this)) return;

		Match match = minigamer.getMatch();
		if (match.isStarted()) return;

		if (((BlockPartyMatchData) match.getMatchData()).getPossibleSongs().isEmpty())
			return;

		new SongVoteMenu(match).open(player);
	}


	@Rows(2)
	@Title("Vote for a song")
	@AllArgsConstructor
	public static class SongVoteMenu extends InventoryProvider {

		private BlockPartyMatchData matchData;

		private SongVoteMenu(Match match) {
			this.matchData = match.getMatchData();
		}

		@Override
		public void init() {
			addCloseItem();

			AtomicInteger col = new AtomicInteger(2);

			matchData.getPossibleSongs().forEach(song -> {
				contents.set(1, col.getAndIncrement(), ClickableItem.of(
					new ItemBuilder(Material.PAPER)
						.name("&e&l" + song.title)
						.lore(
							"&3Artist: &e" + song.artist,
							"",
							"&3Votes: &e" + matchData.getVotes().values().stream().filter(val -> val.equalsIgnoreCase(song.fileName)).count()
						),
					e -> {
						matchData.getVotes().put(e.getPlayer().getUniqueId(), song.fileName);
						refresh();
					}
				));
			});
		}

	}

	@AllArgsConstructor
	public static class BlockPartySong {
		String title;
		String artist;
		String fileName;

		public String getUrl() {
			return "https://cdn.projecteden.gg/blockpartymusic/" + fileName;
		}
	}

	static {
		read();
	}

	@SneakyThrows
	public static void read() {
		Minigames.debug("Loading Block Party Music");
		Path path = Paths.get(FOLDER);
		File file = path.toFile();
		if (!file.exists()) file.createNewFile();
		songList.clear();
		try (Stream<Path> paths = Files.walk(path)) {
			paths.forEach(filePath -> {
				try {
					if (!Files.isRegularFile(filePath)) return;

					String name = filePath.getFileName().toString();
					if (name.startsWith(".")) return;
					if (!name.endsWith(".mp3")) return;

					read(name);
				} catch (Exception ex) {
					Nexus.severe("An error occurred while trying to read block party music file: " + filePath.getFileName().toFile(), ex);
					if (Nexus.isDebug())
						ex.printStackTrace();
				}
			});
		} catch (Exception ex) {
			Nexus.severe("An error occurred while trying to read block party music files: " + ex.getMessage());
			if (Nexus.isDebug())
				ex.printStackTrace();
		}
	}

	@SneakyThrows
	private static void read(String name) {
		String path = FOLDER + name;
		Minigames.debug("Reading file: " + path);

		File mp3File = new File(path);
		AudioFile audioFile = AudioFileIO.read(mp3File);

		Tag tag = audioFile.getTag();
		if (tag == null) {
			Minigames.debug("No metadata found.");
			return;
		}

		String title = tag.getFirst(FieldKey.TITLE);
		String artist = tag.getFirst(FieldKey.ARTIST);

		BlockPartySong song = new BlockPartySong(title, artist, name);
		songList.add(song);

		Minigames.debug("Loaded BP song: %s by %s (%s)".formatted(title, artist, name));
	}

	@EventHandler
	public void onWebClientJoin(BlockPartyWebSocketServer.BlockPartyClientConnectedEvent event) {
		MatchManager.getAll().stream()
			.filter(match -> match.getMechanic() instanceof BlockParty)
			.forEach(match -> {
				BlockPartyMatchData matchData = match.getMatchData();
				if (matchData.getSong() != null) {
					BlockPartySong song = matchData.getSong();
					event.addSong(
						getListenerUUIDs(match),
						song.title,
						song.artist,
						matchData.getSongTimeInSeconds(),
						song.getUrl(),
						matchData.isPlaying()
					);
				}
				if (matchData.getBlock() != null) {
					event.setBlock(getListenerUUIDs(match), matchData.getBlock().name());
				}
			});

		BlockPartySong song = RandomUtils.randomElement(songList);
		if (song != null) {
			event.addSong(
				List.of(UUIDUtils.UUID0),
				song.title,
				song.artist,
				30,
				song.getUrl(),
				true
			);
		}
	}

}
