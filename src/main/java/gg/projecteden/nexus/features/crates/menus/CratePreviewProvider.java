package gg.projecteden.nexus.features.crates.menus;

import com.google.common.util.concurrent.AtomicDouble;
import gg.projecteden.nexus.features.crates.CrateHandler;
import gg.projecteden.nexus.features.crates.Crates;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.Pagination;
import gg.projecteden.nexus.features.minigames.models.perks.PerkType;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.resourcepack.models.font.InventoryTexture;
import gg.projecteden.nexus.framework.exceptions.postconfigured.CrateOpeningException;
import gg.projecteden.nexus.models.crate.CrateConfig.CrateGroup;
import gg.projecteden.nexus.models.crate.CrateConfig.CrateLoot;
import gg.projecteden.nexus.models.crate.CrateDisplay;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.models.perkowner.PerkOwner;
import gg.projecteden.nexus.models.perkowner.PerkOwnerService;
import gg.projecteden.nexus.models.voter.Voter;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

@AllArgsConstructor
public class CratePreviewProvider extends InventoryProvider {

	private static final VoterService voterService = new VoterService();
	private static final PerkOwnerService perkService = new PerkOwnerService();

	private final CrateType type;
	private final CrateGroup group;
	private final ArmorStand clickedCrate;

	@Override
	public String getTitle() {
		return InventoryTexture.getMenuTexture(10, type.getTitleCharacter(), ChatColor.WHITE, 6);
	}

	@Override
	public int getUpdateFrequency() {
		return 30;
	}

	@Override
	public void init() {
		if (group != null) {
			Consumer<ItemClickData> back = e -> new CratePreviewProvider(type, null, clickedCrate).open(viewer);
			for (int i = 0; i < 3; i++)
				contents.set(48 + i, ClickableItem.of(new ItemBuilder(i == 1 ? ItemModelType.GUI_BACK : ItemModelType.INVISIBLE).name("&cBack").build(), back));
		}

		Pagination page = contents.pagination();

		List<ClickableItem> items = new ArrayList<>();

		if (type == CrateType.MINIGAMES) {
			for (int i = 0; i < 28; i++)
				items.add(ClickableItem.empty(new ItemBuilder(RandomUtils.randomElement(PerkType.values()).getPerk().getMenuItem()).name("&eRandom Minigame Cosmetic").build()));
		}
		else {
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
					weightSum.getAndAdd(group.getLootIds().stream().map(CrateLoot::byId).filter(CrateLoot::isActive).mapToDouble(loot -> loot.getWeightForPlayer(viewer)).sum());
			}

			List<CrateDisplay> displays = group == null ? allLoot : new ArrayList<>() {{
				addAll(group.getLootIds().stream().map(CrateLoot::byId).toList());
			}};

			displays.stream()
				.sorted(Comparator.comparingDouble(CrateDisplay::getWeight).reversed())
				.forEachOrdered(display -> {
					if (!display.isActive())
						return;

					ItemBuilder builder = new ItemBuilder(display.getDisplayItem())
						                      .name("&e" + display.getDisplayName())
						                      .amount(1)
						                      .lore("&3Chance: &e" + format.format(((display.getWeightForPlayer(viewer) / weightSum.get()) * 100)) + "%");
					if (display instanceof CrateLoot loot && loot.getItems().size() > 1)
						builder.lore("&7&oClick to see bundle");
					if (display instanceof CrateGroup)
						builder.lore("&7&oClick to see all", "&7&opossible rewards");
					items.add(ClickableItem.of(builder.build(), e -> {
						if (display instanceof CrateLoot loot)
							if (loot.getItems().size() > 1)
								new CratePreviewLootProvider(this, loot).open(viewer);
						if (display instanceof CrateGroup group)
							new CratePreviewProvider(type, group, clickedCrate).open(viewer);
					}));
				});
		}

		for (int i = 0; i < 3; i++)
			contents.set(48 + i, getOpenItem(i == 1));

		paginator().items(items)
			.perPage(28)
			.iterator(MenuUtils.innerSlotIterator(contents))
			.build();

