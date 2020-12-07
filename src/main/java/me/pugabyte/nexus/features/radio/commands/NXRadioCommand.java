package me.pugabyte.nexus.features.radio.commands;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.radio.RadioFeature;
import me.pugabyte.nexus.features.radio.RadioUtils;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
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
import me.pugabyte.nexus.models.radio.RadioSong;
import me.pugabyte.nexus.models.radio.RadioType;
import me.pugabyte.nexus.models.radio.RadioUser;
import me.pugabyte.nexus.models.radio.RadioUserService;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.features.radio.RadioUtils.addPlayer;
import static me.pugabyte.nexus.features.radio.RadioUtils.getListenedRadio;
import static me.pugabyte.nexus.features.radio.RadioUtils.isInRangeOfRadiusRadio;
import static me.pugabyte.nexus.features.radio.RadioUtils.removePlayer;

// TODO on release: Remove admin perms, rename command to RadioCommand

@Permission("group.admin")
public class NXRadioCommand extends CustomCommand {
	RadioUserService userService = new RadioUserService();
	RadioConfigService configService = new RadioConfigService();
	RadioConfig config = configService.get(Nexus.getUUID0());
	RadioUser user;

	public NXRadioCommand(CommandEvent event) {
		super(event);

		if (isPlayer())
			user = userService.get(player());
	}

	@Path("join <radio>")
	@Description("Join a radio")
	void joinRadio(Radio radio) {
		if (isInRangeOfRadiusRadio(player()))
			error("You are too close to another radio!");
		if (!radio.isEnabled())
			error("That radio is not enabled!");

		addPlayer(player(), radio);
	}

