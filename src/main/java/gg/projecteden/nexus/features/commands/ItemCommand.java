package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.equipment.skins.ArmorSkin;
import gg.projecteden.nexus.features.equipment.skins.EquipmentSkinType;
import gg.projecteden.nexus.features.equipment.skins.EquipmentSkinType.EquipmentSkinTypeClass;
import gg.projecteden.nexus.features.recipes.RecipeUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Aliases("i")
@Permission("essentials.item")
@WikiConfig(rank = "Guest", feature = "Creative")
@Redirect(from = "/iframe", to = "/i invisible_item_frame")
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
	void rp(Material material, String id) {
		PlayerUtils.giveItem(player(), new ItemBuilder(material).model(id).build());
	}

	@Path("rp armor <piece> <type>")
	@Permission(Group.STAFF)
	@Description("Spawn a resource pack armor item or set")
	void rp_armor(ArmorPiece piece, ArmorSkin type) {
		for (Material material : piece.getMaterials().getValues())
			PlayerUtils.giveItem(player(), type.apply(new ItemStack(material)));
	}

	@Path("rp skinTemplate <type> <skin>")
	@Permission(Group.STAFF)
	@Description("Spawn a resource pack skin template")
	void rp_skinTemplate(EquipmentSkinTypeClass type, @Arg(context = 1) EquipmentSkinType skin) {
		PlayerUtils.giveItem(player(), skin.getTemplate());
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

	@Permission(Group.SENIOR_STAFF)
	@Path("getInvisibleWithoutTooltip")
	@Description("Spawns an item with no texture and no tooltip")
	void getInvisibleWithoutTooltip() {
		player().give(ItemUtils.getEmptySlotItem());
	}

	@TabCompleterFor(EquipmentSkinType.class)
	List<String> tabCompleteEquipmentSkinType(String filter, EquipmentSkinTypeClass context) {
		return tabCompleteEnum(filter, context.getClazz());
	}

	@ConverterFor(EquipmentSkinType.class)
	EquipmentSkinType convertToEquipmentSkinType(String value, EquipmentSkinTypeClass context) {
		for (Enum<? extends EquipmentSkinType> skinEnum : context.getClazz().getEnumConstants())
			if (skinEnum.name().equalsIgnoreCase(value))
				if (skinEnum instanceof EquipmentSkinType skin)
					return skin;

		throw new IllegalArgumentException("Skin &e" + value + "&c from type &e" + camelCase(context) + "&c not found");
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
