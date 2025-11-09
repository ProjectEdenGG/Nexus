package gg.projecteden.nexus.features.resourcepack.decoration.common;

import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.Utils.ItemFrameRotation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Rotation;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum RotationSnap {
	BOTH(),
	DEGREE_45(ItemFrameRotation.DEGREE_45, ItemFrameRotation.DEGREE_135, ItemFrameRotation.DEGREE_225, ItemFrameRotation.DEGREE_315),
	DEGREE_90(ItemFrameRotation.DEGREE_0, ItemFrameRotation.DEGREE_90, ItemFrameRotation.DEGREE_180, ItemFrameRotation.DEGREE_270);

	final List<Utils.ItemFrameRotation> frameRotations;

	RotationSnap(ItemFrameRotation... rotations) {
		this.frameRotations = Arrays.asList(rotations);
	}

	public boolean contains(ItemFrameRotation frameRotation) {
		return this.frameRotations.contains(frameRotation);
	}

	public boolean contains(Rotation rotation) {
		return this.frameRotations.contains(ItemFrameRotation.of(rotation));
	}
}
