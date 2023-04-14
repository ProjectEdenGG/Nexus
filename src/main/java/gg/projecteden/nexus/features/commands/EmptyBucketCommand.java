package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class EmptyBucketCommand extends CustomCommand {

	public EmptyBucketCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Empty the bucket you are holding")
	void empty() {
		PlayerInventory playerInv = inventory();
		ItemStack bucket = new ItemStack(Material.BUCKET);

		ItemStack heldItem = playerInv.getItem(playerInv.getHeldItemSlot());
		if (isNullOrAir(heldItem) || !isBucket(heldItem))
			error("You must be holding a bucket type");

		if (heldItem == bucket) error("Nothing to empty");

		playerInv.setItem(playerInv.getHeldItemSlot(), bucket);
	}

	@NoLiterals
	@Path("[type]")
	@Description("Empty all buckets of a certain type")
	void emptyType(BucketType type) {
		PlayerInventory playerInv = inventory();
		ItemStack bucket = new ItemStack(Material.BUCKET);
		ItemStack bucketType = new ItemStack(type.getMaterial());

		if (playerInv.all(bucketType).size() == 0)
			error("Nothing to empty");

		playerInv.all(bucketType).forEach((key, value) -> playerInv.setItem(key, bucket));
		send(PREFIX + "Emptied all " + type + " buckets");
	}

	boolean isBucket(ItemStack item) {
		return item.getType().toString().toLowerCase().contains("bucket");
	}

	@Getter
	@AllArgsConstructor
	private enum BucketType {
		LAVA(Material.LAVA_BUCKET),
		WATER(Material.WATER_BUCKET),
		MILK(Material.MILK_BUCKET),
		;

		private Material material;
	}
}
