package me.pugabyte.nexus.features.menus.mutemenu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.Getter;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.models.mutemenu.MuteMenu;
import me.pugabyte.nexus.models.mutemenu.MuteMenuService;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class MuteMenuProvider extends MenuUtils implements InventoryProvider {

	@Getter
	public enum MuteMenuItem {
		GLOBAL("Global and Discord", new ItemStack(Material.GREEN_WOOL)),
		LOCAL("Local Chat", new ItemStack(Material.YELLOW_WOOL)),
		AUTO("Automatic Broadcasts", new ItemStack(Material.REPEATER)),
		JQ("Join/Quit Messages", new ItemStack(Material.OAK_FENCE_GATE)),
		FIRSTJOIN("First Join Sounds", new ItemStack(Material.GOLD_BLOCK)),
		JQSOUND("Join/Quit Sounds", new ItemStack(Material.NOTE_BLOCK)),
		AFK("AFK Announcements", new ItemStack(Material.REDSTONE_LAMP)),
		MINIGAME_ANNOUNCEMENTS("Minigame Announcements", new ItemStack(Material.DIAMOND_SWORD));

		public String title;
		public ItemStack itemStack;

		MuteMenuItem(String title, ItemStack itemStack) {
			this.title = title;
			this.itemStack = itemStack;
		}
	}

	MuteMenuService service = new MuteMenuService();

	@Override
	public void init(Player player, InventoryContents inventoryContents) {
		MuteMenu settings = service.get(player.getUniqueId().toString());
		int row = 2;
		int column = 2;
		for (MuteMenuItem item : MuteMenuItem.values()) {
			ItemStack stack = nameItem(item.getItemStack(), "&e" + item.getTitle());
			if (settings.getUuid() != null)
				addGlowing(stack);
			inventoryContents.set(column, row, ClickableItem.empty(stack));
			if (row == 8) {
				row = 2;
				column++;
			} else
				row++;
		}
	}

	public void mute(Player player, MuteMenuItem item) {
		MuteMenu settings = service.get(player.getUniqueId().toString());
		if (Arrays.asList("GLOBAL", "LOCAL").contains(item.name())) {
			PlayerUtils.runCommand(player, "ch leave " + item.name().toLowerCase());
		}
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}
}
