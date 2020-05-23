package me.pugabyte.bncore.features.scoreboard;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchJoinEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchQuitEvent;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Description;
import me.pugabyte.bncore.framework.commands.models.annotations.HideFromHelp;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.scoreboard.ScoreboardService;
import me.pugabyte.bncore.models.scoreboard.ScoreboardUser;
import me.pugabyte.bncore.utils.BookBuilder;
import me.pugabyte.bncore.utils.JsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;

@NoArgsConstructor
public class ScoreboardCommand extends CustomCommand implements Listener {
	private final ScoreboardService service = new ScoreboardService();
	private ScoreboardUser user;

	static {
		Bukkit.getOnlinePlayers().forEach(player -> {
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
		BookBuilder builder = new BookBuilder();

		int index = 0;
		JsonBuilder json = new JsonBuilder();
		for (ScoreboardLine line : ScoreboardLine.values()) {
			if (!line.isOptional()) continue;
			if (!line.hasPermission(player())) continue;
			json.next((user.getLines().containsKey(line) && user.getLines().get(line)) ? "&a✔" : "&c✗")
					.command("/scoreboard edit toggle " + line.name().toLowerCase())
					.hover("&eClick to toggle")
					.next(" ").group();
			json.next("&3" + camelCase(line.name()))
					.hover(line.render(player()));
			json.newline();

			if (++index % 14 == 0) {
				builder.addPage(json);
				json = new JsonBuilder();
			}
		}

		builder.addPage(json).open(player());
	}

	@HideFromHelp
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
	public void onMatchQuit(MatchQuitEvent event) {
		ScoreboardService service = new ScoreboardService();
		ScoreboardUser user = service.get(event.getMinigamer().getPlayer());
		if (user.isActive())
			user.on();
	}

	@EventHandler
	public void onMatchJoin(MatchJoinEvent event) {
		ScoreboardService service = new ScoreboardService();
		ScoreboardUser user = service.get(event.getMinigamer().getPlayer());
		user.pause();
	}

}
