package gg.projecteden.nexus.features.chat.games;

import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent;
import gg.projecteden.nexus.features.chat.events.PublicChatEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.afk.events.NotAFKEvent;
import gg.projecteden.nexus.models.chatgames.ChatGamesConfig;
import gg.projecteden.nexus.models.chatgames.ChatGamesConfig.ChatGame;
import gg.projecteden.nexus.models.chatgames.ChatGamesConfigService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@NoArgsConstructor
public class ChatGamesCommand extends CustomCommand implements Listener {
	public static final String PREFIX = StringUtils.getPrefix("ChatGames");
	private static final ChatGamesConfigService service = new ChatGamesConfigService();
	private static final ChatGamesConfig config = service.get0();

	static {
		if (config.hasQueuedGames())
			ChatGamesConfig.processQueue();
	}

	public ChatGamesCommand(CommandEvent event) {
		super(event);
	}

	@Path("queue add [amount]")
	@Permission("group.staff")
	void add(@Arg("1") int amount) {
		ChatGamesConfig.queue(amount, player());
	}

	@Path("queue count")
	void count() {
		send(PREFIX + "There are " + config.getQueuedGames() + " queued games");
	}

	@Confirm
	@Path("queue clear")
	@Permission("group.admin")
	void clear() {
		config.setQueuedGames(0);
		ChatGamesConfig.getCurrentGame().cancel();
		send(PREFIX + "Cleared queue");
	}

	@EventHandler
	public void onChat(PublicChatEvent event) {
		if (event.getChatter().getPlayer() == null)
			return;

		final ChatGame game = ChatGamesConfig.getCurrentGame();
		if (game == null || game.getAnswer() == null)
			return;

		if (event.getOriginalMessage().equalsIgnoreCase(game.getAnswer())) {
			game.onAnswer(Nerd.of(event.getChatter()));
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void on(NotAFKEvent event) {
		ChatGamesConfig.processQueue();
	}

	@EventHandler
	public void on(PlayerJoinEvent event) {
		ChatGamesConfig.processQueue();
	}

	@EventHandler
	public void on(PlayerVanishStateChangeEvent event) {
		ChatGamesConfig.processQueue();
	}

}
