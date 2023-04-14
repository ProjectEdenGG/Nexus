package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

@Permission(Group.SENIOR_STAFF)
public class MoreCommand extends CustomCommand {

	public MoreCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Fills the item stack in hand to specified amount, or to maximum size if none is specified")
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
