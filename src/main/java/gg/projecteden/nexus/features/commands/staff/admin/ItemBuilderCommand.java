package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.features.itemtags.Condition;
import gg.projecteden.nexus.features.itemtags.Rarity;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemSetting;
import gg.projecteden.nexus.utils.ItemUtils.NBTDataType.NBTDataTypeType;
import gg.projecteden.nexus.utils.SymbolBanner.Symbol;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Axolotl;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static gg.projecteden.nexus.utils.ColorType.toBukkitColor;
import static java.util.Objects.requireNonNull;

@Permission(Group.SENIOR_STAFF)
public class ItemBuilderCommand extends CustomCommand {
	private ItemBuilder item;

	public ItemBuilderCommand(@NonNull CommandEvent event) {
		super(event);
		player();
		if (isCommandEvent())
			item = new ItemBuilder(getToolRequired());
	}

	@Override
	public void postProcess() {
		player().getInventory().setItemInMainHand(item.build());
	}

	@Path("amount <amount>")
	void amount(int amount) {
		item.amount(amount);
	}

	@Path("color <color>")
	void color(ColorType colorType) {
		item.color(colorType);
	}

	@Path("durability <durability>")
	void durability(int durability) {
		item.durability(durability);
	}

	@Path("damage <damage>")
	void damage(int damage) {
		item.damage(damage);
	}

	@Path("name <name>")
	void name(String name) {
		item.name(name);
	}

	@Path("name reset")
	void name_reset() {
		item.resetName();
	}

	@Path("lore add <text>")
	void lore_add(String text) {
		item.lore(text);
	}

	@Path("lore set <line> <text>")
	void lore_set(int line, String text) {
		item.lore(line, text);
	}

	@Path("lore remove <line>")
	void lore_remove(int line) {
		item.loreRemove(line);
	}

	@Path("lore clear")
	void lore_clear() {
		item.resetLore();
	}

	@Path("enchant <enchant> [level]")
	void enchant(Enchantment enchantment, @Arg("1") int level) {
		item.enchant(enchantment, level);
	}

	@Path("enchant max <enchant>")
	void enchant_max(Enchantment enchantment) {
		item.enchantMax(enchantment);
	}

	@Path("enchant remove <enchant>")
	void enchant_remove(Enchantment enchantment) {
		item.enchantRemove(enchantment);
	}

	@Path("glow [state]")
	void glow(Boolean glow) {
		if (glow == null)
			glow = !item.isGlowing();

		item.glow(glow);
	}

	@Path("unbreakable")
	void unbreakable() {
		item.unbreakable();
	}

	@Path("itemFlags <flags...>")
	void itemFlags(@Arg(type = ItemFlag.class) List<ItemFlag> flags) {
		item.itemFlags(flags);
	}

	@Path("dye <color>")
	void dye(ChatColor color) {
		item.dyeColor(toBukkitColor(color));
	}

	@Path("potion type <type> [--extended] [--upgraded]")
	void potion_type(PotionType potionType, @Switch boolean extended, @Switch boolean upgraded) {
		item.potionType(potionType, extended, upgraded);
	}

	@Path("potion effect <type> <seconds> <amplifier>")
	void potion_effect(PotionEffectType type, int seconds, int amplifier) {
		item.potionEffect(type, seconds, amplifier);
	}

	@Path("potion color <color>")
	void potion_color(ChatColor color) {
		item.potionEffectColor(toBukkitColor(color));
	}

	@Path("firework power <power>")
	void fireworkPower(int power) {
		item.fireworkPower(power);
	}

	@Path("firework effect [--type] [--flicker] [--trail] [--colors] [--fadeColors]")
	void fireworkEffect(
		@Switch Type type,
		@Switch boolean flicker,
		@Switch boolean trail,
		@Switch @Arg(type = ChatColor.class) List<ChatColor> colors,
		@Switch @Arg(type = ChatColor.class) List<ChatColor> fadeColors
	) {
		final Function<List<ChatColor>, List<Color>> colorMapper = colors1 ->
			colors1.stream().map(color -> requireNonNull(toBukkitColor(color))).toList();

		item.fireworkEffect(FireworkEffect.builder()
			.with(type)
			.flicker(flicker)
			.trail(trail)
			.withColor(colorMapper.apply(colors))
			.withFade(colorMapper.apply(fadeColors))
			.build());
	}

	@Path("skull owner <owner>")
	void skullOwner(Nerd owner) {
		item.skullOwner(owner);
	}

	@Path("banner pattern <color> <pattern>")
	void banner_pattern(DyeColor color, PatternType pattern) {
		item.pattern(color, pattern);
	}

	@Path("banner symbol <symbol> <color>")
	void banner_symbol(Symbol symbol, DyeColor color) {
		item.symbolBanner(symbol, color);
	}

	@Path("map id <id>")
	void map_id(int id) {
		item.mapId(id);
	}

	@Path("map view [world]")
	void map_view(@Arg("current") World world) {
		item.createMapView(world);
	}

	@Path("book title <title>")
	void book_title(String title) {
		item.bookTitle(title);
	}

	@Path("book author <author>")
	void book_author(@Arg(tabCompleter = Nerd.class) String author) {
		item.bookAuthor(author);
	}

	@Path("book pages set <page> <content>")
	void book_pages_set(int page, String content) {
		item.bookPage(page, content);
	}

	@Path("book pages add <content>")
	void book_pages_add(String content) {
		item.bookPages(content);
	}

	@Path("book pages remove <page>")
	void book_pages_remove(int page) {
		item.bookPageRemove(page);
	}

	@Path("book generation <generation>")
	void book_generation(Generation generation) {
		item.bookGeneration(generation);
	}

	@Path("axolotl <variant>")
	void axolotl(Axolotl.Variant variant) {
		item.axolotl(variant);
	}

	@SneakyThrows
	@Path("nbt set <type> <key> <value>")
	void nbt_set(NBTDataTypeType type, String key, String value) {
		final var clazz = type.getClazz().getConstructor().newInstance();
		item.nbt(item -> clazz.getSetter().accept(item, key, clazz.getConverter().apply(value)));
	}

	@SneakyThrows
	@Path("nbt unset <key>")
	void nbt(String key) {
		item.nbt(item -> item.removeKey(key));
	}

	@Path("rarity <rarity>")
	void rarity(Rarity rarity) {
		item.rarity(rarity);
	}

	@Path("condition <condition>")
	void condition(Condition condition) {
		item.condition(condition);
	}

	@Path("attribute <attribute> <name> <amount> <operation> [slot]")
	void attribute(Attribute attribute, String name, double amount, Operation operation, EquipmentSlot slot) {
		item.attribute(attribute, new AttributeModifier(UUID.nameUUIDFromBytes(name.getBytes()), name, amount, operation, slot));
	}

	@Path("setting <setting> [state]")
	void setting(ItemSetting setting, Boolean value) {
		if (value == null)
			value = !setting.of(item);

		item.setting(setting, value);
	}

	@Path("setting unset <setting>")
	void setting_unset(ItemSetting setting) {
		item.unset(setting);
	}

	@Path("customModelData <id>")
	void customModelData(int id) {
		item.customModelData(id);
	}

	@Path("soulbound")
	void soulbound() {
		item.soulbound();
	}

}
