package gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.tiles;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class VirtualChunkKey {

	final int x, z;

	@Override
	public String toString() {
		return "ChunkKey{" + "x=" + x + ", z=" + z + '}';
	}
}
