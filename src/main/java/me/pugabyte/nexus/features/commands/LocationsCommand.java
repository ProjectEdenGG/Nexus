package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.StringUtils;
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
		Bukkit.getWorlds().forEach(world -> {
			List<Player> players = world.getPlayers();
			if (players.isEmpty()) return;
			player().sendMessage(Identity.nil(), Component.text("== " + StringUtils.getWorldDisplayName(world) + " ==", NamedTextColor.DARK_AQUA, TextDecoration.BOLD), MessageType.SYSTEM);
			players.forEach(target -> {
				TextComponent component = Component.text(Nickname.of(target)+"  ", TextColor.color(0x3bed8e))
						.append(Component.text(StringUtils.getShorterLocationString(target.getLocation()), NamedTextColor.YELLOW))
						.clickEvent(ClickEvent.runCommand("/tp " + target.getName()));
				player().sendMessage(target, component, MessageType.SYSTEM);
			});
		});
		send();
	}
}
