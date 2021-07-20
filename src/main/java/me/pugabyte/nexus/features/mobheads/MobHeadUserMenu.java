package me.pugabyte.nexus.features.mobheads;

import eden.utils.EnumUtils.IteratableEnum;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.mobheads.common.MobHead;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import me.pugabyte.nexus.models.mobheads.MobHeadUser;
import me.pugabyte.nexus.models.mobheads.MobHeadUserService;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class MobHeadUserMenu extends MenuUtils implements InventoryProvider {
	private final MobHeadUserService service = new MobHeadUserService();

	private KillsFilterType killsFilter = KillsFilterType.OFF;
	private HeadsFilterType headsFilter = HeadsFilterType.OFF;

	@Override
	public void open(Player player, int page) {
		SmartInventory.builder()
			.provider(this)
			.size(6, 9)
			.title("Mob Heads")
			.build()
			.open(player, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		final MobHeadUser user = service.get(player);

		addCloseItem(contents);

		List<ClickableItem> items = new ArrayList<>();

		Consumer<MobHead> addItem = mobHead -> {
			if (filter(user, mobHead))
				return;

			final int kills = user.get(mobHead).getKills();
			final int heads = user.get(mobHead).getHeads();
			ItemStack skull = mobHead.getSkull();

			if (skull == null)
				skull = new ItemStack(Material.BARRIER);

			final ItemBuilder builder = new ItemBuilder(skull)
				.lore("&3Kills: " + (kills > 0 ? "&a" : "&c") + kills)
				.lore("&3Heads: " + (heads > 0 ? "&a" : "&c") + heads);

			items.add(ClickableItem.empty(builder.build()));
		};

		for (MobHeadType mobHeadType : MobHeadType.values()) {
			if (mobHeadType.hasVariants()) {
				for (MobHeadVariant variant : mobHeadType.getVariants())
					addItem.accept(variant);
			} else
				addItem.accept(mobHeadType);
		}

		addPagination(player, contents, items);

		formatKillsFilter(player, contents);
		formatHeadsFilter(player, contents);
	}

	private boolean filter(MobHeadUser user, MobHead mobHead) {
		if (killsFilter.getFilter() != null)
			if (!killsFilter.getFilter().test(user, mobHead))
				return true;

		if (headsFilter.getFilter() != null)
			if (!headsFilter.getFilter().test(user, mobHead))
				return true;
		return false;
	}

	private void formatKillsFilter(Player player, InventoryContents contents) {
		final ItemBuilder item = getFilterItem(Material.NETHERITE_SWORD, killsFilter);
		contents.set(5, 3, ClickableItem.from(item.build(), e -> {
			killsFilter = killsFilter.nextWithLoop();
			open(player, contents.pagination().getPage());
		}));
	}

	private void formatHeadsFilter(Player player, InventoryContents contents) {
		final ItemBuilder item = getFilterItem(Material.ZOMBIE_HEAD, headsFilter);
		contents.set(5, 5, ClickableItem.from(item.build(), e -> {
			headsFilter = headsFilter.nextWithLoop();
			open(player, contents.pagination().getPage());
		}));
	}

	private ItemBuilder getFilterItem(Material zombieHead, IteratableEnum iteratableEnum) {
		return new ItemBuilder(zombieHead).name("&6Filter by:")
			.lore("&7⬇ " + StringUtils.camelCase(iteratableEnum.previousWithLoop().name()))
			.lore("&e⬇ " + StringUtils.camelCase(iteratableEnum.name()))
			.lore("&7⬇ " + StringUtils.camelCase(iteratableEnum.nextWithLoop().name()));
	}

	private interface FilterType extends IteratableEnum {

		String name();

		BiPredicate<MobHeadUser, MobHead> getFilter();

	}

	@Getter
	@AllArgsConstructor
	private enum KillsFilterType implements FilterType {
		OFF(null),
		NO_KILLS((user, mobHead) -> user.get(mobHead).getKills() == 0),
		HAS_KILLS((user, mobHead) -> user.get(mobHead).getKills() > 0),
		;

		private final BiPredicate<MobHeadUser, MobHead> filter;
	}

	@Getter
	@AllArgsConstructor
	private enum HeadsFilterType implements FilterType {
		OFF(null),
		NO_HEADS((user, mobHead) -> user.get(mobHead).getHeads() == 0),
		HAS_HEADS((user, mobHead) -> user.get(mobHead).getHeads() > 0),
		;

		private final BiPredicate<MobHeadUser, MobHead> filter;
	}

}
