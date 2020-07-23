package me.pugabyte.bncore.features.commands;

import lombok.Data;
import lombok.NonNull;
import me.pugabyte.bncore.features.chat.bridge.IngameBridgeListener;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.chat.ChatService;
import me.pugabyte.bncore.models.chat.Chatter;
import me.pugabyte.bncore.models.chat.PublicChannel;
import me.pugabyte.bncore.models.discord.DiscordService;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.Time;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.pugabyte.bncore.features.discord.Discord.discordize;
import static me.pugabyte.bncore.utils.StringUtils.stripColor;

public class ShowEnchantsCommand extends CustomCommand {

	@Data
	private class ItemData {
		private Map<Enchantment, Integer> enchantsMap = new HashMap<>();
		private List<String> customEnchantsList = new ArrayList<>();;
		private List<String> loreList = new ArrayList<>();;
	}

	public ShowEnchantsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("(hand|offhand|helmet|chestplate|leggings|boots) [message...]")
	@Permission("showenchants.use")
	@Cooldown(value = @Part(Time.MINUTE), bypass = "showenchants.bypasscooldown")
	void run(String message) {
		Player player = player();
		ItemStack item = getItem(player, arg(1));
		ItemData data = new ItemData();
		if (message == null) message = "";

		String itemId = item.getType().name();
		if (item.getItemMeta() == null)
			error("Your item has no meta");

		String itemName = item.getItemMeta().getDisplayName();
		String material = getPrettyName(itemId);
		if (isNullOrEmpty(itemName))
			itemName = material;

		int amount = item.getAmount();
		getEnchants(item, data);

		Chatter chatter = new ChatService().get(player);
		if (!(chatter.getActiveChannel() instanceof PublicChannel))
			error("You can't show enchants in private channels");
		PublicChannel channel = (PublicChannel) chatter.getActiveChannel();

		// Ingame
		{
			String enchants = getEnchantsIngame(item);

			String lore = "";
			if (data.getCustomEnchantsList() == null || data.getCustomEnchantsList().size() == 0) {
				if (data.getLoreList() != null)
					lore = getLoreIngame(data);
			} else
				lore = getCustomEnchantsIngame(data);

			int durability = ((Damageable) item.getItemMeta()).getDamage();

			ComponentBuilder herochat = new ComponentBuilder(channel.getChatterFormat(chatter));
			ComponentBuilder json = new ComponentBuilder("{\"id\":\"minecraft:" + itemId.toLowerCase() + "\",\"Count\":1,\"tag\":{\"display\":{\"Lore\":[" + lore + "]},\"Damage\":" + durability + ",\"Enchantments\":[" + enchants + "]}}");
			ComponentBuilder hover = new ComponentBuilder(itemName).event(new HoverEvent(HoverEvent.Action.SHOW_ITEM, json.create()));
			ComponentBuilder enchantedItem = new ComponentBuilder((isNullOrEmpty(message) ? "" : message + " ")).append(channel.getMessageColor() + "[").bold(true).append(hover.create()).bold(true).append(channel.getMessageColor() + "]").bold(true);

			BaseComponent[] component = herochat.append(enchantedItem.create()).append((amount > 1 ? " x" + amount : "")).create();

			channel.broadcastIngame(chatter, new JsonBuilder(component));
		}

		// Discord
		if (channel.getDiscordChannel() != null) {
			String enchants = getEnchantsDiscord(data);

			String durability = String.valueOf(((int) item.getType().getMaxDurability()) - ((Damageable) item.getItemMeta()).getDamage());
			durability += "/" + item.getType().getMaxDurability();

			String discordName = stripColor(itemName) + " ";
			if (itemName.equalsIgnoreCase(item.getItemMeta().getDisplayName()))
				discordName += "(" + material + ")";
			if (amount > 1) discordName += " x" + amount;

			EmbedBuilder embed = new EmbedBuilder()
					.setTitle(discordName)
					.appendDescription(enchants);

			if (!durability.equals("0/0"))
				embed.setFooter(durability);

			DiscordUser user = new DiscordService().get(player);

			String discordMessage = discordize(message);
			discordMessage = IngameBridgeListener.parseMentions(discordMessage);

			Discord.send(new MessageBuilder()
					.setContent(stripColor(user.getBridgeName() + " **>** " + discordMessage))
					.setEmbed(embed.build()));
		}
	}

	private void getEnchants(ItemStack item, ItemData data) {
		if (item.getType().equals(Material.ENCHANTED_BOOK)) {
			EnchantmentStorageMeta book = (EnchantmentStorageMeta) item.getItemMeta();
			data.setEnchantsMap(book.getStoredEnchants());
		} else {
			data.setEnchantsMap(item.getEnchantments());
		}
		getCustomEnchants(item, data);
	}

