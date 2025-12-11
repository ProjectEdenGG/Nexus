package gg.projecteden.nexus.utils;

import lombok.AllArgsConstructor;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static gg.projecteden.nexus.utils.nms.NMSUtils.toNMS;

public class PlayDoorSoundUtil {

	private static final List<Tag<Material>> SUPPORTED_TAGS = List.of(
		Tag.DOORS,
		Tag.TRAPDOORS,
		Tag.FENCE_GATES
	);

	public static void playDoorSound(Location location, Action action, Material material) {
		BlockSetType.values()
			.filter(blockSetType -> material.name().toLowerCase().contains(blockSetType.name()))
			.findFirst()
			.ifPresent(blockSetType -> {
				SUPPORTED_TAGS.stream()
					.filter(tag -> tag.isTagged(material))
					.findFirst()
					.ifPresent(tag -> {
						SoundEvent sound = action.getter.get(tag).apply(blockSetType);
						toNMS(location.getWorld()).playSound(null, toNMS(location), sound, SoundSource.BLOCKS, 1f, 1f);
					});
			});
	}

	@AllArgsConstructor
	public enum Action {
		OPEN(Map.of(
			Tag.DOORS, BlockSetType::doorOpen,
			Tag.TRAPDOORS, BlockSetType::trapdoorOpen,
			Tag.FENCE_GATES, blockSetType -> doorToFenceGate(blockSetType.doorOpen()))
		),
		CLOSE(Map.of(
			Tag.DOORS, BlockSetType::doorClose,
			Tag.TRAPDOORS, BlockSetType::trapdoorClose,
			Tag.FENCE_GATES, blockSetType -> doorToFenceGate(blockSetType.doorClose()))
		),
		;

		private final Map<Tag<Material>, Function<BlockSetType, SoundEvent>> getter;

		private static SoundEvent doorToFenceGate(SoundEvent soundEvent) {
			String path = soundEvent.location().getPath()
				.replace("door", "fence_gate")
				.replace("wooden_", "");

			return new SoundEvent(Identifier.withDefaultNamespace(path), Optional.empty());
		}
	}

}
