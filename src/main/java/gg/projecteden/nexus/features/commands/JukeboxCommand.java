package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.events.store.EventStoreItem;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.jukebox.JukeboxSong;
import gg.projecteden.nexus.models.jukebox.JukeboxUser;
import gg.projecteden.nexus.models.jukebox.JukeboxUserService;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.features.events.Events.STORE_PREFIX;
import static gg.projecteden.nexus.models.jukebox.JukeboxSong.SONGS;

@Aliases({"song", "songs"})
@Redirect(from = {"/sogn", "/sogns"}, to = "/songs")
public class JukeboxCommand extends CustomCommand {
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

	@Path("reload")
	void reload() {
		JukeboxSong.reload().thenRun(() -> send(PREFIX + "Loaded &e" + SONGS.size() + " &3songs"));
	}

	@Path("[page]")
	void songs(@Arg("1") int page) {
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

		paginate(songs, formatter, "/jukebox", page);
	}

	@Path("play <song...>")
	void play(JukeboxSong song) {
		if (!user.owns(song))
			error("You do not own that song");

		user.play(song);
		send(json(PREFIX + "Playing &e" + song.getName() + " ")
			.group().next(stopButton()));
	}

	@Path("store [page]")
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
	void store_preview(JukeboxSong song) {
		user.preview(song);
		send(json(STORE_PREFIX + "Playing &e" + song.getName() + " ")
			.group().next(stopButton())
			.group().next(" ")
			.group().next(buyButton(song)));
	}

	@Confirm
	@Path("store purchase <song...>")
	void store_buy(JukeboxSong song) {
		if (user.owns(song))
			error("You already own that song");

		new EventUserService().edit(user, eventUser -> eventUser.charge(EventStoreItem.SONGS.getPrice()));
		user.give(song);
		service.save(user);
		send(STORE_PREFIX + "Purchased song &e" + song.getName() + "&3. Play with &c/song " + song.getName());
	}

	@Path("stop")
	void stop() {
		if (user.cancel())
			send(STORE_PREFIX + "Song stopped");
		else
			error("No song is playing");
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
			return STORE_PREFIX;
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
			.filter(song -> song.toLowerCase().startsWith(filter.toLowerCase()))
			.collect(Collectors.toList());
	}

}
