package gg.projecteden.nexus.features.events;

import com.ruinscraft.powder.PowderPlugin;
import com.ruinscraft.powder.model.Powder;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import gg.projecteden.annotations.Async;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.store.EventStoreListener;
import gg.projecteden.nexus.features.events.store.models.EventStoreImage;
import gg.projecteden.nexus.features.events.store.providers.EventStoreProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.eventuser.EventUser;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.Env;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.features.events.store.models.EventStoreImage.IMAGES;

@Aliases("event")
public class EventsCommand extends CustomCommand {
	private final EventUserService service = new EventUserService();
	private EventUser user;

	public EventsCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	// Store

	static {
		new EventStoreListener();
	}

	@Path("store")
	void store() {
		if (Nexus.getEnv() == Env.PROD && !isStaff())
			error("Coming Soon™");

		new EventStoreProvider().open(player());
	}

	// Tokens

	private String plural(int tokens) {
		return tokens + plural(" token", tokens);
	}

	@Path("tokens [player]")
	void tokens(@Arg("self") EventUser user) {
		if (isSelf(user)) {
			send(PREFIX + "&3Current balance: &e" + plural(user.getTokens()));
			line();
			send("&3Event tokens are currency earned by participating in server events");
			send("&3You can spend them at the &c/event store &3in exchange for unique rewards");
		} else
			send(PREFIX + "&3" + user.getNickname() + "'s current balance: &e" + plural(user.getTokens()));
	}

	@Async
	@Path("tokens top [page]")
	void tokens_top(@Arg("1") int page) {
		send(PREFIX + "Top Token Earners");
		paginate(service.getTopTokens(), (user, index) -> json(index + " &e" + user.getNickname() + " &7- " + user.getTokens()), "/event tokens top", page);
	}

	/* TODO
	@Path("tokens daily [player]")
	void tokensDaily(@Arg("self") EventUser user) {
		if (isSelf(user))
			send(PREFIX + "&3Daily tokens:");
		else
			send(PREFIX + "&3" + user.getOfflinePlayer().getName() + "'s Daily tokens:");

		for (BFtokensource tokensource : BFtokensource.values()) {
			Map<LocalDate, Integer> dailyMap = user.getTokensReceivedToday().get(tokensource);
			int tokens = 0;
			if (dailyMap != null)
				tokens = dailyMap.getOrDefault(LocalDate.now(), 0);

			int dailyMax = EventUser.DAILY_SOURCE_MAX;
			String sourceColor = tokens == dailyMax ? "&a" : "&3";
			String sourceName = StringUtils.camelCase(tokensource.name());
			send(" " + sourceColor + sourceName + " &7- &e" + tokens + "&3/&e" + dailyMax);
		}
	}
	*/

	@Path("tokens pay <player> <tokens>")
	void tokens_pay(EventUser toUser, int tokens) {
		EventUser fromUser = service.get(player());
		if (isSelf(toUser))
			error("You cannot pay yourself");

		fromUser.takeTokens(tokens);
		toUser.giveTokens(tokens);

		fromUser.sendMessage(PREFIX + "&e" + tokens + " event tokens &3have been sent to &e" + toUser.getNickname());
		toUser.sendMessage(PREFIX + "&e" + tokens + " event tokens &3have been received from &e" + fromUser.getNickname());

		service.save(fromUser);
		service.save(toUser);
	}

	@Path("tokens give <player> <tokens>")
	@Permission("group.admin")
	void tokens_give(EventUser user, int tokens) {
		user.giveTokens(tokens);
		service.save(user);
		send(PREFIX + "&e" + plural(tokens) + " &3given to &e" + user.getNickname());
	}

	@Path("tokens take <player> <tokens>")
	@Permission("group.admin")
	void tokens_take(EventUser user, int tokens) {
		user.takeTokens(tokens);
		service.save(user);
		send(PREFIX + "&e" + plural(tokens) + " &3taken from &e" + user.getNickname());
	}

	@Path("tokens set <player> <tokens>")
	@Permission("group.admin")
	void tokens_set(EventUser user, int tokens) {
		user.setTokens(tokens);
		service.save(user);
		send(PREFIX + "&3Set &e" + user.getNickname() + "&3's balance to &e" + plural(tokens));
	}

	@Path("tokens reset <player>")
	@Permission("group.admin")
	void tokens_reset(EventUser user) {
		user.setTokens(0);
		user.getTokensReceivedByDate().clear();
		service.save(user);
	}

	// Images

	static {
		EventStoreImage.reload();
	}

	@Path("store images reload")
	@Permission("group.admin")
	void store_images_reload() {
		EventStoreImage.reload();
		send(PREFIX + "Loaded " + IMAGES.size() + " maps");
	}

	@Path("store images get <image...>")
	@Permission("group.admin")
	void store_images_get(EventStoreImage image) {
		PlayerUtils.giveItem(player(), image.getSplatterMap());
	}

	@Path("store songs")
	@Permission("group.admin")
	void store_songs() {
		final YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/Powder/powders.yml"));
		final PowderPlugin powder = (PowderPlugin) Bukkit.getServer().getPluginManager().getPlugin("Powder");
		final List<Powder> songs = powder.getPowderHandler().getPowdersFromCategory("Songs");

		final Powder song = songs.iterator().next();
		send(song.getName());
		final ConfigurationSection songConfig = config.getConfigurationSection(String.format("powders.%s.songs.song", song.getPath()));
		final String fileName = songConfig.getString("fileName");

		final Song parse = NBSDecoder.parse(new File("plugins/Powder/songs/" + fileName));
		final RadioSongPlayer radio = new RadioSongPlayer(parse);

		radio.addPlayer(player());
		radio.setStereo(true);
		radio.setPlaying(true);
		Tasks.wait(radio.getSong().getLength(), radio::destroy);
	}

	@ConverterFor(EventStoreImage.class)
	EventStoreImage convertToEventStoreImage(String value) {
		return EventStoreImage.of(value);
	}

	@TabCompleterFor(EventStoreImage.class)
	List<String> tabCompleteEventStoreImage(String filter) {
		return IMAGES.keySet().stream()
			.filter(id -> id.toLowerCase().startsWith(filter.toLowerCase()))
			.collect(Collectors.toList());
	}

}
