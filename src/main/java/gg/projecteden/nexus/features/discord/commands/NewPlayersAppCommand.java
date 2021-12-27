package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;
import gg.projecteden.discord.appcommands.annotations.RequiredRole;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.features.discord.appcommands.NexusAppCommand;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.utils.TimeUtils.TickTime;
import gg.projecteden.utils.TimeUtils.Timespan;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@RequiredRole("Staff")
@HandledBy(Bot.RELAY)
public class NewPlayersAppCommand extends NexusAppCommand {

	public NewPlayersAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command("List new players")
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
				.append(Timespan.of(players.get(player)).format())
				.append(System.lineSeparator()));

		reply(StringUtils.getDiscordPrefix("NewPlayers") + System.lineSeparator() + response);
	}

}
