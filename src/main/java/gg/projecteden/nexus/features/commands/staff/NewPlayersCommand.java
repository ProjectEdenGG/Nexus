package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.utils.TimeUtils.TickTime;
import gg.projecteden.utils.TimeUtils.Timespan;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.function.BiFunction;

@Permission("group.staff")
public class NewPlayersCommand extends CustomCommand {

	public NewPlayersCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[page]")
	void run(@Arg("1") int page) {
		HashMap<Player, Integer> players = new HashMap<>();
		for (Player player : OnlinePlayers.getAll()) {
			Hours hours = new HoursService().get(player.getUniqueId());
			if (hours.getTotal() < (TickTime.HOUR.get() / 20))
				players.put(player, hours.getTotal());
		}

		if (players.isEmpty())
			error("No new players found");

		HashMap<Player, Integer> sorted = Utils.sortByValue(players);

		send(PREFIX);
		BiFunction<Player, String, JsonBuilder> formatter = (player, index) ->
				json(index + "  ")
						.group()
						.next("&6&l[TP]")
						.command("/mcmd vanish on ;; tp " + player.getName())
						.hover("This will automatically vanish you")
						.group()
						.next("  ")
						.group()
						.next("&c&l[WHOTF]")
						.command("/whotf " + player.getName())
						.group()
						.next(" &e" + player.getName() + " &7- " + Timespan.of(sorted.get(player)).format());

		paginate(sorted.keySet(), formatter, "/newplayers", page);
	}

}
