package gg.projecteden.nexus.features.resourcepack.playerplushies;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.PlayerPlushie;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieConfig;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieUser;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerPlushiePickerProvider extends InventoryProvider {
	PlayerPlushieUserService userService = new PlayerPlushieUserService();
	PlayerPlushieUser user;

	Map<Pose, List<UUID>> subscriptions = PlayerPlushieConfig.get().getSubscriptions();


	@Override
	public void init() {
		user = userService.get(viewer);
		ItemBuilder voucherInfo = new ItemBuilder(Material.BOOK).name("Vouchers:").lore("TODO");

		addCloseItem();

		contents.set(SlotPos.of(0, 8), ClickableItem.empty(voucherInfo));

		List<Tier> tierList = List.of(Tier.TIER_1, Tier.TIER_2, Tier.TIER_3);
		for (int row = 1; row < 4; row++) {
			contents.set(SlotPos.of(row, 0), ClickableItem.empty(getUINumber(row)));

			int column = 1;
			for (Pose pose : Pose.of(tierList.get(row - 1))) {
				PlayerPlushie plushie = Pose.plushieMap.get(pose);
				ItemBuilder plushieItem = new ItemBuilder(plushie.getItem());

				contents.set(SlotPos.of(row, column++), ClickableItem.of(plushieItem, e -> buyPlushie(plushie)));
			}
		}
	}

	// TODO
	private void buyPlushie(PlayerPlushie plushie) {
		PlayerUtils.send(viewer, "TODO: BUY PLUSHIE POSE - " + plushie.getPose());

	}
}
