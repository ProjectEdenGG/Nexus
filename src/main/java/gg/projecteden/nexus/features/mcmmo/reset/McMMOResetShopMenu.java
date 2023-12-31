package gg.projecteden.nexus.features.mcmmo.reset;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.ScrollableInventoryProvider;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser.SkillTokenType;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

import java.util.Arrays;

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
			contents.fillRow(start, ClickableItem.of(CustomMaterial.INVISIBLE.getItem(), e -> {}));
			contents.set(start, ClickableItem.of(filter.material, e -> {}));
			contents.set(start + 1, ClickableItem.of(new ItemBuilder(Material.PAPER).modelId(32000 + filter.ordinal()), e -> {
				new McMMOResetShopRewardsMenu(filter, this).open(viewer);
			}));
			++row;
		}

		super.init();
	}

	@Getter
	@AllArgsConstructor
	public enum SkillTokenFilterType {
		ALL(Material.BUNDLE),
		GRANDMASTER(Material.NETHER_STAR),
		ACROBATICS(Material.NETHERITE_BOOTS),
		ALCHEMY(Material.BREWING_STAND),
		ARCHERY(Material.BOW),
		AXES(Material.NETHERITE_AXE),
		EXCAVATION(Material.NETHERITE_SHOVEL),
		FISHING(Material.FISHING_ROD),
		HERBALISM(Material.NETHERITE_HOE),
		MINING(Material.NETHERITE_PICKAXE),
		REPAIR(Material.ANVIL),
		SWORDS(Material.NETHERITE_SWORD),
		TAMING(Material.BONE),
		UNARMED(Material.STICK),
		WOODCUTTING(Material.OAK_LOG),
		;

		private final Material material;

		public SkillTokenType toToken() {
			return SkillTokenType.valueOf(name());
		}
	}

}
