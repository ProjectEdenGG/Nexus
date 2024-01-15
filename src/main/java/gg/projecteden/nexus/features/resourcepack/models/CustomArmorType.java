package gg.projecteden.nexus.features.resourcepack.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomArmorType {
	WITHER(1),
	WARDEN(2),
	;

	private final int id;
}
