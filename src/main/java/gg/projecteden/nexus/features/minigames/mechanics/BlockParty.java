package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.api.BlockPartyWebSocketServer;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchBeginEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchInitializeEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.BlockPartyMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Material;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlockParty extends TeamlessMechanic {

	static List<BlockPartySong> songList = new ArrayList<>();
	private static final String FOLDER = "plugins/Nexus/minigames/blockpartymusic/";

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
	}

	@Override
	public void onJoin(@NotNull MatchJoinEvent event) {
		super.onJoin(event);
		Minigamer minigamer = event.getMinigamer();
		Player player = minigamer.getOnlinePlayer();

		ItemStack menuItem = new ItemBuilder(Material.MUSIC_DISC_CAT).name("Vote for a Song!").build();
		player.getInventory().setItem(0, menuItem);

		minigamer.getMatch().getTasks().wait(30, () ->
			minigamer.tell(new JsonBuilder("&e&lClick Here &3to listen to the music in your browser!")
				.url("https://projecteden.gg/blockparty")
				.hover("&3Open in your browser")
				.build()));
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		super.onStart(event);
		// TODO - pick song based on votes
	}

	@Override
	public void onBegin(@NotNull MatchBeginEvent event) {
		super.onBegin(event);
		playSong(event.getMatch());
		waitWithMusic(event.getMatch());
	}

	private void pasteLogoFloor(Match match) {
		// TODO
	}

	private void pasteNewFloor(Match match) {
		// TODO
		playSong(match);
		waitWithMusic(match);
	}

	private void waitWithMusic(Match match) {
		// TODO
		pauseSong(match);
		selectColor(match);
		doCountdown(match);
	}

	private void selectColor(Match match) {
		// TODO
	}

	private void doCountdown(Match match) {
		// TODO
		clearFloor(match);
	}

	private void clearFloor(Match match) {
		// TODO
		waitAfterClear(match);
	}

	private void waitAfterClear(Match match) {
		// TODO
		if (checkWin(match))
			return;
		pasteNewFloor(match);
	}

	private boolean checkWin(Match match) {
		return false;
	}

	private List<UUID> getListenerUUIDs(Match match) {
		return match.getMinigamersAndSpectators().stream().map(Minigamer::getUniqueId).collect(Collectors.toList());
	}

	private void setSongAndPlay(Match match) {
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
		BlockPartyWebSocketServer.broadcast(
			BlockPartyWebSocketServer.BlockPartyClientMessage.to(getListenerUUIDs(match)).play()
		);
	}

	private void pauseSong(Match match) {
		BlockPartyWebSocketServer.broadcast(
			BlockPartyWebSocketServer.BlockPartyClientMessage.to(getListenerUUIDs(match)).pause()
		);
	}

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

		new SongVoteMenu(match).open(player);
	}

	@AllArgsConstructor
	public static class SongVoteMenu extends InventoryProvider {

		private Match match;
		private BlockPartyMatchData matchData;

		private SongVoteMenu(Match match) {
			this.match = match;
			this.matchData = match.getMatchData();
		}

		@Override
		public void init() {

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

		// TODO - remove, temp for testing
		BlockPartySong song = songList.stream().findFirst().orElse(null);
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
