package me.pugabyte.bncore.features.scoreboard;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
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

	@Path("render")
	void render() {
		user.render();
	}

	@Path("delete")
	void delete() {
		service.delete(user);
	}

	// Need to re-think the ordering approach
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
//			json.next("&7⬆").command("/scoreboard edit up " + line.name() + " " + index)
//					.hover("&eClick to move up")
//					.next(" ").group();
//			json.next("&7⬇").command("/scoreboard edit down " + line.name() + " " + index)
//					.hover("&eClick to move down")
//					.next(" ").group();
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

	@Path("edit toggle <type>")
	void toggle(ScoreboardLine line) {
		if (user.getLines().containsKey(line))
			user.getLines().replace(line, !user.getLines().get(line));
		else
			user.getLines().put(line, true);
		book();
	}

	@Path("edit up <line> <index>")
	void up(ScoreboardLine line, int index) {
		boolean active = user.getLines().containsKey(line) && user.getLines().get(line);
		user.getLines().remove(line);
		user.getLines().put(index - 1, line, active);
		book();
	}

	@Path("edit down <line> <index>")
	void down(ScoreboardLine line, int index) {
		boolean active = user.getLines().containsKey(line) && user.getLines().get(line);
		user.getLines().remove(line);
		user.getLines().put(index + 1, line, active);
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
