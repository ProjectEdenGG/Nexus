package gg.projecteden.nexus.features.justice.misc;

import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.punishments.NameBanConfig;
import gg.projecteden.nexus.models.punishments.NameBanConfigService;
import gg.projecteden.nexus.utils.Name;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import static gg.projecteden.nexus.utils.PlayerUtils.getPlayer;

// TODO All messaging

@NoArgsConstructor
@Permission(Group.MODERATOR)
public class NameBanCommand extends _JusticeCommand implements Listener {
	private final NameBanConfigService service = new NameBanConfigService();
	private final NameBanConfig config = service.get0();

	public NameBanCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void ban(OfflinePlayer player) {
		config.ban(uuid(), player.getUniqueId(), player.getName());
		service.save(config);

		send(PREFIX + "Name banned &e" + player.getName() + "&3, they will be able to join if they change their name");
	}

	@Path("unban <player>")
	void unban(@Arg(tabCompleter = OfflinePlayer.class) String player) {
		config.unban(player);
		service.save(config);

		send(PREFIX + "Removed name ban on &e" + player);
	}

	@Path("addWord <word>")
	void addWord(String word) {
		config.banWord(word);
		service.save(config);

		send(PREFIX + "Name banned the word &e" + word + "&3, future player names containing this word will be prevented");
	}

	@Path("removeWord <word>")
	void removeWord(String word) {
		config.unbanWord(word);
		service.save(config);

		send(PREFIX + "Removed name ban on the word &e" + word);
	}

	@Path("list")
	void list() {
		help();
	}

	@Path("list names [page]")
	void listNames(@Arg("1") int page) {
		config.getBannedNames().forEach((uuid, names) -> send(getPlayer(uuid).getName() + ": " + String.join(", ", names)));
	}

	@Path("list words [page]")
	void listWords(@Arg("1") int page) {
		config.getBannedWords().forEach(this::send);
	}

	@EventHandler
	public void onLogin(AsyncPlayerPreLoginEvent event) {
		final NameBanConfigService service = new NameBanConfigService();
		final NameBanConfig config = service.get0();

		if (config.nameIsBanned(event.getName())) {
			// TODO Improve
			if (!config.playerIsBanned(event.getUniqueId(), event.getName()))
				config.ban(event.getUniqueId(), event.getName());

			event.disallow(Result.KICK_BANNED, NameBanConfig.getBanMessage(Name.put(event.getUniqueId(), event.getName())));
		}

	}

}
