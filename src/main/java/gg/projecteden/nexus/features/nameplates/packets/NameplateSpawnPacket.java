package gg.projecteden.nexus.features.nameplates.packets;

import gg.projecteden.nexus.features.nameplates.NameplatesCommand;
import gg.projecteden.nexus.utils.nms.packet.EntitySpawnPacket;
import net.minecraft.world.entity.EntityTypes;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NameplateSpawnPacket extends EntitySpawnPacket {
	public static int ENTITY_ID_COUNTER = Integer.MAX_VALUE;

	public NameplateSpawnPacket(int entityId) {
		super(entityId, EntityTypes.TEXT_DISPLAY);
	}

	@Override
	public EntitySpawnPacket at(@NotNull Player player) {
		return super.at(player, NameplatesCommand.SPAWN_VERTICAL_OFFSET);
	}
}

