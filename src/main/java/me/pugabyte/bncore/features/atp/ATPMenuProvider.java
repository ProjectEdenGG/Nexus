package me.pugabyte.bncore.features.atp;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.warps.Warps;
import me.pugabyte.bncore.models.home.Home;
import me.pugabyte.bncore.models.home.HomeOwner;
import me.pugabyte.bncore.models.home.HomeService;
import me.pugabyte.bncore.models.warps.Warp;
import me.pugabyte.bncore.models.warps.WarpService;
import me.pugabyte.bncore.models.warps.WarpType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ATPMenuProvider extends MenuUtils implements InventoryProvider {

	WarpService service = new WarpService();

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.from(closeItem(), e -> player.closeInventory()));

		for (Warps.SurvivalWarp warp : Warps.SurvivalWarp.values()) {
			if (warp.name().equalsIgnoreCase("nether")) continue;
			contents.set(warp.getColumn(), warp.getRow(), ClickableItem.from(warp.getMenuItem(), e -> {
				Warp toWarp = service.get(warp.name().replace("_", ""), WarpType.ATP);
				new AnimalTeleportPens(player).confirm(player, toWarp.getLocation());
			}));
		}

		contents.set(2, 7, ClickableItem.from(nameItem(Material.OAK_SIGN, "&3Homes", "&eClick to teleport to||&3one of your homes."), e -> {
			SmartInventory INV = SmartInventory.builder()
					.title("ATP Homes")
					.size(6, 9)
					.provider(new ATPHomesMenuProvider())
					.build();
			INV.open(player);
		}));

	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}

	public class ATPHomesMenuProvider extends MenuUtils implements InventoryProvider {

		HomeService service = new HomeService();

		@Override
		public void init(Player player, InventoryContents contents) {
			HomeOwner owner = service.get(player.getUniqueId());

			contents.set(0, 0, ClickableItem.from(backItem(), e -> new ATPMenu().open(player)));

			int row = 1;
			int column = 1;
			for (Home home : owner.getHomes()) {
				contents.set(row, column, ClickableItem.from(home.getItem(), e -> {
					new AnimalTeleportPens(player).confirm(player, home.getLocation());
				}));

				if (column == 7) {
					column = 1;
					row++;
				} else {
					column++;
				}
			}

		}

		@Override
		public void update(Player player, InventoryContents inventoryContents) {

		}
	}

}
