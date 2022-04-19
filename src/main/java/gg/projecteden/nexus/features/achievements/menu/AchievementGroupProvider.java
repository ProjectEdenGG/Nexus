package gg.projecteden.nexus.features.achievements.menu;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.achievement.AchievementGroup;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor
public class AchievementGroupProvider extends InventoryProvider {

	@Override
	public void open(Player player, int page) {
		SmartInventory.builder()
			.provider(this)
			.rows((int) ((Math.ceil(AchievementGroup.values().length / 9)) + 2))
			.title("&3Achievements")
			.build()
			.open(player, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.fillRow(0, ClickableItem.AIR);
		contents.set(0, 0, ClickableItem.of(new ItemStack(Material.BARRIER), e -> contents.inventory().close(player)));

		for (AchievementGroup group : AchievementGroup.values()) {
			ItemBuilder item = new ItemBuilder(group.getItemStack()).name("&e" + group);
			contents.add(ClickableItem.of(item, e -> new AchievementProvider(group).open(player)));
		}
	}
}
