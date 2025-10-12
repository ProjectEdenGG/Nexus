package gg.projecteden.nexus.utils.nms.packet;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData.MapPatch;
import org.bukkit.map.MapPalette;

import java.awt.image.BufferedImage;
import java.util.Collections;

public class CustomMapPacket extends EdenPacket {
	private final int mapId;
	private final byte scale;
	private final boolean locked;
	private final byte[] colors;

	public CustomMapPacket(int mapId, byte scale, boolean locked, byte[] colors) {
		this.mapId = mapId;
		this.scale = scale;
		this.locked = locked;
		this.colors = colors;
	}

	public static CustomMapPacket of(int mapId, BufferedImage image) {
		return new CustomMapPacket(mapId, (byte) 0, true, imageToMapColors(image));
	}

	@Override
	protected Packet<ClientGamePacketListener> build() {
		// Create a MapPatch with the image data
		MapPatch mapPatch = new MapPatch(0, 0, 128, 128, colors);

		// Create the map packet
		return new ClientboundMapItemDataPacket(
			new MapId(mapId),
			scale,
			locked,
			Collections.emptyList(), // No decorations
			mapPatch
		);
	}

	/**
	 * Converts a BufferedImage to a byte array of map colors using Minecraft's MapColor class
	 * @param image The image to convert
	 * @return A byte array of map colors (128x128)
	 */
	public static byte[] imageToMapColors(BufferedImage image) {
		return MapPalette.imageToBytes(image);
	}
}
