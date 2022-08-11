package gg.projecteden.nexus.features.scoreboard;

import com.gmail.nossr50.events.scoreboard.McMMOScoreboardRevertEvent;
import gg.projecteden.nexus.features.menus.BookBuilder.WrittenBookMenu;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MinigamerQuitEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.scoreboard.ScoreboardService;
import gg.projecteden.nexus.models.scoreboard.ScoreboardUser;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.stream.Collectors;

@NoArgsConstructor
@Aliases({"status", "sidebar", "sb", "featherboard"})
public class ScoreboardCommand extends CustomCommand implements Listener {
	private final ScoreboardService service = new ScoreboardService();
	private ScoreboardUser user;

	static {
		OnlinePlayers.getAll().forEach(player -> {
			ScoreboardUser user = new ScoreboardService().get(player);
			if (user.isActive())
				user.on();
		});
	}

	public ScoreboardCommand(@NonNull CommandEvent event) {
		super(event);
		user = service.get(player());
	}

	@Description("Turn the scoreboard on or off")
	@Path("[on/off]")
	void toggle(Boolean enable) {
		user.setActive((enable != null ? enable : !user.isActive()));
		if (!user.isActive())
			user.off();
		else {
			if (user.getLines().isEmpty())
				user.setLines(ScoreboardLine.getDefaultLines(player()));
			user.on();
		}
		service.save(user);
	}

	@Description("Reset settings to default")
	@Path("reset")
	void reset() {
		user.setActive(true);
		user.setLines(ScoreboardLine.getDefaultLines(player()));
		user.on();
		service.save(user);
	}

	@Description("Control which lines you want to see")
	@Path("edit")
	void book() {
		WrittenBookMenu builder = new WrittenBookMenu();

		int index = 0;
		JsonBuilder json = new JsonBuilder();
		for (ScoreboardLine line : ScoreboardLine.values()) {
			if (!line.isOptional()) continue;
			if (!line.hasPermission(player()) && !user.getLines().containsKey(line)) continue;
			json.next((user.getLines().containsKey(line) && user.getLines().get(line)) ? "&a✔" : "&c✗")
					.command("/scoreboard edit toggle " + line.name().toLowerCase())
					.hover("&eClick to toggle")
					.next(" ").group();
			json.next("&3" + camelCase(line))
					.hover(line.render(player()));
			json.newline().group();

			if (++index % 14 == 0) {
				builder.addPage(json);
				json = new JsonBuilder();
			}
		}

		builder.addPage(json).open(player());
	}

	@HideFromHelp
	@TabCompleteIgnore
	@Path("edit toggle <type> [enable]")
	void toggle(ScoreboardLine line, Boolean enable) {
		if (enable == null)
			enable = !user.getLines().containsKey(line) || !user.getLines().get(line);
		user.getLines().put(line, enable);
		if (!enable)
			user.remove(line);
		user.startTasks();
		service.save(user);
		book();
	}

	@Permission(Group.STAFF)
	@Path("list")
	void list() {
		String collect = OnlinePlayers.getAll().stream()
				.map(player -> new ScoreboardService().get(player))
				.filter(ScoreboardUser::isActive)
				.map(Nickname::of)
				.collect(Collectors.joining("&3, &e"));
		send(PREFIX + "Active scoreboards: ");
		send("&e" + collect);
	}

	@Permission(Group.STAFF)
	@Path("view <player>")
	void view(OfflinePlayer player) {
		send(service.get(player).toString());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		ScoreboardService service = new ScoreboardService();
		ScoreboardUser user = service.get(event.getPlayer());
		if (user.isActive())
			user.on();
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		ScoreboardService service = new ScoreboardService();
		ScoreboardUser user = service.get(event.getPlayer());
		user.pause();
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		ScoreboardService service = new ScoreboardService();
		ScoreboardUser user = service.get(event.getPlayer());
		if (user.isActive())
			user.on();
	}

	@EventHandler
	public void onMatchQuit(MinigamerQuitEvent event) {
		ScoreboardService service = new ScoreboardService();
		ScoreboardUser user = service.get(event.getMinigamer().getPlayer());
		if (user.isActive() && user.isOnline())
			user.on();
	}

	@EventHandler
	public void onMatchEnd(MatchEndEvent event) {
		ScoreboardService service = new ScoreboardService();
		event.getMatch().getMinigamers().forEach(minigamer -> {
			ScoreboardUser user = service.get(minigamer.getOnlinePlayer());
			if (user.isActive() && user.isOnline())
				user.on();
		});
	}

	@EventHandler
	public void onMatchJoin(MatchJoinEvent event) {
		ScoreboardService service = new ScoreboardService();
		ScoreboardUser user = service.get(event.getMinigamer().getPlayer());
		user.pause();
	}

	@EventHandler
	public void onMcMMOScoreboardEnd(McMMOScoreboardRevertEvent event) {
		ScoreboardService service = new ScoreboardService();
		ScoreboardUser user = service.get(event.getTargetPlayer());
		if (user.isActive() && user.isOnline())
			user.on();
	}

}
