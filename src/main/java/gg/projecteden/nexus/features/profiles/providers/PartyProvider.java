package gg.projecteden.nexus.features.profiles.providers;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.party.Party;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Rows(3)
public class PartyProvider extends InventoryProvider {
	InventoryProvider previousMenu = null;
	Party party;

	SlotPos leaderSlot = SlotPos.of(1, 1);

	public PartyProvider(Party party, @Nullable InventoryProvider previousMenu) {
		this(party);
		this.previousMenu = previousMenu;
	}

	public PartyProvider(Party party) {
		this.party = party;
	}

	@Override
	public void init() {
		addBackOrCloseItem(previousMenu);

		UUID owner = party.getOwner();
		addPartyMember(leaderSlot, owner, "Leader");

		int col = 3;
		for (UUID member : party.getMembers()) {
			if (member == owner)
				continue;

			addPartyMember(new SlotPos(1, col++), member, "Member");
		}
	}

	private void addPartyMember(SlotPos slotPos, UUID member, String lore) {
		Nerd nerd = Nerd.of(member);

		ItemBuilder skullOwner = new ItemBuilder(Material.PLAYER_HEAD)
			.skullOwner(member)
			.name("&3" + nerd.getNickname())
			.lore("&e" + lore);

		if (!nerd.isOnline())
			skullOwner.lore("&7Offline");

		skullOwner.lore("", "&eClick &3to view profile");

		contents.set(slotPos, ClickableItem.of(skullOwner, e ->
			new ProfileProvider(nerd.getOfflinePlayer(), this).open(viewer)));
	}
}
