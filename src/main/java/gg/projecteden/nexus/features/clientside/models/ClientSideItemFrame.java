package gg.projecteden.nexus.features.clientside.models;

import dev.morphia.annotations.Entity;
import fr.moribus.imageonmap.image.MapInitEvent;
import gg.projecteden.nexus.models.clientside.ClientSideConfig.ClientSideItemFrameModifier;
import gg.projecteden.nexus.models.clientside.ClientSideUser;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData.MapPatch;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.map.CraftMapView;
import org.bukkit.craftbukkit.map.RenderData;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCursor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static gg.projecteden.nexus.models.clientside.ClientSideConfig.ITEM_FRAME_MODIFIERS;

@Data
@Entity
@NoArgsConstructor
@Accessors(fluent = true, chain = true)
public class ClientSideItemFrame implements IClientSideEntity<ClientSideItemFrame, ItemFrame, org.bukkit.entity.ItemFrame> {
	private UUID uuid;
	private UUID entityUuid;
	private transient int id;
	private Location location;
	private ItemStack content;
	private BlockFace blockFace;
	private int rotation;
	private boolean invisible;
	private boolean glowing;
	// TODO support glow color
	private boolean makeSound;

	@Accessors(fluent = false, chain = false)
	private boolean hidden;

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
			.uuid(UUID.randomUUID())
			.entityUuid(itemFrame.getUniqueId())
			.location(itemFrame.getLocation())
			.content(itemFrame.getItem())
			.blockFace(itemFrame.getFacing())
			.rotation(itemFrame.getRotation().ordinal())
			.invisible(!itemFrame.isVisible())
			.glowing(itemFrame.isGlowing());
	}

	public Rotation getBukkitRotation() {
		return Rotation.values()[rotation()];
	}

	@Override
	public ClientSideItemFrame build() {
		if (content == null)
			content = new ItemStack(Material.AIR);

		if (uuid == null)
			uuid = UUID.randomUUID();

		if (entity == null) {
			entity = new ItemFrame(EntityType.ITEM_FRAME, NMSUtils.toNMS(location.getWorld()));
			id = entity.getId();
			entityUuid = entity.getUUID();
		}
		entity.moveTo(location.getBlockX(), location.getBlockY(), location.getBlockZ(), 0, 0);
		entity.setItem(NMSUtils.toNMS(content), true, makeSound);
		entity.setDirection(NMSUtils.toNMS(blockFace));
		entity.setInvisible(invisible);
		entity.setRotation(rotation);
		entity.setGlowingTag(glowing);
		entity.setSilent(true);
		return this;
	}

	@Override
	public @NotNull List<Packet<ClientGamePacketListener>> getSpawnPackets(Player player) {
		final List<Packet<ClientGamePacketListener>> packets = new ArrayList<>();
		packets.add(NMSUtils.getSpawnPacket(entity));

		if (content.getType() == Material.FILLED_MAP) {
			if (content.getItemMeta() instanceof MapMeta map && map.getMapView() instanceof CraftMapView view) {
				MapInitEvent.initMap(content);
				packets.add(getMapRenderPacket(player, view));
			}
		}

		return packets;
	}

	@NotNull
	private ClientboundMapItemDataPacket getMapRenderPacket(Player player, CraftMapView view) {
		RenderData data = view.render((CraftPlayer) player);
		Collection<MapDecoration> icons = new ArrayList<>();
		for (MapCursor cursor : data.cursors) {
			if (cursor.isVisible()) {

				icons.add(new MapDecoration(BuiltInRegistries.MAP_DECORATION_TYPE.get(cursor.getRawType()).get(), cursor.getX(), cursor.getY(), cursor.getDirection(), Optional.ofNullable(CraftChatMessage.fromStringOrNull(cursor.getCaption()))));
			}
		}

		return new ClientboundMapItemDataPacket(new MapId(view.getId()), view.getScale().getValue(), view.isLocked(), icons, new MapPatch(0, 0, 128, 128, data.buffer));
	}

	@Override
	public @NotNull List<Packet<ClientGamePacketListener>> getUpdatePackets(Player player) {
		var original = entity().getItem();
		for (ClientSideItemFrameModifier modifier : ITEM_FRAME_MODIFIERS) {
			var item = modifier.modify(ClientSideUser.of(player), this);
			entity.setItem(NMSUtils.toNMS(item), true, makeSound);
		}

		final List<DataValue<?>> values = entity().getEntityData().packAll();
		List<Packet<ClientGamePacketListener>> packets = Collections.singletonList(new ClientboundSetEntityDataPacket(entity.getId(), values));

		entity.setItem(original, true, makeSound);
		return packets;
	}

}
