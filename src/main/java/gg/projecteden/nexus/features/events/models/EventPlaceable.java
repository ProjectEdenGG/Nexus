package gg.projecteden.nexus.features.events.models;

import lombok.Builder;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Data
@Builder
public class EventPlaceable {
	private List<Material> blockMaterials;
	private Predicate<Block> blockPredicate;

	public static class EventPlaceableBuilder {

		public EventPlaceableBuilder blockMaterials(Material... materials) {
			return blockMaterials(Arrays.asList(materials));
		}

		public EventPlaceableBuilder blockMaterials(Tag<Material> materials) {
			return blockMaterials(materials.getValues().stream().toList());
		}

		public EventPlaceableBuilder blockMaterials(List<Material> materials) {
			if (blockMaterials == null)
				blockMaterials = new ArrayList<>();

			blockMaterials.addAll(materials);
			return this;
		}

	}
}
