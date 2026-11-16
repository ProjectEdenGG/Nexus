package gg.projecteden.nexus.features.mcmmo.mastery.skills.fishing;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.mcmmo.mastery.models.OrbManager;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.inventory.ItemStack;

public class FishingOrb extends OrbManager<FishHook> {

	@Override
	public ItemStack getItem() {
		return new ItemBuilder(Material.PAPER).maxStackSize(1).model("survival/mcmmoreset/skills/fishing/fishing_orb").build();
	}

	@Override
	public ColorType getColor() {
		return ColorType.BLUE;
	}

	@Override
	public double getRadius() {
		return 21;
	}

	@Override
	public long getDurationTicks() {
		return TickTime.SECOND.x(60);
	}

	@Override
	public String getCooldownType() {
		return "fishing-orb";
	}

	@Override
	public long getCooldownTicks() {
		return TickTime.MINUTE.x(5);
	}

	@Override
	public Class<FishHook> getEntityClass() {
		return FishHook.class;
	}

	@Override
	public void handle(FishHook entity) {
		entity.setMinWaitTime(20);
		entity.setMaxWaitTime(80);
		entity.setMaxLureTime(30);
	}

}
