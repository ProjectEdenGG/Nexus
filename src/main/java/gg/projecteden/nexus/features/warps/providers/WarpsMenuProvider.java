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
import gg.projecteden.nexus.models.warps.WarpsService;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

		WarpsService warpsService = new WarpsService();

		switch (menu) {
			case MAIN -> {
				ItemStack survival = nameItem(Material.GRASS_BLOCK, "&3Survival");
				ItemStack minigames = nameItem(Material.DIAMOND_SWORD, "&3Minigames");
				ItemStack creative = nameItem(Material.QUARTZ, "&3Creative");
				ItemStack skyblock = nameItem(Material.COBBLESTONE, "&3One Block");
				ItemStack other = nameItem(Material.EMERALD, "&3Other");
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
					contents.set(warp.getColumn(), warp.getRow(), ClickableItem.of(nameItem(warp.getItemStack(), "&3" + warp.getDisplayName(), "&eClick to go to the " + warp.getDisplayName() + " warp"), e -> {
						WarpType.NORMAL.get(warp.name().replace("_", "")).teleportAsync(player);
					}));
				}
				ItemStack shops = nameItem(Material.EMERALD, "&3Shops", "&eThis will open||&ethe shop menu");
				ItemStack resource = nameItem(Material.DIAMOND_PICKAXE, "&3Resource", "&eClick to teleport to the resource world");
				ItemStack legacy = nameItem(Material.MOSSY_COBBLESTONE, "&3Legacy", "&eClick to view legacy world warps");
				contents.set(1, 7, ClickableItem.of(shops, e -> new MainMenuProvider(null).open(player)));
				contents.set(2, 7, ClickableItem.of(resource, e -> WarpType.NORMAL.get("resource").teleportAsync(player)));
				contents.set(3, 7, ClickableItem.of(legacy, e -> WarpsMenu.open(player, WarpMenu.LEGACY)));
				contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.BOOK).name("&3Info").lore("&eThese are the " +
						"survival world warps.").lore("&eThey are spread out across the entire world.").loreize(false).build()));
			}
			case LEGACY -> {
				for (Warps.LegacySurvivalWarp warp : Warps.LegacySurvivalWarp.values()) {
					contents.set(warp.getColumn(), warp.getRow(), ClickableItem.of(nameItem(warp.getItemStack(), "&3" + warp.getDisplayName(), "&eClick to go to the " + warp.getDisplayName() + " warp"), e -> {
						WarpType.NORMAL.get("legacy_" + warp.name().replace("_", "")).teleportAsync(player);
					}));
				}
				ItemStack shops2 = nameItem(Material.EMERALD, "&3Shops", "&eThis will open||&ethe shop menu");
				ItemStack newWorld = nameItem(Material.GRASS_BLOCK, "&3Survival", "&eClick to view the survival world warps");
				contents.set(1, 7, ClickableItem.of(shops2, e -> new MainMenuProvider(null).open(player)));
				contents.set(3, 7, ClickableItem.of(newWorld, e -> WarpsMenu.open(player, WarpMenu.SURVIVAL)));
				contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.BOOK).name("&3Info").lore("&eThese are the " +
						"legacy survival world warps.").lore("&eThey are spread out across the entire world.").loreize(false).build()));
			}
			case MINIGAMES -> {
				ItemStack parkour = nameItem(Material.IRON_BOOTS, "&3Parkour");
				ItemStack lobby = nameItem(Material.DIAMOND_SWORD, "&3Minigame Lobby");
				ItemStack arcade = nameItem(Material.LEVER, "&3Arcade");
				contents.set(1, 4, ClickableItem.of(lobby, e -> warp(player, "minigames")));
				contents.set(1, 2, ClickableItem.of(parkour, e -> warp(player, "parkour")));
				contents.set(1, 6, ClickableItem.of(arcade, e -> warp(player, "arcade")));
			}
			case OTHER -> {
				ItemStack leaderboards = nameItem(Material.QUARTZ_STAIRS, "&3Podiums");
				ItemStack staffhall = nameItem(Material.LIGHT_BLUE_CONCRETE, "&3Current Staff");
				ItemStack hoh = nameItem(Material.BEACON, "&3Hall of History");
				ItemStack wog = nameItem(Material.OAK_SIGN, "&3Walls of Grace");
				ItemStack banners = nameItem(Material.CYAN_BANNER, "&3Banners");
				ItemStack storetesting = nameItem(Material.GOLD_INGOT, "&3Store Gallery");
				ItemStack walkthrough = nameItem(Material.NETHER_STAR, "&3Two Year Anniversary", "&e&lHistory Walkthrough||&eCelebrating 2 years||&eof Project Eden");
				ItemStack bearfair = nameItem(Material.FIREWORK_ROCKET, "&3Six Year Anniversary", "&e&lBear Fair||&eCelebrating 6 years||&eof Project Eden");
				ItemStack buildcontests = nameItem(Material.CHEST, "&3Past Build Contests");
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
				ItemStack contest0 = nameItem(Material.JACK_O_LANTERN, "&3Halloween - 2015");
				ItemStack contest1 = nameItem(Material.COARSE_DIRT, "&3Dwarven Cities - 2016");
				ItemStack contest2 = nameItem(Material.JACK_O_LANTERN, "&3Halloween - 2016");
				ItemStack contest3 = nameItem(Material.OBSIDIAN, "&3Space - 2016");
				ItemStack contest4 = nameItem(Material.BRICKS, "&3World Cultures - 2018");
				ItemStack contest5 = nameItem(Material.PINK_WOOL, "&3Celebration - 2018");
				ItemStack contest6 = nameItem(Material.PINK_WOOL, "&3A Day at the Beach - 2019");
				ItemStack contest7 = nameItem(Material.PINK_WOOL, "&3Valentine''s Day - 2019");
				ItemStack contest8 = nameItem(Material.PINK_WOOL, "&3Pirates - 2020");
				ItemStack contest9 = nameItem(Material.PINK_WOOL, "&3Pastry - 2021");
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
