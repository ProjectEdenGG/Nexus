package me.pugabyte.bncore.features.warps.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.warps.WarpMenu;
import me.pugabyte.bncore.features.warps.Warps;
import me.pugabyte.bncore.features.warps.WarpsMenu;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.models.warps.Warp;
import me.pugabyte.bncore.models.warps.WarpService;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.SerializationUtils.JSON;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/*
	TO UPDATE TO 1.13:
		CTRL+F '1.12'
		ALT+ENTER
		CTRL+X
		CTRL+X
		UP ARROW
		CTRL+/
 */

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

		WarpService warpService = new WarpService();
		SettingService settingService = new SettingService();

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

				Setting buildContestSetting = settingService.get("buildcontest", "info");
				Map<String, Object> bcInfo = buildContestSetting.getJson();
				if (bcInfo != null && bcInfo.get("item") != null && (Boolean.parseBoolean((String) bcInfo.get("active")))) {
					contents.set(4, 4, ClickableItem.from(JSON.deserializeItem((String) bcInfo.get("item")), e -> {
						warp(player, "buildcontest");
					}));
				}
				break;

			case SURVIVAL:
				for (Warps.SurvivalWarp warp : Warps.SurvivalWarp.values()) {
					contents.set(warp.getColumn(), warp.getRow(), ClickableItem.from(nameItem(warp.getItemStack(), "&3" + warp.getDisplayName(), "&eClick to go to the " + warp.getDisplayName() + " warp"), e -> {
						Warp warp1 = warpService.getNormalWarp(warp.name().replace("_", ""));
						warp1.teleport(player);
					}));
				}

				ItemStack shub = nameItem(Material.EMERALD, "&3Shops Hub", "&eLearn all about||&eBear Nation's economy");
				ItemStack market = nameItem(Material.SIGN, "&3Market", "&eWhere you'll find all||&ethe 'bear' neccessities.");
				ItemStack shops = new ItemBuilder(Material.SKULL_ITEM).skullType(SkullType.PLAYER).name("&3Player Shops").lore("&ePlayer owned run shops").build();

				contents.set(1, 7, ClickableItem.from(shub, e -> warp(player, "shub")));
				contents.set(2, 7, ClickableItem.from(market, e -> warp(player, "market")));
				contents.set(3, 7, ClickableItem.from(shops, e -> command(player, "shops list")));

				contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.BOOK).name("&3Info").lore("&eThese are the " +
						"survival world warps.").lore("&eThey are spread out across the entire world.").loreize(false).build()));
				break;

			case MINIGAMES:
				ItemStack lobby = nameItem(Material.DIAMOND_SWORD, "&3Minigame Lobby");
				ItemStack spvp = nameItem(Material.IRON_AXE, "&3Survival PVP Arena");
//				ItemStack wither = nameItem(Material.WITHER_SKELETON_SKULL, "&3Wither Arena");
				ItemStack wither = new ItemBuilder(Material.SKULL_ITEM).skullType(SkullType.WITHER).name("&3Wither Arena").build(); // 1.12
//				ItemStack stats = nameItem(Material.GOLDEN_HELMET, "&3Stats and Spectate Hall");
				ItemStack stats = nameItem(Material.GOLD_HELMET, "&3Stats and Spectate Hall"); // 1.12
				ItemStack parkour = nameItem(Material.IRON_BOOTS, "&3Parkour");
//				ItemStack mazes = nameItem(Material.OAK_LEAVES, "&3Mazes");
				ItemStack mazes = nameItem(Material.LEAVES, "&3Mazes"); // 1.12
//				ItemStack mobarena = nameItem(Material.ZOMBIE_HEAD, "&3Mob Arena");
				ItemStack mobarena = new ItemBuilder(Material.SKULL_ITEM).name("&3Mob Arena").skullType(SkullType.ZOMBIE).build(); // 1.12
//				ItemStack connect4 = nameItem(Utils.getRandomElement(Material.BLUE_CONCRETE, Material.RED_CONCRETE), "&3Connect4");
				ItemStack connect4 = nameItem(new ItemBuilder(Material.CONCRETE).color(Utils.getRandomElement(ColorType.BLUE, ColorType.RED)).build(), "&3Connect4"); // 1.12
				ItemStack tictactoe = nameItem(Material.PAPER, "&3Tic Tac Toe");

				contents.set(1, 1, ClickableItem.from(lobby, e -> warp(player, "minigamelobby")));
				contents.set(1, 3, ClickableItem.from(spvp, e -> Utils.runCommand(player, "spvp")));
				contents.set(1, 5, ClickableItem.from(wither, e -> Utils.runCommand(player, "wither")));
				contents.set(1, 7, ClickableItem.from(stats, e -> warp(player, "statshall")));
				contents.set(2, 2, ClickableItem.from(parkour, e -> warp(player, "parkour")));
				contents.set(2, 4, ClickableItem.from(mazes, e -> warp(player, "mazes")));
				contents.set(2, 6, ClickableItem.from(mobarena, e -> warp(player, "mobarenas")));
				contents.set(3, 3, ClickableItem.from(connect4, e -> warp(player, "connect4")));
				contents.set(3, 5, ClickableItem.from(tictactoe, e -> warp(player, "tictactoe")));
				break;

