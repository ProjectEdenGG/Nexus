package gg.projecteden.nexus.features.mcmmo.mastery.skills.acrobatics;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.equipment.skins.ArmorSkin;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class SlimeBoots implements Listener {

	public static final NamespacedKey KEY = new NamespacedKey(Nexus.getInstance(), "slime_boots");

	public static final ItemStack ITEM = ArmorSkin.SLIME.apply(new ItemBuilder(Material.DIAMOND_BOOTS)
			.name("&aSlime Boots")
			.pdc(pdc -> pdc.set(KEY, PersistentDataType.BOOLEAN, true))
			.attribute(Attribute.BOUNCINESS, new AttributeModifier(NamespacedKey.minecraft("slime_boots_bounce"), 1, Operation.ADD_NUMBER, EquipmentSlotGroup.FEET))
			.attribute(Attribute.AIR_DRAG_MODIFIER, new AttributeModifier(NamespacedKey.minecraft("slime_boots_air_drag"), -0.5, Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.FEET))
			.attribute(Attribute.FRICTION_MODIFIER, new AttributeModifier(NamespacedKey.minecraft("slime_boots_friction"), -0.5, Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.FEET)))
		.build();

	static {
		Nexus.registerListener(new SlimeBoots());
	}

	@EventHandler
	public void onFallDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (event.getCause() != DamageCause.FALL) return;

		ItemStack boots = player.getEquipment().getBoots();
		if (Nullables.isNullOrAir(boots)) return;
		if (!boots.getPersistentDataContainer().has(KEY)) return;

		event.setCancelled(true);
	}

}
