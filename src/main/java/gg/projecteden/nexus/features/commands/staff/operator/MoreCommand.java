package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

@Permission(Group.SENIOR_STAFF)
public class MoreCommand extends CustomCommand {

	public MoreCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[amount]")
	@Description("Fills a stack of items in your hand")
	void run(Integer amount) {
		final ItemStack tool = getToolRequired();

		if (amount == null)
			if (tool.getAmount() == tool.getMaxStackSize())
				tool.setAmount(64);
			else
				tool.setAmount(tool.getMaxStackSize());
		else
			tool.setAmount((tool.getAmount() == 1 ? 0 : tool.getAmount()) + amount);
	}

}
