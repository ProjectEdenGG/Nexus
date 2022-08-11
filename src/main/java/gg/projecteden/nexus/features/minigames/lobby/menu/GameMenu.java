package gg.projecteden.nexus.features.minigames.lobby.menu;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicGroup;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.utils.FontUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GameMenu {

	private static final String BASE = "久";
	private static final List<List<String>> SCROLLER_INDEXES = List.of(
		List.of(
			"魉"
		),

		List.of(
			"辆",
			"沩"
		),

		List.of(
			"漷",
			"秬",
			"籽"
		),

		List.of(
			"醭",
			"泽",
			"转",
			"洼"
		),

		List.of(
			"髌",
			"泗",
			"穙",
			"邸",
			"甬"
		),

		List.of(
			"粽",
			"轩",
			"乏",
			"袭",
			"说",
			"魋"
		),

		List.of(
			"廋",
			"糠",
			"稿",
			"膑",
			"配",
			"丸",
			"蝻"
		)
	);

	private static List<Arena> getArenas(MechanicGroup group, MechanicType type) {
		if (group == null) {
			if (type == null)
				throw new NullPointerException("Both MechanicGroup and MechanicType cannot be null");

			return ArenaManager.getAll().stream()
				.filter(arena -> arena.getMechanicType() == type)
				.filter(arena -> !arena.isTestMode())
				.sorted(Comparator.comparing(Arena::getName))
				.collect(Collectors.toList());
		}
		return ArenaManager.getAll().stream()
			.filter(arena -> arena.getMechanicType().getGroup() == group)
			.filter(arena -> !arena.isTestMode())
			.sorted(Comparator.comparing(Arena::getName))
			.collect(Collectors.toList());
	}

	private static String getInventoryName(MechanicGroup group, MechanicType type) {
		if (group == null)
			return type.get().getName();
		return StringUtils.camelCase(group);
	}

	public static class ArenaListMenu extends InventoryProvider {
		private final MechanicGroup group;
		private final MechanicType mechanic;
		private final List<Arena> arenas;
		private final int pages;
		private int page;

		private static final int[][] mapSlots = {
			{10, 1, 2, 3, 9, 0, 11, 12, 18, 19, 20, 21},
			{14, 5, 6, 7, 13, 4, 15, 16, 22, 23, 24, 25},
			{37, 28, 29, 30, 36, 27, 38, 39, 45, 46, 47, 48},
			{41, 32, 33, 34, 40, 31, 42, 43, 49, 50, 51, 52}
		};

		public ArenaListMenu(MechanicGroup group, MechanicType mechanic, int page) {
			this.group = group;
			this.mechanic = mechanic;
			this.arenas = getArenas(group, mechanic);
			this.pages = (int) Math.ceil(arenas.size() / 4d);
			this.page = page;
		}

		@Override
		public String getTitle() {
			return
				"&f" +
				FontUtils.MINUS_TEN +
				BASE +
				FontUtils.MINUS_TEN +
				FontUtils.MINUS_TEN +
				FontUtils.MINUS_TEN +
				FontUtils.MINUS_THREE +
				SCROLLER_INDEXES.get(pages - 1).get(page - 1) +
				FontUtils.MINUS_TEN.repeat(20) +
				"&0" +
				getInventoryName(group, mechanic);
		}

		@Override
		public void init() {
			List<Arena> arenas = this.arenas.subList((page - 1) * 4, Math.min(page * 4, this.arenas.size()));

			for (int i = 0; i < arenas.size(); i++) {
				Arena arena = arenas.get(i);
				for (int j = 0; j < mapSlots[i].length; j++)
					contents.set(mapSlots[i][j], ClickableItem.of(getItem(arena, j == 0), e -> Minigamer.of(player).join(arena)));
			}

			if (page > 1)
				contents.set(8, ClickableItem.of(new ItemBuilder(Material.BARRIER).modelId(1).name("&e^^^^").build(), e -> {
					--page;
					open(player);
				}));
			if (page < Math.ceil(arenas.size() / 4d))
				contents.set(53, ClickableItem.of(new ItemBuilder(Material.BARRIER).modelId(1).name("&evvvv").build(), e -> {
					++page;
					open(player);
				}));
		}

		private ItemStack getItem(Arena arena, boolean main) {
			ItemBuilder item;
			if (main)
				item = new ItemBuilder(Material.GLASS_PANE).modelId(1000 + arena.getId());
			else
				item = new ItemBuilder(Material.BARRIER).modelId(1);

			Match match = MatchManager.get(arena);

			item.name("&ePlay " + arena.getName());
			item.lore("&3Mechanic: &e" + arena.getMechanic().getName());
			int currentPlayers = match.getOnlinePlayers().size();
			item.lore("&3Players: &e" + currentPlayers + "/" + arena.getMaxPlayers());

			if (currentPlayers > 0 && currentPlayers < arena.getMaxPlayers()) {
				if (match.isStarted()) {
					if (arena.canJoinLate())
						item.glow();
				} else
					item.glow();
			}

			return item.build();
		}

	}



}
