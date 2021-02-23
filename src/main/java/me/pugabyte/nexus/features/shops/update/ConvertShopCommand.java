package me.pugabyte.nexus.features.shops.update;

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
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.BlockUtils;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Time;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

public class ConvertShopCommand extends CustomCommand {

	public ConvertShopCommand(CommandEvent event) {
		super(event);
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

	@Path("read [radius]")
	@Cooldown(@Part(value = Time.SECOND, x = 10))
	void read(@Arg("10") int radius) {
		if (radius > 25) {
			send(PREFIX + "Limiting search to 25 block radius. Please run the command multiple times in different locations to find more signs");
			radius = 25;
		}

		List<Block> signs = new ArrayList<>();
		List<SignData> conversions = new ArrayList<>();
		List<Throwable> exceptions = new ArrayList<>();
		BlockUtils.getBlocksInRadius(location(), radius).forEach(block -> {
			if (!MaterialTag.SIGNS.isTagged(block.getType())) return;

			Sign sign = (Sign) block.getState();
			String line1 = stripColor(sign.getLine(0));
			if (!Arrays.asList("[Trade]", "[Ench Trade]", "[Arrow Trade]", "[Potion Trade]").contains(line1)) return;

			signs.add(block);

			try {
				conversions.add(readSign(sign));
			} catch (Exception ex) {
				ex.printStackTrace();
				exceptions.add(ex);
			}
		});

		if (signs.size() == 0)
			error("Could not find any trade signs to convert");

		if (conversions.size() > 0)
			send("Successfully converted &e" + conversions.size() + "&3/&e" + signs.size() + " &3signs within " + radius + " blocks");

		if (exceptions.size() > 0)
			send("&cCould not convert &e" + exceptions.size() + "&c/&e" + signs.size() + " &csigns, errors logged to console");
	}

	public SignData readSign(Sign sign) {
		SignData data = new SignData();
		String[] lines = sign.getLines();

		if (StringUtils.stripColor(lines[0]).equals("[Trade]")) {
			data.setPrice(Double.parseDouble(lines[1].replace("$", "").split(":")[0]));
			data.setMoneyInSign(Double.parseDouble(lines[1].split(":")[1]));
			data.setStock(Integer.parseInt(lines[2].split(" ")[1].split(":")[1]));
			data.setPlayer(PlayerUtils.getPlayer(StringUtils.stripColor(lines[3])));

			String idForSale = lines[2].split(" ")[1].split(":")[0];

			Material material;
			if (idForSale.contains(";"))
				material = convertMaterial(Integer.parseInt(idForSale.split(";")[0]), Byte.parseByte(idForSale.split(";")[1]));
			else if (Utils.isInt(idForSale))
				material = convertMaterial(Integer.parseInt(idForSale.split(";")[0]), (byte) 0);
			else
				material = essentialsAliases(idForSale);

			if (material == null)
				error("Could not convert material &e" + idForSale);
			ItemStack item = new ItemStack(material);
			item.setAmount(Integer.parseInt(lines[2].split(" ")[0]));
			data.setItem(item);
			return data;
		}

		if (StringUtils.stripColor(lines[0]).equals("[Ench Trade]")) {
			data.setPrice(Double.parseDouble(lines[1].replace("$", "").split(" \\| ")[0]));
			data.setItem(new ItemBuilder(Material.ENCHANTED_BOOK)
					.enchant(getEnchantFromShort(lines[2].split(" ")[0]), Integer.parseInt(lines[2].split(" ")[1])).build());
			data.setStock(Integer.parseInt(lines[1].split(" \\| ")[1]));
			data.setPlayer(PlayerUtils.getPlayer(StringUtils.stripColor(lines[3])));
			return data;
		}

		if (StringUtils.stripColor(lines[0]).equals("[Potion Trade]")) {
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
			item.setAmount(Integer.parseInt(lines[1].split(" \\| ")[1]));

			data.setItem(item);
			return data;
		}

		if (StringUtils.stripColor(lines[0]).equals("[Arrow Trade]")) {
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
			item.setAmount(Integer.parseInt(lines[1].split(" \\| ")[1]));

			data.setItem(item);
			return data;
		}
		error("You are not looking at a shop sign");
		return null;
	}

	@SuppressWarnings("deprecation")
	public static Material convertMaterial(int ID, byte Data) {
		for (Material i : EnumSet.allOf(Material.class)) {
			if (!i.isLegacy()) continue;
			if (i.getId() == ID)
				return Bukkit.getUnsafe().fromLegacy(new MaterialData(i, Data));
		}
		return null;
	}

	public Material essentialsAliases(String name) {
		if (name.equalsIgnoreCase("steak"))
			return Material.COOKED_BEEF;
		try {
			ItemStack item = Nexus.getEssentials().getItemDb().get(name);
			return item.getType();
		} catch (Exception e) {
			error("Could not parse item from essentials aliases");
			Nexus.warn("Could not convert the shop from player " + name());
		}
		return null;
	}

	public Enchantment getEnchantFromShort(String potionShort) {
		switch (potionShort) {
			case "Eff.":
				return Enchantment.DIG_SPEED;
			case "Silk Touch":
				return Enchantment.SILK_TOUCH;
			case "Unbreaking":
				return Enchantment.DURABILITY;
			case "Fortune":
				return Enchantment.LOOT_BONUS_BLOCKS;
			case "Luck":
				return Enchantment.LUCK;
			case "Lure":
				return Enchantment.LURE;
			case "Mending":
				return Enchantment.MENDING;
			case "C. of Vanish":
				return Enchantment.VANISHING_CURSE;
			case "Protection":
				return Enchantment.PROTECTION_ENVIRONMENTAL;
			case "Fire Prot.":
				return Enchantment.PROTECTION_FIRE;
			case "Feather Fall.":
				return Enchantment.PROTECTION_FALL;
			case "Blast Prot.":
				return Enchantment.PROTECTION_EXPLOSIONS;
			case "Proj. Prot.":
				return Enchantment.PROTECTION_PROJECTILE;
			case "Respiration":
				return Enchantment.OXYGEN;
			case "Aqua Affinity":
				return Enchantment.WATER_WORKER;
			case "Thorns":
				return Enchantment.THORNS;
			case "Depth Strider":
				return Enchantment.DEPTH_STRIDER;
			case "Frost Walker":
				return Enchantment.FROST_WALKER;
			case "C. of Binding":
				return Enchantment.BINDING_CURSE;
			case "Sharpness":
				return Enchantment.DAMAGE_ALL;
			case "Smite":
				return Enchantment.DAMAGE_UNDEAD;
			case "Bane":
				return Enchantment.DAMAGE_ARTHROPODS;
			case "Knockback":
				return Enchantment.KNOCKBACK;
			case "Fire Aspect":
				return Enchantment.FIRE_ASPECT;
			case "Looting":
				return Enchantment.LOOT_BONUS_MOBS;
			case "Sweeping":
				return Enchantment.SWEEPING_EDGE;
			case "Power":
				return Enchantment.ARROW_DAMAGE;
			case "Punch":
				return Enchantment.ARROW_KNOCKBACK;
			case "Flame":
				return Enchantment.ARROW_FIRE;
			case "Infinity":
				return Enchantment.ARROW_INFINITE;
			default:
				return null;
		}
	}

	public boolean isPotion(String name) {
		return !Arrays.asList("Water", "Mundane", "Thick", "Awkward").contains(name);
	}

	public boolean isMultiplied(String string) {
		String[] split = string.split(" ");
		return Utils.isInt(split[split.length - 1]);
	}


	public PotionType getPotionFromShort(String potionShort) {
		switch (potionShort) {
			case "Water":
				return PotionType.WATER;
			case "Mundane":
				return PotionType.MUNDANE;
			case "Awkward":
				return PotionType.AWKWARD;
			case "Thick":
				return PotionType.THICK;
			case "Night Vision":
				return PotionType.NIGHT_VISION;
			case "Invisibility":
				return PotionType.INVISIBILITY;
			case "Jump Boost":
				return PotionType.JUMP;
			case "Fire Res.":
				return PotionType.FIRE_RESISTANCE;
			case "Speed":
				return PotionType.SPEED;
			case "Slowness":
				return PotionType.SLOWNESS;
			case "Water Br.":
				return PotionType.WATER_BREATHING;
			case "Inst. Health":
				return PotionType.INSTANT_HEAL;
			case "Inst. Damage":
				return PotionType.INSTANT_DAMAGE;
			case "Poison":
				return PotionType.POISON;
			case "Regen.":
				return PotionType.REGEN;
			case "Strength":
				return PotionType.STRENGTH;
			case "Weakness":
				return PotionType.WEAKNESS;
			case "Luck":
				return PotionType.LUCK;
			default:
				return null;
		}
	}

}
