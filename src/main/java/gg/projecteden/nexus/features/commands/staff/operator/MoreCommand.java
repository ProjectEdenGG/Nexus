package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

@Permission("group.seniorstaff")
public class MoreCommand extends CustomCommand {

	public MoreCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[amount]")
	void run(Integer amount) {
		final ItemStack tool = getToolRequired();

		if (amount == null)
			tool.setAmount(tool.getMaxStackSize());
		else
			tool.setAmount((tool.getAmount() == 1 ? 0 : tool.getAmount()) + amount);
	}

}
