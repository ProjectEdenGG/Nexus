package gg.projecteden.nexus.features.achievements.menu;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.achievement.Achievement;
import gg.projecteden.nexus.models.achievement.AchievementGroup;
import gg.projecteden.nexus.models.achievement.AchievementPlayer;
import gg.projecteden.nexus.models.achievement.AchievementService;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class AchievementProvider extends InventoryProvider {
	private final AchievementGroup group;

	public AchievementProvider(AchievementGroup group) {
		this.group = group;
	}

	@Override
	public String getTitle() {
		return group.toString();
	}

	@Override
	protected int getRows(Integer page) {
		return MenuUtils.calculateRows(group.getAchievements().size(), 2);
	}

	@Override
	public void init() {
		AchievementPlayer achievementPlayer = new AchievementService().get(viewer);
		contents.fillRow(0, ClickableItem.empty(new ItemStack(Material.AIR)));
		addBackItem(e -> new AchievementGroupProvider().open(viewer));

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