	private String getEnchantsIngame(ItemStack item) {
		StringBuilder string = new StringBuilder();
		int i = 0;
		for (Map.Entry<Enchantment, Integer> entry : item.getItemMeta().getEnchants().entrySet()) {
			String enchant = entry.getKey().getKey().getKey();
			String level = entry.getValue().toString();

			i++;
			string.append("{\"id\":\"minecraft:-\",\"lvl\":#}".replaceAll("-", enchant).replaceAll("#", level));
			if (i < item.getItemMeta().getEnchants().size())
				string.append(",");
		}
		return string.toString();
	}

	private String getEnchantsDiscord(ItemData data) {
		StringBuilder string = new StringBuilder();
		for (Map.Entry<Enchantment, Integer> entry : data.getEnchantsMap().entrySet()) {
			String enchant = entry.getKey().getKey().getKey();
			int level = entry.getValue();
			string.append(getEnchantNameAndLevel(enchant, level)).append(System.lineSeparator());
		}
		string.append(getCustomEnchantsDiscord(data));
		return stripColor(string.toString());
	}

	private void getCustomEnchants(ItemStack item, ItemData data) {
		// Check lore
		if (item.getItemMeta().hasLore()) {
			List<String> lore = item.getItemMeta().getLore();
			if (lore == null) return;

			for (String line : lore) {
				// TODO: Figure out a better way to determine custom enchantments, lore can be colored too.
				// if the lore is colored, its a custom enchantment, so add it to the list
				if (line.length() != stripColor(line).length()) {
					data.getCustomEnchantsList().add(line);
				} else {
					// else, it's just lore
					data.getLoreList().add(line);
				}
			}
		}
	}

	private String getLoreIngame(ItemData data) {
		StringBuilder string = new StringBuilder();
		int i = 0;
		for (String lore : data.getLoreList()) {
			string.append("\"").append(lore).append("\"");
			if (i < data.getLoreList().size() - 1)
				string.append(",");
			i++;
		}
		return string.toString();
	}

	private String getCustomEnchantsIngame(ItemData data) {
		StringBuilder string = new StringBuilder();
		int i = 0;
		for (String customEnchant : data.getCustomEnchantsList()) {
			string.append("'{\"text\":\"").append(customEnchant).append("\"}'");
			if (i < data.getCustomEnchantsList().size() - 1)
				string.append(",");
			i++;
		}
		return string.toString();
	}

	private String getCustomEnchantsDiscord(ItemData data) {
		StringBuilder string = new StringBuilder();
		for (String customEnchant : data.getCustomEnchantsList()) {
			string.append(customEnchant).append(System.lineSeparator());
		}
		return string.toString();
	}

	private void checkItem(ItemStack item) throws InvalidInputException {
		if (item == null || item.getType().equals(Material.AIR))
			throw new InvalidInputException("You selected nothing!");
		if (!item.getType().equals(Material.ENCHANTED_BOOK)) {
			if (!item.getItemMeta().hasEnchants()) {
				throw new InvalidInputException("That item doesn't have any enchants!");
			}
		} else {
			EnchantmentStorageMeta book = (EnchantmentStorageMeta) item.getItemMeta();
			if (!book.hasStoredEnchants()) {
				throw new InvalidInputException("That book doesn't have any enchants!");
			}
		}
	}

	private ItemStack getItem(Player player, String arg) throws InvalidInputException {
		ItemStack item = new ItemStack(Material.AIR);
		PlayerInventory inv = player.getInventory();
		switch (arg) {
			case "offhand":
				if (!inv.getItemInOffHand().getType().equals(Material.AIR))
					item = inv.getItemInOffHand();
				break;
			case "mainhand":
			case "hand":
				if (!inv.getItemInMainHand().getType().equals(Material.AIR))
					item = inv.getItemInMainHand();
				break;
			case "hat":
			case "head":
			case "helm":
			case "helmet":
				if (inv.getHelmet() != null)
					item = inv.getHelmet();
				break;
			case "chest":
			case "chestplate":
				if (inv.getChestplate() != null)
					item = inv.getChestplate();
				break;
			case "pants":
			case "legs":
			case "leggings":
				if (inv.getLeggings() != null)
					item = inv.getLeggings();
				break;
			case "boots":
			case "feet":
			case "shoes":
				if (inv.getBoots() != null)
					item = inv.getBoots();
				break;
		}
		checkItem(item);
		return item;
	}

	public static String getPrettyName(String item) {
		String out = "";
		if (item.contains("_")) {
			String[] parts = item.split("_");
			for (String part : parts) {
				String temp = part.substring(0, 1).toUpperCase();
				String temp2 = part.substring(1).toLowerCase();
				out += temp + temp2 + " ";
			}
		} else {
			out = item.substring(0, 1).toUpperCase() + item.substring(1).toLowerCase();
		}
		return out.trim();
	}

	static String getEnchantNameAndLevel(String enchantment, int lvl) {
		String level = intToRoman(lvl);
		String enchant = getPrettyName(enchantment);
		return enchant + " " + level;
	}

	static String intToRoman(int num) {
		String[] str = new String[]{"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
		int[] val = new int[]{1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < val.length; i++) {
			while (num >= val[i]) {
				num -= val[i];
				sb.append(str[i]);
			}
		}
		return sb.toString();
	}

}
