package gg.projecteden.nexus.features.clientside.models;

import com.mojang.datafixers.util.Pair;
import dev.morphia.annotations.Entity;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import gg.projecteden.nexus.utils.nms.PacketUtils;
import io.papermc.paper.adventure.AdventureComponent;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Accessors(fluent = true, chain = true)
public class ClientSideArmorStand implements IClientSideEntity<ClientSideArmorStand, ArmorStand, org.bukkit.entity.ArmorStand> {
	private UUID uuid;
	private transient int id;
	private Location location;
	private Map<EquipmentSlot, ItemStack> equipment;
	private String customName;
	private boolean small;
	private boolean invisible;
	private boolean glowing;
	private boolean gravity;
	private boolean showBasePlate;
	private boolean showArms;
	private boolean marker;
	private EulerAngle headPose;
	private EulerAngle bodyPose;
	private EulerAngle leftArmPose;
	private EulerAngle rightArmPose;
	private EulerAngle leftLegPose;
	private EulerAngle rightLegPose;

	@Accessors(fluent = false, chain = false)
	private boolean hidden;

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
			.gravity(armorStand.hasGravity())
			.showBasePlate(armorStand.hasBasePlate())
			.showArms(armorStand.hasArms())
			.marker(armorStand.isMarker())
			.headPose(armorStand.getHeadPose())
			.bodyPose(armorStand.getBodyPose())
			.leftArmPose(armorStand.getLeftArmPose())
			.rightArmPose(armorStand.getRightArmPose())
			.leftLegPose(armorStand.getLeftLegPose())
			.rightLegPose(armorStand.getRightLegPose());
	}

	@Override
	public ClientSideArmorStand build() {
		if (entity == null) {
			entity = new ArmorStand(EntityType.ARMOR_STAND, NMSUtils.toNMS(location.getWorld()));
			id = entity.getId();
		}
		entity.moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		entity.setSmall(small);
		entity.setInvisible(invisible);
		entity.setGlowingTag(glowing);
		entity.setNoGravity(!gravity);
		entity.setNoBasePlate(!showBasePlate);
		entity.setShowArms(showArms);
		entity.setMarker(marker);
		entity.setSilent(true);

		entity.setHeadPose(NMSUtils.toNMS(headPose));
		entity.setBodyPose(NMSUtils.toNMS(bodyPose));
		entity.setLeftArmPose(NMSUtils.toNMS(leftArmPose));
		entity.setRightArmPose(NMSUtils.toNMS(rightArmPose));
		entity.setLeftLegPose(NMSUtils.toNMS(leftLegPose));
		entity.setRightLegPose(NMSUtils.toNMS(rightLegPose));

		if (!Nullables.isNullOrEmpty(customName)) {
			entity.setCustomName(new AdventureComponent(new JsonBuilder(customName).build()));
			entity.setCustomNameVisible(true);
		}

		equipment.forEach((slot, item) -> entity.setItemSlot(NMSUtils.toNMS(slot), NMSUtils.toNMS(item)));

		return this;
	}

	@NotNull
	private List<Pair<net.minecraft.world.entity.EquipmentSlot, net.minecraft.world.item.ItemStack>> convertEquipment() {
		if (equipment == null)
			return NMSUtils.getArmorEquipmentList();

		return new ArrayList<>() {{
			equipment.forEach((slot, item) ->
				add(new Pair<>(NMSUtils.toNMS(slot), NMSUtils.toNMS(item))));
		}};
	}

	@Override
	public @NotNull List<Packet<ClientGamePacketListener>> getSpawnPackets(Player player) {
		return Collections.singletonList(new ClientboundAddEntityPacket(entity, PacketUtils.getObjectId(entity)));
	}

	@Override
	public @NotNull List<Packet<ClientGamePacketListener>> getUpdatePackets(Player player) {
		return new ArrayList<>() {{
			final List<DataValue<?>> values = entity().getEntityData().packAll();
			if (values != null)
				add(new ClientboundSetEntityDataPacket(entity.getId(), values));
			add(new ClientboundSetEquipmentPacket(entity.getId(), convertEquipment()));
		}};
	}

}
