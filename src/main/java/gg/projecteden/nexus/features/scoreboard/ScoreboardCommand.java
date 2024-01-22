package gg.projecteden.nexus.features.scoreboard;

import com.gmail.nossr50.events.scoreboard.McMMOScoreboardMakeboardEvent;
import com.gmail.nossr50.events.scoreboard.McMMOScoreboardObjectiveEvent;
import com.gmail.nossr50.events.scoreboard.McMMOScoreboardRevertEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.BookBuilder.WrittenBookMenu;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchQuitEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.scoreboard.ScoreboardService;
import gg.projecteden.nexus.models.scoreboard.ScoreboardUser;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

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

	@Path("[on/off]")
	@Description("Turn the scoreboard on or off")
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

	@Path("reset")
	@Description("Reset settings to default")
	void reset() {
		user.setActive(true);
		user.setLines(ScoreboardLine.getDefaultLines(player()));
		user.on();
		service.save(user);
	}

	@Path("edit")
	@Description("Control which lines you want to see")
	void book() {
		WrittenBookMenu builder = new WrittenBookMenu();

		int index = 0;
		JsonBuilder json = new JsonBuilder();
		List<ScoreboardLine> lines = user.getOrder().stream().filter(line -> line.hasPermission(player())).toList();
		for (ScoreboardLine line : lines) {
			if (!line.hasPermission(player()) && !user.getLines().containsKey(line)) continue;
			if (line.isOptional())
				json.next((user.getLines().containsKey(line) && user.getLines().get(line)) ? "&a✔" : "&c✗")
					.command("/scoreboard edit toggle " + line.name().toLowerCase())
					.hover("&eClick to toggle")
					.next(" ").group();
			else
				json.next("&a✔").hover("&eRequired").next(" ").group();
			boolean isTop = lines.indexOf(line) == 0;
			boolean isBottom = lines.indexOf(line) == lines.size() - 1;
			json.next((isTop ? "&7" : "&3") + "▲");
			if (!isTop)
				json.command("/scoreboard edit moveUp " + line.name().toLowerCase())
					.hover("&eClick to move up");
			json.next(" ").group();
			json.next((isBottom ? "&7" : "&3") + "▼");
			if (!isBottom)
				json.command("/scoreboard edit moveDown " + line.name().toLowerCase())
					.hover("&eClick to move down");
			json.next(" ").group();
			json.next("&3" + camelCase(line));
			if (line.render(player()) != null)
				json.hover(line.render(player()));
			json.newline().group();

			if (++index % 14 == 0) {
				builder.addPage(json);
				json = new JsonBuilder();
			}

			if (isBottom) {
				json.newline().group();
				json.next("  &cReset:")
					.group()
					.newline()
					.group()
					.next("    ")
					.group()
					.next("&cOrder")
					.command("/scoreboard edit reset order")
					.hover("&cClick to reset order")
					.group()
					.next(" &3| ")
					.group()
					.next("&cVisibility")
					.command("/scoreboard edit reset visible")
					.hover("&cClick to reset visibility")
					.group();
			}
		}

		builder.addPage(json).open(player());
	}

	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	@Path("edit toggle <type> [enable]")
	void toggle(ScoreboardLine line, Boolean enable) {
		if (enable == null)
			enable = !user.getLines().containsKey(line) || !user.getLines().get(line);
		user.getLines().put(line, enable);
		service.save(user);
		book();
	}

	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	@Path("edit moveUp <type>")
	void moveUp(ScoreboardLine line) {
		user.setOrder(line, user.getOrder().indexOf(line) - 1);
		service.save(user);
		book();
	}

	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	@Path("edit moveDown <type>")
	void moveDown(ScoreboardLine line) {
		user.setOrder(line, user.getOrder().indexOf(line) + 1);
		service.save(user);
		book();
	}

	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	@Path("edit reset order")
	void resetOrder() {
		user.getOrder().clear();
		user.fixOrder();
		service.save(user);
		book();
	}

	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	@Path("edit reset visible")
	void resetVisible() {
		user.setLines(ScoreboardLine.getDefaultLines(player()));
		service.save(user);
		book();
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
	public void onMatchQuit(MatchQuitEvent event) {
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
	public void onMcMMOScoreboardObjective(McMMOScoreboardObjectiveEvent event) {
		ScoreboardService service = new ScoreboardService();
		ScoreboardUser user = service.get(event.getTargetPlayer());
		if (user.isActive() && user.isOnline())
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
