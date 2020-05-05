package me.pugabyte.bncore.features.atp;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.warps.Warps;
import me.pugabyte.bncore.models.home.Home;
import me.pugabyte.bncore.models.home.HomeOwner;
import me.pugabyte.bncore.models.home.HomeService;
import me.pugabyte.bncore.models.warps.Warp;
import me.pugabyte.bncore.models.warps.WarpService;
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor
public class ATPMenuProvider extends MenuUtils implements InventoryProvider {

	WarpService service = new WarpService();

	WarpType type = WarpType.ATP;

	public ATPMenuProvider(WarpType type) {
		this.type = type;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.from(closeItem(), e -> player.closeInventory()));

		if (type.equals(WarpType.LEGACY_ATP)) {
			for (Warps.LegacySurvivalWarp warp : Warps.LegacySurvivalWarp.values()) {
				if (warp.name().equalsIgnoreCase("nether")) continue;
				contents.set(warp.getColumn(), warp.getRow(), ClickableItem.from(warp.getMenuItem(), e -> {
					Warp toWarp = service.get(warp.name().replace("_", ""), WarpType.LEGACY_ATP);
					new AnimalTeleportPens(player).confirm(player, toWarp.getLocation());
				}));
			}

			ItemStack newWorld = nameItem(Material.GRASS, "&3Survival", "&eClick to view the||&enew Survival warps");
			contents.set(3, 7, ClickableItem.from(newWorld, e -> new ATPMenu().open(player)));

		} else {
			for (Warps.SurvivalWarp warp : Warps.SurvivalWarp.values()) {
				if (warp.name().equalsIgnoreCase("nether")) continue;
				contents.set(warp.getColumn(), warp.getRow(), ClickableItem.from(warp.getMenuItem(), e -> {
					Warp toWarp = service.get(warp.name().replace("_", ""), WarpType.ATP);
					new AnimalTeleportPens(player).confirm(player, toWarp.getLocation());
				}));
			}

			ItemStack legacy = nameItem(Material.MOSSY_COBBLESTONE, "&3Legacy World", "&eClick to view the||warps of the legacy world");
			contents.set(3, 7, ClickableItem.from(legacy, e -> new ATPMenu().openLegacy(player)));
		}

		contents.set(1, 7, ClickableItem.from(nameItem(Material.OAK_SIGN, "&3Homes", "&eClick to teleport to||&3one of your homes."), e -> {
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
			int column = 0;
			for (Home home : owner.getHomes()) {
				ItemBuilder item;
				if (home.getItem() != null && home.getItem().getItemMeta() != null)
					item = new ItemBuilder(home.getItem());
				else if (home.isLocked())
					item = new ItemBuilder(Material.RED_CONCRETE);
				else
					item = new ItemBuilder(Material.LIME_CONCRETE);

				contents.set(row, column, ClickableItem.from(item.build(), e ->
						new AnimalTeleportPens(player).confirm(player, home.getLocation())));

				if (column == 8) {
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
