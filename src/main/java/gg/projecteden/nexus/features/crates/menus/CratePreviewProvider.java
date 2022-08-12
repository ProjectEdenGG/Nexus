package gg.projecteden.nexus.features.crates.menus;

import com.google.common.util.concurrent.AtomicDouble;
import gg.projecteden.nexus.features.crates.Crates;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.Pagination;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.models.crate.CrateConfig.CrateGroup;
import gg.projecteden.nexus.models.crate.CrateConfig.CrateLoot;
import gg.projecteden.nexus.models.crate.CrateDisplay;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.models.voter.Voter;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.Material;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
public class CratePreviewProvider extends InventoryProvider {

	private static final VoterService voterService = new VoterService();

	private final CrateType type;
	private final CrateGroup group;

	@Override
	public String getTitle() {
		return StringUtils.camelCase(type.name()) + " Crate Rewards";
	}

	@Override
	public void init() {
		contents.fillBorders(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build()));
		if (group != null)
			addBackItem(e -> new CratePreviewProvider(type, null).open(player));

		Pagination page = contents.pagination();

		if (type == CrateType.VOTE) {
			final Voter voter = voterService.get(player);
			contents.set(0, 4, ClickableItem.of(
					new ItemBuilder(CustomMaterial.CRATE_KEY_VOTE).name("&eBuy 1 Key for 2 Vote Points")
							.lore("&3Your Points: &e" + voter.getPoints()).build(),
					e -> {
						if (voter.getPoints() < 2)
							return;

						voter.takePoints(2);
						voterService.save(voter);
						type.giveVPS(player, 1);
						new CratePreviewProvider(type, group).open(player, page.getPage());
					}
			));
		}

		List<ClickableItem> items = new ArrayList<>();

		List<CrateDisplay> allLoot = new ArrayList<>() {{
			addAll(Crates.getLootByType(type));
			List<CrateGroup> groups = Crates.getGroupsByType(type);
			removeIf(id -> {
				if (id instanceof CrateLoot loot)
					return groups.stream().anyMatch(group -> group.getLootIds().contains(loot.getId()));
				return false;
			});
			addAll(groups);
		}};

		DecimalFormat format = new DecimalFormat("#0.00");
		AtomicDouble weightSum = new AtomicDouble(0);

		for (CrateDisplay display : allLoot) {
			if (display instanceof CrateLoot loot)
				if (loot.isActive())
					weightSum.getAndAdd(loot.getWeight());

			if (display instanceof CrateGroup group)
				weightSum.getAndAdd(group.getLootIds().stream().map(CrateLoot::byId).filter(CrateLoot::isActive).mapToDouble(CrateLoot::getWeight).sum());
		}

		List<CrateDisplay> displays = group == null ? allLoot : new ArrayList<>() {{ addAll(group.getLootIds().stream().map(CrateLoot::byId).toList()); }};

		displays.stream()
			.sorted(Comparator.comparingDouble(CrateDisplay::getWeight).reversed())
			.forEachOrdered(display -> {
				if (!display.isActive())
						return;

				ItemBuilder builder = new ItemBuilder(display.getDisplayItem())
					.name("&e" + display.getDisplayName())
					.amount(1)
					.lore("&3Chance: &e" + format.format(((display.getWeight() / weightSum.get()) * 100)) + "%");
				if (display instanceof CrateLoot loot && loot.getItems().size() > 1)
					builder.lore("&7&oClick to see bundle");
				if (display instanceof CrateGroup)
					builder.lore("&7&oClick to see all", "&7&opossible rewards");
				items.add(ClickableItem.of(builder.build(), e -> {
					if (display instanceof CrateLoot loot)
						if (loot.getItems().size() > 1)
							new CratePreviewLootProvider(this, loot).open(player);
					if (display instanceof CrateGroup group)
						new CratePreviewProvider(type, group).open(player);
				}));
			});

		paginator().items(items)
			.perPage(28)
			.iterator(MenuUtils.innerSlotIterator(contents))
			.build();

		if (!page.isFirst())
			contents.set(0, 3, ClickableItem.of(new ItemBuilder(Material.ARROW).name("<-- Back").build(), e ->
				new CratePreviewProvider(type, group).open(player, page.previous())));
		if (!page.isLast())
			contents.set(5, 3, ClickableItem.of(new ItemBuilder(Material.ARROW).name("Next -->").build(), e ->
				new CratePreviewProvider(type, group).open(player, page.next())));
	}

	@NoArgsConstructor
	@AllArgsConstructor
	private static class CratePreviewLootProvider extends InventoryProvider {

		InventoryProvider previous;
		CrateLoot loot;

		@Override
		public void init() {
			contents.fillBorders(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build()));
			addBackItem(e -> previous.open(player));

			List<ClickableItem> items = new ArrayList<>();
			loot.getItems().forEach(itemStack -> items.add(ClickableItem.empty(itemStack)));

			Pagination page = contents.pagination();
			paginator().items(items)
				.perPage(28)
				.iterator(MenuUtils.innerSlotIterator(contents))
				.build();

			if (!page.isFirst())
				contents.set(0, 3, ClickableItem.of(new ItemBuilder(Material.ARROW).name("<-- Back").build(), e ->
					new CratePreviewLootProvider(previous, loot).open(player, page.previous())));
			if (!page.isLast())
				contents.set(5, 3, ClickableItem.of(new ItemBuilder(Material.ARROW).name("Next -->").build(), e ->
					new CratePreviewLootProvider(previous, loot).open(player, page.next())));
		}
	}

}
