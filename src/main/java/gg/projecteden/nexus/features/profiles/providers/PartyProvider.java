package gg.projecteden.nexus.features.profiles.providers;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SignMenuFactory;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.party.PartyCommand;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.party.Party;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemFlags;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Rows(3)
public class PartyProvider extends InventoryProvider {
	InventoryProvider previousMenu = null;
	Party party;
	String TODO = "&cTODO";
	private static final SignMenuFactory signMenuFactory = Nexus.getSignMenuFactory();

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

		fillSettings(owner);
		fillMembers(owner);
	}

	private void fillMembers(UUID owner) {
		int members = 1;
		addPartyMember(SlotPos.of(1, 1), owner, "&eLeader");
		int col = 3;
		for (UUID member : party.getMembers()) {
			if (member == owner)
				continue;

			addPartyMember(SlotPos.of(1, col++), member, null);
			members++;
		}

		if (members < PartyCommand.getPARTY_SIZE_LIMIT()) {
			ItemBuilder invitePlayer = new ItemBuilder(CustomMaterial.GUI_PLUS)
				.dyeColor(Color.LIME)
				.itemFlags(ItemFlags.HIDE_ALL)
				.name("&eInvite a player")
				.lore("&eClick &3to enter a player's name'");

			contents.set(SlotPos.of(1, col), ClickableItem.of(invitePlayer, e ->
				signMenuFactory.lines("", "Enter a player's name")
					.response(lines -> {
						String input = lines[0];
						if (input.length() > 0)
							PlayerUtils.runCommand(viewer, "party invite " + input);

						refresh();
					})
					.open(viewer)));
		}
	}

	private void fillSettings(UUID owner) {
		if (owner == viewer.getUniqueId()) {
			contents.set(SlotPos.of(0, 3),
				ClickableItem.empty(new ItemBuilder(Material.ENDER_PEARL).name("&eWarp").lore(TODO)));
			contents.set(SlotPos.of(0, 5),
				ClickableItem.empty(new ItemBuilder(Material.OAK_DOOR).name(party.isOpen() ? "&cClose" : "&aOpen").lore(TODO)));
			contents.set(SlotPos.of(0, 8),
				ClickableItem.empty(new ItemBuilder(Material.TNT).name("&cDisband").lore(TODO)));

			contents.set(SlotPos.of(2, 4),
				ClickableItem.empty(new ItemBuilder(Material.TNT).name("&eShare Exp").lore(TODO)));

			return;
		}

		contents.set(SlotPos.of(0, 8),
			ClickableItem.empty(new ItemBuilder(Material.TNT).name("&cLeave").lore(TODO)));

		contents.set(SlotPos.of(2, 3),
			ClickableItem.empty(new ItemBuilder(Material.EXPERIENCE_BOTTLE).name("&eShare Exp").lore(TODO)));
		contents.set(SlotPos.of(2, 5),
			ClickableItem.empty(new ItemBuilder(Material.ENDER_PEARL).name("&eWarp Setting").lore(TODO)));
	}

	private void addPartyMember(SlotPos slotPos, UUID member, String lore) {
		Nerd nerd = Nerd.of(member);

		ItemBuilder skullOwner = new ItemBuilder(Material.PLAYER_HEAD)
			.skullOwner(member)
			.name("&3" + nerd.getNickname())
			.lore(lore);

		if (!nerd.isOnline())
			skullOwner.lore("&7Offline");

		skullOwner.lore("", "&eClick &3to view profile");

		contents.set(slotPos, ClickableItem.of(skullOwner, e ->
			new ProfileProvider(nerd.getOfflinePlayer(), this).open(viewer)));
	}
}
