package gg.projecteden.nexus.features.mcmmo;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.inventory.ItemStack;

@Permission("combine.use")
public class CombineCommand extends CustomCommand {

	public CombineCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
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
