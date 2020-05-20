package me.pugabyte.bncore.features.votes.mysterychest;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;

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
		if (time % speed == 0 && time < 350) {
			SoundUtils.Jingle.PING.play(player);
			contents.fillRow(0, ClickableItem.empty(new ItemBuilder(
					Material.valueOf(colors[colorIndex] + "_STAINED_GLASS_PANE")).name(" ").build()));
			contents.fillRow(2, ClickableItem.empty(new ItemBuilder(
					Material.valueOf(colors[colorIndex] + "_STAINED_GLASS_PANE")).name(" ").build()));
			colorIndex++;
			if (lootIndex == MysteryChestLoot.values().length) lootIndex = 0;
			contents.fillRow(1, ClickableItem.NONE);
			MenuUtils.centerItems(Utils.EnumUtils.nextWithLoop(MysteryChestLoot.class, lootIndex).getMenuLoot(),
					contents, 1, true);
			lootIndex++;
		}
		if (time == 250)
			speed = 10;
		if (speed == 300)
			speed = 15;
		if (time == 350) {
			if (lootIndex == MysteryChestLoot.values().length) lootIndex = 0;
			contents.fillRect(0, 0, 2, 8, ClickableItem.empty(new ItemBuilder(
					Material.LIME_STAINED_GLASS_PANE).name(" ").build()));
			contents.fillRow(1, ClickableItem.NONE);
			MenuUtils.centerItems(Utils.EnumUtils.nextWithLoop(MysteryChestLoot.class, lootIndex).getMenuLoot(),
					contents, 1, true);
			Tasks.wait(10, () -> SoundUtils.Jingle.RANKUP.play(player));
		}
		if (time == 450) {
			MysteryChest.INV.close(player);
			player.sendMessage(StringUtils.colorize(
					StringUtils.getPrefix("MysteryChest") +
							"You have received the &e" + MysteryChestLoot.values()[lootIndex + 1].getName() + "&3 reward"
			));
			Utils.giveItems(player, Arrays.asList(MysteryChestLoot.values()[lootIndex + 1].getLoot()));
			SettingService service = new SettingService();
			Setting setting = service.get(player, "mysteryChest");
			setting.setValue("" + (Integer.parseInt(setting.getValue()) - 1));
			service.save(setting);
		}
	}
}
