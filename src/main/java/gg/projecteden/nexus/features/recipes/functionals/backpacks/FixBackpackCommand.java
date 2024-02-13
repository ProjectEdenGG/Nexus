package gg.projecteden.nexus.features.recipes.functionals.backpacks;

import gg.projecteden.nexus.features.legacy.listeners.LegacyShulkerBoxes;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;

@Permission(Permission.Group.ADMIN)
public class FixBackpackCommand extends CustomCommand {

	public FixBackpackCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Add necessary NBT to a shulker box to make it a backpack")
	public void fix() {
		ItemStack item = player().getInventory().getItemInMainHand();
		player().getInventory().setItemInMainHand(fix(item));
	}

	@Path("setTier <size>")
	void setSize(Backpacks.BackpackTier tier) {
		ItemStack item = player().getInventory().getItemInMainHand();
		if (!Backpacks.isBackpack(item)) return;

		player().getInventory().setItemInMainHand(new ItemBuilder(item).nbt(nbt -> nbt.setBoolean(tier.getNBTKey(), true)).build());
		player().updateInventory();
	}

	@Path("convert")
	void convert() {
		ItemStack item = player().getInventory().getItemInMainHand();
		if (!Backpacks.isBackpack(item)) return;

		player().getInventory().setItemInMainHand(Backpacks.convertOldToNew(item));
		player().updateInventory();
	}

	@Path("getOld")
	void getOld() {
		player().getInventory().setItemInMainHand(
			fix(new ItemBuilder(Material.SHULKER_BOX)
				.shulkerBox(new ItemStack(Material.GRASS_BLOCK))
				.build()));
	}

	public static ItemStack fix(ItemStack item) {
		return new ItemBuilder(item)
			.nbt(nbt -> {
				nbt.removeKey(LegacyShulkerBoxes.NBT_KEY);
				nbt.setString(Backpacks.NBT_KEY, randomAlphabetic(10));
			})
			.build();
	}

}
