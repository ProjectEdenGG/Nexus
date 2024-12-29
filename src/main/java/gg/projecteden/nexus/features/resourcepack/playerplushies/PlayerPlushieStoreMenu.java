package gg.projecteden.nexus.features.resourcepack.playerplushies;

import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.ResourcePack.ResourcePackNumber;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.PlayerPlushie;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.store.gallery.StoreGallery;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieUser;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieUserService;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.models.warps.Warps.Warp;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemFlags;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerPlushieStoreMenu extends InventoryProvider {
	private final PlayerPlushieUserService userService = new PlayerPlushieUserService();
	private PlayerPlushieUser user;

	@Override
	public void init() {
		addCloseItem();

		user = userService.get(viewer);

		contents.set(SlotPos.of(0, 8), ClickableItem.empty(new ItemBuilder(CustomMaterial.VOUCHER)
			.dyeColor(Color.ORANGE)
			.itemFlags(ItemFlags.HIDE_ALL)
			.name("Vouchers: &e" + user.getVouchers())));

		final List<ClickableItem> items = new ArrayList<>();
		for (Tier tier : EnumUtils.valuesExcept(Tier.class, Tier.SERVER)) {
			items.add(ClickableItem.empty(ResourcePackNumber.of(tier.ordinal() + 1, ColorType.CYAN).get()
				.name(gg.projecteden.api.common.utils.StringUtils.camelCase(tier))
				.itemFlags(ItemFlags.HIDE_ALL)));

			for (Pose pose : Pose.of(tier)) {
				PlayerPlushie plushie = user.getOrDefault(pose);
				ItemBuilder plushieItem = new ItemBuilder(plushie.getItem());

				plushieItem.resetLore();
				plushieItem.lore("&3Price: " + (user.canPurchase(pose) ? "&a" : "&c") + pose.getCost() + " vouchers");
				plushieItem.lore("&f");
				plushieItem.lore("&eShift+Click to view");
				if (user.canPurchase(pose))
					plushieItem.lore("&eClick to purchase");
				else {
					plushieItem.lore("&f");
					plushieItem.lore("&ePurchase vouchers on the &c/store");
				}

				items.add(ClickableItem.of(plushieItem, e -> click(plushie.getPose(), e)));
			}

			while (items.size() % 9 != 0)
				items.add(ClickableItem.AIR);
		}

		paginate(items);
	}

	public static final String ITEM_FRAME_UUID = "7ad0e359-c969-43a0-9dba-cceadeff2d97";

	private void click(Pose pose, ItemClickData e) {
		try {
			if (e.isShiftClick()) {
				final Warp warp = WarpType.NORMAL.get("playerplushies");
				final Location location = Objects.requireNonNull(warp.getLocation()).clone().add(-1, 0, 1);
				location.getWorld().getChunkAtAsync(location, (Consumer<Chunk>) chunk -> {
					if (Bukkit.getEntity(UUID.fromString(ITEM_FRAME_UUID)) instanceof ItemFrame itemFrame) {
						itemFrame.setItem(user.getOrDefault(pose).getItemBuilder().resetName().build());
						itemFrame.setSilent(true);
					}
				});
				viewer.closeInventory();
				if (!StoreGallery.isInStoreGallery(viewer))
					warp.teleportAsync(viewer);
				return;
			}

			user.checkPurchase(pose);

			final ItemStack item = user.get(pose).getItem();

			ConfirmationMenu.builder()
				.titleWithSlot("&4Are you sure?")
				.displayItem(item)
				.onCancel(e2 -> refresh())
				.onConfirm(e2 -> {
					try {
						user.checkPurchase(pose);
						PlayerUtils.giveItemAndMailExcess(viewer, item, WorldGroup.of(viewer));
						user.takeVouchers(pose);
						userService.save(user);
						close();
					} catch (Exception ex) {
						MenuUtils.handleException(viewer, StringUtils.getPrefix("PlayerPlushies"), ex);
					}
				})
				.open(viewer);
		} catch (Exception ex) {
			MenuUtils.handleException(viewer, StringUtils.getPrefix("PlayerPlushies"), ex);
		}

	}
}
