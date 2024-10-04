package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Addition;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.CraftableDecoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Interactable;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Seat;
import gg.projecteden.nexus.features.resourcepack.decoration.common.interfaces.Toggleable;
import gg.projecteden.nexus.features.resourcepack.decoration.types.instruments.NoiseMaker;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.Edible;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public enum DecorationTagType {
	SEAT(Seat.class, "谣"),
	PLAYABLE(NoiseMaker.class, "麒"),
	CRAFTABLE(CraftableDecoration.class, "殷"),
	EDIBLE(Edible.class, "诐"),
	TOGGLEABLE(Toggleable.class, "腈"),
	INTERACTABLE(Interactable.class, "晖"),
	ADDITION(Addition.class, "香"),
	//
	DECORATION(DecorationConfig.class, "策"),
	//
	TOOL(null, "鼬"),
	;

	@Getter
	final Class<?> clazz;
	final String tag;

	private String getTag() {
		return "&f" + tag;
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

		lore.addAll(getApplicableTags(config));
		config.setLore(lore);
	}

	private static final List<DecorationTagType> INTERACTABLE_SUB_TAGS = List.of(PLAYABLE, TOGGLEABLE, SEAT);
	private static final List<DecorationTagType> IGNORED_TAGS = List.of(DecorationTagType.DECORATION, DecorationTagType.TOOL);

	private static List<String> getApplicableTags(DecorationConfig config) {
		if (config == null)
			return new ArrayList<>();

		List<String> applicableTags = new ArrayList<>(List.of(DECORATION.getTag()));
		for (DecorationTagType tag : values()) {
			if (IGNORED_TAGS.contains(tag))
				continue;

			if (INTERACTABLE_SUB_TAGS.contains(tag))
				continue;

			// Don't want to show interactable tag and its subtype tag
			if (tag == INTERACTABLE && config instanceof Interactable interactable) {
				switch (interactable) {
					case NoiseMaker noiseMaker -> applicableTags.addFirst(PLAYABLE.getTag());
					case Toggleable toggleable -> applicableTags.addFirst(TOGGLEABLE.getTag());
					case Seat seat -> applicableTags.addFirst(SEAT.getTag());
					default -> applicableTags.addFirst(INTERACTABLE.getTag());
				}
				continue;
			}

			if (DecorationUtils.getInstancesOf(config).contains(tag.getClazz()))
				applicableTags.addFirst(tag.getTag());
		}

		return applicableTags;
	}

	public List<String> getTags() {
		if (this == DECORATION)
			return List.of(DECORATION.getTag());

		return List.of(this.getTag(), DECORATION.getTag());
	}
}
