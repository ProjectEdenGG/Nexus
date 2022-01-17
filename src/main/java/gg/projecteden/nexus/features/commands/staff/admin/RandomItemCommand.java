package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

@Permission(Group.ADMIN)
public class RandomItemCommand extends CustomCommand {
	public RandomItemCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void give(@Arg("self") Player player) {
		Material item = RandomUtils.randomElement(Arrays.stream(Material.values()).filter(Material::isItem).collect(Collectors.toList()));
		PlayerUtils.giveItem(player, item);
		String output = PREFIX + "Gave " + camelCase(item);
		if (!isSelf(player))
			output += " to &e" + Nickname.of(player);
		send(output);
	}
}
