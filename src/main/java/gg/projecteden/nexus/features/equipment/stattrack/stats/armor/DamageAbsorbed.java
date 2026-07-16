package gg.projecteden.nexus.features.equipment.stattrack.stats.armor;

import gg.projecteden.nexus.features.equipment.stattrack.StatTrackStatistic;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.DisplayName;
import gg.projecteden.nexus.features.equipment.stattrack.stats.annotations.Id;
import gg.projecteden.nexus.utils.MaterialTag;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Id("damage_absorbed")
@DisplayName("Damage Absorbed")
public class DamageAbsorbed extends StatTrackStatistic {

	@Override
	public MaterialTag getApplicableTools() {
		return MaterialTag.ARMOR;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	@SuppressWarnings("deprecation")
	public void onDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		if (!event.isApplicable(EntityDamageEvent.DamageModifier.ARMOR))
			return;

		double armorMitigation = -event.getDamage(EntityDamageEvent.DamageModifier.ARMOR);

		if (armorMitigation <= 0)
			return;

		List<ArmorPiece> pieces = List.of(
			new ArmorPiece(player.getInventory().getHelmet(), EquipmentSlot.HEAD),
			new ArmorPiece(player.getInventory().getChestplate(), EquipmentSlot.CHEST),
			new ArmorPiece(player.getInventory().getLeggings(), EquipmentSlot.LEGS),
			new ArmorPiece(player.getInventory().getBoots(), EquipmentSlot.FEET)
		);

		double totalArmorPoints = pieces.stream()
			.mapToDouble(piece ->
				getArmorPoints(piece.item(), piece.slot()))
			.sum();

		if (totalArmorPoints <= 0)
			return;

		for (ArmorPiece piece : pieces) {
			ItemStack item = piece.item();

			if (item == null || item.getType().isAir())
				continue;

			double armorPoints = getArmorPoints(item, piece.slot());

			if (armorPoints <= 0)
				continue;

			double share = armorPoints / totalArmorPoints;
			double mitigatedByPiece = armorMitigation * share;

			track(item, mitigatedByPiece);
		}
	}

	private double getArmorPoints(ItemStack item, EquipmentSlot slot) {
		if (item == null || item.getType().isAir())
			return 0;

		ItemAttributeModifiers attributes = item.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS);

		if (attributes == null)
			attributes = item.getType().getDefaultData(DataComponentTypes.ATTRIBUTE_MODIFIERS);

		if (attributes == null)
			return 0;

		return attributes.modifiers().stream()
			.filter(entry -> entry.attribute().equals(Attribute.ARMOR))
			.filter(entry -> entry.getGroup().test(slot))
			.map(ItemAttributeModifiers.Entry::modifier)
			.filter(modifier ->
				modifier.getOperation()
					== AttributeModifier.Operation.ADD_NUMBER)
			.mapToDouble(AttributeModifier::getAmount)
			.sum();
	}

	private record ArmorPiece(ItemStack item, EquipmentSlot slot) {}

}