	@Path("leave")
	@Description("Leave the listened radio")
	void leaveRadio() {
		if (isInRangeOfRadiusRadio(player()))
			error("You cannot leave this radio!");

		removePlayer(player(), getListenedRadio(player()));
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
		Radio radio = RadioUtils.getListenedRadio(player(), true);

		if (radio == null)
			error("You are not listening to a radio!");

		SongPlayer songPlayer = radio.getSongPlayer();
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
		Radio listenedRadio = RadioUtils.getListenedRadio(player(), true);
		if (listenedRadio == null)
			error("You are not listening to a radio!");

		List<Song> songs = listenedRadio.getSongPlayer().getPlaylist().getSongList();
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

	// Staff Commands

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

	@Path("debugUser <player>")
	@Permission("group.admin")
	void debugUser(OfflinePlayer player) {
		RadioUser user = userService.get(player);
		try {
			send(StringUtils.toPrettyString(user));
		} catch (StackOverflowError | Exception ignored) {
			send(user.toString());
		}
	}

	@Path("debugRadio <radio>")
	@Permission("group.admin")
	void debugRadio(Radio radio) {
		try {
			send(StringUtils.toPrettyString(radio));
		} catch (StackOverflowError | Exception ignored) {
			send(radio.toString());
		}
	}

	@Path("songs")
	@Permission("group.admin")
	void listSongs() {
		send("Loaded Songs: ");
		for (RadioSong radioSong : RadioFeature.getAllSongs()) {
			send("- " + radioSong.getName());
		}
	}

	@Path("reload")
	@Description("Reloads the config, and remakes every radio")
	@Permission("group.admin")
	void reloadConfig() {
		Features.get(RadioFeature.class).reload();
		send(RadioFeature.getPREFIX() + "Config reloaded!");
	}

	// Config Commands

	@Path("config reload <radio>")
	@Description("Recreate a radio")
	@Permission("group.admin")
	void configReload(Radio radio) {
		radio.reload();
		send(PREFIX + StringUtils.camelCase(radio.getType()) + " Radio &e" + radio.getId() + " &3reloaded");
	}

	@Path("config create <type> <id> [radius]")
	@Description("Create a radio")
	@Permission("group.admin")
	void configCreate(RadioType type, String id, @Arg("0") int radius) {
		if (type.equals(RadioType.RADIUS)) {
			config.add(Radio.builder()
					.id(id)
					.type(RadioType.RADIUS)
					.radius(radius)
					.location(player().getLocation())
					.build());

		} else if (type.equals(RadioType.SERVER)) {
			config.add(Radio.builder()
					.id(id)
					.type(RadioType.SERVER)
					.build());

		}
		configService.save(config);

		send(PREFIX + StringUtils.camelCase(type) + " Radio &e" + id + " &3created");
	}

	@Path("config setType <radio> <type>")
	@Permission("group.admin")
	void configSetType(Radio radio, RadioType type) {
		radio.setType(type);
		configService.save(config);

		send(PREFIX + "Type set to " + radio.getType() + " on " + radio.getId());
	}

	@Path("config setRadius <radio> <radius>")
	@Permission("group.admin")
	void configSetRadius(Radio radio, int radius) {
		if (!radio.getType().equals(RadioType.RADIUS))
			error("Can only set radius of a radius radio");

		radio.setRadius(radius);
		radio.reload();

		configService.save(config);

		send(PREFIX + "Radius set to " + radio.getRadius() + " on " + radio.getId());
	}

	@Path("config setLocation <radio>")
	@Permission("group.admin")
	void configSetLocation(Radio radio) {
		if (!radio.getType().equals(RadioType.RADIUS))
			error("Can only set location of a radius radio");

		radio.setLocation(player().getLocation());
		radio.reload();

		configService.save(config);

		send(PREFIX + "Location set to " + StringUtils.getShortLocationString(radio.getLocation()) + " on " + radio.getId());
	}

	@Path("config addSong <radio> <song>")
	@Description("Add a song to a radio")
	@Permission("group.admin")
	void configAddSong(Radio radio, RadioSong radioSong) {
		radio.getSongs().add(radioSong.getName());
		configService.save(config);

		send(PREFIX + "Added " + radioSong.getName() + " to " + radio.getId());
	}

	@Path("config removeSong <radio> <song>")
	@Description("Remove a song from a radio")
	@Permission("group.admin")
	void configRemoveSong(Radio radio, @Arg(context = 1) RadioSong radioSong) {
		radio.getSongs().remove(radioSong.getName());
		configService.save(config);

		send(PREFIX + "Removed " + radioSong.getName() + " from " + radio.getId());
	}

	@Path("config enable <radio>")
	@Description("Enable the radio")
	@Permission("group.admin")
	void configEnable(Radio radio) {
		RadioFeature.verify(radio);

		radio.setEnabled(true);
		configService.save(config);

		send(PREFIX + "Enabled " + radio.getId());
	}

	@Path("config disable <radio>")
	@Description("Disable the radio")
	@Permission("group.admin")
	void configDisable(Radio radio) {
		radio.setEnabled(false);
		configService.save(config);

		send(PREFIX + "Disabled " + radio.getId());
	}

	@Confirm
	@Path("config delete <id>")
	@Description("Delete the radio")
	@Permission("group.admin")
	void configDelete(Radio radio) {
		RadioUtils.removeRadio(radio);

		send(PREFIX + "Radio &e" + radio.getId() + " &3deleted");
	}

	@ConverterFor(Radio.class)
	Radio convertToRadio(String value) {
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

	@ConverterFor(RadioSong.class)
	RadioSong convertToRadioSong(String value) {
		return RadioFeature.getRadioSongByName(value)
				.orElseThrow(() -> new InvalidInputException("Song &e" + value + " &cnot found"));
	}

	@TabCompleterFor(RadioSong.class)
	List<String> tabCompleteRadioSong(String filter, Radio context) {
		return RadioFeature.getAllSongs().stream()
				.filter(song -> song.getName().toLowerCase().startsWith(filter.toLowerCase()))
				.filter(song -> {
					if (context != null)
						return context.getSongs().contains(song.getName());
					return true;
				})
				.map(RadioSong::getName)
				.collect(Collectors.toList());
	}

	private int getSongPercent(SongPlayer radio) {
		double songLen = radio.getSong().getLength();
		double current = radio.getTick();
		return (int) ((current / songLen) * 100.0);
	}
}
