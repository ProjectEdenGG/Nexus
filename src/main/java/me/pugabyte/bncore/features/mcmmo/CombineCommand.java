package me.pugabyte.bncore.features.mcmmo;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.inventory.ItemStack;

@Permission("combine.use")
public class CombineCommand extends CustomCommand {

	public CombineCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		for (int slot = 0; slot < player().getInventory().getContents().length; slot++) {
			if (player().getInventory().getContents()[slot] == null) continue;
			if (!player().getInventory().getContents()[slot].getType().name().contains("POTION")) continue;
			ItemStack potion = player().getInventory().getContents()[slot].clone();
			int potionNumber = potion.getAmount();
			for (int i = 0; i < player().getInventory().getContents().length; i++) {
				if (player().getInventory().getContents()[i] == null) continue;
				if (i == slot) continue;
				potion.setAmount(1);
				ItemStack temp = player().getInventory().getContents()[i].clone();
				temp.setAmount(1);
				if (temp.equals(potion)) {
					potionNumber += player().getInventory().getContents()[i].getAmount();
					player().getInventory().setItem(i, null);
				}
			}
			potion.setAmount(potionNumber);
			player().getInventory().setItem(slot, potion);
		}
		send(PREFIX + "Combined all alike potions in your inventory!");
	}

}
