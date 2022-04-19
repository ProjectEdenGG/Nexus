package gg.projecteden.nexus.features.warps.providers;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.shops.providers.MainMenuProvider;
import gg.projecteden.nexus.features.warps.WarpMenu;
import gg.projecteden.nexus.features.warps.Warps;
import gg.projecteden.nexus.features.warps.WarpsMenu;
import gg.projecteden.nexus.models.buildcontest.BuildContest;
import gg.projecteden.nexus.models.buildcontest.BuildContestService;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class WarpsMenuProvider extends MenuUtils implements InventoryProvider {
	private final WarpMenu menu;

	public WarpsMenuProvider(WarpMenu menu) {
		this.menu = menu;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		switch (menu) {
			case MAIN -> addCloseItem(contents);
			case SURVIVAL, LEGACY, MINIGAMES, OTHER -> contents.set(0, 0, ClickableItem.of(backItem(), e -> WarpsMenu.open(player, WarpMenu.MAIN)));
			case BUILD_CONTESTS -> contents.set(0, 0, ClickableItem.of(backItem(), e -> WarpsMenu.open(player, WarpMenu.OTHER)));
		}

		switch (menu) {
			case MAIN -> {
				ItemBuilder survival = new ItemBuilder(Material.GRASS_BLOCK).name("&3Survival");
				ItemBuilder minigames = new ItemBuilder(Material.DIAMOND_SWORD).name("&3Minigames");
				ItemBuilder creative = new ItemBuilder(Material.QUARTZ).name("&3Creative");
				ItemBuilder skyblock = new ItemBuilder(Material.COBBLESTONE).name("&3One Block");
				ItemBuilder other = new ItemBuilder(Material.EMERALD).name("&3Other");
				contents.set(1, 1, ClickableItem.of(survival, e -> {
					if (player.getWorld().getName().matches("world(_nether|the_end|)"))
						WarpsMenu.open(player, WarpMenu.LEGACY);
					else
						WarpsMenu.open(player, WarpMenu.SURVIVAL);
				}));
				contents.set(1, 3, ClickableItem.of(minigames, e -> WarpsMenu.open(player, WarpMenu.MINIGAMES)));
				contents.set(1, 5, ClickableItem.of(creative, e -> warp(player, "creative")));
				contents.set(1, 7, ClickableItem.of(skyblock, e -> command(player, "ob")));
				contents.set(2, 4, ClickableItem.of(other, e -> WarpsMenu.open(player, WarpMenu.OTHER)));
				BuildContest buildContest = new BuildContestService().get0();
				if (buildContest.isActive() && buildContest.getItemStack() != null)
					contents.set(4, 4, ClickableItem.of(buildContest.getItemStack(), e -> warp(player, "buildcontest")));
			}
			case SURVIVAL -> {
				for (Warps.SurvivalWarp warp : Warps.SurvivalWarp.values()) {
					contents.set(warp.getColumn(), warp.getRow(), ClickableItem.of(new ItemBuilder(warp.getItemStack())
						.name("&3" + warp.getDisplayName())
						.lore("&eClick to go to the " + warp.getDisplayName() + " warp"),
						e -> WarpType.NORMAL.get(warp.name().replace("_", "")).teleportAsync(player)));
				}
				ItemBuilder shops = new ItemBuilder(Material.EMERALD).name("&3Shops").lore("&eThis will open", "&ethe shop menu");
				ItemBuilder resource = new ItemBuilder(Material.DIAMOND_PICKAXE).name("&3Resource").lore("&eClick to teleport to the resource world");
				ItemBuilder legacy = new ItemBuilder(Material.MOSSY_COBBLESTONE).name("&3Legacy").lore("&eClick to view legacy world warps");
				contents.set(1, 7, ClickableItem.of(shops, e -> new MainMenuProvider(null).open(player)));
				contents.set(2, 7, ClickableItem.of(resource, e -> WarpType.NORMAL.get("resource").teleportAsync(player)));
				contents.set(3, 7, ClickableItem.of(legacy, e -> WarpsMenu.open(player, WarpMenu.LEGACY)));
				contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.BOOK).name("&3Info").lore("&eThese are the " +
						"survival world warps.").lore("&eThey are spread out across the entire world.").loreize(false)));
			}
			case LEGACY -> {
				for (Warps.LegacySurvivalWarp warp : Warps.LegacySurvivalWarp.values()) {
					contents.set(warp.getColumn(), warp.getRow(), ClickableItem.of(warp.getItemStack(), "&3" + warp.getDisplayName(), "&eClick to go to the " + warp.getDisplayName() + " warp", e ->
						WarpType.NORMAL.get("legacy_" + warp.name().replace("_", "")).teleportAsync(player)));
				}
				ItemBuilder shops2 = new ItemBuilder(Material.EMERALD).name("&3Shops").lore("&eThis will open", "&ethe shop menu");
				ItemBuilder newWorld = new ItemBuilder(Material.GRASS_BLOCK).name("&3Survival").lore("&eClick to view the survival world warps");
				contents.set(1, 7, ClickableItem.of(shops2, e -> new MainMenuProvider(null).open(player)));
				contents.set(3, 7, ClickableItem.of(newWorld, e -> WarpsMenu.open(player, WarpMenu.SURVIVAL)));
				contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.BOOK).name("&3Info").lore("&eThese are the " +
						"legacy survival world warps.").lore("&eThey are spread out across the entire world.").loreize(false).build()));
			}
			case MINIGAMES -> {
				ItemBuilder parkour = new ItemBuilder(Material.IRON_BOOTS).name("&3Parkour");
				ItemBuilder lobby = new ItemBuilder(Material.DIAMOND_SWORD).name("&3Minigame Lobby");
				ItemBuilder arcade = new ItemBuilder(Material.LEVER).name("&3Arcade");
				contents.set(1, 4, ClickableItem.of(lobby, e -> warp(player, "minigames")));
				contents.set(1, 2, ClickableItem.of(parkour, e -> warp(player, "parkour")));
				contents.set(1, 6, ClickableItem.of(arcade, e -> warp(player, "arcade")));
			}
			case OTHER -> {
				ItemBuilder leaderboards = new ItemBuilder(Material.QUARTZ_STAIRS).name("&3Podiums");
				ItemBuilder staffhall = new ItemBuilder(Material.LIGHT_BLUE_CONCRETE).name("&3Current Staff");
				ItemBuilder hoh = new ItemBuilder(Material.BEACON).name("&3Hall of History");
				ItemBuilder wog = new ItemBuilder(Material.OAK_SIGN).name("&3Walls of Grace");
				ItemBuilder banners = new ItemBuilder(Material.CYAN_BANNER).name("&3Banners");
				ItemBuilder storetesting = new ItemBuilder(Material.GOLD_INGOT).name("&3Store Gallery");
				ItemBuilder walkthrough = new ItemBuilder(Material.NETHER_STAR).name("&3Two Year Anniversary").lore("&e&lHistory Walkthrough", "&eCelebrating 2 years", "&eof Project Eden");
				ItemBuilder bearfair = new ItemBuilder(Material.FIREWORK_ROCKET).name("&3Six Year Anniversary").lore("&e&lBear Fair", "&eCelebrating 6 years", "&eof Project Eden");
				ItemBuilder buildcontests = new ItemBuilder(Material.CHEST).name("&3Past Build Contests");
				contents.set(1, 1, ClickableItem.of(leaderboards, e -> warp(player, "podiums")));
				contents.set(1, 3, ClickableItem.of(staffhall, e -> warp(player, "staffhall")));
				contents.set(1, 5, ClickableItem.of(hoh, e -> command(player, "hallofhistory")));
				contents.set(1, 7, ClickableItem.of(wog, e -> command(player, "wog")));
				contents.set(2, 2, ClickableItem.of(banners, e -> warp(player, "banners")));
				contents.set(2, 4, ClickableItem.of(storetesting, e -> warp(player, "store")));
				contents.set(2, 6, ClickableItem.of(buildcontests, e -> WarpsMenu.open(player, WarpMenu.BUILD_CONTESTS)));
				contents.set(3, 3, ClickableItem.of(walkthrough, e -> warp(player, "2y")));
				contents.set(3, 5, ClickableItem.of(bearfair, e -> command(player, "bearfair21")));
			}
			case BUILD_CONTESTS -> {
				ItemBuilder contest0 = new ItemBuilder(Material.JACK_O_LANTERN).name("&3Halloween - 2015");
				ItemBuilder contest1 = new ItemBuilder(Material.COARSE_DIRT).name("&3Dwarven Cities - 2016");
				ItemBuilder contest2 = new ItemBuilder(Material.JACK_O_LANTERN).name("&3Halloween - 2016");
				ItemBuilder contest3 = new ItemBuilder(Material.OBSIDIAN).name("&3Space - 2016");
				ItemBuilder contest4 = new ItemBuilder(Material.BRICKS).name("&3World Cultures - 2018");
				ItemBuilder contest5 = new ItemBuilder(Material.PINK_WOOL).name("&3Celebration - 2018");
				ItemBuilder contest6 = new ItemBuilder(Material.PINK_WOOL).name("&3A Day at the Beach - 2019");
				ItemBuilder contest7 = new ItemBuilder(Material.PINK_WOOL).name("&3Valentine''s Day - 2019");
				ItemBuilder contest8 = new ItemBuilder(Material.PINK_WOOL).name("&3Pirates - 2020");
				ItemBuilder contest9 = new ItemBuilder(Material.PINK_WOOL).name("&3Pastry - 2021");
				contents.set(1, 0, ClickableItem.of(contest0, e -> warp(player, "buildcontest0")));
				contents.set(1, 1, ClickableItem.of(contest1, e -> warp(player, "buildcontest1")));
				contents.set(1, 2, ClickableItem.of(contest2, e -> warp(player, "buildcontest2")));
				contents.set(1, 3, ClickableItem.of(contest3, e -> warp(player, "buildcontest3")));
				contents.set(1, 4, ClickableItem.of(contest4, e -> warp(player, "buildcontest4")));
				contents.set(1, 5, ClickableItem.of(contest5, e -> warp(player, "buildcontest5")));
				contents.set(1, 6, ClickableItem.of(contest6, e -> warp(player, "buildcontest6")));
				contents.set(1, 7, ClickableItem.of(contest7, e -> warp(player, "buildcontest7")));
				contents.set(1, 8, ClickableItem.of(contest8, e -> warp(player, "buildcontest8")));
				contents.set(2, 0, ClickableItem.of(contest9, e -> warp(player, "buildcontest9")));
			}
		}
	}

}
