package gg.projecteden.nexus.features.events;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Arrays;
import java.util.List;

public class ScavHuntLegacy implements Listener {
	private static final String SCAVHUNT_PREFIX = StringUtils.getPrefix("Scav Hunt Legacy");

	private static final List<String> easter17 = Arrays.asList("12e78d50-6272-4bd9-9187-8c89fcb79560", "192e9a04-f27b-4ff2-9402-90df71c3e5cb",
			"1a9dbf06-affd-42cc-881f-1fb3e249a73c", "2337d621-f630-4bfe-bb07-c8b5e4c89b83", "24d27649-9a4f-4666-b184-4735e28150f5",
			"27e2e46c-18b8-4257-b74b-aa3b3383b327", "2dcf79a2-7d34-4eb6-ba36-4dd880cce8fa", "2f3c4c8f-2eb8-4325-9ba1-fb06f10f679f",
			"30c81c0b-ce4a-4bf8-8af1-4b573e04b373", "3a7e5e49-0acb-4fd4-8aa4-d78d6a4675ec", "3d79aa8f-c3eb-4d7e-a971-eee19911780e",
			"41708083-67f5-4359-bb0d-f36214c99fdd", "41c8a8bb-e251-430c-8bfe-27fb974cca55", "48a6fac5-3a40-43ea-9679-f72694c36342",
			"4d6dd22e-d9bb-4f1c-ae12-1812ada42140", "50912445-3df8-4cd2-96c1-57e113730082", "51632d53-ec6e-4344-9ad2-bb48d5d19d5a",
			"5a55f75a-42ba-65f9-f78a-23a79a879967", "63d849fa-54d3-f679-1c28-27801b7726b4", "676ca2db-0aee-3f60-94eb-cff284140325",
			"67e8c714-b4f1-4feb-8034-4a693723fdae", "6d47a1a9-c411-41b5-b3d0-07e2f1eaa6a1", "73bec8bd-bced-44cc-b3ad-3489b846efc4",
			"75d0cf8a-cfd2-48b5-bc44-513347a526fb", "8935f4a1-8334-cf81-eb0a-936fe9f7be45", "8bb84700-e4e0-a696-c08b-463d1d1164f0",
			"8ddcac53-45eb-b258-8135-8ea56427c369", "8ebb484c-a101-4046-ba19-f8a11e170de3", "96b1cade-5bab-4329-a7c1-2be4719b76f8",
			"97ef314a-45f3-4c28-8c18-9aa5cca89398", "a0f7fa03-ffa6-4f73-986f-fc16b63eb196", "b3543f53-12cd-4ac4-a863-87d52dea06fd",
			"b38df6a0-fc14-3f03-d14a-b9bf79a60f42", "b6041f19-5c4b-481f-b0f6-34afccc8626d", "b961514a-7619-43d4-a019-73227510aa2f",
			"ba43c4b9-4b23-4f40-8563-79428689d835", "c073ed95-a8b2-48f5-a332-55edf5631eea", "c716726b-0a33-47e4-af74-1601dff8776b",
			"cb6bbe69-f97f-4635-854c-5cee7f75ee2e", "cbcf8d8f-c79b-4622-907d-6430b8902303", "cbf63332-a108-4d1d-af71-49b49a38f7fb",
			"dcbde316-9548-4041-8b94-b9e745f6f95d", "de807154-4a3a-45dd-8ae4-75a2dfaa0b01", "e89f592d-0dfd-4cc8-b40a-9670686bc846",
			"e9a90a45-7296-4abd-92ce-da34cd265d20", "ee0cd82e-4985-4849-81cd-20a3e575fe19", "f5b11138-11a9-4ceb-a858-d79db20b1523",
			"f5e67258-ef23-4de4-9380-4afb21d63f6c", "f89395ae-eaf0-4a35-9836-c50601e0b523", "f8ace9ab-269a-417b-9756-022b12e72d96",
			"fbd1fc08-42d2-46ec-9841-6f423a9b9be6", "fd3e42ce-6601-408a-aa61-155e4021a716", "fe235c9f-d021-461b-9c22-d265bc867e21");

	public static final List<String> easter19 = Arrays.asList("04049c90-d3e9-4621-9caf-00000aaa4164", "04049c90-d3e9-4621-9caf-00000aaa6812",
			"04049c90-d3e9-4621-9caf-00000aaa6813", "04049c90-d3e9-4621-9caf-00000aaa6814");

	public ScavHuntLegacy() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onSignClick(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (block == null) return;
		if (!MaterialTag.SIGNS.isTagged(block.getType())) return;

		Sign sign = (Sign) block.getState();
		Player player = event.getPlayer();
		if ("[Scav Hunt '16]".equals(StringUtils.stripColor(sign.getLine(0))))
			PlayerUtils.send(player, SCAVHUNT_PREFIX + "You've found a statue from the &eSummer 2016 &3scavenger hunt!");
		else if ("[Scav Hunt '18]".equals(StringUtils.stripColor(sign.getLine(0))))
			PlayerUtils.send(player, SCAVHUNT_PREFIX + "You've found a statue from the &eFebruary 2018 &3scavenger hunt!");
		else if ("[Easter 2020]".equals(StringUtils.stripColor(sign.getLine(0))))
			PlayerUtils.send(player, SCAVHUNT_PREFIX + "You've found an easter egg from the &e2020 Easter egg hunt!");
		else if ("[StatueHunt 20]".equals(StringUtils.stripColor(sign.getLine(0))))
			PlayerUtils.send(player, SCAVHUNT_PREFIX + "You've found an statue from the &e2020 scavenger hunt!");
	}

	@EventHandler
	public void onHeadClick(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		Block block = event.getClickedBlock();
		if (block == null) return;
		if (!MaterialTag.SKULLS.isTagged(block.getType())) return;

		Skull skull = (Skull) block.getState();
		if (skull.getOwningPlayer() == null) return;
		Player player = event.getPlayer();
		if (easter17.contains(skull.getOwningPlayer().getUniqueId().toString())) {
			PlayerUtils.send(player, SCAVHUNT_PREFIX + "You've found an easter egg from the &e2017 Easter egg Hunt!");
		} else if (easter19.contains(skull.getOwningPlayer().getUniqueId().toString())) {
			PlayerUtils.send(player, SCAVHUNT_PREFIX + "You've found a bunny from the &e2019 Easter event!");
		}
	}

	@EventHandler
	public void onEggInteract(PlayerInteractEvent event) {
		if (!ActionGroup.CLICK_BLOCK.applies(event))
			return;

		Block block = event.getClickedBlock();
		if (block == null)
			return;

		if (block.getType() != Material.DRAGON_EGG)
			return;

		WorldGuardUtils worldGuardUtils = new WorldGuardUtils(block.getWorld());
		if (!worldGuardUtils.isInRegion(block.getLocation(), "spawn"))
			return;

		event.setCancelled(true);

		PlayerUtils.send(event.getPlayer(), SCAVHUNT_PREFIX + "You've found an easter egg from the &e2021 Easter egg Hunt!");
	}

}
