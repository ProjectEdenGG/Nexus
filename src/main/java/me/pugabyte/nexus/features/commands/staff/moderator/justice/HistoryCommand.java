package me.pugabyte.nexus.features.commands.staff.moderator.justice;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.punishments.Punishment;
import me.pugabyte.nexus.models.punishments.Punishments;
import me.pugabyte.nexus.utils.JsonBuilder;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

import static java.util.stream.Collectors.toList;

public class HistoryCommand extends CustomCommand {

	public HistoryCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = Punishments.PREFIX;
		DISCORD_PREFIX = Punishments.DISCORD_PREFIX;
	}

	@Path("<player> [page]")
	void run(Punishments player, @Arg("1") int page) {
		if (player.getPunishments().isEmpty())
			error("No history found");

		send("");
		send(PREFIX + "History of &e" + player.getNickname());

		int perPage = 3;

		BiFunction<Punishment, String, JsonBuilder> formatter = (punishment, index) -> {
			JsonBuilder json = punishment.getType().getHistoryDisplay(punishment);
			int indexInt = Integer.parseInt(index);
			if (indexInt % perPage != 0 && indexInt != player.getPunishments().size())
				json.newline();
			return json;
		};


		List<Punishment> sorted = player.getPunishments().stream()
				.sorted(Comparator.comparing(Punishment::getTimestamp).reversed())
				.collect(toList());

		paginate(sorted, formatter, "/history " + player.getName(), page, perPage);
	}

}
