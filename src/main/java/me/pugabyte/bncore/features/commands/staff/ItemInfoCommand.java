package me.pugabyte.bncore.features.commands.staff;

import de.tr7zw.nbtapi.NBTItem;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.StringUtils.paste;
import static me.pugabyte.bncore.utils.StringUtils.stripColor;
import static me.pugabyte.bncore.utils.Utils.isNullOrAir;

@Aliases({"nbt", "itemdb"})
@Permission("group.staff")
public class ItemInfoCommand extends CustomCommand {

	public ItemInfoCommand(CommandEvent event) {
		super(event);
	}

	@Path("extended [material]")
	void extended(Material material) {
		ItemStack tool;
		if (material == null)
			tool = getToolRequired();
		else
			tool = new ItemStack(material);

		if (material == null)
			return;

		sendJson(player(), tool);
		line();
		send("Namespaced key: " + material.getKey());
		send("Blast resistance: " + material.getBlastResistance());
		send("Hardness: " + material.getHardness());
		send("Max durability: " + material.getMaxDurability());
		send("Max stack size: " + material.getMaxStackSize());
		line();
		send("Has gravity: " + material.hasGravity());
		send("Is air: " + material.isAir());
		send("Is block: " + material.isBlock());
		send("Is burnable: " + material.isBurnable());
		send("Is edible: " + material.isEdible());
		send("Is empty: " + material.isEmpty());
		send("Is flammable: " + material.isFlammable());
		send("Is fuel: " + material.isFuel());
		send("Is interactable: " + material.isInteractable());
		send("Is item: " + material.isItem());
		send("Is occluding: " + material.isOccluding());
		send("Is record: " + material.isRecord());
		send("Is solid: " + material.isSolid());
		send("Is transparent: " + material.isTransparent());
		line();
		BlockData blockData = material.createBlockData();
	}

	@Path("[material]")
	void itemInfo(Material material) {
		ItemStack tool;
		if (material == null)
			tool = getToolRequired();
		else
			tool = new ItemStack(material);

		sendJson(player(), tool);
	}

	private void sendJson(Player player, ItemStack tool) {
		send(player, "");
		send(player, "Material: " + tool.getType() + " (" + tool.getType().ordinal() + ")");

		if (!isNullOrAir(tool)) {
			final String nbtString = getNBTString(tool);

			if (nbtString != null && !"{}".equals(nbtString)) {
				int length = nbtString.length();
				if (length > 256) {
					Tasks.async(() -> {
						if (length < 32000) // max char limit in command blocks
							send(player, "NBT: " + colorize(nbtString));
						String url = paste(stripColor(nbtString));
						send(player, json("&e&l[Click to Open NBT]").url(url).hover(url));
					});
				} else {
					send(player, "NBT: " + colorize(nbtString));
					send(player, json("&e&l[Click to Copy NBT]").suggest(nbtString));
				}
			}
		}
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

	@Path("notItems")
	void notItems() {
		for (Material material : Material.values()) {
			if (!material.isLegacy() && !material.isItem())
				send(material.name());
		}
	}

}
