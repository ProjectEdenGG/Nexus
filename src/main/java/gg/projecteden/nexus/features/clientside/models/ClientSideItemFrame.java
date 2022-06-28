package gg.projecteden.nexus.features.clientside.models;

import dev.morphia.annotations.Entity;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static gg.projecteden.nexus.utils.PacketUtils.toNMS;

@Data
@Entity
@NoArgsConstructor
@Accessors(fluent = true, chain = true)
public class ClientSideItemFrame implements IClientSideEntity<ClientSideItemFrame, ItemFrame> {
	private UUID uuid;
	private Location location;
	private ItemStack content;
	private BlockFace blockFace;
	private int rotation;
	private boolean invisible;
	private boolean glowing;
	private boolean makeSound;

	private transient ItemFrame entity;

	@Override
	public ClientSideEntityType getType() {
		return ClientSideEntityType.ITEM_FRAME;
	}

	public ClientSideItemFrame content(ItemBuilder item) {
		return content(item.build());
	}

	public ClientSideItemFrame content(ItemStack item) {
		this.content = item;
		return this;
	}

	public static ClientSideItemFrame builder() {
		return new ClientSideItemFrame();
	}

	public static ClientSideItemFrame of(org.bukkit.entity.ItemFrame itemFrame) {
		return builder()
			.uuid(itemFrame.getUniqueId())
			.location(itemFrame.getLocation())
			.content(itemFrame.getItem())
			.blockFace(itemFrame.getFacing())
			.rotation(itemFrame.getRotation().ordinal())
			.invisible(!itemFrame.isVisible())
			.glowing(itemFrame.isGlowing());
	}

	@Override
	public ClientSideItemFrame build() {
		if (content == null)
			content = new ItemStack(Material.AIR);

		entity = new ItemFrame(EntityType.ITEM_FRAME, toNMS(location));
		entity.moveTo(location.getBlockX(), location.getBlockY(), location.getBlockZ(), 0, 0);
		entity.setItem(toNMS(content), true, makeSound);
		entity.setDirection(toNMS(blockFace));
		entity.setInvisible(invisible);
		entity.setRotation(rotation);
		entity.setGlowingTag(glowing);
		return this;
	}

	public org.bukkit.entity.ItemFrame spawn() {
		final org.bukkit.entity.ItemFrame itemFrame = location.getWorld().spawn(location, org.bukkit.entity.ItemFrame.class);
		itemFrame.setItem(content);
		itemFrame.setFacingDirection(blockFace);
		itemFrame.setVisible(!invisible);
		itemFrame.setRotation(Rotation.values()[rotation]);
		itemFrame.setGlowing(glowing);
		return itemFrame;
	}

	@Override
	public @NotNull List<Packet<ClientGamePacketListener>> getPackets() {
		ClientboundAddEntityPacket rawSpawnPacket = (ClientboundAddEntityPacket) entity.getAddEntityPacket();
		ClientboundSetEntityDataPacket rawMetadataPacket = new ClientboundSetEntityDataPacket(entity.getId(), entity.getEntityData(), true);
		return List.of(rawSpawnPacket, rawMetadataPacket);
	}

}
