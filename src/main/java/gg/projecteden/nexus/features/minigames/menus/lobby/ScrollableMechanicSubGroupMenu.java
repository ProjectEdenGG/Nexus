package gg.projecteden.nexus.features.minigames.menus.lobby;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.content.ScrollableInventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.lobby.MinigameInviter;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicSubGroup;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ScrollableMechanicSubGroupMenu extends ScrollableInventoryProvider {

	private final MechanicSubGroup subGroup;

	private static final Map<SlotPos, SlotPos> mapSlotsMinMax = new LinkedHashMap<>() {{
		put(SlotPos.of(0, 0), SlotPos.of(2, 3));
		put(SlotPos.of(0, 4), SlotPos.of(2, 7));
		put(SlotPos.of(3, 0), SlotPos.of(5, 3));
		put(SlotPos.of(3, 4), SlotPos.of(5, 7));
	}};

	private static final BiFunction<MechanicType, ItemBuilder, ItemBuilder> itemBuilder = (mechanic, item) -> {
		item.itemFlags(ItemFlag.values());
		item.name("&6&l" + mechanic.get().getName());
		item.lore("");
		item.lore("&3Arenas: &e" + ArenaManager.getAllEnabled(mechanic).size());
		item.lore("");
		item.lore("&fClick to view");
		return item;
	};

	public ScrollableMechanicSubGroupMenu(MechanicSubGroup subGroup) {
		this.subGroup = subGroup;
	}

	@Override
	public int getPages() {
		return (int) Math.ceil(subGroup.getMechanics().size() / 4d);
	}

	@Override
	public void init() {
		addCloseItemBottomInventory();

		final ItemBuilder inviteItem = getInviteItem(viewer);

		if (MinigameInviter.canSendInvite(viewer))
			selfContents.set(0, 1, ClickableItem.of(inviteItem, e -> Tasks.wait(2, () -> {
				if (ItemModelType.of(viewer.getItemOnCursor()) == ItemModelType.ENVELOPE_1)
					viewer.setItemOnCursor(new ItemStack(Material.AIR));
				else if (Nullables.isNullOrAir(viewer.getItemOnCursor()))
					viewer.setItemOnCursor(inviteItem.build());
			})));

		final int page = contents.pagination().getPage();
		List<MechanicType> mechanics = this.subGroup.getMechanics().subList(page * 4, Math.min((page + 1) * 4, this.subGroup.getMechanics().size()));

		final Iterator<MechanicType> iterator = mechanics.iterator();
		mapSlotsMinMax.forEach((min, max) -> {
			if (!iterator.hasNext())
				return;

			final MechanicType mechanic = iterator.next();
			final Consumer<ItemClickData> consumer = e -> {
				if (mechanic == MechanicType.TICTACTOE) {
					PlayerUtils.runCommand(e.getPlayer(), "warp " + mechanic.name());
					return;
				}

				final List<Arena> arenas = ArenaManager.getAllEnabled(mechanic);
				boolean holdingInvite = ItemModelType.of(viewer.getItemOnCursor()) == ItemModelType.ENVELOPE_1;
				if (arenas.isEmpty())
					PlayerUtils.send(e.getPlayer(), Minigames.PREFIX + "&cNo arenas found for " + gg.projecteden.api.common.utils.StringUtils.camelCase(mechanic));
				else if (arenas.size() == 1) {
					if (holdingInvite) {
						invite(e.getPlayer(), arenas.get(0));
						return;
					}

					Minigamer.of(e.getPlayer()).join(arenas.get(0));
				} else {
					new ArenasMenu(mechanic).open(e.getPlayer());
					if (holdingInvite) {
						if (MinigameInviter.canSendInvite(e.getPlayer()))
							Tasks.wait(1, () -> e.getPlayer().setItemOnCursor(ArenasMenu.getInviteItem(e.getPlayer()).build()));
					}
				}
			};
			contents.fill(min, max, ClickableItem.of(getArenaItem(mechanic, false), consumer));
			contents.set(min, ClickableItem.of(getArenaItem(mechanic, true), consumer));
		});

		super.init();
	}

	public static ItemBuilder getInviteItem(Player player) {
		return ArenasMenu.getInviteItem(player);
	}

	private ItemStack getArenaItem(MechanicType mechanicType, boolean main) {
		ItemBuilder item = main ? mechanicType.get().getMenuImage() : new ItemBuilder(ItemModelType.INVISIBLE);
		if (Nullables.isNullOrAir(item))
			return new ItemBuilder(ItemModelType.NULL).build();

		return itemBuilder.apply(mechanicType, item).build();
	}

	private static void invite(Player player, Arena arena) {
		if (MinigameInviter.canSendInvite(player)) {
			final MinigameInviter invite = Minigames.inviter().create(player, arena);
			if (Rank.of(player).isStaff() && player.isSneaking())
				invite.inviteAll();
			else
				invite.inviteLobby();
		} else {
			PlayerUtils.send(player, Minigames.PREFIX + "&cYou cannot send invites right now");
			player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
		}
	}

}
