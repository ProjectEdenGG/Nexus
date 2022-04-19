package gg.projecteden.nexus.features.achievements.menu;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.achievement.Achievement;
import gg.projecteden.nexus.models.achievement.AchievementGroup;
import gg.projecteden.nexus.models.achievement.AchievementPlayer;
import gg.projecteden.nexus.models.achievement.AchievementService;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.features.menus.MenuUtils.getRows;

public class AchievementProvider extends InventoryProvider {
	private final AchievementGroup group;

	public AchievementProvider(AchievementGroup group) {
		this.group = group;
	}

	public void open(Player player, int page) {
		SmartInventory.builder()
			.provider(this)
			.rows(getRows(group.getAchievements().size(), 2))
			.title(group.toString())
			.build()
			.open(player);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		AchievementPlayer achievementPlayer = new AchievementService().get(player);
		contents.fillRow(0, ClickableItem.empty(new ItemStack(Material.AIR)));
		contents.set(0, 0, ClickableItem.of(new ItemStack(Material.BARRIER), e -> new AchievementGroupProvider().open(player)));

		for (Achievement achievement : Achievement.values()) {
			if (achievement.getGroup().equals(group)) {
				ItemBuilder item = new ItemBuilder(achievement.getItemStack())
					.name("&6" + achievement)
					.lore("&e" + achievement.getDescription())
					.itemFlags(ItemFlag.values());

				if (achievementPlayer.hasAchievement(achievement))
					item.glow().lore("", "&3Completed");

				contents.add(ClickableItem.empty(item));
			}
		}
	}

}
