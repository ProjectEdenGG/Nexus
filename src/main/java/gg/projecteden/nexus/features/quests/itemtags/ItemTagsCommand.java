package gg.projecteden.nexus.features.quests.itemtags;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemUtils;
import org.bukkit.inventory.ItemStack;

public class ItemTagsCommand extends CustomCommand {

	public ItemTagsCommand(CommandEvent event) {
		super(event);
	}

	@Path()
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
	void debugItem() {
		ItemStack tool = getToolRequired();
		ItemTagsUtils.debugItem(tool, player());
	}

	@Path("update")
	@Description("Update item tags on held item")
	void update() {
		ItemStack tool = getToolRequired();

		ItemStack updated = ItemTagsUtils.updateItem(tool);
		int heldSlot = inventory().getHeldItemSlot();
		inventory().setItem(heldSlot, updated);
	}

	@Path("updateInv")
	@Description("Update item tags on all items in inventory")
	void updateInv() {
		ItemStack[] contents = inventory().getContents();
		int count = 0;
		for (ItemStack item : contents) {
			if (ItemUtils.isNullOrAir(item))
				continue;

			ItemStack updated = ItemTagsUtils.updateItem(item);
			item.setItemMeta(updated.getItemMeta());
			++count;
		}

		send(PREFIX + count + " items itemtags updated!");
	}

	@Path("get")
	@Description("Get item tags on held item")
	@Permission("group.admin")
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
	@Permission("group.admin")
	void setRarity(Rarity rarity) {
		ItemStack tool = getToolRequired();

		ItemStack updated = ItemTagsUtils.finalizeItem(ItemTagsUtils.addRarity(tool, rarity, true));
		int heldSlot = inventory().getHeldItemSlot();
		inventory().setItem(heldSlot, updated);
	}

	@Path("setCondition <condition>")
	@Permission("group.admin")
	void setCondition(Condition condition) {
		ItemStack tool = getToolRequired();

		ItemStack updated = ItemTagsUtils.finalizeItem(ItemTagsUtils.addCondition(tool, condition, true));
		Condition.setDurability(updated, condition);

		int heldSlot = inventory().getHeldItemSlot();
		inventory().setItem(heldSlot, updated);
	}

	@Path("reload")
	@Permission("group.admin")
	void reload() {
		ItemTags.reloadConfig();
		send(PREFIX + "Reloaded config");
	}
}
