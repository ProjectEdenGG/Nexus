package gg.projecteden.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import gg.projecteden.exceptions.EdenException;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.utils.DiscordId.TextChannel;
import gg.projecteden.utils.TimeUtils.TickTime;
import gg.projecteden.utils.TimeUtils.Timespan;
import org.bukkit.entity.Player;

import java.util.HashMap;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@HandledBy(Bot.RELAY)
public class NewPlayersDiscordCommand extends Command {

	public NewPlayersDiscordCommand() {
		this.name = "newplayers";
	}

	protected void execute(CommandEvent event) {
		if (!event.getChannel().getId().equals(TextChannel.STAFF_BRIDGE.getId()))
			return;

		Tasks.async(() -> {
			try {
				HashMap<Player, Integer> players = new HashMap<>() {{
					for (Player player : OnlinePlayers.getAll()) {
						Hours hours = new HoursService().get(player);
						if (hours.getTotal() < (TickTime.HOUR.get() / 20))
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

				event.reply(StringUtils.getDiscordPrefix("NewPlayers") + System.lineSeparator() + response.toString());
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof EdenException))
					ex.printStackTrace();
			}
		});
	}

}
