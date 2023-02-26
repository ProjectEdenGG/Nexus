package gg.projecteden.nexus.features.minigames.menus.lobby;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.FontUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ArenasMenu extends InventoryProvider {
	private final MechanicType mechanic;
	private final List<Arena> arenas;
	private final int pages;

	private static final String BASE = "久";
	private static final List<List<String>> SCROLLER_INDEXES = List.of(
		List.of("魉"),
		List.of("辆", "沩"),
		List.of("漷", "秬", "籽"),
		List.of("醭", "泽", "转", "洼"),
		List.of("髌", "泗", "穙", "邸", "甬"),
		List.of("粽", "轩", "乏", "袭", "说", "魋"),
		List.of("廋", "糠", "稿", "膑", "配", "丸", "蝻"),
		List.of("程", "磉", "暿", "飗", "毪", "轵", "浬", "腒"),
		List.of("骷", "淟", "貉", "陇", "鲌", "砵", "蚯", "涞", "轮"),
		List.of("晌", "夐", "暝", "赳", "盩", "墘", "貌", "糈", "疍", "糅")
	);

	private static final int[][] mapSlots = {
		{0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21},
		{4, 5, 6, 7, 13, 14, 15, 16, 22, 23, 24, 25},
		{27, 28, 29, 30, 36, 37, 38, 39, 45, 46, 47, 48},
		{31, 32, 33, 34, 40, 41, 42, 43, 49, 50, 51, 52}
	};

	public ArenasMenu(MechanicType mechanic) {
		this.mechanic = mechanic;
		this.arenas = getArenas();
		this.pages = (int) Math.ceil(arenas.size() / 4d);
	}

	private List<Arena> getArenas() {
		return ArenaManager.getAllEnabled().stream()
			.filter(arena -> arena.getMechanicType() == mechanic)
			.sorted(Comparator.comparing(Arena::getName))
			.collect(Collectors.toList());
	}

	@Override
	public String getTitle(int page) {
		return
			"&f" +
			FontUtils.minus(10) +
			BASE +
			FontUtils.minus(33) +
			SCROLLER_INDEXES.get(pages - 1).get(page) +
			FontUtils.minus(200) +
			"&0" +
			mechanic.get().getName();
	}

	@Override
	public void init() {
		final int page = contents.pagination().getPage();
		List<Arena> arenas = this.arenas.subList(page * 4, Math.min((page + 1) * 4, this.arenas.size()));
		arenas = arenas.stream()
			// TODO Remove or placeholder image
			.peek(arena -> {
				if (arena.getMenuImage() == null)
					arena.setMenuImage(new ItemBuilder(Material.PAPER).modelId(1700));
			})
			//
			.filter(arena -> arena.getMenuImage() != null)
			.collect(Collectors.toList());

		for (int i = 0; i < arenas.size(); i++) {
			Arena arena = arenas.get(i);
			for (int j = 0; j < mapSlots[i].length; j++)
				contents.set(mapSlots[i][j], ClickableItem.of(getItem(arena, j == 0), e -> Minigamer.of(viewer).join(arena)));
		}

		if (page > 0)
			contents.set(8, ClickableItem.of(new ItemBuilder(CustomMaterial.INVISIBLE).name("&e^^^^").build(), e -> open(viewer, page - 1)));

		if (page < (pages - 1))
			contents.set(53, ClickableItem.of(new ItemBuilder(CustomMaterial.INVISIBLE).name("&evvvv").build(), e -> open(viewer, page + 1)));
	}

	private ItemStack getItem(Arena arena, boolean main) {
		ItemBuilder item = main ? arena.getMenuImage() : new ItemBuilder(CustomMaterial.INVISIBLE);

		Match match = MatchManager.find(arena);

		int currentPlayers = match == null ? 0 : match.getOnlinePlayers().size();
		final String playerCountColor = currentPlayers == arena.getMaxPlayers() ? "&c" : "&e";
		boolean canJoin = canJoin(match);

		item.name("&6&l" + arena.getDisplayName());
		item.lore("&f");
		item.lore("&3Players: " + playerCountColor + currentPlayers + "/" + arena.getMaxPlayers());
		item.lore("");

		if (canJoin)
			item.lore("&fClick to play");
		else
			item.lore("&cMatch in progress");

		if (match != null && canJoin)
			item.glow();

		return item.build();
	}

	private static boolean canJoin(Match match) {
		if (match == null)
			return true;

		if (match.getOnlinePlayers().size() == 0)
			return true;

		if (match.getOnlinePlayers().size() >= match.getArena().getMaxPlayers())
			return false;

		if (!match.isStarted())
			return true;

		return match.getArena().canJoinLate();
	}

}
