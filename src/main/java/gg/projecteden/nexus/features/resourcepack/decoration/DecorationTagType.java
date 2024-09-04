package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Addition;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.CraftableDecoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Interactable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Seat;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Toggleable;
import gg.projecteden.nexus.features.resourcepack.decoration.types.instruments.NoiseMaker;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public enum DecorationTagType {
	SEAT("谣"),
	PLAYABLE("麒"),
	CRAFTABLE("殷"),
	TOGGLEABLE("腈"),
	INTERACTABLE("晖"),
	ADDITION("香"),
	//
	DECORATION("策"),
	;

	final String tag;

	private String getTag() {
		return "&f" + tag;
	}

	private List<String> getLore() {
		if (this == DECORATION)
			return List.of(DECORATION.getTag());

		return List.of(this.getTag(), DECORATION.getTag());
	}

	public static void setLore(DecorationConfig config) {
		setLore(new ArrayList<>(), config);
	}

	public static void setLore(@NonNull String preLore, DecorationConfig config) {
		setLore(List.of(preLore), config);
	}

	public static void setLore(@NonNull List<String> preLore, DecorationConfig config) {
		List<String> lore = new ArrayList<>();
		if (!preLore.isEmpty()) {
			lore.addAll(preLore);
		}

		lore.addAll(getLore(config));
		config.setLore(lore);
	}

	private static List<String> getLore(DecorationConfig config) {
		if (config == null)
			return new ArrayList<>();

		return switch (config) {
			case Seat $0 -> SEAT.getLore();
			case NoiseMaker $1 -> PLAYABLE.getLore();
			case CraftableDecoration $2 -> CRAFTABLE.getLore();
			case Toggleable $3 -> TOGGLEABLE.getLore();
			case Interactable $4 -> INTERACTABLE.getLore();
			case Addition $5 -> ADDITION.getLore();
			default -> DECORATION.getLore();
		};
	}
}
