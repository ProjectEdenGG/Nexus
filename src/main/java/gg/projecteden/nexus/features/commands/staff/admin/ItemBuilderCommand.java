package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.features.itemtags.Condition;
import gg.projecteden.nexus.features.itemtags.ItemTagsUtils;
import gg.projecteden.nexus.features.itemtags.Rarity;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemFlags;
import gg.projecteden.nexus.utils.ItemBuilder.ItemSetting;
import gg.projecteden.nexus.utils.ItemUtils.NBTDataType;
import gg.projecteden.nexus.utils.ItemUtils.NBTDataType.NBTDataTypeType;
import gg.projecteden.nexus.utils.SymbolBanner.Symbol;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

@Permission("nexus.itembuilder")
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

	@Path("material <material>")
	@Description("Set an item's material")
	void material(Material material) {
		item.material(material);
	}

	@Path("amount <amount>")
	@Description("Set a stack's size")
	void amount(int amount) {
		item.amount(amount);
	}

	@Path("color <color>")
	@Description("Set an item's color")
	void color(ColorType colorType) {
		item.color(colorType);
	}

	@Path("dye <color>")
	@Description("Dye an item")
	void dye(ChatColor color) {
		item.dyeColor(ColorType.toBukkitColor(color));
	}

	@Path("durability <durability>")
	@Description("Set an item's durability (deprecated)")
	void durability(int durability) {
		item.durability(durability);
	}

	@Path("damage <damage>")
	@Description("Set an item's damage")
	void damage(int damage) {
		item.damage(damage);
	}

	@Path("name <name>")
	@Description("Set an item's name")
	void name(String name) {
		item.name(name);
	}

	@Path("name reset")
	@Description("Remove an item's name")
	void name_reset() {
		item.resetName();
	}

	@Path("lore add <text...>")
	@Description("Add lore")
	void lore_add(String text) {
		item.lore(text);
	}

	@Path("lore set <line> <text...>")
	@Description("Update lore")
	void lore_set(int line, String text) {
		item.lore(line, text);
	}

	@Path("lore remove <line>")
	@Description("Remove a line of lore")
	void lore_remove(int line) {
		item.loreRemove(line);
	}

	@Path("lore clear")
	@Description("Remove all lore")
	void lore_clear() {
		item.resetLore();
	}

	@Path("enchant <enchant> [level]")
	@Description("Enchant an item")
	void enchant(Enchantment enchantment, @Arg("1") int level) {
		item.enchant(enchantment, level);
	}

	@Path("enchant max <enchant>")
	@Description("Enchant an item with the max level for the provided enchant")
	void enchant_max(Enchantment enchantment) {
		item.enchantMax(enchantment);
	}

	@Path("enchant remove <enchant>")
	@Description("Remove an enchant from an item")
	void enchant_remove(Enchantment enchantment) {
		item.enchantRemove(enchantment);
	}

	@Path("glow [state]")
	@Description("Add an enchant glint to an item")
	void glow(Boolean glow) {
		if (glow == null)
			glow = !item.isGlowing();

		item.glow(glow);
	}

	@Path("unbreakable")
	@Description("Make an item unbreakable")
	void unbreakable() {
		item.unbreakable();
	}

	@Path("itemFlags <flags...>")
	@Description("Add item flags to an item")
	void itemFlags(@Arg(type = ItemFlag.class) List<ItemFlag> flags) {
		item.itemFlags(flags);
	}

	@Path("itemFlags hide_all")
	@Description("Hide all item flags on an item")
	void itemFlags() {
		item.itemFlags(ItemFlags.HIDE_ALL);
	}

	@Path("potion type <type> [--extended] [--upgraded]")
	@Description("Set a potion's type")
	void potion_type(PotionType potionType, @Switch boolean extended, @Switch boolean upgraded) {
		if (extended)
			potionType = PotionType.valueOf("LONG_" + potionType.name());
		if (upgraded)
			potionType = PotionType.valueOf("STRONG_" + potionType.name());
		item.potionType(potionType);
	}

	@Path("potion effect <type> <seconds> <amplifier>")
	@Description("Add a custom potion effect to a potion")
	void potion_effect(PotionEffectType type, int seconds, int amplifier) {
		item.potionEffect(type, seconds, amplifier);
	}

	@Path("potion color <color>")
	@Description("Set a potion's color")
	void potion_color(ChatColor color) {
		item.potionEffectColor(ColorType.toBukkitColor(color));
	}

	@Path("firework power <power>")
	@Description("Set a firework's power")
	void fireworkPower(int power) {
		item.fireworkPower(power);
	}

	@Path("firework effect [--type] [--flicker] [--trail] [--colors] [--fadeColors]")
	@Description("Set a firework's effect")
	void fireworkEffect(
		@Switch Type type,
		@Switch boolean flicker,
		@Switch boolean trail,
		@Switch @Arg(type = ChatColor.class) List<ChatColor> colors,
		@Switch @Arg(type = ChatColor.class) List<ChatColor> fadeColors
	) {
		final Function<List<ChatColor>, List<Color>> colorMapper = colors1 ->
			colors1.stream().map(color -> Objects.requireNonNull(ColorType.toBukkitColor(color))).toList();

		item.fireworkEffect(FireworkEffect.builder()
			.with(type)
			.flicker(flicker)
			.trail(trail)
			.withColor(colorMapper.apply(colors))
			.withFade(colorMapper.apply(fadeColors))
			.build());
	}

	@Path("skull owner [owner]")
	@Description("Set a skull's owner")
	void skullOwner(Nerd owner) {
		if (owner == null) {
			final OfflinePlayer existing = item.skullOwner();
			send(PREFIX + "Skull owner: " + (existing == null ? "null" : Nickname.of(existing)) + " / " + item.skullOwnerName());
		} else
			item.skullOwner(owner);
	}

	@Path("banner pattern <color> <pattern>")
	@Description("Add a banner pattern")
	void banner_pattern(DyeColor color, PatternType pattern) {
		item.pattern(color, pattern);
	}

	@Path("banner symbol <symbol> <color>")
	@Description("Set a banner to a symbol")
	void banner_symbol(Symbol symbol, DyeColor color) {
		item.symbolBanner(symbol, color);
	}

	@Path("map id <id>")
	@Description("Set a map's id")
	void map_id(int id) {
		item.mapId(id);
	}

	@Path("map view [world]")
	@Description("Set a map's world")
	void map_view(@Arg("current") World world) {
		item.createMapView(world);
	}

	@Path("book title <title>")
	@Description("Set a book's title")
	void book_title(String title) {
		item.bookTitle(title);
	}

	@Path("book author <author>")
	@Description("Set a book's author")
	void book_author(@Arg(tabCompleter = Nerd.class) String author) {
		item.bookAuthor(author);
	}

	@Path("book pages set <page> <content>")
	@Description("Set the contents of a book's pages")
	void book_pages_set(int page, String content) {
		item.bookPage(page, content);
	}

	@Path("book pages add <content>")
	@Description("Add a new page to a book")
	void book_pages_add(String content) {
		item.bookPages(content);
	}

	@Path("book pages remove <page>")
	@Description("Remove a page from a book")
	void book_pages_remove(int page) {
		item.bookPageRemove(page);
	}

	@Path("book generation <generation>")
	@Description("Set the generation of a book")
	void book_generation(Generation generation) {
		item.bookGeneration(generation);
	}

	@Path("axolotl <variant>")
	@Description("Set an axolotl bucket's variant")
	void axolotl(Axolotl.Variant variant) {
		item.axolotl(variant);
	}

	@Path("spawnEgg <variant>")
	@Description("Set a spawn egg's entity type")
	void spawnEgg(EntityType entityType) {
		item.spawnEgg(entityType);
	}

	@SneakyThrows
	@Path("nbt set <key> <type> <value>")
	@Description("Set an NBT key")
	void nbt_set(String key, NBTDataTypeType type, String value) {
		nbt_set(type.getClazz().getConstructor().newInstance(), key, value);
	}

	private <T> void nbt_set(NBTDataType<T> clazz, String key, String value) {
		item.nbt(item -> clazz.getSetter().accept(item, key, clazz.getConverter().apply(value)));
	}

	@SneakyThrows
	@Path("nbt unset <key>")
	@Description("Remove an NBT key")
	void nbt(String key) {
		item.nbt(item -> item.removeKey(key));
	}

	@Path("rarity <rarity>")
	@Description("Set an item's rarity tag")
	void rarity(Rarity rarity) {
		item = new ItemBuilder(ItemTagsUtils.updateRarity(item.build(), rarity));
	}

	@Path("condition <condition>")
	@Description("Set an item's condition tag")
	void condition(Condition condition) {
		item = new ItemBuilder(ItemTagsUtils.updateCondition(item.build(), condition));
		item = new ItemBuilder(Condition.setDurability(item.build(), condition));
	}

	@Path("attribute <attribute> <name> <amount> <operation> [slot]")
	@Description("Set an item attribute")
	void attribute(Attribute attribute, String name, double amount, Operation operation, EquipmentSlot slot) {
		item.attribute(attribute, new AttributeModifier(UUID.nameUUIDFromBytes(name.getBytes()), name, amount, operation, slot));
	}

	@Path("setting <setting> [state]")
	@Description("Set an item setting")
	void setting(ItemSetting setting, Boolean value) {
		if (value == null)
			value = !setting.of(item);

		item.setting(setting, value);
	}

	@Path("setting unset <setting>")
	@Description("Remove an item setting")
	void setting_unset(ItemSetting setting) {
		item.unset(setting);
	}

	@Path("modelId <id>")
	@Description("Set an item's model ID")
	void modelId(String id) {
		item.model(id);
	}

	@Path("soulbound")
	@Description("Make an item soulbound without the enchant")
	void soulbound() {
		item.soulbound();
	}

	@Path("customModelData <int>")
	@Description("Set the Custom Model Data")
	void customModelData(int data) {
		item.customModelData(data);
	}

	@Path("maxStackSize <int>")
	@Description("Set the max stack size")
	void maxStackSize(int size) {
		item.maxStackSize(size);
	}

}
