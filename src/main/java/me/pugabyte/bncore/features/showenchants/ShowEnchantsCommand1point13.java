package me.pugabyte.bncore.features.showenchants;

/* 1.13
import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Herochat;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.herochat.HerochatAPI;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.skript.SkriptFunctions;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static me.pugabyte.bncore.features.showenchants.ShowEnchants.coolDownMap;

public class ShowEnchantsCommand implements CommandExecutor {
	private final static String PREFIX = BNCore.getPrefix("ShowEnchants");
	private final static String USAGE = "Correct usage: " + ChatColor.RED + "/showenchants <hand|offhand|hat|chest|pants|boots> [text]";

	private Player player;
	private String message;
	private ItemStack item;
	private String itemId;
	private String material;
	private String itemName;
	private Map<Enchantment, Integer> enchantsMap;
	private List<String> customEnchantsList;
	private List<String> loreList;

	ShowEnchantsCommand() {
		BNCore.registerCommand("showenchants", this);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			checkPlayer(sender);

			if (args.length == 0)
				throw new InvalidInputException(USAGE);

			message = "";
			if (args.length > 1)
				message = String.join(" ", Arrays.copyOfRange(args, 1, args.length)) + " ";

			item = getItem(args[0]);
			itemId = item.getType().name();
			itemName = item.getItemMeta().getDisplayName();
			material = ShowEnchants.getPrettyName(itemId);
			// If item does not have a custom name, use material name
			if (itemName == null || itemName.equals("")) {
				itemName = material;
			}
			int amount = item.getAmount();
			getEnchants();

			Chatter chatter = Herochat.getChatterManager().getChatter(player);
			Channel channel = chatter.getActiveChannel();

			coolDownMap.put(player, LocalDateTime.now());

			// Ingame
			{
				String enchants = getEnchantsIngame();

				String lore = "";
				if (customEnchantsList == null || customEnchantsList.size() == 0) {
					if (loreList != null) {
						lore = getLoreIngame();
					}
				} else {
					lore = getCustomEnchantsIngame();
				}

				int durability = ((Damageable) item.getItemMeta()).getDamage();
				String prefix = channel.getNick();
				ChatColor color = channel.getColor();

				String format = color + "[" + prefix + "] " + SkriptFunctions.getFullChatFormat(player);

				ComponentBuilder herochat = new ComponentBuilder(format + color + ChatColor.BOLD + " > ").append(ChatColor.WHITE + message.trim());
				ComponentBuilder json = new ComponentBuilder("{\"id\":\"minecraft:" + itemId.toLowerCase() + "\",\"Count\":1,\"tag\":{\"display\":{\"Lore\":[" + lore + "]},\"Damage\":" + durability + ",\"Enchantments\":[" + enchants + "]}}");
				ComponentBuilder hover = new ComponentBuilder(itemName).event(new HoverEvent(HoverEvent.Action.SHOW_ITEM, json.create()));
				ComponentBuilder enchantedItem = new ComponentBuilder(" ").append("[").bold(true).append(hover.create()).bold(true).append("]").bold(true);

				BaseComponent[] component = herochat.append(enchantedItem.create()).append((amount > 1 ? " x" + amount : "")).create();

				player.spigot().sendMessage(component); // Recipients doesn't include the sender
				for (Chatter loopChatter : HerochatAPI.getRecipients(chatter, channel)) {
					loopChatter.getPlayer().spigot().sendMessage(component);
				}

			}

			// Discord
			{
				String enchants = getEnchantsDiscord();

				String durability = String.valueOf(((int) item.getType().getMaxDurability()) - ((Damageable) item.getItemMeta()).getDamage());
				durability += "/" + item.getType().getMaxDurability();

				String discordName = ChatColor.stripColor(itemName) + " ";
				if (itemName.equalsIgnoreCase(item.getItemMeta().getDisplayName()))
					discordName += "(" + material + ")";
				if (amount > 1) discordName += " x" + amount;

				SkriptFunctions.showEnchantsOnBridge(player, message, discordName, enchants, durability, channel.getName());
			}

			return true;
		} catch (InvalidInputException ex) {
			sender.sendMessage(PREFIX + ex.getMessage());
			return false;
		}
	}

	private void checkPlayer(CommandSender sender) throws InvalidInputException {
		if (!(sender instanceof Player))
			throw new InvalidInputException("You must be in-game to use this command");
		player = (Player) sender;

		if (!player.hasPermission("showenchants.use"))
			throw new InvalidInputException("You do not have permission to use this command.");
		if (!player.hasPermission("showenchants.bypasscooldown"))
			checkCooldown();
	}

	private void getEnchants() {
		if (item.getType().equals(Material.ENCHANTED_BOOK)) {
			EnchantmentStorageMeta book = (EnchantmentStorageMeta) item.getItemMeta();
			this.enchantsMap = book.getStoredEnchants();
		} else {
			this.enchantsMap = item.getEnchantments();
		}
		getCustomEnchants();
	}

	private String getEnchantsIngame() {
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

	private String getEnchantsDiscord() {
		StringBuilder string = new StringBuilder();
		for (Map.Entry<Enchantment, Integer> entry : enchantsMap.entrySet()) {
			String enchant = entry.getKey().getKey().getKey();
			int level = entry.getValue();
			string.append(ShowEnchants.getEnchantNameAndLevel(enchant, level)).append(System.lineSeparator());
		}
		string.append(getCustomEnchantsDiscord());
		return ChatColor.stripColor(string.toString());
	}

	private void getCustomEnchants() {
		// Check lore
		customEnchantsList = new ArrayList<>();
		loreList = new ArrayList<>();
		if (item.getItemMeta().hasLore()) {
			List<String> lore = item.getItemMeta().getLore();
			for (String line : lore) {
				// if the lore is colored, its a custom enchantment, so add it to the list
				if (line.length() != ChatColor.stripColor(line).length()) {
					customEnchantsList.add(line);
				} else {
					// else, it's just lore
					loreList.add(line);
				}
			}
		}
	}

	private String getLoreIngame() {
		StringBuilder string = new StringBuilder();
		int i = 0;
		for (String lore : loreList) {
			string.append("\"").append(lore).append("\"");
			if (i < loreList.size() - 1) string.append(",");
			i++;
		}
		return string.toString();
	}

	private String getCustomEnchantsIngame() {
		StringBuilder string = new StringBuilder();
		int i = 0;
		for (String customEnchant : customEnchantsList) {
			string.append("\"").append(customEnchant).append("\"");
			if (i < customEnchantsList.size() - 1)
				string.append(",");
			i++;
		}
		return string.toString();
	}

	private String getCustomEnchantsDiscord() {
		StringBuilder string = new StringBuilder();
		for (String customEnchant : customEnchantsList) {
			string.append(customEnchant).append(System.lineSeparator());
		}
		return string.toString();
	}

	private void checkItem() throws InvalidInputException {
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

	private ItemStack getItem(String arg) throws InvalidInputException {
		item = new ItemStack(Material.AIR);
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
		checkItem();
		return item;
	}

	private boolean checkCooldown() throws InvalidInputException {
		// Check if player has a cool down (1 minute):
		LocalDateTime lastUse = coolDownMap.get(player);
		if (lastUse != null) {
			lastUse = lastUse.plus(1, ChronoUnit.MINUTES);
			LocalDateTime curTime = LocalDateTime.now();
			long diff = ChronoUnit.SECONDS.between(curTime, lastUse);
			if (diff > 0) {
				throw new InvalidInputException("You must wait " + diff + " seconds.");
			} else {
				coolDownMap.remove(player);
			}
		}
		return false;
	}

}
*/
