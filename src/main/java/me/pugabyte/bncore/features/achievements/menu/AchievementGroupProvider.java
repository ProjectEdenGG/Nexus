package me.pugabyte.bncore.features.achievements.menu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.models.achievement.Achievement;
import me.pugabyte.bncore.models.achievement.AchievementGroup;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
public class AchievementGroupProvider implements InventoryProvider {

	public static void open(Player player, AchievementGroup group) {
		Set<Achievement> achievements = Arrays.stream(Achievement.values())
				.filter(ach -> ach.getGroup().equals(group))
				.collect(Collectors.toSet());

		SmartInventory inv = SmartInventory.builder()
				.provider(new AchievementProvider(player, group))
				.size((int) (Math.ceil(achievements.size() / 9) + 2), 9)
				.title(group.toString())
				.build();

		inv.open(player);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.fillRow(0, ClickableItem.empty(new ItemStack(Material.AIR)));
		contents.set(0, 0, ClickableItem.from(new ItemStack(Material.BARRIER), e -> contents.inventory().close(player)));

		for (AchievementGroup group : AchievementGroup.values()) {
			ItemStack itemStack = group.getItemStack();
			ItemMeta itemMeta = itemStack.getItemMeta();
			itemMeta.setDisplayName(ChatColor.YELLOW + group.toString());
			itemStack.setItemMeta(itemMeta);

			contents.add(ClickableItem.of(itemStack, e -> AchievementGroupProvider.open(player, group)));
		}
	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}
}