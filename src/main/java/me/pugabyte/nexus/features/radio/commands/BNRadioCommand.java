package me.pugabyte.nexus.features.radio.commands;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.radio.RadioFeature;
import me.pugabyte.nexus.features.radio.Utils;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Confirm;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Description;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.features.Features;
import me.pugabyte.nexus.models.radio.RadioConfig;
import me.pugabyte.nexus.models.radio.RadioConfig.Radio;
import me.pugabyte.nexus.models.radio.RadioConfigService;
import me.pugabyte.nexus.models.radio.RadioUser;
import me.pugabyte.nexus.models.radio.RadioUserService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.features.radio.Utils.addPlayer;
import static me.pugabyte.nexus.features.radio.Utils.isInRangeOfAnyRadiusRadio;
import static me.pugabyte.nexus.features.radio.Utils.removePlayer;

// TODO on release: Remove admin perms, rename command to RadioCommand

@Permission("group.admin")
public class BNRadioCommand extends CustomCommand {
	RadioUserService userService = new RadioUserService();
	RadioConfigService configService = new RadioConfigService();
	RadioConfig config = configService.get(Nexus.getUUID0());
	RadioUser user;

	public BNRadioCommand(CommandEvent event) {
		super(event);

		if (isPlayer())
			user = userService.get(player());
	}

	@Path("join <radio>")
	@Description("Join a radio")
	void joinRadio(Radio radio) {
		if (isInRangeOfAnyRadiusRadio(player()))
			error("You are too close to another radio!");

		addPlayer(player(), radio.getSongPlayer());
	}

	@Path("leave")
	@Description("Leave the listened radio")
	void leaveRadio() {
		if (isInRangeOfAnyRadiusRadio(player()))
			error("You cannot leave this radio!");

		removePlayer(player(), user.getRadio().getSongPlayer());
	}

	@Path("toggle")
	@Description("Toggles in between joining and leaving the server radio")
	void toggleRadio() {
		if (isNullOrEmpty(user.getRadioId()))
			joinRadio(user.getLastRadio());
		else
			leaveRadio();
	}

	@Path("song")
	@Description("Shows the song info of the current song you are listening to")
	void songInfo() {
		SongPlayer songPlayer = Utils.getListenedRadio(player(), true);

		if (songPlayer == null)
			error("You are not listening to a radio!");

		Song song = songPlayer.getSong();
		send(PREFIX + "Current Song Playing:");
		send("&3Title:&e " + song.getTitle());
		send("&3Author:&e " + song.getAuthor());
		send("&3Progress:&e " + getSongPercent(songPlayer) + "&e%");
		send("");
	}

	@Path("playlist")
	@Description("Shows the playlist of the radio you are listening to")
	void playlist() {
		SongPlayer listenedRadio = Utils.getListenedRadio(player(), true);
		if (listenedRadio == null)
			error("You are not listening to a radio!");

		List<Song> songs = listenedRadio.getPlaylist().getSongList();
		int songListSize = songs.size();
		if (songListSize == 0)
			error("No songs in playlist");


		StringBuilder songList = new StringBuilder();
		int ndx = 1;
		for (Song tempSong : songs) {
			File songFile = tempSong.getPath();
			songList.append(ndx).append(" &e").append(songFile.getName());
			ndx++;
		}
		send(PREFIX + "Songs in playlist:");
		send(songList.toString());
	}

	@Path("players <radio>")
	@Description("Lists all players listening to the server radio")
	@Permission("group.staff")
	void listListeners(Radio radio) {
		Set<UUID> uuids = radio.getSongPlayer().getPlayerUUIDs();
		if (uuids.size() == 0)
			error("No players are listening.");

		StringBuilder playerList = new StringBuilder();
		int ndx = 1;
		for (UUID uuid : uuids) {
			Player player = Bukkit.getPlayer(uuid);
			if (player == null)
				continue;

			playerList.append(ndx).append(" &e").append(player.getName());
			ndx++;
		}

		send(PREFIX + "Players listening:");
		send(playerList.toString());
	}

	@Path("reload")
	@Description("Reloads the config, and remakes every radio")
	@Permission("group.admin")
	void reloadConfig() {
		Features.get(RadioFeature.class).reload();
		send(RadioFeature.getPREFIX() + "Config reloaded!");
	}

	@Path("config create radius <id> <radius>")
	void configCreateRadius(String id, int radius) {
		config.add(Radio.builder()
				.id(id)
				.radius(radius)
				.location(player().getLocation())
				.build());

		send(PREFIX + "Radius radio &e" + id + " &3created");
	}

	@Path("config create server <id>")
	void configCreateServer(String id) {
		config.add(Radio.builder()
				.id(id)
				.radius(0)
				.location(null)
				.build());

		send(PREFIX + "Server radio &e" + id + " &3created");
	}

	@Path("config addSong <radio> <song>")
	void configAddSong(Radio radio, RadioSong radioSong) {

	}

	@Path("config removeSong <radio> <song>")
	void configRemoveSong(Radio radio, RadioSong radioSong) {

	}

	@Confirm
	@Path("config delete <id>")
	void configDelete(Radio radio) {
		config.getRadios().remove(radio);
		send(PREFIX + "Radio &e" + radio.getId() + " &3deleted");
	}

	@ConverterFor(Radio.class)
	public Radio convertToRadio(String value) {
		Radio radio = config.getById(value);
		if (radio == null)
			throw new InvalidInputException("Radio &e" + value + " &cnot found");
		return radio;
	}

	@TabCompleterFor(Radio.class)
	List<String> tabCompleteRadio(String filter) {
		return config.getRadios().stream()
				.filter(radio -> radio.getId().toLowerCase().startsWith(filter.toLowerCase()))
				.map(Radio::getId)
				.collect(Collectors.toList());
	}

	private int getSongPercent(SongPlayer radio) {
		double songLen = radio.getSong().getLength();
		double current = radio.getTick();
		return (int) ((current / songLen) * 100.0);
	}

	public class RadioSong {
		String name;
		File file;
	}
}
