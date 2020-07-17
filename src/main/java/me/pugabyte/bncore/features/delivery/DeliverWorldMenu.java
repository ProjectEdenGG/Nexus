package me.pugabyte.bncore.features.delivery;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.delivery.Delivery;
import me.pugabyte.bncore.models.delivery.DeliveryService;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class DeliverWorldMenu extends MenuUtils implements InventoryProvider {
	private final DeliveryService service = new DeliveryService();
	private static List<ItemStack> items;

	public static SmartInventory getInv() {
		return SmartInventory.builder()
				.provider(new DeliverWorldMenu())
				.size(3, 9)
				.title(ChatColor.DARK_AQUA + "Choose World To Deliver To")
				.closeable(false)
				.build();
	}

	public void open(Player player, List<ItemStack> itemStacks) {
		items = itemStacks;
		getInv().open(player);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		Delivery delivery = service.get(player);
		ItemStack survival = new ItemBuilder(Material.GRASS_BLOCK).name("Survival").build();
		ItemStack skyblock = new ItemBuilder(Material.COBBLESTONE).name("Skyblock").lore("&cCurrently Disabled").build();

		contents.set(new SlotPos(1, 2), ClickableItem.from(survival, e -> {
			delivery.addToSurvival(items);
			service.save(delivery);
			getInv().close(player);
			player.sendMessage(DeliveryCommand.PREFIX + colorize("Your items have been delivered to &eSurvival"));
		}));

		contents.set(new SlotPos(1, 6), ClickableItem.empty(skyblock));

//		contents.set(new SlotPos(1, 6), ClickableItem.from(skyblock, e -> {
//			delivery.addToSkyblock(items);
//			service.save(delivery);
//			getInv().close(player);
//			player.sendMessage(DeliveryCommand.PREFIX + colorize("Your items have been delivered to &eSkyblock"));
//		}));
	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}
}
