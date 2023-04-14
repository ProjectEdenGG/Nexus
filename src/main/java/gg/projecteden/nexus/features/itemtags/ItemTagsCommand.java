package gg.projecteden.nexus.features.itemtags;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class ItemTagsCommand extends CustomCommand {

	public ItemTagsCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("View an item's condition and rarity")
	void showTags() {
		send(PREFIX + "Available tags:");
		send(" &eCondition: ");
		for (Condition condition : Condition.values())
			send("  " + condition.getTag());

		send();

		send(" &eRarity: ");
		for (Rarity rarity : Rarity.values())
			send("  " + rarity.getTag());
	}

	@Path("debugItem")
	@Description("View detailed information about an item's tags")
	void debugItem() {
		ItemStack tool = getToolRequired();
		ItemTagsUtils.debugItem(tool, player());
	}

	@Path("update [debug]")
	@Permission(Group.ADMIN)
	@Description("Update item tags on held item")
	void update(@Optional("false") Boolean bool) {
		ItemStack tool = getToolRequired();

		Player debugger = bool ? player() : null;
		ItemTagsUtils.update(tool, debugger);
	}

	@Path("updateInv")
	@Permission(Group.ADMIN)
	@Description("Update item tags on all items in inventory")
	void updateInv() {
		ItemStack[] contents = inventory().getContents();
		int count = 0;
		for (ItemStack item : contents) {
			if (isNullOrAir(item))
				continue;

			ItemUtils.update(item, player());
			++count;
		}

		send(PREFIX + count + " items itemtags updated!");
	}

	@Path("get")
	@Description("Get item tags on held item")
	@Permission(Group.ADMIN)
	void getTags() {
		ItemStack tool = getToolRequired();

		send("");
		send("Item Tags: ");
		Condition condition = Condition.of(tool);
		if (condition != null)
			send(condition.getTag());

		Rarity rarity = Rarity.of(tool, condition);
		if (rarity != null)
			send(rarity.getTag());
		send("");
	}

	@Path("setRarity <rarity>")
	@Permission(Group.ADMIN)
	@Description("Set an item's rarity")
	void setRarity(Rarity rarity) {
		ItemStack tool = getToolRequired();
		ItemTagsUtils.updateRarity(tool, rarity);
	}

	@Path("setCondition <condition>")
	@Permission(Group.ADMIN)
	@Description("Set an item's condition")
	void setCondition(Condition condition) {
		ItemStack tool = getToolRequired();
		ItemTagsUtils.updateCondition(tool, condition);
		Condition.setDurability(tool, condition);
	}

	@Path("reload")
	@Permission(Group.ADMIN)
	@Description("Reload configuration from disk")
	void reload() {
		ItemTags.reloadConfig();
		send(PREFIX + "Reloaded config");
	}
}
