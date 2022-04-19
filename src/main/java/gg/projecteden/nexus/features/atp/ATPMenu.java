package gg.projecteden.nexus.features.atp;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
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

import static gg.projecteden.nexus.utils.StringUtils.camelCase;

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
				.rows(5)
				.title("&3Animal Teleport Pens")
				.provider(this)
				.build()
				.open(player);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.of(closeItem(), e -> player.closeInventory()));

		if (group.equals(ATPGroup.LEGACY)) {
			for (LegacySurvivalWarp warp : LegacySurvivalWarp.values()) {
				if (warp.name().equalsIgnoreCase("nether")) continue;
				contents.set(warp.getColumn(), warp.getRow(), ClickableItem.of(warp.getMenuItem(), e -> {
					Warp toWarp = WarpType.ATP.get("legacy_" + warp.name().replace("_", ""));
					new AnimalTeleportPens(player).confirm(player, toWarp.getLocation());
				}));
			}

			ItemBuilder newWorld = new ItemBuilder(Material.GRASS).name("&3Survival").lore("&eClick to view the", "&enew Survival warps");
			contents.set(3, 7, ClickableItem.of(newWorld, e -> new ATPMenu(ATPGroup.SURVIVAL).open(player)));

		} else {
			for (SurvivalWarp warp : SurvivalWarp.values()) {
				if (warp.name().equalsIgnoreCase("nether")) continue;
				contents.set(warp.getColumn(), warp.getRow(), ClickableItem.of(warp.getMenuItem(), e -> {
					Warp toWarp = WarpType.ATP.get(warp.name().replace("_", ""));
					new AnimalTeleportPens(player).confirm(player, toWarp.getLocation());
				}));
			}

			ItemBuilder legacy = new ItemBuilder(Material.MOSSY_COBBLESTONE).name("&3Legacy World").lore("&eClick to view the", "&ewarps of the legacy world");
			contents.set(3, 7, ClickableItem.of(legacy, e -> new ATPMenu(ATPGroup.LEGACY).open(player)));
		}

		contents.set(1, 7, ClickableItem.of(new ItemBuilder(Material.OAK_SIGN).name("&3Homes").lore("&eClick to teleport to", "&eone of your homes."), e -> {
			SmartInventory.builder()
				.title("ATP Homes")
				.maxSize()
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

			contents.set(0, 0, ClickableItem.of(backItem(), e -> new ATPMenu(group).open(player)));

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

				contents.set(row, column, ClickableItem.of(item.build(), e ->
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
