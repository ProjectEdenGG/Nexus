package me.pugabyte.bncore.features.shops;

import com.earth2me.essentials.Essentials;
import lombok.Data;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class ConvertShopCommand extends CustomCommand {

    public ConvertShopCommand(CommandEvent event) {
        super(event);
    }

    @Data
    public class ShopConverter {
        int price;
        int moneyInSign = 0;
        int amountForSale = 0;
        int idForSale;
        int amountInSign = 0;
        OfflinePlayer player;
        ItemStack item;
    }

    @Path("read")
    void read() {
        Map<String, Object> map = readSign();
        send("Converted Shop Sign with values:");
        send("Price: " + map.get("price"));
        send("Money In Sign: " + map.getOrDefault("moneyInSign", "null"));
        send("Amount for Sale: " + map.getOrDefault("amountForSale", 1));
        send("ID For Sale: " + map.getOrDefault("idForSale", "null"));
        send("Amount in Sign: " + map.get("amountInSign"));
        send("Player: " + ((OfflinePlayer) map.get("player")).getName());
        send("ItemStack: " + map.get("item"));
        ShopConverter converter = new ShopConverter();
        converter.setPrice((int) map.get("price"));
        converter.setMoneyInSign((int) map.getOrDefault("moneyInSign", 0));
        converter.setIdForSale((int) map.getOrDefault("idForSale", 0));
        converter.setAmountInSign((int) map.get("amountInSign"));
        converter.setPlayer((OfflinePlayer) map.get("player"));
        converter.setItem((ItemStack) map.get("item"));
        Utils.giveItem(player(), (ItemStack) map.get("item"));
    }

    public Map<String, Object> readSign() {
        Block block = player().getTargetBlockExact(10);
        if (!MaterialTag.SIGNS.isTagged(block.getType()))
            error("You must be looking at a sign to execute this command");

        Sign sign = (Sign) block.getState();
        String[] lines = sign.getLines();

        if (ChatColor.stripColor(lines[0]).equals("[Trade]")) {
            int price = Integer.parseInt(lines[1].replace("$", "").split(":")[0]);
            int moneyInSign = Integer.parseInt(lines[1].split(":")[1]);
            int amountForSale = Integer.parseInt(lines[2].split(" ")[0]);
            String idForSale = lines[2].split(" ")[1].split(":")[0];
            int amountInSign = Integer.parseInt(lines[2].split(" ")[1].split(":")[1]);
            OfflinePlayer player = Utils.getPlayer(ChatColor.stripColor(lines[3]));

            Material material;
            if (idForSale.contains(";"))
                material = convertMaterial(Integer.parseInt(idForSale.split(";")[0]), Byte.parseByte(idForSale.split(";")[1]));
            else if (Utils.isInt(idForSale))
                material = convertMaterial(Integer.parseInt(idForSale.split(";")[0]), (byte) 0);
            else
                material = essentialsAliases(idForSale);
            ItemStack item = new ItemStack(material);

            return new HashMap<String, Object>() {{
                put("price", price);
                put("moneyInSign", moneyInSign);
                put("amountForSale", amountForSale);
                put("idForSale", idForSale);
                put("amountInSign", amountInSign);
                put("player", player);
                put("item", item);
            }};
        }

        if (ChatColor.stripColor(lines[0]).equals("[Ench Trade]")) {
            int price = Integer.parseInt(lines[1].replace("$", "").split(" \\| ")[0]);
            for (String string : lines[1].split(" \\| "))
                BNCore.log(string);
            int amountForSale = Integer.parseInt(lines[1].split(" \\| ")[1]);
            Enchantment enchantment = getEnchantFromShort(lines[2].split(" ")[0]);
            ItemStack item = new ItemBuilder(Material.ENCHANTED_BOOK).enchant(enchantment, Integer.parseInt(lines[2].split(" ")[1])).build();

            OfflinePlayer player = Utils.getPlayer(ChatColor.stripColor(lines[3]));
            return new HashMap<String, Object>() {{
                put("price", price);
                put("amountInSign", amountForSale);
                put("player", player);
                put("item", item);
            }};
        }
        if (Arrays.asList("[Arrow Trade]", "[Potion Trade]").contains(ChatColor.stripColor(lines[0]))) {

            int price = Integer.parseInt(lines[1].replace("$", "").split(" \\| ")[0]);
            int amountForSale = Integer.parseInt(lines[1].split(" \\| ")[1]);
            OfflinePlayer player = Utils.getPlayer(ChatColor.stripColor(lines[3]));

            Map<String, Object> map = new HashMap<String, Object>() {{
                put("price", price);
                put("amountInSign", amountForSale);
                put("player", player);
            }};

            boolean ext = (StringUtils.right(lines[2], 3).equals("Ext"));
            boolean isMultiplied = isMultiplied(lines[2]);


            ItemStack item = null;

            if (ChatColor.stripColor(lines[0]).equals("[Arrow Trade]"))
                item = new ItemStack(Material.TIPPED_ARROW);

            if (ChatColor.stripColor(lines[0]).equals("[Potion Trade]")) {
                item = new ItemStack(Material.POTION);
                if (StringUtils.left(lines[2], 2).equals("S "))
                    item.setType(Material.SPLASH_POTION);
                if (StringUtils.left(lines[2], 2).equals("L "))
                    item.setType(Material.LINGERING_POTION);
            }

            String potionName = lines[2].replace("P ", "").replace("L ", "")
                    .replace("S ", "").replace(" Ext", "").replace(" 2", "");

            PotionMeta arrowMeta = (PotionMeta) item.getItemMeta();
            arrowMeta.setBasePotionData(new PotionData(
                    getPotionFromShort(potionName),
                    ext, isMultiplied));
            item.setItemMeta(arrowMeta);

            map.put("item", item);
            return map;
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
        Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        if (name.equalsIgnoreCase("steak"))
            return Material.COOKED_BEEF;
        try {
            ItemStack item = essentials.getItemDb().get(name);
            return item.getType();
        } catch (Exception e) {
            error("Could not parse item from essentials aliases");
            BNCore.warn("Could not convert the shop from player " + player().getName());
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
