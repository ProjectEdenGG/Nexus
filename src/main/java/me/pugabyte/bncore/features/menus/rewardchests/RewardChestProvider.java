package me.pugabyte.bncore.features.menus.rewardchests;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.RandomUtils;
import me.pugabyte.bncore.utils.SoundUtils;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static me.pugabyte.bncore.utils.ColorType.LIGHT_BLUE;
import static me.pugabyte.bncore.utils.ColorType.LIGHT_GREEN;
import static me.pugabyte.bncore.utils.ColorType.MAGENTA;
import static me.pugabyte.bncore.utils.ColorType.ORANGE;
import static me.pugabyte.bncore.utils.ColorType.PINK;
import static me.pugabyte.bncore.utils.ColorType.RED;
import static me.pugabyte.bncore.utils.ColorType.YELLOW;

public class RewardChestProvider extends MenuUtils implements InventoryProvider {


	public RewardChestLoot[] loot;
	public ClickableItem[][] menuItems;

	public int time;
	public int colorIndex = 0;
	public int lootIndex;
	public int speed = 0;

	public RewardChestProvider(RewardChestLoot... loot) {
		this.loot = loot;
		menuItems = new ClickableItem[loot.length][];
		for (int i = 0; i < loot.length; i++) {
			menuItems[i] = new ClickableItem[loot[i].getItems().length];
			for (int j = 0; j < loot[i].getItems().length; j++)
				menuItems[i][j] = ClickableItem.empty(loot[i].getItems()[j]);
		}
		time = 0;
		lootIndex = RandomUtils.randomInt(0, loot.length);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.fill(ClickableItem.empty(new ItemBuilder(
				Material.BLACK_STAINED_GLASS_PANE).name(" ").build()));
	}

	@Override
	public void update(Player player, InventoryContents contents) {
		time++;
		if (time < 10) return;
		speed = (int) ((1 / 20.0) * time - 5 / 2.0);
		if (speed < 8) speed = 4;
		ColorType[] colors = {LIGHT_GREEN, LIGHT_BLUE, RED, MAGENTA, PINK, YELLOW, ORANGE};
		if (colorIndex == colors.length) colorIndex = 0;

		if (time % speed == 0 && time < 350) {
			player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 1);
			contents.fillRow(0, ClickableItem.empty(new ItemBuilder(colors[colorIndex].getStainedGlassPane()).name(" ").build()));
			contents.fillRow(2, ClickableItem.empty(new ItemBuilder(colors[colorIndex].getStainedGlassPane()).name(" ").build()));
			colorIndex++;
			if (lootIndex == menuItems.length) lootIndex = 0;
			contents.fillRow(1, ClickableItem.NONE);
			MenuUtils.centerItems(menuItems[lootIndex], contents, 1, true);
			lootIndex++;
		}

		if (time == 350) {
			if (lootIndex == menuItems.length) lootIndex = 0;
			contents.fillRect(0, 0, 2, 8, ClickableItem.empty(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name(" ").build()));
			contents.fillRow(1, ClickableItem.NONE);
			MenuUtils.centerItems(menuItems[lootIndex], contents, 1, true);
			Tasks.wait(10, () -> SoundUtils.Jingle.RANKUP.play(player));
		}

		if (time >= 450) {
			RewardChest.getInv(loot).close(player);
			player.sendMessage(StringUtils.colorize(StringUtils.getPrefix("RewardChest") + "You have received the &e" +
					loot[lootIndex].getTitle() + "&3 reward"));
			Utils.giveItems(player, Arrays.asList(loot[lootIndex].getItems()));
		}
	}
}
