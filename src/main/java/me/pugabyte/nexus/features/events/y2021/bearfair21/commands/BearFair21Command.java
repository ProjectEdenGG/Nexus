package me.pugabyte.nexus.features.events.y2021.bearfair21.commands;

import com.mojang.datafixers.util.Pair;
import me.pugabyte.nexus.features.events.y2021.bearfair21.Fairgrounds;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Interactables;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Seeker;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.ClientSideContent;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.PacketUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import net.minecraft.server.v1_16_R3.ItemStack;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;

import java.util.List;

@Permission("group.staff")
@Aliases("bf21")
public class BearFair21Command extends CustomCommand {

	public BearFair21Command(CommandEvent event) {
		super(event);
	}

	@Path
	void warp() {
		runCommand("bearfair21warp");
	}

	@Path("strengthTest")
	@Permission("group.admin")
	void strengthTest() {
		commandBlock();
		Interactables.strengthTest();
	}

	@Path("seeker")
	void seeker() {
		send("Find the crimson button");
		Seeker.addPlayer(player());
	}

	@Path("rides enable")
	void ridesEnable() {
		for (String ride : Fairgrounds.rides)
			PlayerUtils.runCommandAsConsole("rideadm bf21_" + ride + " enable");
	}

	@Path("rides disable")
	void ridesDisable() {
		for (String ride : Fairgrounds.rides)
			PlayerUtils.runCommandAsConsole("rideadm bf21_" + ride + " disable");
	}

	@Path("armorstand select")
	void armorstandSelect() {
		Entity entity = getTargetEntityRequired();

		if (!(entity instanceof ArmorStand))
			error("that's not an armorstand");

		ClientSideContent.armorStand = (ArmorStand) entity;
		send("selected armorstand");
	}


	@Path("armorstand show")
	void armorstandShow() {
		if (ClientSideContent.armorStand == null)
			error("armorstand is null");

		List<Pair<EnumItemSlot, ItemStack>> equipment = PacketUtils.getEquipmentList(new ItemBuilder(Material.CAKE).customModelData(1).build(), null, null, null);
		PacketUtils.updateArmorStandArmor(
				player(),
				ClientSideContent.armorStand,
				equipment);
	}

	@Path("armorstand hide")
	void armorStandHide() {
		if (ClientSideContent.armorStand == null)
			error("armorstand is null");

		PacketUtils.updateArmorStandArmor(player(), ClientSideContent.armorStand, PacketUtils.getEquipmentList());
	}

	@Path("armorstand spawn")
	void armorStandSpawn() {
		List<Pair<EnumItemSlot, ItemStack>> equipment = PacketUtils.getEquipmentList(new ItemBuilder(Material.CAKE).customModelData(1).build(), null, null, null);
		PacketUtils.spawnArmorStand(player(), location(), equipment, true);
	}

	@Path("itemframe select")
	void itemframeSelect() {
		Entity entity = getTargetEntityRequired();

		if (!(entity instanceof ItemFrame))
			error("that's not an itemframe");

		ClientSideContent.itemFrame = (ItemFrame) entity;
		send("selected itemframe");
	}

	@Path("itemframe show")
	void itemframeShow() {
		if (ClientSideContent.itemFrame == null)
			error("itemframe is null");

		PacketUtils.updateItemFrame(
				player(),
				ClientSideContent.itemFrame,
				new ItemBuilder(Material.CAKE).customModelData(1).build(),
				0);

		send("Sent update packet");
	}

	@Path("itemframe hide")
	void itemframeHide() {
		if (ClientSideContent.itemFrame == null)
			error("itemframe is null");

		PacketUtils.updateItemFrame(player(), ClientSideContent.itemFrame, null, 0);

		send("Sent update packet");
	}

	@Path("itemframe spawn")
	void itemframeSpawn() {
		PacketUtils.spawnItemFrame(player(), location(),
				BlockFace.UP, new ItemBuilder(Material.CAKE).customModelData(1).build(), 0, false, true);

		send("Sent spawn packet");
	}


}
