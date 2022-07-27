package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.api.discord.appcommands.annotations.Command;
import gg.projecteden.api.discord.appcommands.annotations.RequiredRole;
import gg.projecteden.nexus.features.discord.appcommands.NexusAppCommand;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@RequiredRole("Staff")
@Command("List new players")
public class NewPlayersAppCommand extends NexusAppCommand {

	public NewPlayersAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command(value = "List new players", literals = false)
	void run() {
		final HoursService service = new HoursService();
		Map<Player, Integer> players = new HashMap<>() {{
			for (Player player : OnlinePlayers.getAll()) {
				Hours hours = service.get(player);
				if (!hours.has(TickTime.HOUR))
					put(player, hours.getTotal());
			}
		}};

		if (players.isEmpty())
			throw new InvalidInputException("No new players found");

		StringBuilder response = new StringBuilder();
		Utils.sortByValue(players).forEach((player, hours) ->
			response
				.append(Nickname.of(player))
				.append(" - ")
				.append(Timespan.ofSeconds(players.get(player)).format())
				.append(System.lineSeparator()));

		reply(StringUtils.getDiscordPrefix("NewPlayers") + System.lineSeparator() + response);
	}

}
