package gg.projecteden.nexus.features.scoreboard;

import com.gmail.nossr50.events.scoreboard.McMMOScoreboardObjectiveEvent;
import com.gmail.nossr50.events.scoreboard.McMMOScoreboardRevertEvent;
import gg.projecteden.nexus.features.dialog.DialogCommand.DialogBuilder;
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
import io.papermc.paper.registry.data.dialog.DialogBase.DialogAfterAction;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

@NoArgsConstructor
@Aliases({"status", "scoreboard", "sb", "featherboard"})
public class SidebarCommand extends CustomCommand implements Listener {
	private final ScoreboardService service = new ScoreboardService();
	private ScoreboardUser user;

	static {
		OnlinePlayers.getAll().forEach(player -> {
			ScoreboardUser user = new ScoreboardService().get(player);
			if (user.isActive())
				user.on();
		});
	}

	public SidebarCommand(@NonNull CommandEvent event) {
		super(event);
		user = service.get(player());
	}

	@Path("[on/off]")
	@Description("Turn the sidebar on or off")
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
	@SuppressWarnings("UnstableApiUsage")
	@Description("Edit the order and visibility of lines on the sidebar")
	void edit() {
		var builder = new DialogBuilder()
			.title(new JsonBuilder("Sidebar Configuration").bold())
			.closeWithEscape(true)
			.after(DialogAfterAction.NONE)
			.bodyText("Edit the order and visibility of lines on the sidebar")
			.multiAction();

		List<ScoreboardLine> lines = user.getOrder().stream()
			.filter(line -> line.hasPermission(player()))
			.filter(line -> user.getLines().containsKey(line))
			.toList();

		for (ScoreboardLine line : lines) {
			boolean enabled = user.getLines().containsKey(line) && user.getLines().get(line);
			boolean isFirst = lines.indexOf(line) == 0;
			boolean isLast = lines.indexOf(line) == lines.size() - 1;

			builder.button(isFirst ? "" : "▲", 20, action -> {
				if (!isFirst)
					moveUp(line);
				edit();
			});

			builder.button(isLast ? "" : "▼", 20, action -> {
				if (!isLast)
					moveDown(line);
				edit();
			});

			String tooltip = line.render(player());
			builder.button((enabled ? "&a" : "&c") + line.camelCase(), tooltip, 150, action -> {
				toggle(line, null);
				edit();
			});
		}

		/*
		builder.button("Reset Order", 95, action -> {
			resetOrder();
			edit();
		});

		builder.button("Reset Visibility", 95, action -> {
			resetVisible();
			edit();
		});
		*/

		builder.exitButton("Close").columns(3).show(player());
	}

	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	@Path("edit toggle <type> [enable]")
	void toggle(ScoreboardLine line, Boolean enable) {
		if (enable == null)
			enable = !user.getLines().containsKey(line) || !user.getLines().get(line);
		user.getLines().put(line, line.isOptional() ? enable : true);
		service.save(user);
		edit();
	}

	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	@Path("edit moveUp <type>")
	void moveUp(ScoreboardLine line) {
		user.setOrder(line, user.getOrder().indexOf(line) - 1);
		service.save(user);
		edit();
	}

	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	@Path("edit moveDown <type>")
	void moveDown(ScoreboardLine line) {
		user.setOrder(line, user.getOrder().indexOf(line) + 1);
		service.save(user);
		edit();
	}

	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	@Path("edit reset order")
	void resetOrder() {
		user.getOrder().clear();
		user.fixOrder();
		service.save(user);
		edit();
	}

	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	@Path("edit reset visible")
	void resetVisible() {
		user.setLines(ScoreboardLine.getDefaultLines(player()));
		service.save(user);
		edit();
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

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
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
