package gg.projecteden.nexus.features.crates.menus;

import com.google.common.util.concurrent.AtomicDouble;
import gg.projecteden.nexus.features.crates.Crates;
import gg.projecteden.nexus.features.crates.models.CrateLoot;
import gg.projecteden.nexus.features.crates.models.CrateType;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.Pagination;
import gg.projecteden.nexus.models.voter.Voter;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
public class CratePreviewProvider extends InventoryProvider {
	private final CrateType type;
	private final CrateLoot loot;

	@Override
	public void open(Player player, int page) {
		SmartInventory.builder()
			.provider(this)
			.title(StringUtils.camelCase(type.name()) + " Crate Rewards")
			.maxSize()
			.build()
			.open(player, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.fillBorders(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build()));

		Pagination page = contents.pagination();

		if (type == CrateType.VOTE) {
			final VoterService voterService = new VoterService();
			final Voter voter = voterService.get(player);
			contents.set(0, 4, ClickableItem.of(
					new ItemBuilder(Material.TRIPWIRE_HOOK).glow().name("&eBuy 1 Key for 2 Vote Points")
							.lore("&3Your Points: &e" + voter.getPoints()).build(),
					e -> {
						if (voter.getPoints() < 2)
							return;

						voter.takePoints(2);
						voterService.save(voter);
						type.giveVPS(player, 1);
						new CratePreviewProvider(type, null).open(player, page.getPage());
					}
			));
		}

		List<ClickableItem> items = new ArrayList<>();
		if (loot == null) {
			List<CrateLoot> crateLoots = Crates.getLootByType(type);
			DecimalFormat format = new DecimalFormat("#0.00");
			AtomicDouble weightSum = new AtomicDouble(0);

			for (CrateLoot loot : crateLoots)
				if (loot.isActive())
					weightSum.getAndAdd(loot.getWeight());

			crateLoots.stream()
				.sorted(Comparator.comparingDouble(CrateLoot::getWeight).reversed())
				.forEachOrdered(crateLoot -> {
					if (!crateLoot.isActive())
						return;

					ItemBuilder builder = new ItemBuilder(crateLoot.getDisplayItem())
						.name("&e" + crateLoot.getTitle())
						.amount(1)
						.lore("&3Chance: &e" + format.format(((crateLoot.getWeight() / weightSum.get()) * 100)) + "%")
						.lore("&7&oClick for more");
					items.add(ClickableItem.of(builder.build(), e -> new CratePreviewProvider(type, crateLoot).open(player)));
				});
		} else {
			loot.getItems().forEach(itemStack -> items.add(ClickableItem.empty(itemStack)));
			addBackItem(contents, e -> new CratePreviewProvider(type, null).open(player));
		}

		paginator(player, contents, items)
			.perPage(28)
			.iterator(MenuUtils.innerSlotIterator(contents))
			.build();

		if (!page.isFirst())
			contents.set(0, 3, ClickableItem.of(new ItemBuilder(Material.ARROW).name("<-- Back").build(), e ->
				new CratePreviewProvider(type, loot).open(player, page.previous())));
		if (!page.isLast())
			contents.set(5, 3, ClickableItem.of(new ItemBuilder(Material.ARROW).name("Next -->").build(), e ->
				new CratePreviewProvider(type, loot).open(player, page.next())));
	}
}
