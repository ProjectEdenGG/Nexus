package me.pugabyte.bncore.features.menus.warps.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.models.MenuUtils;
import me.pugabyte.bncore.features.menus.warps.WarpMenu;
import me.pugabyte.bncore.features.menus.warps.WarpsMenu;
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
				contents.set(0, 0, ClickableItem.of(closeItem(), e -> contents.inventory().close(player)));
				break;
			case SURVIVAL:
			case MINIGAMES:
//			case CREATIVE:
//			case SKYBLOCK:
			case OTHER:
				contents.set(0, 0, ClickableItem.of(backItem(), e -> WarpsMenu.open(player, WarpMenu.MAIN)));
				break;
			case BUILD_CONTESTS:
				contents.set(0, 0, ClickableItem.of(backItem(), e -> WarpsMenu.open(player, WarpMenu.OTHER)));
				break;
		}

		switch (menu) {
			case MAIN:
				ItemStack survival = nameItem(new ItemStack(Material.GRASS_BLOCK), "&3Survival");
				ItemStack minigames = nameItem(new ItemStack(Material.DIAMOND_SWORD), "&3Minigames");
				ItemStack creative = nameItem(new ItemStack(Material.QUARTZ), "&3Creative");
				ItemStack skyblock = nameItem(new ItemStack(Material.COBBLESTONE), "&3Skyblock");
				ItemStack other = nameItem(new ItemStack(Material.EMERALD), "&3Other");

				contents.set(1, 1, ClickableItem.of(survival, e -> WarpsMenu.open(player, WarpMenu.SURVIVAL)));
				contents.set(1, 3, ClickableItem.of(minigames, e -> WarpsMenu.open(player, WarpMenu.MINIGAMES)));
				contents.set(1, 5, ClickableItem.of(creative, e -> warp(player, "creative")));
				contents.set(1, 7, ClickableItem.of(skyblock, e -> warp(player, "skyblock")));
				contents.set(2, 4, ClickableItem.of(other, e -> WarpsMenu.open(player, WarpMenu.OTHER)));
				break;

			case SURVIVAL:
				ItemStack northwest2 = nameItem(new ItemStack(Material.JUNGLE_LOG), "&3Northwest #2");
				ItemStack north2 = nameItem(new ItemStack(Material.OAK_PLANKS), "&3North #2");
				ItemStack northeast2 = nameItem(new ItemStack(Material.GRAY_TERRACOTTA), "&3Northeast #2");

				ItemStack northwest = nameItem(new ItemStack(Material.PODZOL), "&3Northwest");
				ItemStack north = nameItem(new ItemStack(Material.BONE_BLOCK), "&3North");
				ItemStack northeast = nameItem(new ItemStack(Material.COBBLESTONE_STAIRS), "&3Northeast");

				ItemStack west2 = nameItem(new ItemStack(Material.ACACIA_PLANKS), "&3West #2");
				ItemStack west = nameItem(new ItemStack(Material.SPRUCE_LOG), "&3West");
				ItemStack spawn = nameItem(new ItemStack(Material.CHISELED_STONE_BRICKS), "&3Spawn");
				ItemStack east = nameItem(new ItemStack(Material.ICE), "&3East");
				ItemStack east2 = nameItem(new ItemStack(Material.OAK_LOG), "&3East #2");

				ItemStack southwest = nameItem(new ItemStack(Material.HAY_BLOCK), "&3Southwest");
				ItemStack south = nameItem(new ItemStack(Material.GRAY_WOOL), "&3South");
				ItemStack southeast = nameItem(new ItemStack(Material.BIRCH_PLANKS), "&3Southeast");

				ItemStack southwest2 = nameItem(new ItemStack(Material.SAND), "&3Southwest #2");
				ItemStack south2 = nameItem(new ItemStack(Material.STONE_BRICKS), "&3South #2");
				ItemStack southeast2 = nameItem(new ItemStack(Material.SNOW_BLOCK), "&3Southeast #2");

				ItemStack nether = nameItem(new ItemStack(Material.NETHERRACK), "&3Nether");

				ItemStack shub = nameItem(new ItemStack(Material.EMERALD), "&3Shops Hub", "&eLearn all about||&eBear Nation's economy");
				ItemStack market = nameItem(new ItemStack(Material.SIGN), "&3Market", "&eWhere you'll find all||&ethe 'bear' neccessities.");
				ItemStack shops = nameItem(new ItemStack(Material.PLAYER_HEAD), "&3Player Shops", "&eFind more items at||&echeaper prices");

				contents.set(0, 1, ClickableItem.of(northwest2, e -> warp(player, "northwest2")));
				contents.set(0, 3, ClickableItem.of(north2, e -> warp(player, "north2")));
				contents.set(0, 5, ClickableItem.of(northeast2, e -> warp(player, "northeast2")));

				contents.set(1, 2, ClickableItem.of(northwest, e -> warp(player, "northwest")));
				contents.set(1, 3, ClickableItem.of(north, e -> warp(player, "north")));
				contents.set(1, 4, ClickableItem.of(northeast, e -> warp(player, "northeast")));

				contents.set(2, 1, ClickableItem.of(west2, e -> warp(player, "west2")));
				contents.set(2, 2, ClickableItem.of(west, e -> warp(player, "west")));
				contents.set(2, 3, ClickableItem.of(spawn, e -> warp(player, "spawn")));
				contents.set(2, 4, ClickableItem.of(east, e -> warp(player, "east")));
				contents.set(2, 5, ClickableItem.of(east2, e -> warp(player, "east2")));

				contents.set(3, 2, ClickableItem.of(southwest, e -> warp(player, "southwest")));
				contents.set(3, 3, ClickableItem.of(south, e -> warp(player, "south")));
				contents.set(3, 4, ClickableItem.of(southeast, e -> warp(player, "southeast")));

				contents.set(4, 1, ClickableItem.of(southwest2, e -> warp(player, "southwest2")));
				contents.set(4, 3, ClickableItem.of(south2, e -> warp(player, "south2")));
				contents.set(4, 5, ClickableItem.of(southeast2, e -> warp(player, "southeast2")));

				contents.set(4, 0, ClickableItem.of(nether, e -> warp(player, "nether")));

				contents.set(1, 7, ClickableItem.of(shub, e -> warp(player, "shub")));
				contents.set(2, 7, ClickableItem.of(market, e -> warp(player, "market")));
				contents.set(3, 7, ClickableItem.of(shops, e -> warp(player, "northeast2")));
				break;

			case MINIGAMES:
				ItemStack lobby = nameItem(new ItemStack(Material.DIAMOND_SWORD), "&3Minigame Lobby");
				ItemStack spvp = nameItem(new ItemStack(Material.IRON_AXE), "&3Survival PVP Arena");
				ItemStack wither = nameItem(new ItemStack(Material.WITHER_SKELETON_SKULL), "&3Wither Arena");
				ItemStack stats = nameItem(new ItemStack(Material.GOLDEN_HELMET), "&3Stats and Spectate Hall");
				ItemStack parkour = nameItem(new ItemStack(Material.IRON_BOOTS), "&3Parkour");
				ItemStack mazes = nameItem(new ItemStack(Material.OAK_LEAVES), "&3Mazes");
				ItemStack mobarena = nameItem(new ItemStack(Material.ZOMBIE_HEAD), "&3Mob Arena");
				ItemStack connect4 = nameItem(new ItemStack(Material.BLUE_CONCRETE), "&3Connect4");
				ItemStack tictactoe = nameItem(new ItemStack(Material.PAPER), "&3Tic Tac Toe");

				contents.set(1, 1, ClickableItem.of(lobby, e -> warp(player, "minigamelobby")));
				contents.set(1, 3, ClickableItem.of(spvp, e -> Bukkit.dispatchCommand(player, "spvp")));
				contents.set(1, 5, ClickableItem.of(wither, e -> Bukkit.dispatchCommand(player, "wither")));
				contents.set(1, 7, ClickableItem.of(stats, e -> warp(player, "statshall")));
				contents.set(2, 2, ClickableItem.of(parkour, e -> warp(player, "parkour")));
				contents.set(2, 4, ClickableItem.of(mazes, e -> warp(player, "mazes")));
				contents.set(2, 6, ClickableItem.of(mobarena, e -> warp(player, "mobarenas")));
				contents.set(3, 3, ClickableItem.of(connect4, e -> warp(player, "connect4")));
				contents.set(3, 5, ClickableItem.of(tictactoe, e -> warp(player, "tictactoe")));
				break;

//			case CREATIVE:
//			case SKYBLOCK:
			case OTHER:
				ItemStack podiums = nameItem(new ItemStack(Material.QUARTZ_STAIRS), "&3Podiums and Current Staff");
				ItemStack hoh = nameItem(new ItemStack(Material.BEACON), "&3Hall of History");
				ItemStack wog = nameItem(new ItemStack(Material.SIGN), "&3Walls of Grace");
				ItemStack ba = nameItem(new ItemStack(Material.WOODEN_AXE), "&3BuildAdmin World");
				ItemStack banners = nameItem(new ItemStack(Material.CYAN_BANNER), "&3Banners");
				ItemStack storetesting = nameItem(new ItemStack(Material.GOLD_INGOT), "&3Store Perk Testing Area");
				ItemStack stranded = nameItem(new ItemStack(Material.WRITABLE_BOOK), "&3Stranded", "&eAn adventure map!||&eCan ye survive a seabattle, ||&ea ship wreck, scores of ||&ezombies and find the ||&elegendary treasure... matey?");
				ItemStack walkthrough = nameItem(new ItemStack(Material.NETHER_STAR), "&3Two Year Anniversary", "&e&lHistory Walkthrough||&eCelebrating 2 years||&eof Bear Nation");
				ItemStack bearfair = nameItem(new ItemStack(Material.FIREWORK_ROCKET), "&3Three Year Anniversary", "&e&lBear Fair||&eCelebrating 3 years||&eof Bear Nation");
				ItemStack buildcontests = nameItem(new ItemStack(Material.CHEST), "&3Past Build Contests");

				contents.set(1, 1, ClickableItem.of(podiums, e -> warp(player, "podiumsandstaffhall")));
				contents.set(1, 3, ClickableItem.of(hoh, e -> command(player, "hallofhistory")));
				contents.set(1, 5, ClickableItem.of(wog, e -> command(player, "wog")));
				contents.set(1, 7, ClickableItem.of(ba, e -> warp(player, "buildadmin")));
				contents.set(2, 2, ClickableItem.of(banners, e -> warp(player, "banners")));
				contents.set(2, 4, ClickableItem.of(storetesting, e -> warp(player, "donortrial")));
				contents.set(2, 6, ClickableItem.of(stranded, e -> warp(player, "stranded")));
				contents.set(3, 3, ClickableItem.of(walkthrough, e -> warp(player, "2y")));
				contents.set(3, 5, ClickableItem.of(bearfair, e -> warp(player, "bearfair")));
				contents.set(3, 5, ClickableItem.of(buildcontests, e -> WarpsMenu.open(player, WarpMenu.BUILD_CONTESTS)));
				break;

			case BUILD_CONTESTS:
				ItemStack contest0 = nameItem(new ItemStack(Material.JACK_O_LANTERN), "&3Halloween - 2015");
				ItemStack contest1 = nameItem(new ItemStack(Material.COARSE_DIRT), "&3Dwarven Cities - 2016");
				ItemStack contest2 = nameItem(new ItemStack(Material.JACK_O_LANTERN), "&3Halloween - 2016");
				ItemStack contest3 = nameItem(new ItemStack(Material.OBSIDIAN), "&3Space - 2016");
				ItemStack contest4 = nameItem(new ItemStack(Material.BRICKS), "&3World Cultures - 2018");
				ItemStack contest5 = nameItem(new ItemStack(Material.PINK_WOOL), "&3Celebration - 2018");

				contents.set(1, 0, ClickableItem.of(contest0, e -> warp(player, "buildcontest0")));
				contents.set(1, 1, ClickableItem.of(contest1, e -> warp(player, "buildcontest1")));
				contents.set(1, 2, ClickableItem.of(contest2, e -> warp(player, "buildcontest2")));
				contents.set(1, 3, ClickableItem.of(contest3, e -> warp(player, "buildcontest3")));
				contents.set(1, 4, ClickableItem.of(contest4, e -> warp(player, "buildcontest4")));
				contents.set(1, 5, ClickableItem.of(contest5, e -> warp(player, "buildcontest5")));
				break;

		}
	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}

}
