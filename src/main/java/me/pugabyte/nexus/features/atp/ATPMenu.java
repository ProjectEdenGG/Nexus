package me.pugabyte.nexus.features.atp;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.warps.Warps;
import me.pugabyte.nexus.models.home.Home;
import me.pugabyte.nexus.models.home.HomeOwner;
import me.pugabyte.nexus.models.home.HomeService;
import me.pugabyte.nexus.models.warps.Warp;
import me.pugabyte.nexus.models.warps.WarpService;
import me.pugabyte.nexus.models.warps.WarpType;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.nexus.utils.StringUtils.camelCase;
import static me.pugabyte.nexus.utils.StringUtils.colorize;

@NoArgsConstructor
public class ATPMenu extends MenuUtils implements InventoryProvider {
	private final WarpService service = new WarpService();
	private ATPGroup group;

	public ATPMenu(ATPGroup group) {
		this.group = group;
	}

	public enum ATPGroup {
		SURVIVAL,
		LEGACY,
		RESOURCE
	}

	@Override
	public void open(Player player, int page) {
		SmartInventory.builder()
				.size(5, 9)
				.title(colorize("&3Animal Teleport Pens"))
				.provider(this)
				.build()
				.open(player);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.from(closeItem(), e -> player.closeInventory()));

		if (group.equals(ATPGroup.LEGACY)) {
			for (Warps.LegacySurvivalWarp warp : Warps.LegacySurvivalWarp.values()) {
				if (warp.name().equalsIgnoreCase("nether")) continue;
				contents.set(warp.getColumn(), warp.getRow(), ClickableItem.from(warp.getMenuItem(), e -> {
					Warp toWarp = service.get("legacy_" + warp.name().replace("_", ""), WarpType.ATP);
					new AnimalTeleportPens(player).confirm(player, toWarp.getLocation());
				}));
			}

			ItemStack newWorld = nameItem(Material.GRASS, "&3Survival", "&eClick to view the||&enew Survival warps");
			contents.set(3, 7, ClickableItem.from(newWorld, e -> new ATPMenu(ATPGroup.SURVIVAL).open(player)));

		} else {
			for (Warps.SurvivalWarp warp : Warps.SurvivalWarp.values()) {
				if (warp.name().equalsIgnoreCase("nether")) continue;
				contents.set(warp.getColumn(), warp.getRow(), ClickableItem.from(warp.getMenuItem(), e -> {
					Warp toWarp = service.get(warp.name().replace("_", ""), WarpType.ATP);
					new AnimalTeleportPens(player).confirm(player, toWarp.getLocation());
				}));
			}

			ItemStack legacy = nameItem(Material.MOSSY_COBBLESTONE, "&3Legacy World", "&eClick to view the||&ewarps of the legacy world");
			contents.set(3, 7, ClickableItem.from(legacy, e -> new ATPMenu(ATPGroup.LEGACY).open(player)));
		}

		contents.set(1, 7, ClickableItem.from(nameItem(Material.OAK_SIGN, "&3Homes", "&eClick to teleport to||&eone of your homes."), e -> {
			SmartInventory.builder()
					.title("ATP Homes")
					.size(6, 9)
					.provider(new ATPHomesMenuProvider())
					.build()
					.open(player);
		}));

	}

	public class ATPHomesMenuProvider extends MenuUtils implements InventoryProvider {

		HomeService service = new HomeService();

		@Override
		public void init(Player player, InventoryContents contents) {
			HomeOwner owner = service.get(player.getUniqueId());

			contents.set(0, 0, ClickableItem.from(backItem(), e -> new ATPMenu(group).open(player)));

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
				item.name("&f" + camelCase(home.getName()));

				contents.set(row, column, ClickableItem.from(item.build(), e ->
						new AnimalTeleportPens(player).confirm(player, home.getLocation())));

				if (column == 8) {
					column = 0;
					row++;
				} else {
					column++;
				}
			}

		}
	}

}
