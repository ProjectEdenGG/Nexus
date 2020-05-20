package me.pugabyte.bncore.features.votes.mysterychest;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.SoundUtils;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MysteryChestProvider extends MenuUtils implements InventoryProvider {

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.fill(ClickableItem.empty(new ItemBuilder(
				Material.BLACK_STAINED_GLASS_PANE).name(" ").build()));
	}

	public static int time = 0;
	public static int colorIndex = 0;
	public static int lootIndex = 0;
	public static int speed = 4;

	@Override
	public void update(Player player, InventoryContents contents) {
		time++;
		if (time < 10) return;
		String[] colors = {"LIME", "LIGHT_BLUE", "RED", "MAGENTA", "PINK", "YELLOW", "ORANGE"};
		if (colorIndex == colors.length) colorIndex = 0;
		if (time % speed == 0 && time < 400) {
			SoundUtils.Jingle.PING.play(player);
			contents.fillRow(0, ClickableItem.empty(new ItemBuilder(
					Material.valueOf(colors[colorIndex] + "_STAINED_GLASS_PANE")).name(" ").build()));
			contents.fillRow(2, ClickableItem.empty(new ItemBuilder(
					Material.valueOf(colors[colorIndex] + "_STAINED_GLASS_PANE")).name(" ").build()));
			colorIndex++;
			if (lootIndex == MysteryChestLoot.values().length) lootIndex = 0;
			contents.fillRow(1, ClickableItem.NONE);
			MenuUtils.centerItems(Utils.EnumUtils.nextWithLoop(MysteryChestLoot.class, lootIndex).getLoot(),
					contents, 1, false);
			lootIndex++;
		}
		if (time == 250)
			speed = 10;
		if (speed == 300)
			speed = 15;
		if (time == 400) {
			if (lootIndex == MysteryChestLoot.values().length) lootIndex = 0;
			contents.fillRect(0, 0, 2, 8, ClickableItem.empty(new ItemBuilder(
					Material.LIME_STAINED_GLASS_PANE).name(" ").build()));
			contents.fillRow(1, ClickableItem.NONE);
			MenuUtils.centerItems(Utils.EnumUtils.nextWithLoop(MysteryChestLoot.class, lootIndex).getLoot(),
					contents, 1, false);
			SoundUtils.Jingle.RANKUP.play(player);
		}
		if (time == 500)
			player.closeInventory();
	}
}
