package gg.projecteden.nexus.features.warps.providers;

import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.warps.WarpMenu;
import gg.projecteden.nexus.models.buildcontest.BuildContestService;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

import java.util.function.Supplier;

@Title("&3Warps")
@RequiredArgsConstructor
public class WarpsMenuProvider extends InventoryProvider {
	private final WarpMenu menu;

	@Override
	protected int getRows(Integer page) {
		return menu.getSize();
	}

	@Override
	public void init() {
		switch (menu) {
			case MAIN -> addCloseItem();
			case SURVIVAL, LEGACY, MINIGAMES, OTHER ->
				addBackItem(e -> new WarpsMenuProvider(WarpMenu.MAIN).open(viewer));
			case BUILD_CONTESTS -> addBackItem(e -> new WarpsMenuProvider(WarpMenu.OTHER).open(viewer));
		}

		switch (menu) {
			case MAIN -> {
				ItemBuilder survival = new ItemBuilder(Material.GRASS_BLOCK).name("&3Survival");
				ItemBuilder minigames = new ItemBuilder(Material.DIAMOND_SWORD).name("&3Minigames");
				ItemBuilder creative = new ItemBuilder(Material.QUARTZ).name("&3Creative");
				ItemBuilder skyblock = new ItemBuilder(Material.COBBLESTONE).name("&3One Block");
				ItemBuilder other = new ItemBuilder(Material.EMERALD).name("&3Other");
				contents.set(1, 1, ClickableItem.of(survival, e -> {
					if (viewer.getWorld().getName().matches("world(_nether|the_end|)"))
						new WarpsMenuProvider(WarpMenu.LEGACY).open(viewer);
					else
						new WarpsMenuProvider(WarpMenu.SURVIVAL).open(viewer);
				}));
				contents.set(1, 3, ClickableItem.of(minigames, e -> new WarpsMenuProvider(WarpMenu.MINIGAMES).open(viewer)));
				contents.set(1, 5, ClickableItem.of(creative, e -> warp("creative")));
				contents.set(1, 7, ClickableItem.of(skyblock, e -> command("ob")));
				contents.set(2, 4, ClickableItem.of(other, e -> new WarpsMenuProvider(WarpMenu.OTHER).open(viewer)));

				var event = EdenEvent.getActiveEvent(viewer);
				var buildContest = new BuildContestService().get0();

				Supplier<ClickableItem> eventItem = () -> ClickableItem.of(event.getWarpMenuItem(), e -> command(event.getName().toLowerCase()));
				Supplier<ClickableItem> buildContestItem = () -> ClickableItem.of(buildContest.getItemStack(), e -> warp("buildcontest"));

				if (buildContest.isActive() && buildContest.getItemStack() != null && event != null && event.getWarpMenuItem() != null) {
					contents.set(4, 3, buildContestItem.get());
					contents.set(4, 5, eventItem.get());
				} else if (buildContest.isActive() && buildContest.getItemStack() != null) {
					contents.set(4, 4, buildContestItem.get());
				} else if (event != null && event.getWarpMenuItem() != null) {
					contents.set(4, 4, eventItem.get());
				}
			}
			case SURVIVAL -> {
				ItemBuilder survival = new ItemBuilder(Material.GRASS_BLOCK).name("&3Survival").lore("&eClick to teleport to random spot in survival");
				ItemBuilder resource = new ItemBuilder(Material.DIAMOND_PICKAXE).name("&3Resource").lore("&eClick to teleport to the resource world");
				contents.set(1, 2, ClickableItem.of(survival, e -> command("rtp")));
				contents.set(1, 6, ClickableItem.of(resource, e -> warp("resource")));
			}
			case MINIGAMES -> {
				ItemBuilder parkour = new ItemBuilder(Material.IRON_BOOTS).name("&3Parkour");
				ItemBuilder lobby = new ItemBuilder(Material.DIAMOND_SWORD).name("&3Minigame Lobby");
				ItemBuilder arcade = new ItemBuilder(Material.LEVER).name("&3Arcade");
				contents.set(1, 4, ClickableItem.of(lobby, e -> warp("minigames")));
				contents.set(1, 2, ClickableItem.of(parkour, e -> warp("parkour")));
				contents.set(1, 6, ClickableItem.of(arcade, e -> warp("arcade")));
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
				contents.set(1, 1, ClickableItem.of(leaderboards, e -> warp("podiums")));
				contents.set(1, 3, ClickableItem.of(staffhall, e -> warp("staffhall")));
				contents.set(1, 5, ClickableItem.of(hoh, e -> command("hallofhistory")));
				contents.set(1, 7, ClickableItem.of(storetesting, e -> warp("store")));
				contents.set(2, 2, ClickableItem.of(buildcontests, e -> new WarpsMenuProvider(WarpMenu.BUILD_CONTESTS).open(viewer)));
				contents.set(2, 4, ClickableItem.of(walkthrough, e -> warp("2y")));
				contents.set(2, 6, ClickableItem.of(bearfair, e -> command("bearfair21")));
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
				contents.set(1, 0, ClickableItem.of(contest0, e -> warp("buildcontest0")));
				contents.set(1, 1, ClickableItem.of(contest1, e -> warp("buildcontest1")));
				contents.set(1, 2, ClickableItem.of(contest2, e -> warp("buildcontest2")));
				contents.set(1, 3, ClickableItem.of(contest3, e -> warp("buildcontest3")));
				contents.set(1, 4, ClickableItem.of(contest4, e -> warp("buildcontest4")));
				contents.set(1, 5, ClickableItem.of(contest5, e -> warp("buildcontest5")));
				contents.set(1, 6, ClickableItem.of(contest6, e -> warp("buildcontest6")));
				contents.set(1, 7, ClickableItem.of(contest7, e -> warp("buildcontest7")));
				contents.set(1, 8, ClickableItem.of(contest8, e -> warp("buildcontest8")));
				contents.set(2, 0, ClickableItem.of(contest9, e -> warp("buildcontest9")));
			}
		}
	}

}
