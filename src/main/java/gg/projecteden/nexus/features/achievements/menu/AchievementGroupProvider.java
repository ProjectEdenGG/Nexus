package gg.projecteden.nexus.features.achievements.menu;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.achievement.AchievementGroup;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor
@Title("&3Achievements")
public class AchievementGroupProvider extends InventoryProvider {

	@Override
	protected int getRows() {
		return MenuUtils.calculateRows(AchievementGroup.values().length / 9, 2);
	}

	@Override
	public void init() {
		contents.fillRow(0, ClickableItem.AIR);
		contents.set(0, 0, ClickableItem.of(new ItemStack(Material.BARRIER), e -> contents.inventory().close(player)));

		for (AchievementGroup group : AchievementGroup.values()) {
			ItemBuilder item = new ItemBuilder(group.getItemStack()).name("&e" + group);
			contents.add(ClickableItem.of(item, e -> new AchievementProvider(group).open(player)));
		}
	}
}
