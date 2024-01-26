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
import gg.projecteden.nexus.models.party.PartyUser;
import gg.projecteden.nexus.models.party.PartyUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemFlags;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static gg.projecteden.nexus.features.menus.api.SignMenuFactory.ARROWS;

@Rows(3)
public class PartyProvider extends InventoryProvider {
	InventoryProvider previousMenu = null;
	Party party;
	UUID partyOwner;
	boolean isViewerOwner;
	private static final SignMenuFactory signMenuFactory = Nexus.getSignMenuFactory();

	public PartyProvider(Player viewer, Party party, @Nullable InventoryProvider previousMenu) {
		this(viewer, party);
		this.previousMenu = previousMenu;
	}

	public PartyProvider(Player viewer, Party party) {
		this.viewer = viewer;
		this.party = party;
		this.partyOwner = party.getOwner();
		this.isViewerOwner = viewer.getUniqueId().equals(partyOwner);
	}

	@Override
	public String getTitle() {
		if (isViewerOwner)
			return "Edit Party";

		return "View Party";
	}

	@Override
	public void init() {
		addBackOrCloseItem(previousMenu);

		fillSettings();
		fillMembers();
	}

	private void fillMembers() {
		int members = 1;
		addPartyMember(SlotPos.of(1, 1), partyOwner, "&eLeader");
		int col = 3;
		for (UUID member : party.getMembers()) {
			if (member == partyOwner)
				continue;

			addPartyMember(SlotPos.of(1, col++), member, null);
			members++;
		}

		if (members < PartyCommand.getPARTY_SIZE_LIMIT()) {
			ItemBuilder invitePlayer = new ItemBuilder(CustomMaterial.GUI_PLUS)
				.dyeColor(Color.LIME)
				.itemFlags(ItemFlags.HIDE_ALL)
				.name("&eInvite a player")
				.lore("&eClick &3to enter a player's name");

			contents.set(SlotPos.of(1, col), ClickableItem.of(invitePlayer, e ->
				signMenuFactory.lines("", ARROWS, "Enter a", "player's name")
					.response(lines -> {
						String input = lines[0];
						if (input.length() > 0)
							PlayerUtils.runCommand(viewer, "party invite " + input);

						refresh();
					})
					.open(viewer)));
		}
	}

	private void fillSettings() {
		PartyUser partyUser = new PartyUserService().get(viewer);

		boolean isXPSharing = partyUser.isXpShare();
		ItemBuilder shareXPItem = new ItemBuilder(Material.TNT)
			.name("&3Share Experience")
			.lore(isXPSharing ? "&aEnabled" : "&cDisabled");

		boolean isInstantWarp = partyUser.isInstantWarp();
		ItemBuilder instantWarpItem = new ItemBuilder(Material.ENDER_PEARL)
			.name("&3Instant Warp")
			.lore(isInstantWarp ? "&aEnabled" : "&cDisabled");

		// Owner Settings
		if (isViewerOwner) {
			ItemBuilder warpItem = new ItemBuilder(Material.ENDER_PEARL).name("&3Warp");
			contents.set(SlotPos.of(0, 3), ClickableItem.of(warpItem, e -> {
				PlayerUtils.runCommand(viewer, "party warp");
			}));

			boolean isPartyOpen = party.isOpen();
			ItemBuilder statusItem = new ItemBuilder(Material.OAK_DOOR)
				.name("&3Join Status")
				.lore(isPartyOpen ? "&eJoinable" : "&eInvite only");
			contents.set(SlotPos.of(0, 5), ClickableItem.of(statusItem, e -> {
				PlayerUtils.runCommand(viewer, "party " + (isPartyOpen ? "close" : "open"));
				refresh();
			}));

			ItemBuilder disbandItem = new ItemBuilder(Material.TNT).name("&3Disband");
			contents.set(SlotPos.of(0, 8), ClickableItem.of(disbandItem, e -> {
				PlayerUtils.runCommand(viewer, "party disband");
				previousMenu.open(viewer);
			}));

			contents.set(SlotPos.of(2, 4), ClickableItem.of(shareXPItem, e -> {
				PlayerUtils.runCommand(viewer, "party settings xpshare " + (isXPSharing ? "off" : "on"));
				refresh();
			}));

			return;
		}

		// Member Settings
		ItemBuilder leaveItem = new ItemBuilder(Material.TNT).name("&cLeave");
		contents.set(SlotPos.of(0, 8),
			ClickableItem.of(leaveItem, e -> {
				PlayerUtils.runCommand(viewer, "party leave");
				previousMenu.open(viewer);
			}));

		contents.set(SlotPos.of(2, 3), ClickableItem.of(shareXPItem, e -> {
			PlayerUtils.runCommand(viewer, "party settings xpshare " + (isXPSharing ? "off" : "on"));
			refresh();
		}));

		contents.set(SlotPos.of(2, 5),
			ClickableItem.of(instantWarpItem, e -> {
				PlayerUtils.runCommand(viewer, "party settings warp " + (isInstantWarp ? "off" : "on"));
				refresh();
			}));
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
