package gg.projecteden.nexus.features.nameplates;

import gg.projecteden.nexus.utils.nms.packet.EntitySpawnPacket;
import net.minecraft.world.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NameplateSpawnPacket extends EntitySpawnPacket {
	public static int ENTITY_ID_COUNTER = 32333;

	public NameplateSpawnPacket(int entityId) {
		super(entityId, EntityType.TEXT_DISPLAY);
	}

	@Override
	public EntitySpawnPacket at(@NotNull Player player) {
		return super.at(player, NameplatesCommand.SPAWN_VERTICAL_OFFSET);
	}
}

