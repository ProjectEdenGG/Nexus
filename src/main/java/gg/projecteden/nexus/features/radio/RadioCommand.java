package gg.projecteden.nexus.features.radio;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.*;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.radio.RadioConfig;
import gg.projecteden.nexus.models.radio.RadioConfig.Radio;
import gg.projecteden.nexus.models.radio.RadioConfig.RadioSong;
import gg.projecteden.nexus.models.radio.RadioConfig.RadioType;
import gg.projecteden.nexus.models.radio.RadioConfigService;
import gg.projecteden.nexus.models.radio.RadioUser;
import gg.projecteden.nexus.models.radio.RadioUserService;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RadioCommand extends CustomCommand {
	RadioConfigService configService = new RadioConfigService();
	RadioConfig config = configService.get0();
	RadioUserService userService = new RadioUserService();
	RadioUser user;

	public RadioCommand(CommandEvent event) {
		super(event);
		PREFIX = RadioFeature.PREFIX;

		if (isPlayer())
			user = userService.get(player());
	}

	@Path("join [radio]")
	@Description("Join a radio")
	void joinRadio(Radio radio) {
		if (user.isMute())
			error("You have muted all radios!");

		if (radio == null) {
			radio = RadioUtils.getRadiusRadio(player());
			if (radio == null)
				error("You are not near a radio!\nChoices: &e"
						+ RadioUtils.getServerRadios().stream().map(Radio::getId).collect(Collectors.joining("&3, &e")));
		}

		if (radio.getType().equals(RadioType.RADIUS) && !RadioUtils.isInRangeOfRadiusRadio(player(), radio))
			error("You are not near that radio!");

		if (!radio.isEnabled())
			error("That radio is not enabled!");

		Radio listenedRadio = RadioUtils.getListenedRadio(player(), true);
		if (listenedRadio != null)
			RadioUtils.removePlayer(player(), listenedRadio);

		if (radio.getType().equals(RadioType.RADIUS))
			user.getLeftRadiusRadios().remove(radio.getId());

		RadioUtils.addPlayer(player(), radio);
	}

	@Path("leave")
	@Description("Leave the listened radio")
	void leaveRadio() {
		Radio radio = RadioUtils.getListenedRadio(player(), true);
		if (radio == null)
			return;

		RadioUtils.removePlayer(player(), radio);
		if (radio.getType().equals(RadioType.RADIUS))
			user.getLeftRadiusRadios().add(radio.getId());
		else
			user.setLastServerRadioId(null);

		if (user.getLastServerRadio() != null)
			RadioUtils.addPlayer(player(), user.getLastServerRadio());

		userService.save(user);
	}

	@Path("toggle")
	@Description("Toggles in between joining and leaving the server radio")
	void toggleRadio() {
		if (Nullables.isNullOrEmpty(user.getServerRadioId()))
			joinRadio(user.getLastServerRadio());
		else
			leaveRadio();
	}

	@Path("volume <number>")
	@Description("Sets the volume for all radios for yourself")
	void volume(@Arg(value = "0", min = 0, max = 100) byte volume) {
		user.setVolume(volume);
		userService.save(user);

		send(PREFIX + "Volume set to: &e" + user.getVolume());
	}

	@Path("info")
	@Description("Shows info about the radio you are listening to")
	void songInfo() {
		Radio radio = RadioUtils.getListenedRadio(player(), true);

		if (radio == null)
			error("You are not listening to a radio!");

		SongPlayer songPlayer = radio.getSongPlayers().get(0); // only difference is their locations
		Song song = songPlayer.getSong();

		send(PREFIX + "Radio Info:");
		send("&3Radio: &e" + StringUtils.camelCase(radio.getId()));

		List<String> list = RadioUtils.getPlaylistHover(radio);
		send(json("&3Songs: &e[" + list.size() + "]").hover(list).loreize(false));

		send("&3Playing: &e" + song.getTitle() + " &3by &e" + song.getAuthor() + " &3(" + getSongPercent(songPlayer) + "%)");
		line();
	}

	@Path("mute [enable]")
	@Description("Mute all radios")
	void mute(Boolean enable) {
		if (enable) {
			user.setMute(true);
			leaveRadio();
			send(PREFIX + "Muted all radios");
		} else {
			user.setMute(false);
			send(PREFIX + "Unmuted all radios");
		}

		userService.save(user);
	}

	// Staff Commands

	@Path("players <radio>")
	@Permission(Group.STAFF)
	@Description("Lists all players listening to the server radio")
	void listListeners(Radio radio) {
		if (radio.getType() != RadioType.SERVER)
			error("This command can only be used with server radios");

		Set<UUID> uuids = radio.getSongPlayers().get(0).getPlayerUUIDs();
		if (uuids.isEmpty())
			error("No players are listening to " + radio.getId());

		send(PREFIX + "Players listening to " + radio.getId() + ":");
		int ndx = 1;
		for (UUID uuid : uuids) {
			send("&3" + ndx++ + " &e" + Nickname.of(uuid));
		}
	}

	@Path("teleport <radio> [index]")
	@Permission(Group.STAFF)
	@Description("Teleport to the location of the radio")
	void teleport(Radio radio, @Arg("0") Integer index) {
		switch (radio.getType()) {
			case SERVER -> error("This radio type doesn't have a location");
			case RADIUS -> player().teleportAsync(radio.getRadiusLocation(), TeleportCause.COMMAND);
			case STATION -> player().teleportAsync(radio.getStationLocations().get(index), TeleportCause.COMMAND);
		}
	}

	@Path("debugUser <player>")
	@Permission(Group.ADMIN)
	@Description("Display data of a user")
	void debugUser(OfflinePlayer player) {
		RadioUser user = userService.get(player);

		send(PREFIX + "&3User Debug: ");
		send("&3Player: &e" + user.getNickname());
		send("&3Is Mute: &e" + user.isMute());
		send("&3ServerRadioId: &e" + user.getServerRadioId());
		send("&3LastServerRadioId: &e" + user.getLastServerRadioId());
		send("&3LeftRadiusRadios: &e" + user.getLeftRadiusRadios());
		line();
	}

	@Path("debugRadio <radio>")
	@Permission(Group.ADMIN)
	@Description("Display data of a radio")
	void debugRadio(Radio radio) {
		send(PREFIX + "&3Radio Debug: ");
		send("&3Id: &e" + radio.getId());
		send("&3Enabled: &e" + radio.isEnabled());
		send("&3Type: &e" + StringUtils.camelCase(radio.getType()));
		send("&3Updates Playing: &e" + radio.isUpdatePlaying());

		if (radio.getType().equals(RadioType.RADIUS)) {
			send("&3Location: &e" + StringUtils.getShortLocationString(radio.getRadiusLocation()));
			send("&3Radius: &e" + radio.getRadius());
			send("&3Particles: &e" + radio.isParticles());
		}

		if (radio.getType() == RadioType.STATION) {
			send("&3Locations:");
			for (Location location : radio.getStationLocations()) {
				send("&3- &e" + StringUtils.getShortLocationString(location));
			}
			send("&3Radius: &e" + radio.getRadius());
			send("&3Particles: &e" + radio.isParticles());
		}

		List<String> list = RadioUtils.getPlaylistHover(radio);
		send(json("&3Songs: &e[" + list.size() + "]").hover(list));

		line();
	}

	@Path("reload")
	@Permission(Group.ADMIN)
	@Description("Reloads the config, and remakes every radio")
	void reloadConfig() {
		Features.get(RadioFeature.class).reload();
		send(PREFIX + "Config reloaded!");
	}

	// Config Commands

	@Path("config reload <radio>")
	@Permission(Group.ADMIN)
	@Description("Recreate a radio")
	void configReload(Radio radio) {
		if (!radio.isEnabled())
			error("Radio is not enabled");
		radio.reload();
		send(PREFIX + StringUtils.camelCase(radio.getType()) + " Radio &e" + radio.getId() + " &3reloaded");
	}

	@Path("config create <type> <id> [radius]")
	@Permission(Group.ADMIN)
	@Description("Create a radio")
	void configCreate(RadioType type, String id, @Arg("0") int radius) {
		switch (type) {
			case SERVER -> {
				config.add(Radio.builder()
						.id(id)
						.type(RadioType.SERVER)
						.build());
			}

			case RADIUS, STATION -> {
				config.add(Radio.builder()
						.id(id)
						.type(type)
						.radius(radius)
						.locations(Collections.singletonList(location()))
						.build());
			}
		}

		configService.save(config);

		send(PREFIX + StringUtils.camelCase(type) + " Radio &e" + id + " &3created");
	}

	@Path("config setParticles <radio> <enable>")
	@Permission(Group.ADMIN)
	@Description("Toggle the particles of the radio")
	void configSetParticles(Radio radio, boolean enable) {
		if (!radio.getType().isRadiusBased())
			error("You can only set particles of a radius based radio");

		radio.setParticles(enable);
		configService.save(config);

		send(PREFIX + "Particles set to " + radio.isParticles() + " for " + radio.getId());
	}

	@Path("config setUpdatePlaying <radio> <enable>")
	@Permission(Group.ADMIN)
	@Description("Toggle the playing updates of the radio")
	void configSetUpdatePlaying(Radio radio, boolean enable) {
		if (!radio.getType().isRadiusBased())
			error("You can only set this on a radius based radio");

		radio.setUpdatePlaying(enable);
		configService.save(config);

		send(PREFIX + "Update Playing set to " + radio.isUpdatePlaying() + " for " + radio.getId());
	}

	@Path("config setType <radio> <type>")
	@Permission(Group.ADMIN)
	@Description("Set the radio type")
	void configSetType(Radio radio, RadioType type) {
		radio.setType(type);
		configService.save(config);

		send(PREFIX + "Type set to " + radio.getType() + " for " + radio.getId());
	}

	@Path("config setRadius <radio> <radius>")
	@Permission(Group.ADMIN)
	@Description("Set the radius of the radio")
	void configSetRadius(Radio radio, int radius) {
		if (!radio.getType().isRadiusBased())
			error("You can only set radius of a radius based radio");

		radio.setRadius(radius);
		if (radio.isEnabled())
			radio.reload();

		configService.save(config);

		send(PREFIX + "Radius set to " + radio.getRadius() + " for " + radio.getId());
	}

	@Path("config setRadiusLocation <radio>")
	@Permission(Group.ADMIN)
	@Description("Set the location of the radius radio to your current location")
	void configSetLocation(Radio radio) {
		if (radio.getType() != RadioType.RADIUS)
			error("This radio type is not a radius radio");

		radio.setRadiusLocation(location());

		if (radio.isEnabled())
			radio.reload();

		configService.save(config);
		send(PREFIX + "Location set to " + StringUtils.getShortLocationString(radio.getRadiusLocation()) + " for " + radio.getId());
	}

	@Path("config addStationLocation <radio>")
	@Permission(Group.ADMIN)
	@Description("Adds your current location to the station radio")
	void configAddStationLocation(Radio radio) {
		if (radio.getType() != RadioType.STATION)
			error("This radio type is not a station radio");

		radio.addStationLocation(location());
		if (radio.isEnabled())
			radio.reload();

		configService.save(config);
		send(PREFIX + "Added location at " + StringUtils.getShortLocationString(radio.getRadiusLocation()) + " to " + radio.getId());
	}

	@Path("config removeStationLocation <radio> <index>")
	@Permission(Group.ADMIN)
	@Description("Removes the location from the station radio")
	void configRemoveStationLocation(Radio radio, int index) {
		if (radio.getType() != RadioType.STATION)
			error("This radio type is not a station radio");

		radio.removeStationLocation(index);
		if (radio.isEnabled())
			radio.reload();

		configService.save(config);
		send(PREFIX + "Removed location at " + StringUtils.getShortLocationString(radio.getRadiusLocation()) + " from " + radio.getId());
	}

	@Path("config removeStationLocation <radio>")
	@Permission(Group.ADMIN)
	@Description("Lists the locations of this station radio")
	void configListStationLocation(Radio radio) {
		if (radio.getType() != RadioType.STATION)
			error("This radio type is not a station radio");

		send("&3Locations: ");
		int index = 0;
		for (Location location : radio.getLocations()) {
			send(" &3" + index + " - " + StringUtils.getShortLocationString(location));
		}
	}

	@Path("config addSong <radio> <song>")
	@Permission(Group.ADMIN)
	@Description("Add a song to a radio")
	void configAddSong(Radio radio, @Arg(type = RadioSong.class) List<RadioSong> radioSongs) {
		for (RadioSong radioSong : radioSongs)
			config.addSong(radio, radioSong);

		configService.save(config);

		if (radio.isEnabled())
			radio.reload();

		send(PREFIX + "Added " + radioSongs.stream().map(RadioSong::getName).collect(Collectors.joining(", ")) + " to " + radio.getId());
	}

	@Path("config removeSong <radio> <song>")
	@Permission(Group.ADMIN)
	@Description("Remove a song from a radio")
	void configRemoveSong(Radio radio, @Arg(context = 1) RadioSong radioSong) {
		config.removeSong(radio, radioSong);

		configService.save(config);

		if (radio.isEnabled())
			radio.reload();

		send(PREFIX + "Removed " + radioSong.getName() + " from " + radio.getId());
	}

	@Path("config clearSongs <radio>")
	@Permission(Group.ADMIN)
	@Description("Remove all songs from a radio")
	void configRemoveSong(Radio radio) {
		config.clearSongs(radio);

		configService.save(config);

		if (radio.isEnabled())
			radio.reload();

		send(PREFIX + "Removed all songs from " + radio.getId());
	}

	@Path("config setId <radio> <id>")
	@Permission(Group.ADMIN)
	@Description("Set the id of the radio")
	void configSetType(Radio radio, String id) {
		String oldId = radio.getId();
		radio.setId(id);
		configService.save(config);

		String newId = radio.getId();
		userService.clearCache();

		List<RadioUser> radioUsers = userService.getAll();
		for (RadioUser user : radioUsers) {
			if (user.getServerRadioId() != null && user.getServerRadioId().equalsIgnoreCase(oldId))
				user.setServerRadioId(newId);

			if (user.getLastServerRadioId() != null && user.getLastServerRadioId().equalsIgnoreCase(oldId))
				user.setLastServerRadioId(newId);

			if (user.getLeftRadiusRadios() != null && user.getLeftRadiusRadios().contains(oldId)) {
				user.getLeftRadiusRadios().remove(oldId);
				user.getLeftRadiusRadios().add(newId);
			}

			userService.save(user);
		}

		send(PREFIX + "Id set to " + radio.getId());
	}

	@Path("config enable <radio>")
	@Permission(Group.ADMIN)
	@Description("Enable the radio")
	void configEnable(Radio radio) {
		RadioFeature.verify(radio);

		radio.setEnabled(true);
		configService.save(config);

		send(PREFIX + "Enabled " + radio.getId());
	}

	@Path("config disable <radio>")
	@Permission(Group.ADMIN)
	@Description("Disable the radio")
	void configDisable(Radio radio) {
		radio.setEnabled(false);
		configService.save(config);

		send(PREFIX + "Disabled " + radio.getId());
	}

	@Confirm
	@Path("config delete <id>")
	@Permission(Group.ADMIN)
	@Description("Delete the radio")
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

	private int getSongPercent(SongPlayer songPlayer) {
		double songLen = songPlayer.getSong().getLength();
		double current = songPlayer.getTick();
		return (int) ((current / songLen) * 100.0);
	}
}
