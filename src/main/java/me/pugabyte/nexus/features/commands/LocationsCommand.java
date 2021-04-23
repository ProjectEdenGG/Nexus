package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.hours.Hours;
import me.pugabyte.nexus.models.hours.HoursService;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.TimeUtils.Timespan.TimespanBuilder;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class LocationsCommand extends CustomCommand {
	public LocationsCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Permission("group.staff")
	void execute() {
		send();
		HoursService service = new HoursService();
		Bukkit.getWorlds().forEach(world -> {
			List<Player> players = world.getPlayers();
			if (players.isEmpty()) return;
			player().sendMessage(Identity.nil(), Component.text("== " + StringUtils.getWorldDisplayName(world) + " ==", NamedTextColor.DARK_AQUA, TextDecoration.BOLD), MessageType.SYSTEM);
			players.forEach(target -> {
				Hours hours = service.get(target);
				int playtimeSeconds = hours.getTotal();
				TextComponent component = Component.text(Nickname.of(target)+"  ", TextColor.color(0x3bed8e))
						.append(Component.text(StringUtils.getShorterLocationString(target.getLocation()), NamedTextColor.YELLOW))
						.append(Component.text("  " + TimespanBuilder.of(playtimeSeconds).noneDisplay(true).format(), (playtimeSeconds <= 3600 ? NamedTextColor.RED : NamedTextColor.GRAY)))
						.clickEvent(ClickEvent.runCommand("/tp " + target.getName()));
				player().sendMessage(target, component, MessageType.SYSTEM);
			});
		});
		send();
	}
}
