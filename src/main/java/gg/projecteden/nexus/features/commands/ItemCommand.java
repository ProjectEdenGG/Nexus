package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.recipes.RecipeUtils;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Switch;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.WikiConfig;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Aliases("i")
@Permission("essentials.item")
@WikiConfig(rank = "Guest", feature = "Creative")
public class ItemCommand extends CustomCommand {

	public ItemCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("<item> [amount] [nbt...]")
	@Description("Spawn an item")
	void run(ItemStack item, @Arg(min = 1, max = 2304, minMaxBypass = Group.STAFF) Integer amount, @Permission(Group.STAFF) String nbt) {
		item.setAmount(amount == null ? item.getType().getMaxStackSize() : amount);
		PlayerUtils.giveItem(player(), item, nbt);
	}

	@Path("rp <material> <id>")
	@Permission(Group.STAFF)
	@Description("Spawn a resource pack item")
	void rp(Material material, int id) {
		PlayerUtils.giveItem(player(), new ItemBuilder(material).modelId(id).build());
	}

	@Path("tag <tag> [amount]")
	@Description("Spawn all items in a tag")
	void tag(Tag<?> tag, @Optional("1") int amount) {
		tag.getValues().forEach(tagged -> {
			if (tagged instanceof Material material)
				run(new ItemStack(material), amount, null);
			else if (tagged instanceof CustomBlock customBlock)
				run(customBlock.get().getItemStack(), amount, null);
			else
				error("Unsupported tag type");
		});
	}

	@Permission(Group.SENIOR_STAFF)
	@Path("ingredients <item> [amount] [--index]")
	@Description("Spawn all items in a recipe")
	void ingredients(ItemStack itemStack, @Optional("1") int amount, @Switch int index) {
		final List<List<ItemStack>> recipes = RecipeUtils.uncraft(itemStack);
		if (recipes.isEmpty())
			error("No recipes found for &e" + camelCase(arg(2)));

		if (index >= recipes.size())
			error(camelCase(arg(2)) + " only has &e" + recipes.size() + plural(" recipe", recipes.size()));

		for (int i = 0; i < amount; i++)
			recipes.get(index).forEach(this::giveItem);
	}

}
