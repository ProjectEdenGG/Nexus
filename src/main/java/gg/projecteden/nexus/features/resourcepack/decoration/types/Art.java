package gg.projecteden.nexus.features.resourcepack.decoration.types;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.WallThing;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.block.BlockFace;

import java.util.List;
import java.util.Map;

import static gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox.light;

public class Art extends WallThing {
	@Getter
	private final ArtSize size;

	public Art(String name, CustomMaterial material, ArtSize size) {
		super(name, material, size.getHitboxes());
		this.size = size;
		this.rotatable = false;
	}

	@AllArgsConstructor
	public enum ArtSize {
		_1x1(Hitbox.single(light())),

		_1x2h(List.of(
			Hitbox.origin(light()),
			Hitbox.origin(light()),
			Hitbox.offset(light(), BlockFace.EAST, 1)
		)),

		_1x2v(List.of(
			Hitbox.origin(light()),
			Hitbox.offset(light(), BlockFace.UP, 1)
		)),

		_2x2(List.of(
			Hitbox.origin(light()),
			Hitbox.offset(light(), BlockFace.EAST, 1),
			Hitbox.offset(light(), BlockFace.UP, 1),
			new Hitbox(light(), Map.of(BlockFace.EAST, 1, BlockFace.UP, 1))
		)),

		_1x3h(List.of(
			Hitbox.origin(light()),
			Hitbox.offset(light(), BlockFace.EAST, 1),
			Hitbox.offset(light(), BlockFace.EAST, 2)
		)),

		_1x3v(List.of(
			Hitbox.origin(light()),
			Hitbox.offset(light(), BlockFace.UP, 1),
			Hitbox.offset(light(), BlockFace.UP, 2)
		)),

		_2x3h(List.of(
			Hitbox.origin(light()),
			Hitbox.offset(light(), BlockFace.EAST, 1),
			Hitbox.offset(light(), BlockFace.EAST, 2),
			Hitbox.offset(light(), BlockFace.UP, 1),
			new Hitbox(light(), Map.of(BlockFace.UP, 1, BlockFace.EAST, 1)),
			new Hitbox(light(), Map.of(BlockFace.UP, 1, BlockFace.EAST, 2))
		)),

		_2x3v(List.of(
			Hitbox.origin(light()),
			Hitbox.offset(light(), BlockFace.UP, 1),
			Hitbox.offset(light(), BlockFace.UP, 2),
			Hitbox.offset(light(), BlockFace.EAST, 1),
			new Hitbox(light(), Map.of(BlockFace.EAST, 1, BlockFace.UP, 1)),
			new Hitbox(light(), Map.of(BlockFace.EAST, 1, BlockFace.UP, 2))
		)),

		_3x3(List.of(
			Hitbox.origin(light()),
			Hitbox.offset(light(), BlockFace.UP, 1),
			Hitbox.offset(light(), BlockFace.UP, 2),
			Hitbox.offset(light(), BlockFace.EAST, 1),
			Hitbox.offset(light(), BlockFace.EAST, 2),
			new Hitbox(light(), Map.of(BlockFace.EAST, 1, BlockFace.UP, 1)),
			new Hitbox(light(), Map.of(BlockFace.EAST, 1, BlockFace.UP, 2)),
			new Hitbox(light(), Map.of(BlockFace.EAST, 2, BlockFace.UP, 1)),
			new Hitbox(light(), Map.of(BlockFace.EAST, 2, BlockFace.UP, 2))

		)),
		;

		@Getter
		private final List<Hitbox> hitboxes;
	}
}
