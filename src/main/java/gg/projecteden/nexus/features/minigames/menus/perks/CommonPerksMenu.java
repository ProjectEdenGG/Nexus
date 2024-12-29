package gg.projecteden.nexus.features.minigames.menus.perks;

import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.perks.Perk;
import gg.projecteden.nexus.features.minigames.models.perks.PerkCategory;
import gg.projecteden.nexus.features.minigames.models.perks.PerkType;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.perkowner.PerkOwner;
import gg.projecteden.nexus.models.perkowner.PerkOwnerService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public abstract class CommonPerksMenu extends InventoryProvider {
	public static final PerkOwnerService service = new PerkOwnerService();
	protected final PerkCategory category;

	protected static List<String> getLore(Player player, Perk perk) {
		List<String> lore = new ArrayList<>();
		lore.add("&e" + perk.getPerkCategory());
		lore.addAll(perk.getDescription().stream().map(line -> line.formatted(Nickname.of(player))).toList());
		return lore;
	}

	protected static ItemBuilder getItem(Perk perk, List<String> lore) {
		return new ItemBuilder(perk.getMenuItem()).name("&b" + perk.getName()).lore(lore);
	}

	protected static void send(Player player, String message) {
		player.sendMessage(Minigames.PREFIX + StringUtils.colorize(message));
	}

	@Getter
	protected static class PerkSortWrapper {
		private final boolean owned;
		private final String name;
		private final PerkType perkType;
		private final int price;

		public PerkSortWrapper(boolean owned, PerkType perkType) {
			this.owned = owned;
			name = perkType.getPerk().getName();
			this.perkType = perkType;
			price = -1 * perkType.getPerk().getPrice(); // lazy reversal lol
		}

		public static PerkSortWrapper of(PerkOwner owner, PerkType perkType) {
			return new PerkSortWrapper(owner.getPurchasedPerks().containsKey(perkType), perkType);
		}
	}
}
