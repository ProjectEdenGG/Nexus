package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GiveCommand extends CustomCommand {

	public GiveCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player> <type> [amount] [nbt...]")
	void run(Player player, Material material, @Arg(min = 1, max = 2304, minMaxBypass = "group.staff") Integer amount, @Arg(permission = "group.staff") String nbt) {
		if (!player().hasPermission("essentials.give"))
			if (!player().hasPermission("essentials.item"))
				permissionError();
			else if (!isSelf(player))
				error("You cannot give items to other players, only yourself");

		PlayerUtils.giveItem(player, material, amount == null ? material.getMaxStackSize() : amount, nbt);
	}

}
