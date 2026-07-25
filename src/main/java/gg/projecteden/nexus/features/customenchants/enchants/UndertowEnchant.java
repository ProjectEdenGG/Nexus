package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class UndertowEnchant extends CustomEnchant {

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public List<Material> getSupportedMaterials() {
		return List.of(Material.TRIDENT);
	}

	public UndertowEnchant() {
		Tasks.repeat(1, 5, () -> {
			OnlinePlayers.getAll().stream()
				.filter(LivingEntity::isSwimming)
				.forEach(player ->  {
					if (getLevel(player.getInventory().getItemInMainHand()) == 0 && getLevel(player.getInventory().getItemInOffHand()) == 0)
						return;

					player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.DOLPHINS_GRACE)
						.particles(false)
						.icon(false)
						.duration(TickTime.SECOND.get())
						.build());
				});
		});
	}
}
