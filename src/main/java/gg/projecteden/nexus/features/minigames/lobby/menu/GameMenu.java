package gg.projecteden.nexus.features.minigames.lobby.menu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.managers.PlayerManager;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicGroup;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.FontUtils.MINUS_TEN;
import static gg.projecteden.nexus.utils.FontUtils.MINUS_THREE;

public class GameMenu {

	private static final String BASE = "久";
	private static final Map<Integer, Map<Integer, String>> SCROLLER_INDEXES = new HashMap<>();

	static {
		SCROLLER_INDEXES.put(1, new HashMap<>() {{
			put(1, "魉");
		}});
		SCROLLER_INDEXES.put(2, new HashMap<>() {{
			put(1, "辆");
			put(2, "沩");
		}});
		SCROLLER_INDEXES.put(3, new HashMap<>() {{
			put(1, "漷");
			put(2, "秬");
			put(3, "籽");
		}});
		SCROLLER_INDEXES.put(4, new HashMap<>() {{
			put(1, "醭");
			put(2, "泽");
			put(3, "转");
			put(4, "洼");
		}});
		SCROLLER_INDEXES.put(5, new HashMap<>() {{
			put(1, "髌");
			put(2, "泗");
			put(3, "穙");
			put(4, "邸");
			put(5, "甬");
		}});
		SCROLLER_INDEXES.put(6, new HashMap<>() {{
			put(1, "粽");
			put(2, "轩");
			put(3, "乏");
			put(4, "袭");
			put(5, "说");
			put(6, "魋");
		}});
		SCROLLER_INDEXES.put(7, new HashMap<>() {{
			put(1, "廋");
			put(2, "糠");
			put(3, "稿");
			put(4, "膑");
			put(5, "配");
			put(6, "丸");
			put(7, "蝻");
		}});
	}

	public static void open(Player player, MechanicGroup group, MechanicType mechanic) {
		open(player, group, mechanic, 1);
	}

	private static void open(Player player, MechanicGroup group, MechanicType mechanic, int page) {
		List<Arena> arenas = getArenas(group, mechanic);
		int pages = (int) Math.ceil(arenas.size() / 4d);
		String title = "&f" +
				MINUS_TEN +
				BASE +
				MINUS_TEN +
				MINUS_TEN +
				MINUS_TEN +
				MINUS_THREE +
				SCROLLER_INDEXES.get(pages).get(page) +
				MINUS_TEN.repeat(20) +
				"&0" +
				getInventoryName(group, mechanic);

		SmartInventory.builder()
			.title(StringUtils.colorize(title))
			.size(6, 9)
			.provider(new GameLobbyMenuProvider(group, mechanic, arenas, page))
			.build()
			.open(player);
	}

	private static List<Arena> getArenas(MechanicGroup group, MechanicType type) {
		if (group == null || group == MechanicGroup.MECHANIC) {
			if (type == null) {
				throw new NullPointerException("Both MechanicGroup and MechanicType cannot be null");
			}
			return ArenaManager.getAll().stream().filter(arena -> arena.getMechanicType() == type).collect(Collectors.toList());
		}
		return ArenaManager.getAll().stream().filter(arena -> arena.getMechanicType().getGroup() == group).collect(Collectors.toList());
	}

	private static String getInventoryName(MechanicGroup group, MechanicType type) {
		if (group == null || group == MechanicGroup.MECHANIC)
			return type.get().getName();
		return StringUtils.camelCase(group);
	}


	@AllArgsConstructor
	public static class GameLobbyMenuProvider implements InventoryProvider {

		private static final int[][] mapSlots = {
			{10, 1, 2, 3, 9, 0, 11, 12, 18, 19, 20, 21},
			{14, 5, 6, 7, 13, 4, 15, 16, 22, 23, 24, 25},
			{37, 28, 29, 30, 36, 27, 38, 39, 45, 46, 47, 48},
			{41, 32, 33, 34, 40, 31, 42, 43, 49, 50, 51, 52}
		};

		private MechanicGroup group;
		private MechanicType type;
		private List<Arena> arenas;
		private int page;

		@Override
		public void init(Player player, InventoryContents contents) {
			List<Arena> arenas = this.arenas.subList((page - 1) * 4, Math.min(page * 4, this.arenas.size()));

			for (int i = 0; i < arenas.size(); i++) {
				Arena arena = arenas.get(i);
				for (int j = 0; j < mapSlots[i].length; j++) {
					ItemBuilder item;
					if (j == 0)
						item = new ItemBuilder(Material.GLASS_PANE).customModelData(2 /* TODO */);
					else
						item = new ItemBuilder(Material.BARRIER).customModelData(1);
					contents.set(mapSlots[i][j], ClickableItem.from(item.name("&ePlay " + arena.getName()).build(), e -> {
						PlayerManager.get(e.getPlayer()).join(arena);
					}));
				}
			}

			if (page > 1)
				contents.set(8, ClickableItem.from(new ItemBuilder(Material.BARRIER).customModelData(1).name("&e^^^^").build(), e -> {
					GameMenu.open(player, group, type, page - 1);
				}));
			if (page < Math.ceil(arenas.size() / 4d))
				contents.set(53, ClickableItem.from(new ItemBuilder(Material.BARRIER).customModelData(1).name("&evvvv").build(), e -> {
					GameMenu.open(player, group, type, page + 1);
				}));

		}
	}



}
