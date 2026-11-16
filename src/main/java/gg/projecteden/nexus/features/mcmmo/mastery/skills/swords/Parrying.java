package gg.projecteden.nexus.features.mcmmo.mastery.skills.swords;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.mcmmo.mastery.models.ItemAttributeHandler;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Tasks;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BlocksAttacks;
import io.papermc.paper.datacomponent.item.blocksattacks.DamageReduction;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Parrying extends ItemAttributeHandler {
	public static final String PERMISSION = "nexus.mcmmo.swords.parrying";

	private static final int STUN_TICKS = 20;
	private static final BlocksAttacks SWORD_BLOCKING = BlocksAttacks.blocksAttacks()
		.blockDelaySeconds(0)
		.addDamageReduction(DamageReduction.damageReduction()
			.horizontalBlockingAngle(90)
			.base(0)
			.factor(1)
			.build())
		.blockSound(Key.key("minecraft:item.shield.block"))
		.build();

	private final NamespacedKey perkMarker;
	private final Map<UUID, Object> stunnedMobs = new HashMap<>();

	public Parrying() {
		this.perkMarker = new NamespacedKey(Nexus.getInstance(), "parrying_sword");
	}

	@Override
	public MaterialTag getApplicableItems() {
		return MaterialTag.SWORDS;
	}

	@Override
	public String getPermission() {
		return PERMISSION;
	}

	@Override
	public void apply(Player player) {
		ItemStack sword = player.getInventory().getItemInMainHand();
		if (sword.hasData(DataComponentTypes.BLOCKS_ATTACKS))
			return;

		sword.setData(DataComponentTypes.BLOCKS_ATTACKS, SWORD_BLOCKING);
		sword.editPersistentDataContainer(data -> data.set(perkMarker, PersistentDataType.BYTE, (byte) 1));
	}

	@Override
	public void remove(Player player) {
		stripPerk(player.getInventory().getItemInMainHand());
		stripPerk(player.getInventory().getItemInOffHand());
	}

	private boolean hasPerk(ItemStack item) {
		return item != null && item.getPersistentDataContainer().has(perkMarker, PersistentDataType.BYTE);
	}

	private void stripPerk(ItemStack item) {
		if (!hasPerk(item))
			return;

		item.resetData(DataComponentTypes.BLOCKS_ATTACKS);
		item.editPersistentDataContainer(data -> data.remove(perkMarker));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSwordUse(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (!event.getPlayer().hasPermission(PERMISSION) && hasPerk(event.getItem()))
			event.setUseItemInHand(Event.Result.DENY);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onUnauthorizedBlock(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player defender
			&& !defender.hasPermission(PERMISSION)
			&& hasPerk(defender.getActiveItem())
			&& event.isApplicable(DamageModifier.BLOCKING)
			&& event.getDamage(DamageModifier.BLOCKING) < 0) {
			event.setDamage(DamageModifier.BLOCKING, 0);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onParry(EntityDamageByEntityEvent event) {
		if (event.getCause() != DamageCause.ENTITY_ATTACK
			|| !(event.getEntity() instanceof Player defender)
			|| !defender.hasPermission(PERMISSION)
			|| !hasPerk(defender.getActiveItem())
			|| !event.isApplicable(DamageModifier.BLOCKING)
			|| event.getDamage(DamageModifier.BLOCKING) >= 0)
			return;

		if (event.getDamager() instanceof Player attacker) {
			attacker.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, STUN_TICKS, 0));
		} else if (event.getDamager() instanceof Mob attacker) {
			UUID id = attacker.getUniqueId();
			Object stun = new Object();
			stunnedMobs.put(id, stun);

			attacker.setTarget(null);
			attacker.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, STUN_TICKS, 3));

			Tasks.wait(STUN_TICKS, () -> stunnedMobs.remove(id, stun));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onMobTarget(EntityTargetLivingEntityEvent event) {
		if (event.getTarget() instanceof Player && stunnedMobs.containsKey(event.getEntity().getUniqueId()))
			event.setCancelled(true);
	}
}
