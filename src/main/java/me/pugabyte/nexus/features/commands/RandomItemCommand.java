package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RandomItemCommand extends CustomCommand {
	public RandomItemCommand(CommandEvent event) {
		super(event);
	}

	@Permission("group.admin")
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