		if (!page.isFirst())
			contents.set(0, 3, ClickableItem.of(new ItemBuilder(Material.ARROW).name("<-- Back").build(), e ->
				new CratePreviewProvider(type, group, clickedCrate).open(viewer, page.previous())));
		if (!page.isLast())
			contents.set(5, 3, ClickableItem.of(new ItemBuilder(Material.ARROW).name("Next -->").build(), e ->
				new CratePreviewProvider(type, group, clickedCrate).open(viewer, page.next())));
	}

	public Consumer<ItemClickData> getOpenButtonAction() {
		Consumer<ItemClickData> onClick = null;
		if (type == CrateType.VOTE) {
			final Voter voter = voterService.get(viewer);
			if (voter.getPoints() >= 2)
				onClick = e -> {
					if (voter.getPoints() < 2)
						return;

					voter.takePoints(2);
					voterService.save(voter);
					close();

					try {
						if (CrateHandler.isInUse(clickedCrate)) {
							PlayerUtils.send(viewer, Crates.PREFIX + "That crate is already being used");
							voter.givePoints(2);
							voterService.save(voter);
							return;
						}
						CrateHandler.openCrate(type, clickedCrate, viewer, 1, false);
					} catch (CrateOpeningException ex) {
						if (ex.getMessage() != null)
							PlayerUtils.send(viewer, Crates.PREFIX + ex.getMessage());
						CrateHandler.reset(clickedCrate);
						voter.givePoints(2);
						voterService.save(voter);
					}
				};
		}

		if (type == CrateType.MINIGAMES) {
			PerkOwner perkOwner = perkService.get(viewer);
			if (perkOwner.getTokens() >= 50)
				onClick =e -> {
					if (perkOwner.getTokens() < 50)
						return;

					perkOwner.takeTokens(50);
					perkService.save(perkOwner);
					close();

					try {
						if (CrateHandler.isInUse(clickedCrate)) {
							PlayerUtils.send(viewer, Crates.PREFIX + "That crate is already being used");
							perkOwner.giveTokens(50);
							perkService.save(perkOwner);
							return;
						}
						CrateHandler.openCrate(type, clickedCrate, viewer, 1, false);
					} catch (CrateOpeningException ex) {
						if (ex.getMessage() != null)
							PlayerUtils.send(viewer, Crates.PREFIX + ex.getMessage());
						CrateHandler.reset(clickedCrate);
						perkOwner.giveTokens(50);
						perkService.save(perkOwner);
					}
				};
		}

		Consumer<ItemClickData> finalOnClick = onClick;
		return e -> {
			int keys = getAvailableKeys();

			if (keys == 0 && finalOnClick != null) {
				finalOnClick.accept(e);
				return;
			}

			try {
				if (keys > 1 && e.isShiftClick())
					ConfirmationMenu.builder()
						.title("Open " + keys + " keys?")
						.onConfirm(e2 -> CrateHandler.openCrate(type, clickedCrate, viewer, keys, true))
						.open(viewer);
				else {
					CrateHandler.openCrate(type, clickedCrate, viewer, 1, true);
					close();
				}
			} catch (CrateOpeningException ex) {
				if (ex.getMessage() != null)
					PlayerUtils.send(viewer, Crates.PREFIX + ex.getMessage());
				CrateHandler.reset(clickedCrate);
			}
		};
	}

	public ClickableItem getOpenItem(boolean main) {
		ItemBuilder builder = new ItemBuilder(main ? ItemModelType.GUI_OPEN : ItemModelType.INVISIBLE)
			.name("&eOpen");

		int keys = getAvailableKeys();
		if (keys > 0) {
			builder.lore("&3Click to open &e1");
			if (keys > 1)
				builder.lore("&3Shift-Click to open &e" + keys);
		}

		boolean canPurchase = false;

		if (keys == 0) {
			if (type == CrateType.VOTE) {
				final Voter voter = voterService.get(viewer);
				if (voter.getPoints() >= 2) {
					canPurchase = true;
					builder.lore("&3Purchase for &e2 Vote Points");
					builder.lore("&3Your Points: &e" + voter.getPoints());
				}
			}
			if (type == CrateType.MINIGAMES) {
				PerkOwner perkOwner = perkService.get(viewer);
				if (perkOwner.getTokens() >= 50) {
					canPurchase = true;
					builder.lore("&3Purchase for &e50 Tokens");
					builder.lore("&3Your Tokens: &e" + perkOwner.getTokens());
				}
			}
		}

		if (keys == 0 && !canPurchase) {
			builder.name("&7No keys available");
			if (main)
				builder.model(ItemModelType.GUI_OPEN_DISABLED);
		}


		return ClickableItem.of(builder.build(), getOpenButtonAction());
	}

	public int getAvailableKeys() {
		int count = 0;
		for (ItemStack item : viewer.getInventory().getContents()) {
			if (Nullables.isNullOrAir(item))
				continue;
			if (ItemUtils.isModelMatch(item, type.getKey()))
				count += item.getAmount();
		}
		return count;
	}

	@Override
	public void update() {
		if (type == CrateType.MINIGAMES) {
			List<ClickableItem> items = new ArrayList<>();

			for (int i = 0; i < 28; i++)
				items.add(ClickableItem.empty(new ItemBuilder(RandomUtils.randomElement(PerkType.values()).getPerk().getMenuItem()).name("&eRandom Minigame Cosmetic").build()));

			paginator().items(items)
				.perPage(28)
				.iterator(MenuUtils.innerSlotIterator(contents))
				.build();
		}
	}

	@NoArgsConstructor
	@AllArgsConstructor
	private static class CratePreviewLootProvider extends InventoryProvider {

		InventoryProvider previous;
		CrateLoot loot;

		@Override
		public String getTitle() {
			return InventoryTexture.getMenuTexture(10, loot.getType().getTitleCharacter(), ChatColor.WHITE, 6);
		}

		@Override
		public void init() {
			Consumer<ItemClickData> back = e -> previous.open(viewer);
			for (int i = 0; i < 3; i++)
				contents.set(48 + i, ClickableItem.of(new ItemBuilder(i == 1 ? ItemModelType.GUI_BACK : ItemModelType.INVISIBLE).name("&cBack").build(), back));

			List<ClickableItem> items = new ArrayList<>();
			loot.getItems().forEach(itemStack -> items.add(ClickableItem.empty(itemStack)));

			Pagination page = contents.pagination();
			paginator().items(items)
				.perPage(28)
				.iterator(MenuUtils.innerSlotIterator(contents))
				.build();

			if (!page.isFirst())
				contents.set(0, 3, ClickableItem.of(new ItemBuilder(Material.ARROW).name("<-- Back").build(), e ->
					new CratePreviewLootProvider(previous, loot).open(viewer, page.previous())));
			if (!page.isLast())
				contents.set(5, 3, ClickableItem.of(new ItemBuilder(Material.ARROW).name("Next -->").build(), e ->
					new CratePreviewLootProvider(previous, loot).open(viewer, page.next())));
		}
	}

}
