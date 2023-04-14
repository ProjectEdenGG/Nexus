package gg.projecteden.nexus.features.mcmmo;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.WikiConfig;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.inventory.ItemStack;

@Permission("combine.use")
@WikiConfig(rank = "Guest", feature = "McMMO Prestige")
public class CombineCommand extends CustomCommand {

	public CombineCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Combine matching postions in your inventory in stacks")
	void run() {
		if (worldGroup() != WorldGroup.SURVIVAL)
			permissionError();

		for (int slot = 0; slot < inventory().getContents().length; slot++) {
			if (inventory().getContents()[slot] == null)
				continue;
			if (!inventory().getContents()[slot].getType().name().contains("POTION"))
				continue;

			ItemStack potion = inventory().getContents()[slot].clone();
			int potionNumber = potion.getAmount();
			for (int i = 0; i < inventory().getContents().length; i++) {
				if (inventory().getContents()[i] == null) continue;
				if (i == slot) continue;
				potion.setAmount(1);
				ItemStack temp = inventory().getContents()[i].clone();
				temp.setAmount(1);
				if (temp.equals(potion)) {
					potionNumber += inventory().getContents()[i].getAmount();
					inventory().setItem(i, null);
				}
			}
			potion.setAmount(potionNumber);
			inventory().setItem(slot, potion);
		}
		send(PREFIX + "Combined all alike potions in your inventory!");
	}

}
