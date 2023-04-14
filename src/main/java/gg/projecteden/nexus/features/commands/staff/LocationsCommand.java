package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.api.common.utils.TimeUtils.Timespan.TimespanBuilder;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

import static gg.projecteden.nexus.utils.StringUtils.getCoordinateString;

@Permission(Group.STAFF)
public class LocationsCommand extends CustomCommand {
	public LocationsCommand(CommandEvent event) {
		super(event);
	}

	@Override
	@NoLiterals
	@Description("View player locations by world")
	public void help() {
		HoursService service = new HoursService();

		line();
		Bukkit.getWorlds().forEach(world -> {
			List<Player> players = world.getPlayers();
			if (players.isEmpty())
				return;

			send("&6&l " + StringUtils.getWorldDisplayName(world));
			players.forEach(target -> {
				int playtimeSeconds = service.get(target.getUniqueId()).getTotal();
				NamedTextColor playtimeColor = playtimeSeconds <= 3600 ? NamedTextColor.RED : NamedTextColor.GRAY;

				send(json()
						.next("&f  " + Nerd.of(target).getColoredName() + "  ")
						.copy(target.getUniqueId().toString())
						.hover("&fClick to copy UUID")
						.group()
						.next(Component.text(getCoordinateString(target.getLocation()), NamedTextColor.YELLOW))
						.next(Component.text("  " + TimespanBuilder.ofSeconds(playtimeSeconds).noneDisplay(true).format(), playtimeColor))
						.command("/tp " + target.getName()));
			});
		});
		line();
	}
}
