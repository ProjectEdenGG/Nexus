package me.pugabyte.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import eden.exceptions.EdenException;
import eden.utils.TimeUtils.Time;
import eden.utils.TimeUtils.Timespan;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.DiscordId.TextChannel;
import me.pugabyte.nexus.features.discord.HandledBy;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.hours.Hours;
import me.pugabyte.nexus.models.hours.HoursService;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.entity.Player;

import java.util.HashMap;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

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
					for (Player player : PlayerUtils.getOnlinePlayers()) {
						Hours hours = new HoursService().get(player);
						if (hours.getTotal() < (Time.HOUR.get() / 20))
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
