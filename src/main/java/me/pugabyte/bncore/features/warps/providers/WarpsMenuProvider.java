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
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.SerializationUtils.JSON;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

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
			case LEGACY:
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
				ItemStack survival = nameItem(Material.GRASS_BLOCK, "&3Survival");
				ItemStack minigames = nameItem(Material.DIAMOND_SWORD, "&3Minigames");
				ItemStack creative = nameItem(Material.QUARTZ, "&3Creative");
				ItemStack skyblock = nameItem(Material.COBBLESTONE, "&3Skyblock", "&cCurrently Disabled");
				ItemStack other = nameItem(Material.EMERALD, "&3Other");

				contents.set(1, 1, ClickableItem.from(survival, e -> {
					if (player.getWorld().getName().matches("world(_nether|the_end|)"))
						WarpsMenu.open(player, WarpMenu.LEGACY);
					else
						WarpsMenu.open(player, WarpMenu.SURVIVAL);
				}));
				contents.set(1, 3, ClickableItem.from(minigames, e -> WarpsMenu.open(player, WarpMenu.MINIGAMES)));
				contents.set(1, 5, ClickableItem.from(creative, e -> warp(player, "creative")));
				//contents.set(1, 7, ClickableItem.from(skyblock, e -> warp(player, "skyblock")));
				contents.set(1, 7, ClickableItem.empty(skyblock));
				contents.set(2, 4, ClickableItem.from(other, e -> WarpsMenu.open(player, WarpMenu.OTHER)));

				Setting buildContestSetting = settingService.get("buildcontest", "info");
				Map<String, Object> bcInfo = buildContestSetting.getJson();
				if (bcInfo != null && bcInfo.get("item") != null && (Boolean.parseBoolean((String) bcInfo.get("active")))) {
					contents.set(4, 4, ClickableItem.from(JSON.deserializeItemStack((String) bcInfo.get("item")), e -> {
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

				ItemStack shops = nameItem(Material.EMERALD, "&3Shops", "&eThis will open||&ethe shop menu||||&cCurrently Disabled");
				ItemStack resource = nameItem(Material.DIAMOND_PICKAXE, "&3Resource", "&eClick to teleport to the resource world");
				ItemStack legacy = nameItem(Material.MOSSY_COBBLESTONE, "&3Legacy", "&eClick to view legacy world warps");

				//contents.set(1, 7, ClickableItem.from(shops, e -> new MainMenuProvider(null).open(player)));
				contents.set(1, 7, ClickableItem.empty(shops));
				contents.set(2, 7, ClickableItem.from(resource, e -> warpService.get("resource", WarpType.NORMAL).teleport(player)));
				contents.set(3, 7, ClickableItem.from(legacy, e -> WarpsMenu.open(player, WarpMenu.LEGACY)));

				contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.BOOK).name("&3Info").lore("&eThese are the " +
						"survival world warps.").lore("&eThey are spread out across the entire world.").loreize(false).build()));
				break;

			case LEGACY:
				for (Warps.LegacySurvivalWarp warp : Warps.LegacySurvivalWarp.values()) {
					contents.set(warp.getColumn(), warp.getRow(), ClickableItem.from(nameItem(warp.getItemStack(), "&3" + warp.getDisplayName(), "&eClick to go to the " + warp.getDisplayName() + " warp"), e -> {
						Warp warp1 = warpService.get("legacy_" + warp.name().replace("_", ""), WarpType.NORMAL);
						if (warp1 == null) {
							player.sendMessage(StringUtils.colorize(StringUtils.getPrefix("Warps") + "&cThere was an error while trying to teleport you to the warp"));
							return;
						}
						warp1.teleport(player);
					}));
				}

				ItemStack shops2 = nameItem(Material.EMERALD, "&3Shops", "&eThis will open||&ethe shop menu||||&cCurrently Disabled");
				ItemStack newWorld = nameItem(Material.GRASS_BLOCK, "&3Survival", "&eClick to view the survival world warps");

				//contents.set(1, 7, ClickableItem.from(shops2, e -> new MainMenuProvider(null).open(player)));
				contents.set(1, 7, ClickableItem.empty(shops2));
				contents.set(3, 7, ClickableItem.from(newWorld, e -> WarpsMenu.open(player, WarpMenu.SURVIVAL)));

				contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.BOOK).name("&3Info").lore("&eThese are the " +
						"legacy survival world warps.").lore("&eThey are spread out across the entire world.").loreize(false).build()));

				break;
			case MINIGAMES:
				ItemStack lobby = nameItem(Material.DIAMOND_SWORD, "&3Minigame Lobby");
				ItemStack spvp = nameItem(Material.IRON_AXE, "&3Survival PVP Arena");
				ItemStack wither = nameItem(Material.WITHER_SKELETON_SKULL, "&3Wither Arena");
				ItemStack stats = nameItem(Material.GOLDEN_HELMET, "&3Stats and Spectate Hall");
				ItemStack parkour = nameItem(Material.IRON_BOOTS, "&3Parkour");
				ItemStack mazes = nameItem(Material.OAK_LEAVES, "&3Mazes");
				ItemStack mobarena = nameItem(Material.ZOMBIE_HEAD, "&3Mob Arena");
				ItemStack connect4 = nameItem((Material) Utils.getRandomElement(Material.BLUE_CONCRETE, Material.RED_CONCRETE), "&3Connect4");
				ItemStack tictactoe = nameItem(Material.PAPER, "&3Tic Tac Toe");

				contents.set(1, 1, ClickableItem.from(lobby, e -> warp(player, "minigames")));
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
				ItemStack leaderboards = nameItem(Material.QUARTZ_STAIRS, "&3Leaderboards");
				ItemStack staffhall = nameItem(Material.LIGHT_BLUE_CONCRETE, "&3Current Staff");
				ItemStack hoh = nameItem(Material.BEACON, "&3Hall of History");
				ItemStack wog = nameItem(Material.OAK_SIGN, "&3Walls of Grace");
				ItemStack banners = nameItem(Material.CYAN_BANNER, "&3Banners");
				ItemStack storetesting = nameItem(Material.GOLD_INGOT, "&3Store Perk Testing Area");
				ItemStack walkthrough = nameItem(Material.NETHER_STAR, "&3Two Year Anniversary", "&e&lHistory Walkthrough||&eCelebrating 2 years||&eof Bear Nation");
				ItemStack bearfair = nameItem(Material.FIREWORK_ROCKET, "&3Three Year Anniversary", "&e&lBear Fair||&eCelebrating 3 years||&eof Bear Nation");
				ItemStack buildcontests = nameItem(Material.CHEST, "&3Past Build Contests");

				contents.set(1, 1, ClickableItem.from(leaderboards, e -> warp(player, "leaderboards")));
				contents.set(1, 3, ClickableItem.from(staffhall, e -> warp(player, "staffhall")));
				contents.set(1, 5, ClickableItem.from(hoh, e -> command(player, "hallofhistory")));
				contents.set(1, 7, ClickableItem.from(wog, e -> command(player, "wog")));
				contents.set(2, 2, ClickableItem.from(banners, e -> warp(player, "banners")));
				contents.set(2, 4, ClickableItem.from(storetesting, e -> warp(player, "donortrial")));
				contents.set(2, 6, ClickableItem.from(buildcontests, e -> WarpsMenu.open(player, WarpMenu.BUILD_CONTESTS)));
				contents.set(3, 3, ClickableItem.from(walkthrough, e -> warp(player, "2y")));
				contents.set(3, 5, ClickableItem.from(bearfair, e -> warp(player, "bearfair")));
				break;

			case BUILD_CONTESTS:
				ItemStack contest0 = nameItem(Material.JACK_O_LANTERN, "&3Halloween - 2015");
				ItemStack contest1 = nameItem(Material.COARSE_DIRT, "&3Dwarven Cities - 2016");
				ItemStack contest2 = nameItem(Material.JACK_O_LANTERN, "&3Halloween - 2016");
				ItemStack contest3 = nameItem(Material.OBSIDIAN, "&3Space - 2016");
				ItemStack contest4 = nameItem(Material.BRICKS, "&3World Cultures - 2018");
				ItemStack contest5 = nameItem(Material.PINK_WOOL, "&3Celebration - 2018");

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
