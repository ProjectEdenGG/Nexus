package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class EmptyCommand extends CustomCommand {

	public EmptyCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void empty() {
		PlayerInventory playerInv = player().getInventory();
		ItemStack bucket = new ItemStack(Material.BUCKET);

		ItemStack heldItem = playerInv.getItem(playerInv.getHeldItemSlot());
		if (Utils.isNullOrAir(heldItem) || !isBucket(heldItem))
			error("You must be holding a bucket type");

		if (heldItem == bucket) error("Nothing to empty");

		playerInv.setItem(playerInv.getHeldItemSlot(), bucket);
	}

	@Path("[string]")
	void emptyType(String type) {
		PlayerInventory playerInv = player().getInventory();
		ItemStack bucket = new ItemStack(Material.BUCKET);

		ItemStack bucketType;
		switch (type) {
			case "lava":
				bucketType = new ItemStack(Material.LAVA_BUCKET);
				break;
			case "water":
				bucketType = new ItemStack(Material.WATER_BUCKET);
				break;
			case "milk":
				bucketType = new ItemStack(Material.MILK_BUCKET);
				break;
			default:
				error("/empty [lava|water|milk]");
				return;
		}

		if (playerInv.all(bucketType).size() != 0) {
			playerInv.all(bucketType).forEach((key, value) -> playerInv.setItem(key, bucket));
			send(PREFIX + "Emptied all " + type + " buckets");
		} else
			error("Nothing to empty");
	}

	boolean isBucket(ItemStack item) {
		return item.getType().toString().toLowerCase().contains("bucket");
	}
}
