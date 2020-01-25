package me.pugabyte.bncore.features.commands;

import de.tr7zw.itemnbtapi.NBTItem;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;
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
		String nbt = "{}";

		NBTItem nbtItem = new NBTItem(tool);
		if (nbtItem.hasNBTData())
			nbt = nbtItem.asNBTString();

		String give = material.name() + " " + amount + " 0 " + nbt;

		// clean up of garbage
		give = give.replaceAll("\"\"", "");
		give = give.replaceAll("\\{\"\"text\"\":\"\"\\n\"\"},", "");
		give = give.replaceAll("\\n", "");
		give = give.replaceAll("\\\\", "");

		// highlight keywords
		give = give.replaceAll("run_command", "&crun_command&f");
		give = give.replaceAll("suggest_command", "&csuggest_command&f");
		give = give.replaceAll("insert_command", "&cinsert_command&f");
		give = give.replaceAll("open_url", "&copen_url&f");
		give = give.replaceAll("open_file", "&copen_file&f");

		give = give.replaceAll("clickEvent", "&cclickEvent&f");
		give = give.replaceAll("hoverEvent", "&choverEvent&f");

		send("");

		if (!overrideBool && (material.equals(Material.WRITTEN_BOOK) || material.equals(Material.BOOK_AND_QUILL))) {
			int length = give.length();
			if (length > 12400) {
				send("String very big, length: " + length);
				json("&e&l[Click to Try]||sgt:/iteminfo override||ttp:&cCaution: May crash you");
				return;
			}
		}

		send(give);
		json("&e&l[Click to Copy]||sgt:" + give);
	}
}
