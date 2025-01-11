package gg.projecteden.nexus.features.clientside.models;

import dev.morphia.annotations.Entity;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Painting;
import org.bukkit.Art;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.entity.CraftPainting;
import org.bukkit.entity.Player;
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
			entity = new Painting(EntityType.PAINTING, NMSUtils.toNMS(location.getWorld()));
			id = entity.getId();
		}
		entity.moveTo(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getYaw(), location.getPitch());
		entity.setDirection(NMSUtils.toNMS(blockFace));
		var optional = entity.registryAccess().lookupOrThrow(Registries.PAINTING_VARIANT).get(ResourceLocation.tryParse(variant));
		if (!optional.isPresent())
			return this;

		entity.setVariant(optional.get());
		entity.setSilent(true);
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
	public @NotNull List<Packet<ClientGamePacketListener>> getSpawnPackets(Player player) {
		return Collections.singletonList(new ClientboundAddEntityPacket(entity, entity.getDirection().get3DDataValue(), entity.blockPosition().atY(entity.getBlockY() - (height - 1))));
	}

	@Override
	public @NotNull List<Packet<ClientGamePacketListener>> getUpdatePackets(Player player) {
		final List<DataValue<?>> values = entity().getEntityData().packAll();
		if (values == null)
			return Collections.emptyList();

		return Collections.singletonList(new ClientboundSetEntityDataPacket(entity.getId(), values));
	}

}
