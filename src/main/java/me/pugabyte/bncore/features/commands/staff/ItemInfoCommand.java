package me.pugabyte.bncore.features.commands.staff;

import de.tr7zw.itemnbtapi.NBTItem;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Permission("group.staff")
public class ItemInfoCommand extends CustomCommand {

	public ItemInfoCommand(CommandEvent event) {
		super(event);
	}

	@Path("[override]")
	void itemInfo(@Arg("false") String override) {
		boolean overrideBool = override.equalsIgnoreCase("override");

		ItemStack tool = player().getInventory().getItemInMainHand();
		if (Utils.isNullOrAir(tool)) error("Must be holding an item");

		Material material = tool.getType();
		int amount = tool.getAmount();
		String nbtString = null;

		NBTItem nbtItem = new NBTItem(tool);
		if (nbtItem.hasNBTData()) {
			nbtString = nbtItem.asNBTString();
			nbtString = ChatColor.stripColor(nbtString);
		}

		String spawnCommand = "/i " + material.name() + " " + amount + (nbtString == null ? "" : " " + nbtString);

		if (nbtString != null) {
			// clean up of garbage
			nbtString = nbtString.replaceAll("\"\"", "");
			nbtString = nbtString.replaceAll("\\{\"\"text\"\":\"\"\\n\"\"},", "");
			nbtString = nbtString.replaceAll("\\n", "");
			nbtString = nbtString.replaceAll("\\\\", "");

			// highlight keywords
			nbtString = nbtString.replaceAll("run_command", "&crun_command&f");
			nbtString = nbtString.replaceAll("suggest_command", "&csuggest_command&f");
			nbtString = nbtString.replaceAll("insert_command", "&cinsert_command&f");
			nbtString = nbtString.replaceAll("open_url", "&copen_url&f");
			nbtString = nbtString.replaceAll("open_file", "&copen_file&f");

			nbtString = nbtString.replaceAll("clickEvent", "&cclickEvent&f");
			nbtString = nbtString.replaceAll("hoverEvent", "&choverEvent&f");
		}

		send("");

		if (!overrideBool && (material.equals(Material.WRITTEN_BOOK) || material.equals(Material.BOOK_AND_QUILL))) {
			if (nbtString != null) {
				int length = nbtString.length();
				if (length > 12400) {
					send("String very big, length: " + length);
					send(json2("&e&l[Click to Try]").suggest("/iteminfo override").hover("&cCaution: May crash you"));
					return;
				}
			}
		}

		send("ID: " + material.getId() + ":" + tool.getDurability());
		send("Material: " + material);
		if (nbtString != null) {
			send("NBT: " + Utils.colorize(nbtString));
			send(json2("&e&l[Click to Copy]").suggest(spawnCommand));
		}
	}
}
