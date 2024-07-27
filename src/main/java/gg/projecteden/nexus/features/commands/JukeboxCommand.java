package gg.projecteden.nexus.features.commands;

import com.xxmicloxx.NoteBlockAPI.event.SongEndEvent;
import gg.projecteden.nexus.features.events.store.EventStoreItem;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.jukebox.JukeboxSong;
import gg.projecteden.nexus.models.jukebox.JukeboxUser;
import gg.projecteden.nexus.models.jukebox.JukeboxUserService;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.features.events.EdenEvent.PREFIX_STORE;
import static gg.projecteden.nexus.models.jukebox.JukeboxSong.SONGS;

@NoArgsConstructor
@Aliases({"song", "songs"})
@Redirect(from = {"/sogn", "/sogns"}, to = "/songs")
public class JukeboxCommand extends CustomCommand implements Listener {
	private final JukeboxUserService service = new JukeboxUserService();
	private JukeboxUser user;

	public JukeboxCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	static {
		JukeboxSong.reload().thenRun(() -> {
			for (JukeboxUser user : new JukeboxUserService().getOnline()) {
				if (user.getCurrentSong() == null)
					return;

				user.play(JukeboxSong.of(user.getCurrentSong()), user.getCurrentTick());
			}
		});
	}

	@Override
	public void _shutdown() {
		final JukeboxUserService service = new JukeboxUserService();
		for (JukeboxUser user : service.getOnline()) {
			user.pause();
			service.saveSync(user);
		}
	}

	@HideFromWiki
	@Path("reload")
	@Permission(Group.ADMIN)
	void reload() {
		JukeboxSong.reload().thenRun(() -> send(PREFIX + "Loaded &e" + SONGS.size() + " &3songs"));
	}

	@Path("list [page]")
	@Description("View your owned songs")
	void list(@Arg("1") int page) {
		if (SONGS.isEmpty())
			error("No songs loaded");

		final List<JukeboxSong> songs = SONGS.stream()
			.filter(song -> user.owns(song))
			.toList();

		if (songs.isEmpty())
			error("You do not own any songs. Purchase some with Event Tokens at /jukebox store");

		send(PREFIX);

		final BiFunction<JukeboxSong, String, JsonBuilder> formatter = (song, index) ->
			json("&3" + index + " ")
				.group().next(playButton(song))
				.group().next(" &e" + song.getName());

		paginate(songs, formatter, "/jukebox list", page);
	}

	@Path("play <song...>")
	@Description("Play a song")
	void play(JukeboxSong song) {
		if (!user.owns(song))
			error("You do not own that song");

		user.play(song);
		send(json(PREFIX + "Playing &e" + song.getName() + " ")
			.group().next(stopButton()));
	}

	@Path("store [page]")
	@Description("View the jukebox store")
	void store(@Arg("1") int page) {
		if (SONGS.isEmpty())
			error("No songs loaded");

		final List<JukeboxSong> songs = SONGS.stream()
			.filter(song -> !user.owns(song))
			.toList();

		if (songs.isEmpty())
			error("No songs available for purchase");

		send(PREFIX + "Store");

		final BiFunction<JukeboxSong, String, JsonBuilder> formatter = (song, index) ->
			json("&3" + index + " ")
				.group().next(previewButton(song))
				.group().next(" ")
				.group().next(buyButton(song))
				.group().next(" &e" + song.getName());

		paginate(songs, formatter, "/jukebox store", page);
	}

	@Path("store preview <song...>")
	@Description("Preview a song")
	void store_preview(JukeboxSong song) {
		user.preview(song);
		send(json(PREFIX_STORE + "Playing &e" + song.getName() + " ")
			.group().next(stopButton())
			.group().next(" ")
			.group().next(buyButton(song)));
	}

	@Confirm
	@Path("store purchase <song...>")
	@Description("Purchase a song")
	void store_buy(JukeboxSong song) {
		if (user.owns(song))
			error("You already own that song");

		new EventUserService().edit(user, eventUser -> eventUser.charge(EventStoreItem.SONGS.getPrice()));
		user.give(song);
		service.save(user);
		send(PREFIX_STORE + "Purchased song &e" + song.getName() + "&3. Play with &c/song play " + song.getName());
	}

	@Path("stop")
	@Description("Stop playing a song")
	void stop() {
		if (user.cancel())
			send(PREFIX_STORE + "Song stopped");
		else
			error("No song is playing");
	}

	@Permission(Group.ADMIN)
	@Path("store give <player> <song...>")
	@Description("Give a player a song")
	void store_give(JukeboxUser user, JukeboxSong song) {
		if (user.owns(song))
			error("&e" + user.getNickname() + " &calready owns &e" + song.getName());

		user.give(song);
		service.save(user);
		send(PREFIX + "Gave &e" + song.getName() + " &3to &e" + user.getNickname());
	}

	@NotNull
	private JsonBuilder playButton(JukeboxSong song) {
		return json()
			.next("&a[Play]")
			.hover("&eClick to play")
			.command("/jukebox play " + song.getName());
	}

	@NotNull
	private JsonBuilder previewButton(JukeboxSong song) {
		return json()
			.next("&a[Preview]")
			.hover("&eClick to preview")
			.command("/jukebox store preview " + song.getName());
	}

	@NotNull
	private JsonBuilder stopButton() {
		return json()
			.next("&c[Stop]")
			.hover("&eClick to stop")
			.command("/jukebox stop");
	}

	@NotNull
	private JsonBuilder buyButton(JukeboxSong song) {
		return json()
			.next("&6[Purchase]")
			.hover("&eClick to purchase")
			.command("/jukebox store purchase " + song.getName());
	}

	@Override
	public String getPrefix() {
		if ("store".equalsIgnoreCase(arg(1)))
			return PREFIX_STORE;
		return super.getPrefix();
	}

	@ConverterFor(JukeboxSong.class)
	JukeboxSong convertToJukeboxSong(String value) {
		final JukeboxSong song = JukeboxSong.of(value);
		if (song == null)
			throw new InvalidInputException("Song &e" + value + " &cnot found");
		return song;
	}

	@TabCompleterFor(JukeboxSong.class)
	List<String> tabCompleteJukeboxSong(String filter) {
		return SONGS.stream()
				.map(JukeboxSong::getName)
				.filter(song -> song.toLowerCase().contains(filter.toLowerCase()))
			.collect(Collectors.toList());
	}

	@EventHandler
	public void onSongEnd(SongEndEvent event) {
		for (JukeboxUser user : new JukeboxUserService().getOnline())
			if (event.getSongPlayer().equals(user.getSongPlayer()))
				user.cancel();
	}

}
