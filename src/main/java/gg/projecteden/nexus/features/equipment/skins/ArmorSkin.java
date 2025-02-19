package gg.projecteden.nexus.features.equipment.skins;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.models.CustomArmorType;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.DoubleStream;

public enum ArmorSkin implements EquipmentSkinType {
	DEFAULT,
	BERSERKER,
	BROWN_BERSERK,
	COPPER,
	DAMASCUS,
	DRUID,
	FISHING,
	HELLFIRE,
	JARL,
	MYTHRIL,
	TANK,
	THOR,
	WARDEN,
	WITHER,
	WOLF,
	WIZARD,
	;

	public String getBaseModel() {
		return "skins/armor/" + this.name().toLowerCase();
	}

	public CustomArmorType getCustomArmorType() {
		if (this == DEFAULT)
			return null;
		return CustomArmorType.valueOf(this.name());
	}

	/**
	 * This method is very picky... NBTAPI requires a fairly specific order when calling setType in combination with it.
	 * I wouldn't recommend touching this unless absolutely necessary... it's not clean, but it's functional
	 */
	// TODO - retain original dye color?
	@Override
	public ItemStack apply(ItemStack item) {
		ItemBuilder builder = new ItemBuilder(item);

		if (this == DEFAULT) {
			AtomicReference<Material> original = new AtomicReference<>();
			builder.nbt(nbt -> {
				try {
					if (!Nullables.isNullOrEmpty(nbt.getString("original_item")))
						original.set(Material.valueOf(nbt.getString("original_item").toUpperCase()));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});

			if (original.get() == null)
				return builder.build();

			if (isDefaultName(builder.material(), AdventureUtils.asLegacyText(item.getItemMeta().customName())))
				builder.resetName();

			builder.material(original.get());
			builder.nbt(nbt -> nbt.removeKey("original_item"));

			builder.removeModel();
			builder.removeAttribute(Attribute.ARMOR, new AttributeModifier(new NamespacedKey(Nexus.getInstance(), "skin_armor"), 0, AttributeModifier.Operation.ADD_NUMBER));
			builder.removeAttribute(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(new NamespacedKey(Nexus.getInstance(), "skin_armor_toughness"), 0, AttributeModifier.Operation.ADD_NUMBER));
			builder.removeAttribute(Attribute.KNOCKBACK_RESISTANCE, new AttributeModifier(new NamespacedKey(Nexus.getInstance(), "skin_knockback"), 0, AttributeModifier.Operation.ADD_NUMBER));

			builder.components(nbt -> {
				if (nbt.getCompoundList("minecraft:attribute_modifiers").isEmpty())
					nbt.removeKey("minecraft:attribute_modifiers");
			});

			return builder.build();
		}

		AtomicReference<Material> originalItem = new AtomicReference<>(builder.material());
		builder.nbt(nbt -> {
			if (!Nullables.isNullOrEmpty(nbt.getString("original_item")))
				originalItem.set(Material.valueOf(nbt.getString("original_item").toUpperCase()));
		});

		double armor = builder.material().getDefaultAttributeModifiers().get(Attribute.ARMOR).stream()
			.flatMapToDouble(attributeModifier -> DoubleStream.of(attributeModifier.getAmount())).sum();
		double toughness = builder.material().getDefaultAttributeModifiers().get(Attribute.ARMOR_TOUGHNESS).stream()
			.flatMapToDouble(attributeModifier -> DoubleStream.of(attributeModifier.getAmount())).sum();
		double kb = builder.material().getDefaultAttributeModifiers().get(Attribute.KNOCKBACK_RESISTANCE).stream()
			.flatMapToDouble(attributeModifier -> DoubleStream.of(attributeModifier.getAmount())).sum();

		builder.material(Material.valueOf("LEATHER" + builder.material().name().substring(builder.material().name().lastIndexOf("_"))));

		// This needs to be after the material call... apparently that clears the custom_data tag?
		builder.nbt(nbt -> nbt.setString("original_item", originalItem.get().name().toLowerCase()));

		if (armor > 0) {
			builder.removeAttribute(Attribute.ARMOR);
			builder.attribute(Attribute.ARMOR, new AttributeModifier(new NamespacedKey(Nexus.getInstance(), "skin_armor"), armor, AttributeModifier.Operation.ADD_NUMBER));
		}
		if (toughness > 0) {
			builder.removeAttribute(Attribute.ARMOR_TOUGHNESS);
			builder.attribute(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(new NamespacedKey(Nexus.getInstance(), "skin_armor_toughness"), toughness, AttributeModifier.Operation.ADD_NUMBER));
		}
		if (kb > 0) {
			builder.removeAttribute(Attribute.KNOCKBACK_RESISTANCE);
			builder.attribute(Attribute.KNOCKBACK_RESISTANCE, new AttributeModifier(new NamespacedKey(Nexus.getInstance(), "skin_knockback"), kb, AttributeModifier.Operation.ADD_NUMBER));
		}

		String customName = null;
		if (item.getItemMeta().hasCustomName())
			customName = AdventureUtils.asLegacyText(item.getItemMeta().customName());
		if (Nullables.isNullOrEmpty(customName) || isDefaultName(originalItem.get(), customName))
			builder.name(getDefaultName(originalItem.get()));

		String toolType = builder.material().name().toLowerCase().substring(builder.material().name().lastIndexOf('_') + 1);
		String model = getBaseModel() + "/" + toolType;

		builder.model(model);
		builder.dyeColor(getCustomArmorType().getShaderDyeColor());
		builder.components(nbt -> {
			nbt.getCompound("minecraft:dyed_color").setBoolean("show_in_tooltip", false);
		});

		return builder.build();
	}

	public String getDefaultName(Material material) {
		return switch (material) {
			case LEATHER_HELMET -> "Leather Cap";
			case LEATHER_CHESTPLATE -> "Leather Tunic";
			case LEATHER_LEGGINGS -> "Leather Pants";
			case CHAINMAIL_HELMET, CHAINMAIL_CHESTPLATE, CHAINMAIL_LEGGINGS, CHAINMAIL_BOOTS -> "&e" + StringUtils.camelCase(material.name());
			default -> StringUtils.camelCase(material.name());
		};
	}

	public boolean isDefaultName(Material material, String name) {
		return switch (material) {
			case LEATHER_HELMET -> "Leather Cap".equalsIgnoreCase(name);
			case LEATHER_CHESTPLATE -> "Leather Tunic".equalsIgnoreCase(name);
			case LEATHER_LEGGINGS -> "Leather Pants".equalsIgnoreCase(name);
			case CHAINMAIL_HELMET, CHAINMAIL_CHESTPLATE, CHAINMAIL_LEGGINGS, CHAINMAIL_BOOTS -> ("&e" + StringUtils.camelCase(material.name())).equalsIgnoreCase(name);
			default -> StringUtils.camelCase(material.name()).equalsIgnoreCase(name);
		};
	}

	@Override
	public boolean applies(ItemStack item) {
		return MaterialTag.ARMOR.isTagged(item);
	}

	@Override
	public ItemStack getBig(ItemStack item) {
		if (item == null)
			return null;

		if (this == DEFAULT)
			return new ItemBuilder(Material.LEATHER_HORSE_ARMOR)
				.model("skins/armor/default/big/" + item.getType().name().toLowerCase())
				.hideTooltip()
				.dyeColor(new ItemBuilder(item).dyeColor())
				.build();

		ItemBuilder builder = new ItemBuilder(item);
		String toolType = builder.material().name().toLowerCase().substring(builder.material().name().lastIndexOf('_') + 1);
		String model = getBaseModel() + "/big/" + toolType;

		if (MaterialTag.ARMOR_LEATHER.isTagged(item))
			builder.dyeColor(new ItemBuilder(item).dyeColor());

		return builder.model(model).hideTooltip().build();
	}

	@Override
	public ItemStack getTemplate() {
		return new ItemBuilder(Material.PAPER)
			.name("&e" + StringUtils.camelCase(name()) + " Armor Skin")
			.model(getBaseModel() + "/template")
			.build();
	}

	public static ArmorSkin of(ItemStack stack) {
		if (stack == null)
			return null;

		String model = ItemBuilder.Model.of(stack);
		if (model == null)
			return null;

		String baseModel = model.toLowerCase().substring(0, model.lastIndexOf('/'));

		for (ArmorSkin skin : values())
			if (skin.getBaseModel().equals(baseModel))
				return skin;

		return null;
	}

}
