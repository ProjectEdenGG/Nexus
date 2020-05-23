package me.pugabyte.bncore.features.scoreboard;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Description;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.scoreboard.ScoreboardService;
import me.pugabyte.bncore.models.scoreboard.ScoreboardUser;
import me.pugabyte.bncore.utils.BookBuilder;
import me.pugabyte.bncore.utils.JsonBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;

@Permission("group.staff")
public class ScoreboardCommand extends CustomCommand implements Listener {
	private final ScoreboardService service = new ScoreboardService();
	private final ScoreboardUser user;

	public ScoreboardCommand(@NonNull CommandEvent event) {
		super(event);
		user = service.get(player());
	}

	@Path
	void create() {
		if (user.getLines().isEmpty())
			user.setLines(ScoreboardLine.getDefaultLines(player()));
		user.start();
	}

	@Description("Turn the scoreboard on or off")
	@Path("[boolean]")
	void toggle(Boolean enable) {
		if (enable == null) enable = !user.isActive();
		user.setActive(enable);
		if (!user.isActive())
			user.getScoreboard().delete();
		service.save(user);
	}

	@Description("Reset settings to default")
	@Path("reset")
	void reset() {
		user.setActive(true);
		user.setLines(ScoreboardLine.getDefaultLines(player()));
		service.save(user);
		user.render();
	}

	@Description("Control which lines you want to see")
	@Path("edit")
	void book() {
		BookBuilder builder = new BookBuilder();

		int index = 0;
		JsonBuilder json = new JsonBuilder();
		for (ScoreboardLine line : ScoreboardLine.values()) {
			if (!line.hasPermission(player())) continue;
			json.next((user.getLines().containsKey(line) && user.getLines().get(line)) ? "&a✔" : "&c✗")
					.command("/scoreboard edit toggle " + line.name())
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

	@Path("renderTest")
	void renderTest() {
		send(ScoreboardLine.COMPASS.render(player()));
	}

	@Path("edit toggle <type> [enable]")
	void toggle(ScoreboardLine line, Boolean enable) {
		if (enable == null) enable = !user.getLines().get(line);
		user.getLines().put(line, enable);
		user.render();
		book();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		ScoreboardService service = new ScoreboardService();
		ScoreboardUser user = service.get(event.getPlayer());
		if (user.isActive())
			user.start();
	}

}
