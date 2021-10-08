package gg.projecteden.nexus.features.events.y2020.bearfair20.quests.arcademachine;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.inventory.ItemStack;

@Permission("group.admin")
public class BFArcadeCommand extends CustomCommand {

	public BFArcadeCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void open() {
		new ArcadeMachineMenu().open(player(), (ItemStack[]) null);
	}

}
