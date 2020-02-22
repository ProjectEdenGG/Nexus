package me.pugabyte.bncore.features.warps.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.warps.WarpMenu;
import me.pugabyte.bncore.features.warps.WarpsMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WarpsMenuProvider extends MenuUtils implements InventoryProvider {
	private WarpMenu menu;

	public WarpsMenuProvider(WarpMenu menu) {
		this.menu = menu;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		switch (menu) {
			case MAIN:
				contents.set(0, 0, ClickableItem.from(closeItem(), e -> contents.inventory().close(player)));
				break;
			case SURVIVAL:
			case MINIGAMES:
//			case CREATIVE:
//			case SKYBLOCK:
			case OTHER:
				contents.set(0, 0, ClickableItem.from(backItem(), e -> WarpsMenu.open(player, WarpMenu.MAIN)));
				break;
			case BUILD_CONTESTS:
				contents.set(0, 0, ClickableItem.from(backItem(), e -> WarpsMenu.open(player, WarpMenu.OTHER)));
				break;
		}

		switch (menu) {
			case MAIN:
				ItemStack survival = nameItem(Material.GRASS, "&3Survival");
				ItemStack minigames = nameItem(Material.DIAMOND_SWORD, "&3Minigames");
				ItemStack creative = nameItem(Material.QUARTZ, "&3Creative");
				ItemStack skyblock = nameItem(Material.COBBLESTONE, "&3Skyblock");
				ItemStack other = nameItem(Material.EMERALD, "&3Other");

				contents.set(1, 1, ClickableItem.from(survival, e -> WarpsMenu.open(player, WarpMenu.SURVIVAL)));
				contents.set(1, 3, ClickableItem.from(minigames, e -> WarpsMenu.open(player, WarpMenu.MINIGAMES)));
				contents.set(1, 5, ClickableItem.from(creative, e -> warp(player, "creative")));
				contents.set(1, 7, ClickableItem.from(skyblock, e -> warp(player, "skyblock")));
				contents.set(2, 4, ClickableItem.from(other, e -> WarpsMenu.open(player, WarpMenu.OTHER)));
				break;

			case SURVIVAL:
//				ItemStack northwest2 = nameItem(Material.JUNGLE_LOG, "&3Northwest #2");
//				ItemStack north2 = nameItem(Material.OAK_PLANKS, "&3North #2");
//				ItemStack northeast2 = nameItem(Material.GRAY_TERRACOTTA, "&3Northeast #2");

//				ItemStack northwest = nameItem(Material.PODZOL, "&3Northwest");
				ItemStack north = nameItem(Material.BONE_BLOCK, "&3North");
				ItemStack northeast = nameItem(Material.COBBLESTONE_STAIRS, "&3Northeast");

//				ItemStack west2 = nameItem(Material.ACACIA_PLANKS, "&3West #2");
//				ItemStack west = nameItem(Material.SPRUCE_LOG, "&3West");
//				ItemStack spawn = nameItem(Material.CHISELED_STONE_BRICKS, "&3Spawn");
				ItemStack east = nameItem(Material.ICE, "&3East");
//				ItemStack east2 = nameItem(Material.OAK_LOG, "&3East #2");

				ItemStack southwest = nameItem(Material.HAY_BLOCK, "&3Southwest");
//				ItemStack south = nameItem(Material.GRAY_WOOL, "&3South");
//				ItemStack southeast = nameItem(Material.BIRCH_PLANKS, "&3Southeast");

				ItemStack southwest2 = nameItem(Material.SAND, "&3Southwest #2");
//				ItemStack south2 = nameItem(Material.STONE_BRICKS, "&3South #2");
				ItemStack southeast2 = nameItem(Material.SNOW_BLOCK, "&3Southeast #2");

				ItemStack nether = nameItem(Material.NETHERRACK, "&3Nether");

				ItemStack shub = nameItem(Material.EMERALD, "&3Shops Hub", "&eLearn all about||&eBear Nation's economy");
				ItemStack market = nameItem(Material.SIGN, "&3Market", "&eWhere you'll find all||&ethe 'bear' neccessities.");
//				ItemStack shops = nameItem(Material.PLAYER_HEAD, "&3Player Shops", "&eFind more items at||&echeaper prices");

//				contents.set(0, 1, ClickableItem.from(northwest2, e -> warp(player, "northwest2")));
//				contents.set(0, 3, ClickableItem.from(north2, e -> warp(player, "north2")));
//				contents.set(0, 5, ClickableItem.from(northeast2, e -> warp(player, "northeast2")));
//
//				contents.set(1, 2, ClickableItem.from(northwest, e -> warp(player, "northwest")));
//				contents.set(1, 3, ClickableItem.from(north, e -> warp(player, "north")));
//				contents.set(1, 4, ClickableItem.from(northeast, e -> warp(player, "northeast")));
//
//				contents.set(2, 1, ClickableItem.from(west2, e -> warp(player, "west2")));
//				contents.set(2, 2, ClickableItem.from(west, e -> warp(player, "west")));
//				contents.set(2, 3, ClickableItem.from(spawn, e -> warp(player, "spawn")));
//				contents.set(2, 4, ClickableItem.from(east, e -> warp(player, "east")));
//				contents.set(2, 5, ClickableItem.from(east2, e -> warp(player, "east2")));
//
//				contents.set(3, 2, ClickableItem.from(southwest, e -> warp(player, "southwest")));
//				contents.set(3, 3, ClickableItem.from(south, e -> warp(player, "south")));
//				contents.set(3, 4, ClickableItem.from(southeast, e -> warp(player, "southeast")));
//
//				contents.set(4, 1, ClickableItem.from(southwest2, e -> warp(player, "southwest2")));
//				contents.set(4, 3, ClickableItem.from(south2, e -> warp(player, "south2")));
//				contents.set(4, 5, ClickableItem.from(southeast2, e -> warp(player, "southeast2")));

				contents.set(4, 0, ClickableItem.from(nether, e -> warp(player, "nether")));

				contents.set(1, 7, ClickableItem.from(shub, e -> warp(player, "shub")));
				contents.set(2, 7, ClickableItem.from(market, e -> warp(player, "market")));
//				contents.set(3, 7, ClickableItem.from(shops, e -> warp(player, "northeast2")));
				break;

			case MINIGAMES:
				ItemStack lobby = nameItem(Material.DIAMOND_SWORD, "&3Minigame Lobby");
				ItemStack spvp = nameItem(Material.IRON_AXE, "&3Survival PVP Arena");
//				ItemStack wither = nameItem(Material.WITHER_SKELETON_SKULL, "&3Wither Arena");
//				ItemStack stats = nameItem(Material.GOLDEN_HELMET, "&3Stats and Spectate Hall");
				ItemStack parkour = nameItem(Material.IRON_BOOTS, "&3Parkour");
//				ItemStack mazes = nameItem(Material.OAK_LEAVES, "&3Mazes");
//				ItemStack mobarena = nameItem(Material.ZOMBIE_HEAD, "&3Mob Arena");
//				ItemStack connect4 = nameItem(Material.BLUE_CONCRETE, "&3Connect4");
				ItemStack tictactoe = nameItem(Material.PAPER, "&3Tic Tac Toe");

				contents.set(1, 1, ClickableItem.from(lobby, e -> warp(player, "minigamelobby")));
				contents.set(1, 3, ClickableItem.from(spvp, e -> Bukkit.dispatchCommand(player, "spvp")));
//				contents.set(1, 5, ClickableItem.from(wither, e -> Bukkit.dispatchCommand(player, "wither")));
//				contents.set(1, 7, ClickableItem.from(stats, e -> warp(player, "statshall")));
				contents.set(2, 2, ClickableItem.from(parkour, e -> warp(player, "parkour")));
//				contents.set(2, 4, ClickableItem.from(mazes, e -> warp(player, "mazes")));
//				contents.set(2, 6, ClickableItem.from(mobarena, e -> warp(player, "mobarenas")));
//				contents.set(3, 3, ClickableItem.from(connect4, e -> warp(player, "connect4")));
				contents.set(3, 5, ClickableItem.from(tictactoe, e -> warp(player, "tictactoe")));
				break;

//			case CREATIVE:
//			case SKYBLOCK:
			case OTHER:
				ItemStack podiums = nameItem(Material.QUARTZ_STAIRS, "&3Podiums and Current Staff");
				ItemStack hoh = nameItem(Material.BEACON, "&3Hall of History");
				ItemStack wog = nameItem(Material.SIGN, "&3Walls of Grace");
//				ItemStack ba = nameItem(Material.WOODEN_AXE, "&3BuildAdmin World");
//				ItemStack banners = nameItem(Material.CYAN_BANNER, "&3Banners");
				ItemStack storetesting = nameItem(Material.GOLD_INGOT, "&3Store Perk Testing Area");
//				ItemStack stranded = nameItem(Material.WRITABLE_BOOK, "&3Stranded", "&eAn adventure map!||&eCan ye survive a seabattle, ||&ea ship wreck, scores of ||&ezombies and find the ||&elegendary treasure... matey?");
				ItemStack walkthrough = nameItem(Material.NETHER_STAR, "&3Two Year Anniversary", "&e&lHistory Walkthrough||&eCelebrating 2 years||&eof Bear Nation");
//				ItemStack bearfair = nameItem(Material.FIREWORK_ROCKET, "&3Three Year Anniversary", "&e&lBear Fair||&eCelebrating 3 years||&eof Bear Nation");
				ItemStack buildcontests = nameItem(Material.CHEST, "&3Past Build Contests");

				contents.set(1, 1, ClickableItem.from(podiums, e -> warp(player, "podiumsandstaffhall")));
				contents.set(1, 3, ClickableItem.from(hoh, e -> command(player, "hallofhistory")));
				contents.set(1, 5, ClickableItem.from(wog, e -> command(player, "wog")));
//				contents.set(1, 7, ClickableItem.from(ba, e -> warp(player, "buildadmin")));
//				contents.set(2, 2, ClickableItem.from(banners, e -> warp(player, "banners")));
				contents.set(2, 4, ClickableItem.from(storetesting, e -> warp(player, "donortrial")));
//				contents.set(2, 6, ClickableItem.from(stranded, e -> warp(player, "stranded")));
				contents.set(3, 3, ClickableItem.from(walkthrough, e -> warp(player, "2y")));
//				contents.set(3, 5, ClickableItem.from(bearfair, e -> warp(player, "bearfair")));
				contents.set(3, 5, ClickableItem.from(buildcontests, e -> WarpsMenu.open(player, WarpMenu.BUILD_CONTESTS)));
				break;

			case BUILD_CONTESTS:
				ItemStack contest0 = nameItem(Material.JACK_O_LANTERN, "&3Halloween - 2015");
//				ItemStack contest1 = nameItem(Material.COARSE_DIRT, "&3Dwarven Cities - 2016");
				ItemStack contest2 = nameItem(Material.JACK_O_LANTERN, "&3Halloween - 2016");
				ItemStack contest3 = nameItem(Material.OBSIDIAN, "&3Space - 2016");
//				ItemStack contest4 = nameItem(Material.BRICKS, "&3World Cultures - 2018");
//				ItemStack contest5 = nameItem(Material.PINK_WOOL, "&3Celebration - 2018");

				contents.set(1, 0, ClickableItem.from(contest0, e -> warp(player, "buildcontest0")));
//				contents.set(1, 1, ClickableItem.from(contest1, e -> warp(player, "buildcontest1")));
				contents.set(1, 2, ClickableItem.from(contest2, e -> warp(player, "buildcontest2")));
				contents.set(1, 3, ClickableItem.from(contest3, e -> warp(player, "buildcontest3")));
//				contents.set(1, 4, ClickableItem.from(contest4, e -> warp(player, "buildcontest4")));
//				contents.set(1, 5, ClickableItem.from(contest5, e -> warp(player, "buildcontest5")));
				break;

		}
	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}

}
