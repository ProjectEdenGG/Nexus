package me.pugabyte.bncore.features.commands.staff;

import de.tr7zw.nbtapi.NBTItem;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.bncore.utils.StringUtils.*;

@Aliases("nbt")
@Permission("group.staff")
public class ItemInfoCommand extends CustomCommand {

	public ItemInfoCommand(CommandEvent event) {
		super(event);
	}

	@Path("[material]")
	void itemInfo(Material material) {
		ItemStack tool = player().getInventory().getItemInMainHand();

		if (material != null)
			tool = new ItemStack(material);
		else if (Utils.isNullOrAir(tool))
			error("Must be holding an item");

		material = tool.getType();
		int amount = tool.getAmount();
		String nbtString = null;

		NBTItem nbtItem = new NBTItem(tool);
		if (nbtItem.hasNBTData()) {
			nbtString = nbtItem.asNBTString();
			nbtString = StringUtils.stripColor(nbtString);
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
		send("Material: " + material + " (" + material.ordinal() + ")");
		if (nbtString != null) {
			int length = nbtString.length();
			if (length > 256) {
				String finalNbtString = nbtString;
				Tasks.async(() -> {
					String url = paste(stripColor(finalNbtString));
					send(json("&eNBT: &l[Click to Open]").url(url).hover(url));
				});
			} else {
				send("NBT: " + colorize(nbtString));
				send(json("&e&l[Click to Copy]").suggest(spawnCommand));
			}
		}
	}

}