//			case CREATIVE:
//			case SKYBLOCK:
			case OTHER:
				ItemStack podiums = nameItem(Material.QUARTZ_STAIRS, "&3Podiums and Current Staff");
				ItemStack hoh = nameItem(Material.BEACON, "&3Hall of History");
				ItemStack wog = nameItem(Material.SIGN, "&3Walls of Grace");
//				ItemStack ba = nameItem(Material.WOODEN_AXE, "&3BuildAdmin World");
				ItemStack ba = nameItem(Material.WOOD_AXE, "&3BuildAdmin World"); // 1.12
//				ItemStack banners = nameItem(Material.CYAN_BANNER, "&3Banners");
				ItemStack banners = new ItemBuilder(Material.BANNER).dyeColor(ColorType.CYAN).name("&3Banners").build(); // 1.12
				ItemStack storetesting = nameItem(Material.GOLD_INGOT, "&3Store Perk Testing Area");
//				ItemStack stranded = nameItem(Material.WRITABLE_BOOK, "&3Stranded", "&eAn adventure map!||&eCan ye survive a seabattle, ||&ea ship wreck, scores of ||&ezombies and find the ||&elegendary treasure... matey?");
				ItemStack stranded = nameItem(Material.WRITTEN_BOOK, "&3Stranded", "&eAn adventure map!||&eCan ye survive a seabattle, ||&ea ship wreck, scores of ||&ezombies and find the ||&elegendary treasure... matey?"); // 1.12
				ItemStack walkthrough = nameItem(Material.NETHER_STAR, "&3Two Year Anniversary", "&e&lHistory Walkthrough||&eCelebrating 2 years||&eof Bear Nation");
//				ItemStack bearfair = nameItem(Material.FIREWORK_ROCKET, "&3Three Year Anniversary", "&e&lBear Fair||&eCelebrating 3 years||&eof Bear Nation");
				ItemStack bearfair = nameItem(Material.FIREWORK, "&3Bear Fair", "&eCelebrating 4 years||&eof Bear Nation"); // 1.12
				ItemStack buildcontests = nameItem(Material.CHEST, "&3Past Build Contests");

				contents.set(1, 1, ClickableItem.from(podiums, e -> warp(player, "podiumsandstaffhall")));
				contents.set(1, 3, ClickableItem.from(hoh, e -> command(player, "hallofhistory")));
				contents.set(1, 5, ClickableItem.from(wog, e -> command(player, "wog")));
				contents.set(1, 7, ClickableItem.from(ba, e -> warp(player, "buildadmin")));
				contents.set(2, 2, ClickableItem.from(banners, e -> warp(player, "banners")));
				contents.set(2, 4, ClickableItem.from(storetesting, e -> warp(player, "donortrial")));
				contents.set(2, 6, ClickableItem.from(stranded, e -> warp(player, "stranded")));
				contents.set(3, 3, ClickableItem.from(walkthrough, e -> warp(player, "2y")));
				contents.set(3, 5, ClickableItem.from(bearfair, e -> warp(player, "bearfair")));
				contents.set(4, 4, ClickableItem.from(buildcontests, e -> WarpsMenu.open(player, WarpMenu.BUILD_CONTESTS)));
				break;

			case BUILD_CONTESTS:
				ItemStack contest0 = nameItem(Material.JACK_O_LANTERN, "&3Halloween - 2015");
//				ItemStack contest1 = nameItem(Material.COARSE_DIRT, "&3Dwarven Cities - 2016");
				ItemStack contest1 = nameItem(Material.DIRT, "&3Dwarven Cities - 2016"); // 1.12
				ItemStack contest2 = nameItem(Material.JACK_O_LANTERN, "&3Halloween - 2016");
				ItemStack contest3 = nameItem(Material.OBSIDIAN, "&3Space - 2016");
//				ItemStack contest4 = nameItem(Material.BRICKS, "&3World Cultures - 2018");
				ItemStack contest4 = nameItem(Material.BRICK, "&3World Cultures - 2018"); // 1.12
//				ItemStack contest5 = nameItem(Material.PINK_WOOL, "&3Celebration - 2018");
				ItemStack contest5 = nameItem(new ItemBuilder(Material.WOOL).color(ColorType.PINK).build(), "&3Celebration - 2018"); // 1.12

				contents.set(1, 0, ClickableItem.from(contest0, e -> warp(player, "buildcontest0")));
				contents.set(1, 1, ClickableItem.from(contest1, e -> warp(player, "buildcontest1")));
				contents.set(1, 2, ClickableItem.from(contest2, e -> warp(player, "buildcontest2")));
				contents.set(1, 3, ClickableItem.from(contest3, e -> warp(player, "buildcontest3")));
				contents.set(1, 4, ClickableItem.from(contest4, e -> warp(player, "buildcontest4")));
				contents.set(1, 5, ClickableItem.from(contest5, e -> warp(player, "buildcontest5")));
				break;

		}
	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}

}
