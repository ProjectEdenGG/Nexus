package me.pugabyte.bncore.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.ItemClickData;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class AddProductProvider extends _ShopProvider {
	private Material chosen;

	public AddProductProvider(_ShopProvider previousMenu) {
		this.previousMenu = previousMenu;
	}

	@Override
	public void open(Player viewer, int page) {
		SmartInventory.builder()
				.provider(this)
				.title(colorize("&0Add Item"))
				.size(6, 9)
				.build()
				.open(viewer, page);
	}

	private final ItemStack less8 = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).amount(8).name("&cDecrease amount by 8").build();
	private final ItemStack less1 = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name("&cDecrease amount").build();
	private final ItemStack more1 = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("&aIncrease amount").build();
	private final ItemStack more8 = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).amount(8).name("&aIncrease amount by 8").build();

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);

		addItemSelector(player, contents);
	}

	public void addItemSelector(Player player, InventoryContents contents) {
		ItemStack placeholder = new ItemBuilder(Material.BLACK_STAINED_GLASS).name("&ePlace your item here").lore("&7or click to search for an item").build();

		AtomicReference<Consumer<ItemClickData>> action = new AtomicReference<>();

		BiConsumer<InventoryContents, AtomicReference<Consumer<ItemClickData>>> panes = (contents1, action1) -> {
			if (contents.get(1, 4).isPresent() && contents.get(1, 4).get().getItem().equals(placeholder)) {
				contents.set(1, 2, ClickableItem.empty(less8));
				contents.set(1, 3, ClickableItem.empty(less1));
				contents.set(1, 5, ClickableItem.empty(more1));
				contents.set(1, 6, ClickableItem.empty(more8));
			} else {
				contents1.set(1, 2, ClickableItem.from(less8, e2 -> contents1.get(1, 4).ifPresent(i -> {
					ItemStack item2 = i.getItem();
					item2.setAmount(Math.max(1, item2.getAmount() == 64 ? 56 : item2.getAmount() - 8));
					contents1.set(1, 4, ClickableItem.from(item2, action1.get()));
				})));
				contents1.set(1, 3, ClickableItem.from(less1, e2 -> contents1.get(1, 4).ifPresent(i -> {
					ItemStack item2 = i.getItem();
					item2.setAmount(Math.max(1, item2.getAmount() - 1));
					contents1.set(1, 4, ClickableItem.from(item2, action1.get()));
				})));
				contents1.set(1, 5, ClickableItem.from(more1, e2 -> contents1.get(1, 4).ifPresent(i -> {
					ItemStack item2 = i.getItem();
					item2.setAmount(Math.min(64, item2.getAmount() + 1));
					contents1.set(1, 4, ClickableItem.from(item2, action1.get()));
				})));
				contents1.set(1, 6, ClickableItem.from(more8, e2 -> contents1.get(1, 4).ifPresent(i -> {
					ItemStack item2 = i.getItem();
					item2.setAmount(Math.min(64, item2.getAmount() == 1 ? 8 : item2.getAmount() + 8));
					contents1.set(1, 4, ClickableItem.from(item2, action1.get()));
				})));
			}
		};

		Runnable defaultFormat = () -> {
			if (chosen != null) {
				contents.set(1, 4, ClickableItem.from(new ItemStack(chosen), action.get()));
				chosen = null; // one time use
			} else
				contents.set(1, 4, ClickableItem.from(placeholder, action.get()));

			panes.accept(contents, action);
		};

		action.set(e -> {
			((InventoryClickEvent) e.getEvent()).setCancelled(true);
			if (!Utils.isNullOrAir(player.getItemOnCursor())) {
				ItemBuilder item = new ItemBuilder(player.getItemOnCursor().clone());
				contents.set(1, 4, ClickableItem.from(item.build(), action.get()));
				panes.accept(contents, action);
			} else if (contents.get(1, 4).isPresent() && contents.get(1, 4).get().getItem().equals(placeholder)) {
				BNCore.getSignMenuFactory().lines("", "^ ^ ^ ^ ^ ^", "Enter a", "search term").response((_player, response) -> {
					try {
						if (response[0].length() > 0) {
							Function<Material, Boolean> filter = material -> material.name().toLowerCase().contains(response[0].toLowerCase());
							new ItemSearchProvider(this, filter, onChoose -> {
								chosen = onChoose.getItem().getType();
								open(player);
							}).open(player);
						} else
							open(player);
					} catch (Exception ex) {
						_player.sendMessage(ex.getMessage());
						open(player);
					}
				}).open(player);
			} else {
				defaultFormat.run();
			}
		});

		defaultFormat.run();
	}

}
