package gg.projecteden.nexus.features.resourcepack.playerplushies;

import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.ResourcePack.ResourcePackNumber;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.PlayerPlushie;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieUser;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieUserService;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemFlags;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;

public class PlayerPlushieStoreMenu extends InventoryProvider {
	private final PlayerPlushieUserService userService = new PlayerPlushieUserService();
	private PlayerPlushieUser user;

	@Override
	public void init() {
		addCloseItem();

		user = userService.get(viewer);

		ItemBuilder voucherInfo = new ItemBuilder(Material.BOOK).name("Vouchers:").lore("TODO");
		contents.set(SlotPos.of(0, 8), ClickableItem.empty(voucherInfo));

		final List<ClickableItem> items = new ArrayList<>();
		for (Tier tier : EnumUtils.valuesExcept(Tier.class, Tier.SERVER)) {
			items.add(ClickableItem.empty(ResourcePackNumber.of(tier.ordinal() + 1, ColorType.CYAN).get()
				.name(camelCase(tier))
				.itemFlags(ItemFlags.HIDE_ALL)));

			for (Pose pose : Pose.of(tier)) {
				PlayerPlushie plushie = user.getOrDefault(pose);
				ItemBuilder plushieItem = new ItemBuilder(plushie.getItem());

				plushieItem.resetLore();
				plushieItem.lore("&eVouchers: " + (user.hasVouchers(tier) ? "&a" : "&c") + user.getVouchers(tier));
				plushieItem.lore("&f");
				if (user.hasVouchers(tier))
					plushieItem.lore("&fClick to purchase");
				else
					plushieItem.lore("&ePurchase vouchers on the &c/store");

				items.add(ClickableItem.of(plushieItem, e -> buy(plushie)));
			}

			while (items.size() % 9 != 0)
				items.add(ClickableItem.AIR);
		}

		paginate(items);
	}

	private void buy(PlayerPlushie plushie) {
		final ItemStack item = user.get(plushie.getPose()).getItem();
		ConfirmationMenu.builder()
			.displayItem(item)
			.onCancel(e -> refresh())
			.onConfirm(e -> {
				try {
					if (user.canPurchase(plushie.getPose())) {
						PlayerUtils.giveItemAndMailExcess(viewer, item, WorldGroup.of(viewer));
						user.takeVouchers(plushie.getPose().getTier(), 1);
					}
				} catch (Exception ex) {
					MenuUtils.handleException(viewer, StringUtils.getPrefix("PlayerPlushies"), ex);
				}
			})
			.open(viewer);

	}
}
