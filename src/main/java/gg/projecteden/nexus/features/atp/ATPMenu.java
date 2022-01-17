package gg.projecteden.nexus.features.atp;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.warps.Warps.LegacySurvivalWarp;
import gg.projecteden.nexus.features.warps.Warps.SurvivalWarp;
import gg.projecteden.nexus.models.home.Home;
import gg.projecteden.nexus.models.home.HomeOwner;
import gg.projecteden.nexus.models.home.HomeService;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.models.warps.Warps.Warp;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.utils.StringUtils.colorize;

@NoArgsConstructor
public class ATPMenu extends MenuUtils implements InventoryProvider {
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
			for (LegacySurvivalWarp warp : LegacySurvivalWarp.values()) {
				if (warp.name().equalsIgnoreCase("nether")) continue;
				contents.set(warp.getColumn(), warp.getRow(), ClickableItem.from(warp.getMenuItem(), e -> {
					Warp toWarp = WarpType.ATP.get("legacy_" + warp.name().replace("_", ""));
					new AnimalTeleportPens(player).confirm(player, toWarp.getLocation());
				}));
			}

			ItemStack newWorld = nameItem(Material.GRASS, "&3Survival", "&eClick to view the||&enew Survival warps");
			contents.set(3, 7, ClickableItem.from(newWorld, e -> new ATPMenu(ATPGroup.SURVIVAL).open(player)));

		} else {
			for (SurvivalWarp warp : SurvivalWarp.values()) {
				if (warp.name().equalsIgnoreCase("nether")) continue;
				contents.set(warp.getColumn(), warp.getRow(), ClickableItem.from(warp.getMenuItem(), e -> {
					Warp toWarp = WarpType.ATP.get(warp.name().replace("_", ""));
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
