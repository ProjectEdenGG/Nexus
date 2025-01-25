package gg.projecteden.nexus.features.justice.misc;

import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.punishments.Punishment;
import gg.projecteden.nexus.models.punishments.PunishmentType;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.models.punishments.PunishmentsService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class HistoryCommand extends _JusticeCommand {
	private final PunishmentsService service = new PunishmentsService();

	public HistoryCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player> [page]")
	@Description("View a player's punishment history")
	void run(@Arg(value = "self", permission = Group.MODERATOR) Punishments player, @Arg("1") int page) {
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
			int indexInt = Integer.parseInt(StringUtils.stripColor(index));
			if (indexInt % perPage != 0 && indexInt != player.getPunishments().size())
				json.newline();
			return json;
		};

		List<Punishment> sorted = player.getPunishments().stream()
				.sorted(Comparator.comparing(Punishment::getTimestamp).reversed())
				.filter(punishment -> isStaff() || !isSelf(player) || punishment.getType() != PunishmentType.WATCHLIST)
				.collect(Collectors.toList());

		new Paginator<Punishment>()
			.values(sorted)
			.formatter(formatter)
			.command("/history " + player.getName())
			.page(page)
			.perPage(perPage)
			.send();
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
