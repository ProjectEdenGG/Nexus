package gg.projecteden.nexus.features.justice.misc;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.Confirm;
import gg.projecteden.nexus.framework.commandsv2.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.punishments.Punishment;
import gg.projecteden.nexus.models.punishments.PunishmentType;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.models.punishments.PunishmentsService;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.NonNull;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;
import static java.util.stream.Collectors.toList;

public class HistoryCommand extends _JusticeCommand {
	private final PunishmentsService service = new PunishmentsService();

	public HistoryCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("<player> [page]")
	@Description("View a player's punishment history")
	void run(@Optional("self") @Permission(Group.MODERATOR) Punishments player, @Optional("1") int page) {
		if (player.getPunishments().isEmpty())
			if (isSelf(player))
				error("You do not have any logged punishments");
			else
				error("No history found for " + player.getNickname());

		send("");
		send(PREFIX + "History" + (isSelf(player) ? "" : " of &e" + player.getNickname()));

		int perPage = 3;

		BiFunction<Punishment, String, JsonBuilder> formatter = (punishment, index) -> {
			JsonBuilder json = punishment.getType().getHistoryDisplay(punishment);
			int indexInt = Integer.parseInt(stripColor(index));
			if (indexInt % perPage != 0 && indexInt != player.getPunishments().size())
				json.newline();
			return json;
		};

		List<Punishment> sorted = player.getPunishments().stream()
				.sorted(Comparator.comparing(Punishment::getTimestamp).reversed())
				.filter(punishment -> isStaff() || !isSelf(player) || punishment.getType() != PunishmentType.WATCHLIST)
				.collect(toList());

		paginate(sorted, formatter, "/history " + player.getName(), page, perPage);
	}

	@Confirm
	@TabCompleteIgnore
	@Path("delete <player> <id>")
	@Permission(Group.MODERATOR)
	@Description("Delete an entry from a player's punishment history")
	void delete(Punishments player, @Arg(context = 1) Punishment punishment) {
		punishment.setRemover(uuid());
		player.remove(punishment);
		service.save(player);
		send(PREFIX + "Punishment deleted");
	}

	@ConverterFor(Punishment.class)
	Punishment convertToPunishment(String value, Punishments context) {
		return context.getPunishment(UUID.fromString(value));
	}

}
