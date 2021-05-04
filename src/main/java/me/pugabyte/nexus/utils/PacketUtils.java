package me.pugabyte.nexus.utils;

import com.comphenix.packetwrapper.WrapperPlayServerBlockChange;
import com.comphenix.packetwrapper.WrapperPlayServerEntityHeadRotation;
import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.packetwrapper.WrapperPlayServerTileEntityData;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import me.lexikiq.HasPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collections;

public class PacketUtils {

	public static void copyTileEntityClient(HasPlayer recipient, Block origin, Location destination) {
		BlockPosition destinationPosition = toBlockPosition(destination);

		WrapperPlayServerBlockChange blockChange = new WrapperPlayServerBlockChange();
		blockChange.setLocation(destinationPosition);
		blockChange.setBlockData(WrappedBlockData.createData(Material.PLAYER_HEAD));

		WrapperPlayServerTileEntityData tileEntityData = new WrapperPlayServerTileEntityData();
		tileEntityData.setAction(4);
		tileEntityData.setLocation(destinationPosition);
		tileEntityData.setNbtData(NbtFactory.readBlockState(origin));

		Player player = recipient.getPlayer();
		blockChange.sendPacket(player);
		tileEntityData.sendPacket(player);
	}

	public static BlockPosition toBlockPosition(Location destination) {
		return new BlockPosition(destination.getBlockX(), destination.getBlockY(), destination.getBlockZ());
	}

	/*
	public void addNPCPacket(EntityPlayer npc, Player player) {
		PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
		connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc)); // "Adds the player data for the client to use when spawning a player" - https://wiki.vg/Protocol#Spawn_Player
		connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc)); // Spawns the NPC for the player client.
		connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360))); // Correct head rotation when spawned in player look direction.
	}
	 */

	public void npcPacket(Entity entity, HasPlayer recipient) {
		WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();

		playerInfo.setAction(PlayerInfoAction.UPDATE_DISPLAY_NAME);
		//
		PlayerInfoData hi = new PlayerInfoData(
				WrappedGameProfile.fromHandle(playerInfo.getHandle()),
				0,
				NativeGameMode.SURVIVAL,
				WrappedChatComponent.fromText("hi")
		);

		PlayerInfoData playerInfoData = playerInfo.getData().get(0);
		WrappedGameProfile profile = playerInfoData.getProfile();
		WrappedGameProfile newProfile = profile.withName("hi");

		playerInfo.setData(Collections.singletonList(hi));
		//

		WrapperPlayServerNamedEntitySpawn entitySpawn = new WrapperPlayServerNamedEntitySpawn();
		entitySpawn.setEntityID(entity.getEntityId());

		WrapperPlayServerEntityHeadRotation headRotation = new WrapperPlayServerEntityHeadRotation();
		headRotation.setEntityID(entity.getEntityId());
		headRotation.setHeadYaw((byte) (entity.getLocation().getYaw() * 256 / 360));

		Player player = recipient.getPlayer();
		playerInfo.sendPacket(player);
		entitySpawn.sendPacket(player);
		headRotation.sendPacket(player);
	}



}
