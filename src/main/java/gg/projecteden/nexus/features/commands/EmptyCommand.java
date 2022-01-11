package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@Description("Empty buckets in your inventory")
public class EmptyCommand extends CustomCommand {

	public EmptyCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void empty() {
		PlayerInventory playerInv = inventory();
		ItemStack bucket = new ItemStack(Material.BUCKET);

		ItemStack heldItem = playerInv.getItem(playerInv.getHeldItemSlot());
		if (isNullOrAir(heldItem) || !isBucket(heldItem))
			error("You must be holding a bucket type");

		if (heldItem == bucket) error("Nothing to empty");

		playerInv.setItem(playerInv.getHeldItemSlot(), bucket);
	}

	@Path("[string]")
	void emptyType(String type) {
		PlayerInventory playerInv = inventory();
		ItemStack bucket = new ItemStack(Material.BUCKET);

		ItemStack bucketType;
		switch (type) {
			case "lava" -> bucketType = new ItemStack(Material.LAVA_BUCKET);
			case "water" -> bucketType = new ItemStack(Material.WATER_BUCKET);
			case "milk" -> bucketType = new ItemStack(Material.MILK_BUCKET);
			default -> {
				error("/empty [lava|water|milk]");
				return;
			}
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
