package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.recipes.RecipeUtils;
import gg.projecteden.nexus.features.resourcepack.models.CustomArmorType;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Color;
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

	@Path("<item> [amount] [nbt...]")
	@Description("Spawn an item")
	void run(ItemStack item, @Arg(min = 1, max = 2304, minMaxBypass = Group.STAFF) Integer amount, @Arg(permission = Group.STAFF) String nbt) {
		item.setAmount(amount == null ? item.getType().getMaxStackSize() : amount);
		PlayerUtils.giveItem(player(), item, nbt);
	}

	@Path("light [amount] [--level]")
	@Permission(Group.STAFF)
	@HideFromHelp
	@HideFromWiki
	void light(@Arg(min = 1, max = 2304, minMaxBypass = Group.STAFF) Integer amount, @Arg(value = "15", min = 0, max = 15) @Switch int level) {
		run(new ItemStack(Material.LIGHT), amount, "{BlockStateTag:{level:%d}}".formatted(level));
	}

	@Path("rp <material> <id>")
	@Permission(Group.STAFF)
	@Description("Spawn a resource pack item")
	void rp(Material material, int id) {
		PlayerUtils.giveItem(player(), new ItemBuilder(material).modelId(id).build());
	}

	@Path("rp armor <piece> <type>")
	@Permission(Group.STAFF)
	@Description("Spawn a resource pack armor item or set")
	void rp_armor(ArmorPiece piece, CustomArmorType type) {
		for (Material material : piece.getMaterials().getValues())
			PlayerUtils.giveItem(player(), new ItemBuilder(material).modelId(type.getId()).dyeColor(Color.fromRGB(type.getId())).build());
	}

	@Path("tag <tag> [amount]")
	@Description("Spawn all items in a tag")
	void tag(Tag<?> tag, @Arg("1") int amount) {
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
	void ingredients(ItemStack itemStack, @Arg("1") int amount, @Switch int index) {
		final List<List<ItemStack>> recipes = RecipeUtils.uncraft(itemStack);
		if (recipes.isEmpty())
			error("No recipes found for &e" + camelCase(arg(2)));

		if (index >= recipes.size())
			error(camelCase(arg(2)) + " only has &e" + recipes.size() + plural(" recipe", recipes.size()));

		for (int i = 0; i < amount; i++)
			recipes.get(index).forEach(this::giveItem);
	}

	@Getter
	@AllArgsConstructor
	private enum ArmorPiece {
		HELMET(Material.LEATHER_HELMET),
		CHESTPLATE(Material.LEATHER_CHESTPLATE),
		LEGGINGS(Material.LEATHER_LEGGINGS),
		BOOTS(Material.LEATHER_BOOTS),
		ALL(MaterialTag.ARMOR_LEATHER),
		;

		private final MaterialTag materials;

		ArmorPiece(Material material) {
			this.materials = new MaterialTag(material);
		}
	}

}
