package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.recipes.RecipeUtils;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Aliases("i")
@Permission("essentials.item")
public class ItemCommand extends CustomCommand {

	public ItemCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<type> [amount] [nbt...]")
	void run(Material material, @Arg(min = 1, max = 2304, minMaxBypass = Group.STAFF) Integer amount, @Arg(permission = Group.STAFF) String nbt) {
		PlayerUtils.giveItem(player(), material, amount == null ? material.getMaxStackSize() : amount, nbt);
	}

	@Permission(Group.STAFF)
	@Path("rp <material> <id>")
	void rp(Material material, int id) {
		PlayerUtils.giveItem(player(), new ItemBuilder(material).modelId(id).build());
	}

	@Path("tag <tag> [amount]")
	void tag(Tag<Material> tag, @Arg("1") int amount) {
		tag.getValues().forEach(material -> run(material, amount, null));
	}

	@Permission(Group.SENIOR_STAFF)
	@Path("ingredients <material> [amount] [--index]")
	void ingredients(Material material, @Arg("1") int amount, @Switch int index) {
		final List<List<ItemStack>> recipes = RecipeUtils.uncraft(new ItemStack(material));
		if (recipes.isEmpty())
			error("No recipes found for &e" + camelCase(material));

		if (index >= recipes.size())
			error(camelCase(material) + " only has &e" + recipes.size() + plural(" recipe", recipes.size()));

		for (int i = 0; i < amount; i++)
			recipes.get(index).forEach(this::giveItem);
	}

}
