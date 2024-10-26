package gg.projecteden.nexus.utils.nms.packet;

import com.mojang.datafixers.util.Pair;
import lombok.Data;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@Data
public class EntityEquipmentPacket extends EdenPacket {
	private final int entityId;
	private final List<Pair<EquipmentSlot, ItemStack>> equipment;

	@Override
	protected Packet<ClientGamePacketListener> build() {
		return new ClientboundSetEquipmentPacket(entityId, equipment);
	}
}
