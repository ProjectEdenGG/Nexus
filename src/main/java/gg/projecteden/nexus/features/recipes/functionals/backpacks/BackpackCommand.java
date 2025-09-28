package gg.projecteden.nexus.features.recipes.functionals.backpacks;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.handler.NBTHandlers;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import gg.projecteden.nexus.features.legacy.listeners.LegacyShulkerBoxes;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks.BackpackTier;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Permission(Group.ADMIN)
@Aliases("backpacks")
public class BackpackCommand extends CustomCommand {

	public BackpackCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("get <tier>")
	void get(BackpackTier tier) {
		giveItem(tier.create());
	}

	@Path("fix")
	@Description("Add necessary NBT to a shulker box to make it a backpack")
	public void fix() {
		ItemStack item = player().getInventory().getItemInMainHand();
		player().getInventory().setItemInMainHand(fix(item));
	}

	@Path("setTier <size>")
	@Description("Set a backpack's tier")
	void setSize(BackpackTier tier) {
		ItemStack item = player().getInventory().getItemInMainHand();
		if (!Backpacks.isBackpack(item)) return;

		player().getInventory().setItemInMainHand(new ItemBuilder(item).nbt(nbt -> nbt.setBoolean(tier.getNBTKey(), true)).build());
		player().updateInventory();
	}

	@Path("convert [--nbt]")
	@Description("Convert and old backpack into a new one")
	void convert(@Switch boolean nbt) {
		ItemStack item = player().getInventory().getItemInMainHand();
		if (!Backpacks.isBackpack(item)) return;

		if (nbt) {
			ReadWriteNBT pe = NBT.createNBTObject();
			ReadWriteNBTCompoundList newList = pe.getCompoundList("Items");
			new ItemBuilder(item).nbt(tag -> {
				ReadWriteNBT bet = tag.getCompound("BlockEntityTag");
				ReadWriteNBTCompoundList list = bet.getCompoundList("Items");
				for (int i = 0; i < list.size(); i++) {
					newList.addCompound(list.get(i));
				}
			});

			ItemBuilder converted = new ItemBuilder(Backpacks.convertOldToNew(item, world()));
			converted.nbt(tag -> {
				tag.set("ProjectEden", pe, NBTHandlers.STORE_READWRITE_TAG);
			});

			player().getInventory().setItemInMainHand(converted.build());
			player().updateInventory();
			return;
		}

		player().getInventory().setItemInMainHand(Backpacks.convertOldToNew(item, world()));
		player().updateInventory();
	}

	@Path("getOld")
	@Description("Give yourself and old backpack for testing")
	void getOld() {
		player().getInventory().setItemInMainHand(
			fix(new ItemBuilder(Material.SHULKER_BOX)
				.shulkerBox(new ItemStack(Material.GRASS_BLOCK))
				.build()));
	}

	public static ItemStack fix(ItemStack item) {
		var tier = BackpackTier.of(item);

		return new ItemBuilder(item)
			.model(tier.getModel())
			.nbt(nbt -> {
				nbt.removeKey(LegacyShulkerBoxes.NBT_KEY);
				nbt.setString(Backpacks.NBT_KEY, RandomStringUtils.randomAlphabetic(10));
				nbt.setBoolean(tier.getNBTKey(), true);
			})
			.build();
	}

}
