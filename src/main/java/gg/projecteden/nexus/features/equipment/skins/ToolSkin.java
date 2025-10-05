package gg.projecteden.nexus.features.equipment.skins;

import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.ToolType;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
public enum ToolSkin implements EquipmentSkinType {
	DEFAULT,
	EIGHT_BIT {
		@Override
		public String getFolderName() {
			return "8bit";
		}
	},
	ADAMANTITE,
	AMETHYST,
	BONE,
	CHERRY,
	COBALT,
	HELLFIRE,
	MECHANICAL,
	MYTHRIL,
	SCULK,
	SHADOW,
	VINES;

	public String getBaseModel() {
		return "skins/tools/" + getFolderName();
	}

	public String getFolderName() {
		return name().toLowerCase();
	}

	public static final List<ToolType> APPLICABLE_TYPES = List.of(
		ToolType.SWORD,
		ToolType.PICKAXE,
		ToolType.SHOVEL,
		ToolType.AXE,
		ToolType.HOE,
		ToolType.BOW,
		ToolType.CROSSBOW,
		ToolType.MACE,
		ToolType.FISHING_ROD
	);

	@Override
	public ItemStack apply(ItemStack item) {
		if (!applies(item))
			return item;

		ItemBuilder builder = new ItemBuilder(item);

		if (this == DEFAULT)
			return builder.removeModel().build();

		String toolType = builder.material() == Material.FISHING_ROD ? "fishing_rod" : builder.material().name().toLowerCase().substring(builder.material().name().lastIndexOf('_') + 1);
		String model = getBaseModel() + "/" + toolType;

		return builder.model(model).build();
	}

	@Override
	public boolean applies(ItemStack item) {
		if (item == null)
			return false;

		return APPLICABLE_TYPES.contains(ToolType.of(item));
	}

	@Override
	public ItemStack getBig(ItemStack item) {
		if (item == null)
			return null;

		if (this == DEFAULT)
			return new ItemBuilder(Material.PAPER).model("skins/tools/default/big/" + item.getType().name().toLowerCase()).hideTooltip().build();

		ItemBuilder builder = new ItemBuilder(item);
		String toolType = builder.material() == Material.FISHING_ROD ? "fishing_rod" : builder.material().name().toLowerCase().substring(builder.material().name().lastIndexOf('_') + 1);
		String model = getBaseModel() + "/big/" + toolType;

		return builder.model(model).hideTooltip().build();
	}

	@Override
	public ItemStack getTemplate() {
		return new ItemBuilder(Material.PAPER)
				.name("&e" + StringUtils.camelCase(getFolderName()) + " Tool Skin")
				.model(getBaseModel() + "/template")
				.build();
	}

	public static ToolSkin of(ItemStack stack) {
		if (stack == null)
			return null;

		String model = Model.of(stack);
		if (model == null)
			return null;

		String baseModel = model.toLowerCase().substring(0, model.lastIndexOf('/'));

		for (ToolSkin skin : values())
			if (skin.getBaseModel().equals(baseModel))
				return skin;

		return null;
	}

}
