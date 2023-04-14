package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.features.itemtags.Condition;
import gg.projecteden.nexus.features.itemtags.Rarity;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.ErasureType;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Switch;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.TabCompleter;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Vararg;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
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

	@Description("Set an item's material")
	void material(Material material) {
		item.material(material);
	}

	@Description("Set a stack's size")
	void amount(int amount) {
		item.amount(amount);
	}

	@Description("Set an item's color")
	void color(ColorType color) {
		item.color(color);
	}

	@Description("Set an item's durability (deprecated)")
	void durability(int durability) {
		item.durability(durability);
	}

	@Description("Set an item's damage")
	void damage(int damage) {
		item.damage(damage);
	}

	@Description("Set an item's name")
	void name(String name) {
		item.name(name);
	}

	@Description("Remove an item's name")
	void name_reset() {
		item.resetName();
	}

	@Description("Add lore")
	void lore_add(@Vararg String text) {
		item.lore(text);
	}

	@Description("Update lore")
	void lore_set(int line, @Vararg String text) {
		item.lore(line, text);
	}

	@Description("Remove a line of lore")
	void lore_remove(int line) {
		item.loreRemove(line);
	}

	@Description("Remove all lore")
	void lore_clear() {
		item.resetLore();
	}

	@Description("Enchant an item")
	void enchant(Enchantment enchant, @Optional("1") int level) {
		item.enchant(enchant, level);
	}

	@Description("Enchant an item with the max level for the provided enchant")
	void enchant_max(Enchantment enchant) {
		item.enchantMax(enchant);
	}

	@Description("Remove an enchant from an item")
	void enchant_remove(Enchantment enchant) {
		item.enchantRemove(enchant);
	}

	@Description("Add an enchant glint to an item")
	void glow(@Optional Boolean glow) {
		if (glow == null)
			glow = !item.isGlowing();

		item.glow(glow);
	}

	@Description("Make an item unbreakable")
	void unbreakable() {
		item.unbreakable();
	}

	@Description("Add item flags to an item")
	void itemFlags(@ErasureType(ItemFlag.class) List<ItemFlag> flags) {
		item.itemFlags(flags);
	}

	@Description("Dye an item")
	void dye(ChatColor color) {
		item.dyeColor(toBukkitColor(color));
	}

	@Description("Set a potion's type")
	void potion_type(PotionType type, @Switch @Optional boolean extended, @Switch @Optional boolean upgraded) {
		item.potionType(type, extended, upgraded);
	}

	@Description("Add a custom potion effect to a potion")
	void potion_effect(PotionEffectType type, int seconds, int amplifier) {
		item.potionEffect(type, seconds, amplifier);
	}

	@Description("Set a potion's color")
	void potion_color(ChatColor color) {
		item.potionEffectColor(toBukkitColor(color));
	}

	@Description("Set a firework's power")
	void firework_power(int power) {
		item.fireworkPower(power);
	}

	@Description("Set a firework's effect")
	void firework_effect(
		@Switch @Optional Type type,
		@Switch @Optional boolean flicker,
		@Switch @Optional boolean trail,
		@Switch @Optional @ErasureType(ChatColor.class) List<ChatColor> colors,
		@Switch @Optional @ErasureType(ChatColor.class) List<ChatColor> fadeColors
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

	@Description("Set or view a skull's owner")
	void skullOwner(@Optional Nerd owner) {
		if (owner == null) {
			final OfflinePlayer existing = item.skullOwner();
			send(PREFIX + "Skull owner: " + (existing == null ? "null" : Nickname.of(existing)) + " / " + item.skullOwnerName());
		} else
			item.skullOwner(owner);
	}

	@Description("Add a banner pattern")
	void banner_pattern(DyeColor color, PatternType pattern) {
		item.pattern(color, pattern);
	}

	@Description("Set a banner to a symbol")
	void banner_symbol(Symbol symbol, DyeColor color) {
		item.symbolBanner(symbol, color);
	}

	@Description("Set a map's id")
	void map_id(int id) {
		item.mapId(id);
	}

	@Description("Set a map's world")
	void map_view(@Optional("current") World world) {
		item.createMapView(world);
	}

	@Description("Set a book's title")
	void book_title(String title) {
		item.bookTitle(title);
	}

	@Description("Set a book's author")
	void book_author(@TabCompleter(Nerd.class) String author) {
		item.bookAuthor(author);
	}

	@Description("Set the contents of a book's pages")
	void book_pages_set(int page, String content) {
		item.bookPage(page, content);
	}

	@Description("Add a new page to a book")
	void book_pages_add(String content) {
		item.bookPages(content);
	}

	@Description("Remove a page from a book")
	void book_pages_remove(int page) {
		item.bookPageRemove(page);
	}

	@Description("Set the generation of a book")
	void book_generation(Generation generation) {
		item.bookGeneration(generation);
	}

	@Description("Set an axolotl bucket's variant")
	void axolotl(Axolotl.Variant variant) {
		item.axolotl(variant);
	}

	@Description("Set a spawn egg's entity type")
	void spawnEgg(EntityType entityType) {
		item.spawnEgg(entityType);
	}

	@SneakyThrows
	@Description("Set an NBT key")
	void nbt_set(String key, NBTDataTypeType type, String value) {
		nbt_set(type.getClazz().getConstructor().newInstance(), key, value);
	}

	private <T> void nbt_set(NBTDataType<T> clazz, String key, String value) {
		item.nbt(item -> clazz.getSetter().accept(item, key, clazz.getConverter().apply(value)));
	}

	@SneakyThrows
	@Description("Remove an NBT key")
	void nbt_unset(String key) {
		item.nbt(item -> item.removeKey(key));
	}

	@Description("Set an item's rarity tag")
	void rarity(Rarity rarity) {
		item.rarity(rarity);
	}

	@Description("Set an item's condition tag")
	void condition(Condition condition) {
		item.condition(condition);
	}

	@Description("Set an item attribute")
	void attribute(Attribute attribute, String name, double amount, Operation operation, @Optional EquipmentSlot slot) {
		item.attribute(attribute, new AttributeModifier(UUID.nameUUIDFromBytes(name.getBytes()), name, amount, operation, slot));
	}

	@Description("Set an item setting")
	void setting(ItemSetting setting, @Optional Boolean value) {
		if (value == null)
			value = !setting.of(item);

		item.setting(setting, value);
	}

	@Description("Remove an item setting")
	void setting_unset(ItemSetting setting) {
		item.unset(setting);
	}

	@Description("Set an item's model ID")
	void modelId(int id) {
		item.modelId(id);
	}

	@Description("Make an item soulbound without the enchant")
	void soulbound() {
		item.soulbound();
	}

}
