package gg.projecteden.nexus.features.achievements.menu;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.achievement.Achievement;
import gg.projecteden.nexus.models.achievement.AchievementGroup;
import gg.projecteden.nexus.models.achievement.AchievementPlayer;
import gg.projecteden.nexus.models.achievement.AchievementService;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AchievementProvider implements InventoryProvider {
	private final AchievementPlayer achievementPlayer;
	private final AchievementGroup group;

	public AchievementProvider(Player player, AchievementGroup group) {
		this.achievementPlayer = new AchievementService().get(player);
		this.group = group;
	}

	public static void open(Player player) {
		SmartInventory inv = SmartInventory.builder()
				.provider(new AchievementGroupProvider())
				.rows((int) ((Math.ceil(AchievementGroup.values().length / 9)) + 2))
				.title("&3Achievements")
				.build();

		inv.open(player);
	}

	@Override
	public void init(Player player, InventoryContents contents) {

		contents.fillRow(0, ClickableItem.empty(new ItemStack(Material.AIR)));
		contents.set(0, 0, ClickableItem.of(new ItemStack(Material.BARRIER), e -> AchievementProvider.open(player)));

		for (Achievement achievement : Achievement.values()) {
			if (achievement.getGroup().equals(group)) {
				ItemStack itemStack = achievement.getItemStack();

				List<String> lore = new ArrayList<>();
				lore.add(ChatColor.YELLOW + achievement.getDescription());
				if (achievementPlayer.hasAchievement(achievement)) {
					itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);

					lore.add("");
					lore.add(ChatColor.DARK_GREEN + "Completed");
				}

				ItemMeta itemMeta = itemStack.getItemMeta();
				itemMeta.setDisplayName(ChatColor.GOLD + achievement.toString());
				itemMeta.setLore(lore);
				addHideFlags(itemMeta);

				itemStack.setItemMeta(itemMeta);

				contents.add(ClickableItem.empty(itemStack));
			}
		}

	}

	private void addHideFlags(ItemMeta itemMeta) {
		for (ItemFlag itemFlag : ItemFlag.values()) {
			itemMeta.addItemFlags(itemFlag);
		}
	}
}
