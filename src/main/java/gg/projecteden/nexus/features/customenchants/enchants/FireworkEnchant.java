package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customenchants.CustomEnchant;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.FireworkLauncher;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.RandomUtils;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class FireworkEnchant extends CustomEnchant implements Listener {
	private static final int DURATION = 30;
	private static final long DELAY = 5;
	private static final String METADATA = "enchantFirework";

	public FireworkEnchant(@NotNull NamespacedKey key) {
		super(key);
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onEntityShootBow(EntityShootBowEvent event) {
		ItemStack bow = event.getBow();
		if (isNullOrAir(bow))
			return;

		int level = getLevel(bow);
		if (level == 0)
			return;

		if (!(event.getEntity() instanceof Player player))
			return;

		if (!event.getBow().equals(player.getInventory().getItem(event.getHand())))
			return;

		if (!new CooldownService().check(player, "fireworkbow", TickTime.SECOND))
			return;

		int chance = Math.max(100 - (5 + (5 * level)), 0);
		if (RandomUtils.chanceOf(chance))
			return;

		AtomicInteger lifeTicks = new AtomicInteger(DURATION + level);

		AtomicInteger taskId = new AtomicInteger();
		Entity entity = event.getProjectile();
		entity.setMetadata(METADATA, new FixedMetadataValue(Nexus.getInstance(), true));

		taskId.set(Tasks.repeat(1, DELAY, () -> {
			if (lifeTicks.get() > 0) {
				Location loc = entity.getLocation();
				if (!entity.isDead()) {
					FireworkLauncher firework = FireworkLauncher.random(loc).power(1);
					firework.launch();

					lifeTicks.decrementAndGet();
					return;
				}
			}

			Tasks.cancel(taskId.get());
		}));
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Arrow arrow))
			return;

		if (!arrow.hasMetadata(METADATA))
			return;

		arrow.getWorld().createExplosion(arrow.getLocation(), 0);
	}

	@Override
	public @NotNull EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.BOW;
	}

}
