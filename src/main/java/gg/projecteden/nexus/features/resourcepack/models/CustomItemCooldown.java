package gg.projecteden.nexus.features.resourcepack.models;

public class CustomItemCooldown {

	private static final CustomMaterial FIRST = CustomMaterial.ITEM_COOLDOWN_MIN;
	private static final CustomMaterial LAST = CustomMaterial.ITEM_COOLDOWN_MAX;
	private static final int TOTAL_FRAMES = LAST.getModelId() - FIRST.getModelId();

}
