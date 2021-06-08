package me.pugabyte.nexus.features.shops.update;

import eden.utils.TimeUtils.Time;
import joptsimple.internal.Strings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.NexusException;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.banker.BankerService;
import me.pugabyte.nexus.models.banker.Transaction.TransactionCause;
import me.pugabyte.nexus.models.shop.Shop;
import me.pugabyte.nexus.models.shop.Shop.ExchangeType;
import me.pugabyte.nexus.models.shop.Shop.Product;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.models.shop.ShopService;
import me.pugabyte.nexus.utils.BlockUtils;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.Name;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@Permission("group.admin")
@Cooldown(@Part(value = Time.SECOND))
public class ConvertShopCommand extends CustomCommand {

	public ConvertShopCommand(CommandEvent event) {
		super(event);
	}

	@Path("convert")
	void convert() {
		tryConvert(getTargetSignRequired());
		results();
	}

	@Path("convertRadius [radius]")
	void convertRadius(@Arg(value = "10", max = 25) int radius) {
		for (Block block : BlockUtils.getBlocksInRadius(location(), radius))
			if (MaterialTag.SIGNS.isTagged(block.getType()))
				tryConvert(block);

		results();
	}

	int signs = 0;
	int conversions = 0;
	int exceptions = 0;

	private void tryConvert(Block block) {
		tryConvert((Sign) block.getState());
	}

	private void tryConvert(Sign sign) {
		try {
			convert(sign);
		} catch (NexusException ex) {
			event.handleException(ex);
		} catch (Exception ex) {
			ex.printStackTrace();
			++exceptions;
		}
	}

