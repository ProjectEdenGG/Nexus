package gg.projecteden.nexus.features.commands;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.SerializationUtils.Json;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.SerializationUtils.Json.serialize;
import static gg.projecteden.nexus.utils.StringUtils.colorize;
import static gg.projecteden.nexus.utils.StringUtils.paste;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;
import static gg.projecteden.nexus.utils.Utils.dump;

@Aliases({"nbt", "itemdb"})
public class ItemInfoCommand extends CustomCommand {

	public ItemInfoCommand(CommandEvent event) {
		super(event);
	}

	@Path("[material]")
	@Description("View an item's NBT")
	void itemInfo(Material material) {
		ItemStack tool = material == null ? getToolRequired() : new ItemStack(material);

		line();
		sendJson(tool);
		line();
	}

	@Path("extended [material]")
	@Permission(Group.STAFF)
	@Description("View miscellaneous information about a material")
	void extended(Material material) {
		ItemStack tool = material == null ? getToolRequired() : new ItemStack(material);
		material = tool.getType();

		line(5);
		sendJson(tool);
		line();
		send(" Namespaced key: " + material.getKey());
		try {send(" Blast resistance: " + material.getBlastResistance());} catch (Exception ignored) {}
		try {send(" Hardness: " + material.getHardness());} catch (Exception ignored) {}
		try {send(" Slipperiness: " + material.getSlipperiness());} catch (Exception ignored) {}
		send(" Max durability: " + material.getMaxDurability());
		send(" Max stack size: " + material.getMaxStackSize());
		line();
		send(" Has gravity: " + StringUtils.bool(material.hasGravity()));
		send(" Is air: " + StringUtils.bool(material.isAir()));
		send(" Is block: " + StringUtils.bool(material.isBlock()));
		send(" Is burnable: " + StringUtils.bool(material.isBurnable()));
		send(" Is edible: " + StringUtils.bool(material.isEdible()));
		send(" Is empty: " + StringUtils.bool(material.isEmpty()));
		send(" Is flammable: " + StringUtils.bool(material.isFlammable()));
		send(" Is fuel: " + StringUtils.bool(material.isFuel()));
		send(" Is interactable: " + StringUtils.bool(material.isInteractable()));
		send(" Is item: " + StringUtils.bool(material.isItem()));
		send(" Is occluding: " + StringUtils.bool(material.isOccluding()));
		send(" Is record: " + StringUtils.bool(material.isRecord()));
		send(" Is solid: " + StringUtils.bool(material.isSolid()));
		send(" Is transparent: " + StringUtils.bool(material.isTransparent()));
		line();
		send(" Applicable tags: " + String.join(", ", MaterialTag.getApplicable(material).keySet()));
		line();
		try {
			BlockData blockData = material.createBlockData();
			send(" BlockData: " + material.data.getSimpleName());
			dump(blockData).forEach((method, output) -> send(" - " + method + "(): " + output));
			line();
		} catch (Exception ignored) {}
		send("Is Decoration: " + (DecorationConfig.of(tool) != null));
	}

	private void sendJson(ItemStack tool) {
		line();
		send("Material: " + tool.getType() + " (" + tool.getType().ordinal() + ")");

		if (!isNullOrAir(tool)) {
			final String nbtString = getNBTString(tool);

			if (nbtString != null && !"{}".equals(nbtString)) {
				int length = nbtString.length();
				if (length > 256) {
					Tasks.async(() -> {
						if (length < 32000) // max char limit in command blocks
							send("NBT: " + colorize(nbtString));
						String url = paste(stripColor(nbtString));
						send(json("&e&l[Click to Open NBT]").url(url).hover(url));
					});
				} else {
					send("NBT: " + colorize(nbtString));
					send(json("&e&l[Click to Copy NBT]").hover("&e&l[Click to Copy NBT]").copy(nbtString));
				}
			}
		}
	}

	@Path("serialize json [material] [amount]")
	@Permission(Group.ADMIN)
	@Description("Debug the serialization of an item")
	void serializeJson(Material material, @Arg("1") int amount) {
		ItemStack tool = material == null ? getToolRequired() : new ItemStack(material);

		send(json("&e&l[Click to Copy NBT]").hover("&e&l[Click to Copy NBT]").copy(Json.toString(serialize(tool))));
	}

	@Nullable
	private String getNBTString(ItemStack itemStack) {
		NBTItem nbtItem = new NBTItem(itemStack);
		String nbtString = null;

		if (nbtItem.hasNBTData()) {
			nbtString = nbtItem.asNBTString();
			nbtString = StringUtils.stripColor(nbtString);
		}

		if (nbtString != null) {
			// highlight keywords
			nbtString = nbtString.replaceAll("run_command", "&crun_command&f");
			nbtString = nbtString.replaceAll("suggest_command", "&csuggest_command&f");
			nbtString = nbtString.replaceAll("insert_command", "&cinsert_command&f");
			nbtString = nbtString.replaceAll("open_url", "&copen_url&f");
			nbtString = nbtString.replaceAll("open_file", "&copen_file&f");

			nbtString = nbtString.replaceAll("clickEvent", "&cclickEvent&f");
			nbtString = nbtString.replaceAll("hoverEvent", "&choverEvent&f");

			// clean up of garbage
			nbtString = nbtString.replaceAll("\"\"", "");
			nbtString = nbtString.replaceAll("\\{\"\"text\"\":\"\"\\n\"\"},", "");
			nbtString = nbtString.replaceAll("\\n", "");
			nbtString = nbtString.replaceAll("\\\\", "");
		}
		return nbtString;
	}

	@Path("nonItems")
	@Permission(Group.ADMIN)
	@Description("View a list of non-item materials (i.e. water)")
	void nonItems() {
		for (Material material : Material.values())
			if (!material.isLegacy() && !material.isItem())
				send(material.name());
	}

	@Path("enchanted")
	@Permission(Group.STAFF)
	@Description("View all items with an enchant glint")
	void enchanted() {
		new EnchantedItemsMenu().open(player());
	}

	@Title("Enchanted Items")
	private static class EnchantedItemsMenu extends InventoryProvider {

		@Override
		public void init() {
			paginator().items(new ArrayList<>() {{
				for (Material material : Material.values())
					if (!material.isLegacy() && material.isItem())
						if (new ItemStack(material).getItemMeta() != null)
							add(ClickableItem.empty(new ItemBuilder(material).enchant(Enchant.INFINITY).build()));
			}}).build();
		}

	}

}
