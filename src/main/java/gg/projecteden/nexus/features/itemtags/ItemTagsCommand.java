package gg.projecteden.nexus.features.itemtags;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemUtils;
import org.bukkit.entity.Player;
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

	@Path("update [debug]")
	@Permission(Group.ADMIN)
	@Description("Update item tags on held item")
	void update(@Arg("false") Boolean bool) {
		ItemStack tool = getToolRequired();

		Player debugger = bool ? player() : null;
		ItemTagsUtils.updateItem(tool, debugger);
	}

	@Path("updateInv")
	@Permission(Group.ADMIN)
	@Description("Update item tags on all items in inventory")
	void updateInv() {
		ItemStack[] contents = inventory().getContents();
		int count = 0;
		for (ItemStack item : contents) {
			if (ItemUtils.isNullOrAir(item))
				continue;

			ItemTagsUtils.updateItem(item);
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
	void setRarity(Rarity rarity) {
		ItemStack tool = getToolRequired();
		ItemTagsUtils.updateRarity(tool, rarity);
	}

	@Path("setCondition <condition>")
	@Permission(Group.ADMIN)
	void setCondition(Condition condition) {
		ItemStack tool = getToolRequired();
		ItemTagsUtils.updateCondition(tool, condition);
		Condition.setDurability(tool, condition);
	}

	@Path("reload")
	@Permission(Group.ADMIN)
	void reload() {
		ItemTags.reloadConfig();
		send(PREFIX + "Reloaded config");
	}
}
