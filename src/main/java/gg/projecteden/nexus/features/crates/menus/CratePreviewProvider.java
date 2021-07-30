package gg.projecteden.nexus.features.crates.menus;

import com.google.common.util.concurrent.AtomicDouble;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import gg.projecteden.nexus.features.crates.Crates;
import gg.projecteden.nexus.features.crates.models.CrateLoot;
import gg.projecteden.nexus.features.crates.models.CrateType;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.models.voter.Voter;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CratePreviewProvider extends MenuUtils implements InventoryProvider {

	CrateType type;
	CrateLoot loot;

	public CratePreviewProvider(CrateType type, CrateLoot loot) {
		this.type = type;
		this.loot = loot;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.fillBorders(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build()));

		Pagination page = contents.pagination();

		if (type == CrateType.VOTE)
			contents.set(0, 4, ClickableItem.from(
					new ItemBuilder(Material.TRIPWIRE_HOOK).glow().name("&eBuy 1 Key for 2 Vote Points")
							.lore("&3Your Points: &e" + new VoterService().get(player).getPoints()).build(),
					e -> {
						Voter voter = new VoterService().get(player);
						if (voter.getPoints() < 2) return;
						voter.takePoints(2);
						type.giveVPS(player, 1);
						type.previewDrops(null).open(player, page.getPage());
					}
			));

		List<ClickableItem> items = new ArrayList<>();
		if (loot == null) {
			List<CrateLoot> crateLoots = Crates.getLootByType(type);
			DecimalFormat format = new DecimalFormat("#0.00");
			AtomicDouble weightSum = new AtomicDouble(0);
			for (CrateLoot loot : crateLoots)
				weightSum.getAndAdd(loot.getWeight());
			crateLoots.stream().sorted(Comparator.comparingDouble(CrateLoot::getWeight).reversed())
					.forEachOrdered(crateLoot -> {
						ItemBuilder builder = new ItemBuilder(crateLoot.getDisplayItem())
								.name("&e" + crateLoot.getTitle())
								.amount(1)
								.lore("&3Chance: &e" + format.format(((crateLoot.getWeight() / weightSum.get()) * 100)) + "%")
								.lore("&7&oClick for more");
						items.add(ClickableItem.from(builder.build(), e -> type.previewDrops(crateLoot).open(player)));
					});
		} else {
			loot.getItems().forEach(itemStack -> items.add(ClickableItem.empty(itemStack)));
			addBackItem(contents, e -> type.previewDrops(null).open(player));
		}

		page.setItems(items.toArray(ClickableItem[]::new));
		page.setItemsPerPage(28);
		SlotIterator.Impl iterator = new SlotIterator.Impl(contents, type.previewDrops(loot), SlotIterator.Type.HORIZONTAL, 1, 1);
		for (int c = 0; c < 2; c++)
			for (int r = 0; r < 6; r++)
				iterator.blacklist(r, c * 8);
		page.addToIterator(iterator);

		if (!page.isFirst())
			contents.set(0, 3, ClickableItem.from(new ItemBuilder(Material.ARROW).name("<-- Back").build(), e ->
					type.previewDrops(loot).open(player, page.previous().getPage())));
		if (!page.isLast())
			contents.set(5, 3, ClickableItem.from(new ItemBuilder(Material.ARROW).name("Next -->").build(), e ->
					type.previewDrops(loot).open(player, page.next().getPage())));
	}
}
