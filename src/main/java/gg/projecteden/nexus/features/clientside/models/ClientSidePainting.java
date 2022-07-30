package gg.projecteden.nexus.features.clientside.models;

import dev.morphia.annotations.Entity;
import gg.projecteden.nexus.utils.NMSUtils;
import gg.projecteden.nexus.utils.PacketUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Painting;
import org.bukkit.Art;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPainting;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Accessors(fluent = true, chain = true)
public class ClientSidePainting implements IClientSideEntity<ClientSidePainting, Painting, org.bukkit.entity.Painting> {
	private UUID uuid;
	private transient int id;
	private Location location;
	private BlockFace blockFace;
	private int height;
	private int width;
	private String variant;

	@Accessors(fluent = false, chain = false)
	private boolean hidden;

	private transient Painting entity;

	@Override
	public ClientSideEntityType getType() {
		return ClientSideEntityType.PAINTING;
	}

	public static ClientSidePainting builder() {
		return new ClientSidePainting();
	}

	public static ClientSidePainting of(org.bukkit.entity.Painting painting) {
		return builder()
			.uuid(painting.getUniqueId())
			.location(NMSUtils.fromNMS(painting.getWorld(), (((CraftPainting) painting).getHandle()).getPos()))
			.blockFace(painting.getFacing())
			.width(painting.getArt().getBlockWidth())
			.height(painting.getArt().getBlockHeight())
			.variant(painting.getArt().getKey().getKey());
	}

	@Override
	public ClientSidePainting build() {
		if (entity == null) {
			entity = new Painting(EntityType.PAINTING, PacketUtils.toNMS(location.getWorld()));
			id = entity.getId();
		}
		entity.moveTo(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getYaw(), location.getPitch());
		entity.setDirection(PacketUtils.toNMS(blockFace));
		entity.setVariant(Holder.direct(Registry.PAINTING_VARIANT.get(ResourceLocation.tryParse(variant))));
		return this;
	}

	@Override
	public org.bukkit.entity.Painting spawn() {
		// The art was defaulting to kebab (first one) when spawning the NMS entity for some reason, so manually fix it via bukkit
		final org.bukkit.entity.Painting painting = IClientSideEntity.super.spawn();
		final Art art = Art.getByName(variant);
		if (art != null)
			painting.setArt(art);
		return painting;

	}

	@Override
	public @NotNull List<Packet<ClientGamePacketListener>> getSpawnPackets() {
		return Collections.singletonList((ClientboundAddEntityPacket) entity.getAddEntityPacket());
	}

	@Override
	public @NotNull List<Packet<ClientGamePacketListener>> getUpdatePackets() {
		return Collections.singletonList(new ClientboundSetEntityDataPacket(entity.getId(), entity.getEntityData(), true));
	}

}
