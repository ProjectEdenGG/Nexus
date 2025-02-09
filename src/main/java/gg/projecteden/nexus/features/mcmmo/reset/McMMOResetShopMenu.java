package gg.projecteden.nexus.features.mcmmo.reset;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.content.ScrollableInventoryProvider;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser.SkillTokenType;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.function.Consumer;

public class McMMOResetShopMenu extends ScrollableInventoryProvider {

	@Override
	public int getPages() {
		return 10;
	}

	@Override
	public String getTitle(int page) {
		return super.getTitle(page) + "&0McMMO Reset Shop";
	}

	@Override
	public void init() {
		final int page = contents.pagination().getPage();
		final var list = Arrays.asList(SkillTokenFilterType.values()).subList(page, page + 6);

		var row = 0;
		for (SkillTokenFilterType filter : list) {
			final int start = row * 9;
			final Consumer<ItemClickData> consumer = e -> new McMMOResetShopRewardsMenu(filter, this).open(viewer);
			final Consumer<ItemBuilder> format = item -> {
				item.name(StringUtils.camelCase(filter));
				// Lore
			};
			contents.fillRow(start, ClickableItem.of(CustomMaterial.INVISIBLE.getItem(), consumer));
			contents.set(start, ClickableItem.of(filter.material, consumer));
			contents.set(start + 1, ClickableItem.of(new ItemBuilder(Material.PAPER).model(filter.getModel()), consumer));
			++row;
		}

		super.init();
	}

	@Getter
	@AllArgsConstructor
	public enum SkillTokenFilterType {
		ALL(Material.BUNDLE, CustomMaterial.GUI_MCMMO_ALL),
		GRANDMASTER(Material.NETHER_STAR, CustomMaterial.GUI_MCMMO_GRANDMASTER),
		ACROBATICS(Material.NETHERITE_BOOTS, CustomMaterial.GUI_MCMMO_ACROBATICS),
		ALCHEMY(Material.BREWING_STAND, CustomMaterial.GUI_MCMMO_ALCHEMY),
		ARCHERY(Material.BOW, CustomMaterial.GUI_MCMMO_ARCHERY),
		AXES(Material.NETHERITE_AXE, CustomMaterial.GUI_MCMMO_AXES),
		EXCAVATION(Material.NETHERITE_SHOVEL, CustomMaterial.GUI_MCMMO_EXCAVATION),
		FISHING(Material.FISHING_ROD, CustomMaterial.GUI_MCMMO_FISHING),
		HERBALISM(Material.NETHERITE_HOE, CustomMaterial.GUI_MCMMO_HERBALISM),
		MINING(Material.NETHERITE_PICKAXE, CustomMaterial.GUI_MCMMO_MINING),
		REPAIR(Material.ANVIL, CustomMaterial.GUI_MCMMO_REPAIR),
		SWORDS(Material.NETHERITE_SWORD, CustomMaterial.GUI_MCMMO_SWORDS),
		TAMING(Material.BONE, CustomMaterial.GUI_MCMMO_TAMING),
		UNARMED(Material.STICK, CustomMaterial.GUI_MCMMO_UNARMED),
		WOODCUTTING(Material.OAK_LOG, CustomMaterial.GUI_MCMMO_WOODCUTTING),
		;

		private final Material material;
		private final CustomMaterial model;

		public SkillTokenType toToken() {
			return SkillTokenType.valueOf(name());
		}
	}

}
