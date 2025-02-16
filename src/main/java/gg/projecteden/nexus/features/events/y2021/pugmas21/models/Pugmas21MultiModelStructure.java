package gg.projecteden.nexus.features.events.y2021.pugmas21.models;

import gg.projecteden.nexus.features.commands.ArmorStandEditorCommand;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.LocationUtils.CardinalDirection;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
public class Pugmas21MultiModelStructure {
	private Location location;
	private final List<Model> models = new ArrayList<>();

	public static final double SEPARATOR = 7.5;

	@Data
	@RequiredArgsConstructor
	public static class Model {
		private final Map<BlockFace, Integer> modifiers;
		private final String modelId;
		private BlockFace direction;

		private ArmorStand armorStand;

		public Model direction(BlockFace direction) {
			this.direction = direction;
			return this;
		}

		public Location modify(Location location) {
			modifiers.forEach((direction, amount) -> location.add(direction.getDirection().multiply(SEPARATOR * amount)));
			if (direction != null)
				location.setYaw(CardinalDirection.of(direction).getYaw());
			return location;
		}

		public void spawn(Location location) {
			armorStand = ArmorStandEditorCommand.summon(modify(location.clone()), armorStand -> {
				armorStand.setVisible(false);
				armorStand.setItem(EquipmentSlot.HEAD, new ItemBuilder(Material.PAPER).model(modelId).build());
			});
		}

	}

	public static Pugmas21MultiModelStructure builder() {
		return new Pugmas21MultiModelStructure();
	}

	public Pugmas21MultiModelStructure from(Location location) {
		this.location = location;
		return this;
	}

	public Pugmas21MultiModelStructure add(Map<BlockFace, Integer> modifier, String modelId) {
		models.add(new Model(modifier, modelId));
		return this;
	}

	public Pugmas21MultiModelStructure cardinal(Function<BlockFace, Model> function) {
		for (BlockFace direction : CardinalDirection.blockFaces())
			models.add(function.apply(direction));
		return this;
	}

	public Pugmas21MultiModelStructure spawn() {
		for (Model model : models)
			model.spawn(location);
		return this;
	}

}
