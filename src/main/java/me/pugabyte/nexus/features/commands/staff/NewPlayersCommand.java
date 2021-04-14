package me.pugabyte.nexus.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.hours.Hours;
import me.pugabyte.nexus.models.hours.HoursService;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import me.pugabyte.nexus.utils.TimeUtils.Timespan;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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
		for (Player player : Bukkit.getOnlinePlayers()) {
			Hours hours = new HoursService().get(player);
			if (hours.getTotal() < (Time.HOUR.get() / 20))
				players.put(player, hours.getTotal());
		}

		if (players.isEmpty())
			error("No new players found");

		HashMap<Player, Integer> sorted = Utils.sortByValue(players);

		send(PREFIX);
		BiFunction<Player, String, JsonBuilder> formatter = (player, index) ->
				json("&3" + index + "  ")
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

		paginate(new ArrayList<>(sorted.keySet()), formatter, "/newplayers", page);
	}

}
