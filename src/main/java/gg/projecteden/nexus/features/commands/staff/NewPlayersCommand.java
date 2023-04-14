package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Utils;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@Permission(Group.STAFF)
public class NewPlayersCommand extends CustomCommand {

	public NewPlayersCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("List new players")
	void run(@Optional("1") int page) {
		final Map<Player, Integer> players = new HashMap<>();
		final HoursService service = new HoursService();
		for (Player player : OnlinePlayers.getAll()) {
			Hours hours = service.get(player.getUniqueId());
			if (!hours.has(TickTime.HOUR))
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
						.next(" &e" + player.getName() + " &7- " + Timespan.ofSeconds(sorted.get(player)).format());

		paginate(sorted.keySet(), formatter, "/newplayers", page);
	}

}
