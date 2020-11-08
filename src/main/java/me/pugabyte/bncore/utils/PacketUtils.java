package me.pugabyte.bncore.utils;

import com.comphenix.packetwrapper.WrapperPlayServerEntityHeadRotation;
import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collections;

public class PacketUtils {

	public void npcPacket(Entity entity, Player player) {
		WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();

		playerInfo.setAction(PlayerInfoAction.UPDATE_DISPLAY_NAME);
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

		WrapperPlayServerNamedEntitySpawn entitySpawn = new WrapperPlayServerNamedEntitySpawn();
		entitySpawn.setEntityID(entity.getEntityId());

		WrapperPlayServerEntityHeadRotation headRotation = new WrapperPlayServerEntityHeadRotation();
		headRotation.setEntityID(entity.getEntityId());
		headRotation.setHeadYaw((byte) (entity.getLocation().getYaw() * 256 / 360));

		playerInfo.sendPacket(player);
		entitySpawn.sendPacket(player);
		headRotation.sendPacket(player);
	}



}
