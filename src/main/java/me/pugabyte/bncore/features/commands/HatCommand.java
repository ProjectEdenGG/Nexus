package me.pugabyte.bncore.features.commands;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static me.pugabyte.bncore.utils.Utils.isNullOrAir;

@Permission("essentials.hat")
public class HatCommand extends CustomCommand {

	public HatCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		final PlayerInventory inv = player().getInventory();
		final ItemStack hat = inv.getHelmet();
		final ItemStack hand = getTool();
		final ItemStack air = new ItemStack(Material.AIR);

		if (!isNullOrAir(hat))
			if (hat.getEnchantments().containsKey(Enchantment.BINDING_CURSE))
				if (!player().hasPermission("group.staff"))
					error("You cannot remove your hat, as it has the Curse of Binding!");

		if (isNullOrAir(hand))
			if (isNullOrAir(hat))
				error("There is nothing on your head or in your hand");
			else {
				inv.setHelmet(air);
				inv.setItemInMainHand(hat);
				send(PREFIX + "Hat removed");
			}
		else {
			inv.setHelmet(hand);
			inv.setItemInMainHand(hat);
			send(PREFIX + "Hat updated");
		}
	}

	@Path("remove")
	void remove() {
		final PlayerInventory inv = player().getInventory();
		final ItemStack hat = inv.getHelmet();
		final ItemStack hand = getTool();
		final ItemStack air = new ItemStack(Material.AIR);

		if (isNullOrAir(hat))
			error("You do not have a hat");

		inv.setHelmet(air);
		if (isNullOrAir(hand))
			inv.setItemInMainHand(hat);
		else
			Utils.giveItem(player(), hat);
		send(PREFIX + "Hat removed");
	}

}
