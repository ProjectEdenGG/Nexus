package me.pugabyte.nexus.features.commands;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.Material;

@Aliases("i")
@Permission("essentials.item")
public class ItemCommand extends CustomCommand {

	public ItemCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<type> [amount] [nbt...]")
	void run(Material material, @Arg(value = "64", min = 1, max = 2304, minMaxBypass = "group.staff") int amount, @Arg(permission = "group.staff") String nbt) {
		PlayerUtils.giveItem(player(), material, amount, nbt);
	}

	@Path("rp <material> <id>")
	@Permission("group.staff")
	void rp(Material material, int id) {
		PlayerUtils.giveItem(player(), new ItemBuilder(material).customModelData(id).build());
	}

}
