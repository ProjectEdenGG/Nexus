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
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

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
		ItemStack survival = new ItemBuilder(Material.GRASS_BLOCK).name("&3Survival").build();
		ItemStack skyblock = new ItemBuilder(Material.COBBLESTONE).name("&3Skyblock").lore("&cCurrently Disabled").build();

		contents.set(new SlotPos(1, 2), ClickableItem.from(survival, e -> {
			delivery.addToSurvival(items);
			Tasks.async(() -> {
//				Utils.send(player, "Size1: " + delivery.getSurvivalItems().size());
//				Utils.send(player, stripColor(delivery.getSurvivalItems().toString());
				service.deleteSync(delivery);

//				Utils.send(player, "\nSize1.5: " + delivery.getSurvivalItems().size());
//				Utils.send(player, stripColor(delivery.getSurvivalItems().toString());
				service.saveSync(delivery);

				Delivery delivery1 = service.get(player);
//				Utils.send(player, "\nSize2: " + delivery1.getSurvivalItems().size());
//				Utils.send(player, stripColor(delivery1.getSurvivalItems().toString());
			});

			getInv().close(player);
			Utils.send(player, DeliveryCommand.PREFIX + "Your items have been delivered to &eSurvival");
		}));

		contents.set(new SlotPos(1, 6), ClickableItem.empty(skyblock));

//		contents.set(new SlotPos(1, 6), ClickableItem.from(skyblock, e -> {
//			delivery.addToSkyblock(items);
//			service.delete(delivery);
//			service.save(delivery);
//
//			getInv().close(player);
//			Utils.send(player, DeliveryCommand.PREFIX + "Your items have been delivered to &eSkyblock");
//		}));
	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}
}
