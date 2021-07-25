package gg.projecteden.nexus.features.quests.itemtags;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.features.quests.itemtags.ItemTagsUtils.addCondition;
import static gg.projecteden.nexus.features.quests.itemtags.ItemTagsUtils.addRarity;
import static gg.projecteden.nexus.features.quests.itemtags.ItemTagsUtils.finalizeItem;
import static gg.projecteden.nexus.features.quests.itemtags.ItemTagsUtils.updateItem;

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

	@Path("update")
	@Description("Update item tags on held item")
	@Permission("group.admin")
	void update() {
		ItemStack tool = getToolRequired();

		ItemStack updated = updateItem(tool);
		int heldSlot = inventory().getHeldItemSlot();
		inventory().setItem(heldSlot, updated);
	}

	@Path("updateInv")
	@Description("Update item tags on all items in inventory")
	@Permission("group.admin")
	void updateInv() {
		ItemStack[] contents = inventory().getContents();
		int count = 0;
		for (ItemStack item : contents) {
			if (ItemUtils.isNullOrAir(item))
				continue;

			ItemStack updated = updateItem(item);
			item.setItemMeta(updated.getItemMeta());
			++count;
		}

		send(PREFIX + count + " items itemtags updated!");
	}

	@Path("setRarity <rarity>")
	@Permission("group.admin")
	void setRarity(Rarity rarity) {
		ItemStack tool = getToolRequired();

		ItemStack updated = finalizeItem(addRarity(tool, rarity, true));
		int heldSlot = inventory().getHeldItemSlot();
		inventory().setItem(heldSlot, updated);
	}

	@Path("setCondition <condition>")
	@Permission("group.admin")
	void setCondition(Condition condition) {
		ItemStack tool = getToolRequired();

		ItemStack updated = finalizeItem(addCondition(tool, condition, true));
		ItemUtils.setDurability(updated, RandomUtils.randomInt(condition.getMin(), condition.getMax()));

		int heldSlot = inventory().getHeldItemSlot();
		inventory().setItem(heldSlot, updated);
	}

	@Path("debugItem")
	@Permission("group.admin")
	void debugItem() {
		ItemStack tool = getToolRequired();
		ItemTagsUtils.debugItem(tool, player());
	}

	@Path("reload")
	@Permission("group.admin")
	void reload() {
		ItemTags.reloadConfig();
	}
}
