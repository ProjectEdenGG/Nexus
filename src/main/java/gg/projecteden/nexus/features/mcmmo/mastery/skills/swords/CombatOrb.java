package gg.projecteden.nexus.features.mcmmo.mastery.skills.swords;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.mcmmo.mastery.models.OrbManager;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class CombatOrb extends OrbManager<Player> {

	@Override
	public ItemStack getItem() {
		return new ItemBuilder(Material.PAPER).maxStackSize(1).model("survival/mcmmoreset/skills/swords/combat_orb").build();
	}

	@Override
	public ColorType getColor() {
		return ColorType.RED;
	}

	@Override
	public double getRadius() {
		return 21;
	}

	@Override
	public long getDurationTicks() {
		return TickTime.SECOND.x(30);
	}

	@Override
	public String getCooldownType() {
		return "combat-orb";
	}

	@Override
	public long getCooldownTicks() {
		return TickTime.MINUTE.x(2);
	}

	@Override
	public Class<Player> getEntityClass() {
		return Player.class;
	}

	private final PotionEffect STRENGTH = new PotionEffectBuilder(PotionEffectType.STRENGTH).amplifier(1).particles(false).duration(40).build();
	private final PotionEffect REGEN = new PotionEffectBuilder(PotionEffectType.REGENERATION).amplifier(1).particles(false).duration(40).build();

	@Override
	public void handle(Player entity) {
		entity.addPotionEffects(List.of(STRENGTH, REGEN));
	}

}
