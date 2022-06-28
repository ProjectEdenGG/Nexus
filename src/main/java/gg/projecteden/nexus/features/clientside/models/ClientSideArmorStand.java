package gg.projecteden.nexus.features.clientside.models;

import com.mojang.datafixers.util.Pair;
import dev.morphia.annotations.Entity;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PacketUtils;
import io.papermc.paper.adventure.AdventureComponent;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.PacketUtils.toNMS;

@Data
@Entity
@NoArgsConstructor
@Accessors(fluent = true, chain = true)
public class ClientSideArmorStand implements IClientSideEntity<ClientSideArmorStand, ArmorStand> {
	private UUID uuid;
	private Location location;
	private Map<EquipmentSlot, ItemStack> equipment;
	private String customName;
	private boolean small;
	private boolean invisible;
	private boolean glowing;
	private boolean showBasePlate;
	private boolean showArms;
	private EulerAngle headPose;
	private EulerAngle bodyPose;
	private EulerAngle leftArmPose;
	private EulerAngle rightArmPose;
	private EulerAngle leftLegPose;
	private EulerAngle rightLegPose;

	private transient ArmorStand entity;

	@Override
	public ClientSideEntityType getType() {
		return ClientSideEntityType.ARMOR_STAND;
	}

	public static ClientSideArmorStand builder() {
		return new ClientSideArmorStand();
	}

	public static ClientSideArmorStand of(org.bukkit.entity.ArmorStand armorStand) {
		return builder()
			.uuid(armorStand.getUniqueId())
			.location(armorStand.getLocation())
			.equipment(new HashMap<>() {{
				for (EquipmentSlot slot : EquipmentSlot.values())
					put(slot, armorStand.getEquipment().getItem(slot));
			}})
			.customName(armorStand.getCustomName())
			.small(armorStand.isSmall())
			.invisible(armorStand.isInvisible())
			.glowing(armorStand.isGlowing())
			.showBasePlate(armorStand.hasBasePlate())
			.showArms(armorStand.hasArms())
			.headPose(armorStand.getHeadPose())
			.bodyPose(armorStand.getBodyPose())
			.leftArmPose(armorStand.getLeftArmPose())
			.rightArmPose(armorStand.getRightArmPose())
			.leftLegPose(armorStand.getLeftLegPose())
			.rightLegPose(armorStand.getRightLegPose());
	}

	@Override
	public ClientSideArmorStand build() {
		entity = new ArmorStand(EntityType.ARMOR_STAND, toNMS(location));
		entity.moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		entity.setSmall(small);
		entity.setInvisible(invisible);
		entity.setGlowingTag(glowing);
		entity.setNoBasePlate(!showBasePlate);
		entity.setShowArms(showArms);

		entity.setHeadPose(toNMS(headPose));
		entity.setBodyPose(toNMS(bodyPose));
		entity.setLeftArmPose(toNMS(leftArmPose));
		entity.setRightArmPose(toNMS(rightArmPose));
		entity.setLeftLegPose(toNMS(leftLegPose));
		entity.setRightLegPose(toNMS(rightLegPose));

		if (!isNullOrEmpty(customName)) {
			entity.setCustomName(new AdventureComponent(new JsonBuilder(customName).build()));
			entity.setCustomNameVisible(true);
		}

		return this;
	}

	@NotNull
	private List<Pair<net.minecraft.world.entity.EquipmentSlot, net.minecraft.world.item.ItemStack>> convertEquipment() {
		if (equipment == null)
			return PacketUtils.getEquipmentList();

		return new ArrayList<>() {{
			equipment.forEach((slot, item) ->
				add(new Pair<>(toNMS(slot), toNMS(item))));
		}};
	}

	@Override
	public @NotNull List<Packet<ClientGamePacketListener>> getPackets() {
		ClientboundAddEntityPacket rawSpawnPacket = new ClientboundAddEntityPacket(entity, PacketUtils.getObjectId(entity));
		ClientboundSetEntityDataPacket rawMetadataPacket = new ClientboundSetEntityDataPacket(entity.getId(), entity.getEntityData(), true);
		ClientboundSetEquipmentPacket rawEquipmentPacket = new ClientboundSetEquipmentPacket(entity.getId(), convertEquipment());
		return List.of(rawSpawnPacket, rawMetadataPacket, rawEquipmentPacket);
	}

}