	private void results() {
		if (signs == 0)
			error("Could not find any trade signs to convert");

		if (conversions > 0)
			send(PREFIX + "Successfully converted &e" + conversions + "&3/&e" + signs + " &3signs");

		if (exceptions > 0)
			send(PREFIX + "&cCould not convert &e" + exceptions + "&c/&e" + signs + " &csigns, errors logged to console");
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SignData {
		private OfflinePlayer player;
		private ItemStack item;
		private double price;
		private double moneyInSign = 0;
		private int stock = 0;
	}

	private void convert(Sign sign) {
		ShopService service = new ShopService();
		ShopGroup shopGroup = ShopGroup.of(player());

		if (!isValidShopSign(sign))
			error("Not a valid shop sign");

		SignData data = read(sign);
		if (!isSelf(data.getPlayer()) && !isStaff())
			error("This sign belongs to " + Name.of(data.getPlayer()));

		++signs;
		Shop shop = service.get(data.getPlayer());
		Product product = new Product(data.getPlayer().getUniqueId(), shopGroup, data.getItem(), data.getStock(), ExchangeType.SELL, data.getPrice());
		shop.getProducts().add(product);
		service.save(shop);
		if (data.getMoneyInSign() > 0)
			new BankerService().deposit(data.getPlayer(), data.getMoneyInSign(), shopGroup, TransactionCause.SERVER);
		sign.getBlock().setType(Material.AIR);
		++conversions;
	}

	private boolean isValidShopSign(Sign sign) {
		return Arrays.asList("[Trade]", "[Ench Trade]", "[Arrow Trade]", "[Potion Trade]").contains(stripColor(sign.getLine(0)));
	}

	public SignData read(Sign sign) {
		String[] lines = sign.getLines();

		if (StringUtils.stripColor(lines[0]).equals("[Trade]"))
			return readNormalSign(lines);

		if (StringUtils.stripColor(lines[0]).equals("[Ench Trade]"))
			return readEnchTradeSign(lines);

		if (StringUtils.stripColor(lines[0]).equals("[Potion Trade]"))
			return readPotionTradeSign(lines);

		if (StringUtils.stripColor(lines[0]).equals("[Arrow Trade]"))
			return readArrowTradeSign(lines);

		throw new InvalidInputException("Not a valid shop sign");
	}

	@NotNull
	private SignData readNormalSign(String[] lines) {
		SignData data = new SignData();
		data.setPrice(Double.parseDouble(lines[1].replace("$", "").split(":")[0]));
		data.setMoneyInSign(Double.parseDouble(lines[1].split(":")[1]));
		data.setStock(Integer.parseInt(lines[2].split(" ")[1].split(":")[1]));
		data.setPlayer(PlayerUtils.getPlayer(StringUtils.stripColor(lines[3])));

		String idForSale = lines[2].split(" ")[1].split(":")[0];

		Material material = Material.matchMaterial(idForSale);
		if (material == null) {
			if (idForSale.contains(";"))
				material = convertMaterial(Integer.parseInt(idForSale.split(";")[0]), Byte.parseByte(idForSale.split(";")[1]));
			else if (Utils.isInt(idForSale))
				material = convertMaterial(Integer.parseInt(idForSale.split(";")[0]), (byte) 0);
			else
				material = essentialsAliases(idForSale);
		}

		if (material == null)
			error("Could not convert material &e" + idForSale);
		ItemStack item = new ItemStack(material);
		item.setAmount(Integer.parseInt(lines[2].split(" ")[0]));
		data.setItem(item);
		return data;
	}

	@NotNull
	private SignData readEnchTradeSign(String[] lines) {
		SignData data = new SignData();
		data.setPrice(Double.parseDouble(lines[1].replace("$", "").split(" \\| ")[0]));
		String[] line3 = lines[2].split(" ");
		String enchShort = Strings.join(Arrays.copyOfRange(line3, 0, line3.length - 1), " ");
		Enchantment enchant = getEnchantFromShort(enchShort);
		int level = Integer.parseInt(line3[line3.length - 1]);

		if (enchant == null)
			throw new InvalidInputException("Enchantment from &e" + enchShort + " &cnot found");
		if (level < 1)
			throw new InvalidInputException("Enchantment level cannot be &e" + level);

		data.setItem(new ItemBuilder(Material.ENCHANTED_BOOK).enchant(enchant, level).build());
		data.setStock(Integer.parseInt(lines[1].split(" \\| ")[1]));
		data.setPlayer(PlayerUtils.getPlayer(StringUtils.stripColor(lines[3])));
		return data;
	}

	@NotNull
	private SignData readPotionTradeSign(String[] lines) {
		SignData data = new SignData();
		data.setPrice(Integer.parseInt(lines[1].replace("$", "").split(" \\| ")[0]));
		data.setStock(Integer.parseInt(lines[1].split(" \\| ")[1]));
		data.setPlayer(PlayerUtils.getPlayer(StringUtils.stripColor(lines[3])));

		boolean ext = (StringUtils.right(lines[2], 3).equals("Ext"));
		boolean isMultiplied = isMultiplied(lines[2]);

		ItemStack item = new ItemStack(Material.POTION);

		if (StringUtils.left(lines[2], 2).equals("S "))
			item.setType(Material.SPLASH_POTION);
		if (StringUtils.left(lines[2], 2).equals("L "))
			item.setType(Material.LINGERING_POTION);

		String potionName = lines[2].replace("P ", "").replace("L ", "")
				.replace("S ", "").replace(" Ext", "").replace(" 2", "");

		PotionMeta arrowMeta = (PotionMeta) item.getItemMeta();
		arrowMeta.setBasePotionData(new PotionData(getPotionFromShort(potionName), ext, isMultiplied));
		item.setItemMeta(arrowMeta);
		item.setAmount(1);

		data.setItem(item);
		return data;
	}

	@NotNull
	private SignData readArrowTradeSign(String[] lines) {
		SignData data = new SignData();
		data.setPrice(Integer.parseInt(lines[1].replace("$", "").split(" \\| ")[0]));
		data.setStock(Integer.parseInt(lines[1].split(" \\| ")[1]));
		data.setPlayer(PlayerUtils.getPlayer(StringUtils.stripColor(lines[3])));

		boolean ext = (StringUtils.right(lines[2], 3).equals("Ext"));
		boolean isMultiplied = isMultiplied(lines[2]);

		ItemStack item = new ItemStack(Material.TIPPED_ARROW);

		String potionName = lines[2].replace(" Ext", "").replace(" 2", "");
		String amountMaybe = potionName.split(" ")[0];
		if (Utils.isInt(amountMaybe)) {
			item.setAmount(Integer.parseInt(amountMaybe));
			potionName = potionName.replace(amountMaybe + " ", "");
		}

		PotionMeta arrowMeta = (PotionMeta) item.getItemMeta();
		arrowMeta.setBasePotionData(new PotionData(getPotionFromShort(potionName), ext, isMultiplied));
		item.setItemMeta(arrowMeta);
		item.setAmount(1);

		data.setItem(item);
		return data;
	}

	@SuppressWarnings("deprecation")
	public static Material convertMaterial(int id, byte data) {
		for (Material i : EnumSet.allOf(Material.class)) {
			if (!i.isLegacy())
				continue;
			if (i.getId() == id)
				return Bukkit.getUnsafe().fromLegacy(new MaterialData(i, data));
		}
		return null;
	}

	public Enchantment getEnchantFromShort(String enchantShort) {
		return switch (enchantShort) {
			case "Eff." -> Enchantment.DIG_SPEED;
			case "Silk Touch" -> Enchantment.SILK_TOUCH;
			case "Unbreaking" -> Enchantment.DURABILITY;
			case "Fortune" -> Enchantment.LOOT_BONUS_BLOCKS;
			case "Luck" -> Enchantment.LUCK;
			case "Lure" -> Enchantment.LURE;
			case "Mending" -> Enchantment.MENDING;
			case "C. of Vanish" -> Enchantment.VANISHING_CURSE;
			case "Protection" -> Enchantment.PROTECTION_ENVIRONMENTAL;
			case "Fire Prot." -> Enchantment.PROTECTION_FIRE;
			case "Feather Fall." -> Enchantment.PROTECTION_FALL;
			case "Blast Prot." -> Enchantment.PROTECTION_EXPLOSIONS;
			case "Proj. Prot." -> Enchantment.PROTECTION_PROJECTILE;
			case "Respiration" -> Enchantment.OXYGEN;
			case "Aqua Affinity" -> Enchantment.WATER_WORKER;
			case "Thorns" -> Enchantment.THORNS;
			case "Depth Strider" -> Enchantment.DEPTH_STRIDER;
			case "Frost Walker" -> Enchantment.FROST_WALKER;
			case "C. of Binding" -> Enchantment.BINDING_CURSE;
			case "Sharpness" -> Enchantment.DAMAGE_ALL;
			case "Smite" -> Enchantment.DAMAGE_UNDEAD;
			case "Bane" -> Enchantment.DAMAGE_ARTHROPODS;
			case "Knockback" -> Enchantment.KNOCKBACK;
			case "Fire Aspect" -> Enchantment.FIRE_ASPECT;
			case "Looting" -> Enchantment.LOOT_BONUS_MOBS;
			case "Sweeping" -> Enchantment.SWEEPING_EDGE;
			case "Power" -> Enchantment.ARROW_DAMAGE;
			case "Punch" -> Enchantment.ARROW_KNOCKBACK;
			case "Flame" -> Enchantment.ARROW_FIRE;
			case "Infinity" -> Enchantment.ARROW_INFINITE;
			default -> null;
		};
	}

	public boolean isPotion(String name) {
		return !Arrays.asList("Water", "Mundane", "Thick", "Awkward").contains(name);
	}

	public boolean isMultiplied(String string) {
		String[] split = string.split(" ");
		return Utils.isInt(split[split.length - 1]);
	}


	public PotionType getPotionFromShort(String potionShort) {
		return switch (potionShort) {
			case "Water" -> PotionType.WATER;
			case "Mundane" -> PotionType.MUNDANE;
			case "Awkward" -> PotionType.AWKWARD;
			case "Thick" -> PotionType.THICK;
			case "Night Vision" -> PotionType.NIGHT_VISION;
			case "Invisibility" -> PotionType.INVISIBILITY;
			case "Jump Boost" -> PotionType.JUMP;
			case "Fire Res." -> PotionType.FIRE_RESISTANCE;
			case "Speed" -> PotionType.SPEED;
			case "Slowness" -> PotionType.SLOWNESS;
			case "Water Br." -> PotionType.WATER_BREATHING;
			case "Inst. Health" -> PotionType.INSTANT_HEAL;
			case "Inst. Damage" -> PotionType.INSTANT_DAMAGE;
			case "Poison" -> PotionType.POISON;
			case "Regen." -> PotionType.REGEN;
			case "Strength" -> PotionType.STRENGTH;
			case "Weakness" -> PotionType.WEAKNESS;
			case "Luck" -> PotionType.LUCK;
			default -> null;
		};
	}

	public Material essentialsAliases(String name) {
		try {
			return ItemDb.get(name);
		} catch (Exception e) {
			error("Could not parse item from essentials aliases");
			Nexus.warn("Could not convert the shop from player " + name());
		}
		return null;
	}

	private static class ItemDb {
		private static final Map<String, Material> items = new HashMap<>();

		static {
			String line;
			try {
				BufferedReader br = new BufferedReader(new FileReader(Nexus.getFile("items.csv")));
				while ((line = br.readLine()) != null) {
					try {
						String[] split = line.split(",");
						items.put(split[0].toUpperCase(), convertMaterial(Integer.parseInt(split[1]), Byte.parseByte(split[2])));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public static Material get(String id) {
			return items.get(id.toUpperCase());
		}
	}

}
