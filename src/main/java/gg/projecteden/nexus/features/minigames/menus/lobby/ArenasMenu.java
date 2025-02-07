package gg.projecteden.nexus.features.minigames.menus.lobby;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.content.ScrollableInventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.lobby.MinigameInviter;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.managers.MatchManager;
import gg.projecteden.nexus.features.minigames.menus.annotations.Scroll;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicSubGroup;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ArenasMenu extends ScrollableInventoryProvider {
	private final MechanicType mechanic;
	private final List<Arena> arenas;
	@Getter
	private final int pages;

	Pair<SlotPos, SlotPos> randomSlotsMinMax = Pair.of(SlotPos.of(0, 0), SlotPos.of(2, 3));

	private static final Map<SlotPos, SlotPos> mapSlotsMinMax = new LinkedHashMap<>() {{
		put(SlotPos.of(0, 0), SlotPos.of(2, 3));
		put(SlotPos.of(0, 4), SlotPos.of(2, 7));
		put(SlotPos.of(3, 0), SlotPos.of(5, 3));
		put(SlotPos.of(3, 4), SlotPos.of(5, 7));
	}};

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
		return super.getTitle(page) + "&0" + mechanic.get().getName();
	}

	@Override
	public void init() {
		final MechanicSubGroup group = MechanicSubGroup.from(mechanic);
		if (group != null)
			addBackItemBottomInventory(e -> {
				boolean scroll = false;
				try {
					scroll = MechanicSubGroup.class.getField(group.name()).isAnnotationPresent(Scroll.class);
				} catch (NoSuchFieldException | SecurityException ignored) {}

				if (!scroll)
					new MechanicSubGroupMenu(group).open(e.getPlayer());
				else
					new ScrollableMechanicSubGroupMenu(group).open(e.getPlayer());
			});
		else
			addCloseItemBottomInventory();

		final ItemBuilder inviteItem = getInviteItem(viewer);

		if (MinigameInviter.canSendInvite(viewer))
			selfContents.set(0, 1, ClickableItem.of(inviteItem, e -> Tasks.wait(2, () -> {
				if (CustomMaterial.of(viewer.getItemOnCursor()) == CustomMaterial.ENVELOPE_1)
					viewer.setItemOnCursor(new ItemStack(Material.AIR));
				 else if (Nullables.isNullOrAir(viewer.getItemOnCursor()))
					viewer.setItemOnCursor(inviteItem.build());
			})));

		final int page = contents.pagination().getPage();
		List<Arena> arenas = this.arenas.subList(page * 4, Math.min((page + 1) * 4, this.arenas.size()));
		arenas = arenas.stream()
			// TODO Remove or placeholder image
			.peek(arena -> {
				if (arena.getMenuImage() == null)
					arena.setMenuImage(new ItemBuilder(Material.PAPER).model(CustomMaterial.GUI_GAMELOBBY_ARENAS_BLANK));
			})
			//
			.filter(arena -> arena.getMenuImage() != null)
			.toList();

		// Random Arena
		if (page == 0) {
			SlotPos min = randomSlotsMinMax.getFirst();
			SlotPos max = randomSlotsMinMax.getSecond();

			final Consumer<ItemClickData> consumer = e -> {
				final Optional<Match> mostPlayers = MatchManager.getAll().stream()
					.filter(match -> mechanic == null || mechanic == match.getArena().getMechanicType())
					.filter(match -> !match.getMinigamers().isEmpty())
					.filter(match -> !match.isStarted() || match.getArena().canJoinLate())
					.max(Comparator.comparingInt(match -> match.getMinigamers().size()));

				final Arena arena;
				if (mostPlayers.isPresent())
					arena = mostPlayers.get().getArena();
				else
					arena = RandomUtils.randomElement(ArenaManager.getAllEnabled(mechanic));

				if (CustomMaterial.of(viewer.getItemOnCursor()) == CustomMaterial.ENVELOPE_1)
					if (MinigameInviter.canSendInvite(viewer))
						Tasks.wait(2, () -> inviteAll(e, arena));
					else {
						viewer.setItemOnCursor(new ItemStack(Material.AIR));
						PlayerUtils.send(viewer, Minigames.PREFIX + "You cannot send invites right now!");
					}
				else
					Minigamer.of(viewer).join(arena);
			};

			contents.fill(min, max, ClickableItem.of(getRandomArenaItem(false), consumer));
			contents.set(min, ClickableItem.of(getRandomArenaItem(true), consumer));
		}
		//

		final Iterator<Arena> arenaIterator = arenas.iterator();
		mapSlotsMinMax.forEach((min, max) -> {
			if (!arenaIterator.hasNext())
				return;

			if (page == 0 && min.matches(randomSlotsMinMax.getFirst()) && max.matches(randomSlotsMinMax.getSecond()))
				return;

			final Arena arena = arenaIterator.next();
			final Consumer<ItemClickData> consumer = e -> {
				if (CustomMaterial.of(viewer.getItemOnCursor()) == CustomMaterial.ENVELOPE_1)
					if (MinigameInviter.canSendInvite(viewer))
						Tasks.wait(2, () -> inviteAll(e, arena));
					else {
						viewer.setItemOnCursor(new ItemStack(Material.AIR));
						PlayerUtils.send(viewer, Minigames.PREFIX + "You cannot send invites right now!");
					}
				else
					Minigamer.of(viewer).join(arena);
			};
			contents.fill(min, max, ClickableItem.of(getArenaItem(arena, false), consumer));
			contents.set(min, ClickableItem.of(getArenaItem(arena, true), consumer));
		});

		super.init();
	}

	public static ItemBuilder getInviteItem(Player player) {
		final ItemBuilder inviteItem = new ItemBuilder(CustomMaterial.ENVELOPE_1)
			.name("&eInvite")
			.lore("")
			.lore("&fClick a map to send an invite");

		if (Rank.of(player).isStaff())
			inviteItem.lore("&eShift+Click &fto invite all online players");

		return inviteItem;
	}

	private void inviteAll(ItemClickData e, Arena arena) {
		try {
			viewer.setItemOnCursor(new ItemStack(Material.AIR));
			final Supplier<MinigameInviter> invite = () -> Minigames.inviter().create(viewer, arena);
			if (Rank.of(viewer).isStaff() && e.isShiftClick())
				ConfirmationMenu.builder()
					.title("Invite all online players (" + OnlinePlayers.where().exclude(viewer).count() + ")?")
					.onConfirm(e2 -> {
						try {
							invite.get().inviteAll();
						} catch (Exception ex) {
							MenuUtils.handleException(viewer, Minigames.PREFIX, ex);
						}
					})
					.onCancel(e2 -> open(viewer, contents.pagination()))
					.open(viewer);
			else
				invite.get().inviteLobby();
		} catch (Exception ex) {
			MenuUtils.handleException(viewer, Minigames.PREFIX, ex);
		}
	}

	private ItemStack getRandomArenaItem(boolean main) {
		ItemBuilder randomMenuImage = new ItemBuilder(CustomModel.of(Material.PAPER, 1699));
		ItemBuilder item = main ? randomMenuImage : new ItemBuilder(CustomMaterial.INVISIBLE);

		item.name("&6&lRandom Map");
		item.lore("");
		item.lore("&fClick to play");

		return item.build();
	}

	private ItemStack getArenaItem(Arena arena, boolean main) {
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

		if (match.getOnlinePlayers().isEmpty())
			return true;

		if (match.getOnlinePlayers().size() >= match.getArena().getMaxPlayers())
			return false;

		if (!match.isStarted())
			return true;

		return match.getArena().canJoinLate();
	}

}
