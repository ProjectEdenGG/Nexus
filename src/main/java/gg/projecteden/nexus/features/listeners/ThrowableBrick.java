package gg.projecteden.nexus.features.listeners;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.utils.Nullables.isNotNullOrAir;

public class ThrowableBrick implements Listener {

	private static final ItemModelType MODEL = ItemModelType.THROWN_BRICK;
	private static final List<Material> BREAK_TYPES = new ArrayList<>() {{
		addAll(MaterialTag.ICE.getValues());
		addAll(MaterialTag.ALL_GLASS.getValues());
	}};

	@EventHandler
	public void on(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND)
			return;

		if (!ActionGroup.RIGHT_CLICK.applies(event))
			return;

		if (isNotNullOrAir(event.getClickedBlock()))
			if (MaterialTag.INTERACTABLES.isTagged(event.getClickedBlock()))
				return;

		ItemStack item = event.getItem();
		if (Nullables.isNullOrAir(item) || item.getType() != Material.BRICK)
			return;

		Player player = event.getPlayer();

		ItemUtils.subtract(player, item);

		Location location = player.getLocation().add(0, 1.5, 0);
		location.add(player.getLocation().getDirection());

		Snowball snowball = (Snowball) player.getWorld().spawnEntity(location, EntityType.SNOWBALL);
		snowball.setItem(new ItemBuilder(MODEL).build());
		snowball.setVelocity(location.getDirection().multiply(0.8));
		snowball.setShooter(player);

		new SoundBuilder(Sound.ENTITY_WITCH_THROW).location(player.getLocation()).volume(0.5).pitch(0.2).play();
	}

	@EventHandler
	public void on(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof Snowball snowball)) return;
		if (ItemModelType.of(snowball.getItem()) != MODEL) return;
		if (!(snowball.getShooter() instanceof Player player)) return;

		new SoundBuilder(Sound.BLOCK_DEEPSLATE_BREAK).location(snowball.getLocation()).pitch(0.75).play();
		if (event.getHitEntity() != null) {
			damageEntity(player, snowball, event.getHitEntity());
			return;
		}

		if (event.getHitBlock() != null) {
			breakBlock(player, event.getHitBlock());
		}
	}

	private void damageEntity(Player shooter, Snowball snowball, Entity entity) {
		double damage = 1.0;
		EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(snowball, entity, DamageCause.PROJECTILE, DamageSource.builder(DamageType.ARROW).build(), damage);
		if (!damageEvent.callEvent())
			return;

		LivingEntity nmsEntity = NMSUtils.toNMS(shooter);
		NMSUtils.hurtEntity(entity, NMSUtils.getDamageSources(entity).mobProjectile(NMSUtils.toNMS(snowball), nmsEntity), (float) damage);
	}

	private void breakBlock(Player shooter, Block block) {
		if (!BREAK_TYPES.contains(block.getType())) return;

		BlockBreakEvent breakEvent = new BlockBreakEvent(block, shooter);
		if (!breakEvent.callEvent())
			return;

		new SoundBuilder(block.getBlockSoundGroup().getBreakSound()).location(block).play();
		new ParticleBuilder(Particle.BLOCK).data(block.getBlockData()).location(block.getLocation().toCenterLocation()).count(50).spawn();
		block.setType(Material.AIR);
	}


}
