package me.pugabyte.bncore.features.mcmmo;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Permission("combine.use")
public class CombineCommand extends CustomCommand {

	public CombineCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		if (player().getInventory().getItemInMainHand().getType() != Material.SPLASH_POTION)
			error("You must be holding a splash potion to run this command");
		int slot = player().getInventory().getHeldItemSlot();
		ItemStack potion = player().getInventory().getItemInMainHand();
		int potionNumber = 1;
		for (int i = 0; i < player().getInventory().getContents().length; i++) {
			if (player().getInventory().getContents()[i] == null) continue;
			if (i == slot) continue;
			if (player().getInventory().getContents()[i].equals(potion)) {
				player().getInventory().setItem(i, null);
				potionNumber++;
			}
		}
		player().getInventory().getItemInMainHand().setAmount(potionNumber);
		send(PREFIX + "Successfully combined all alike potions in your inventory!");
	}

}
