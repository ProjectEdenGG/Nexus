package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.chat.bridge.IngameBridgeListener;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown.Part;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.ChatterService;
import gg.projecteden.nexus.models.chat.PublicChannel;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.utils.TimeUtils.Time;
import lombok.Data;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.kyori.adventure.audience.MessageType;
import net.md_5.bungee.api.ChatColor;
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

import static gg.projecteden.nexus.features.discord.Discord.discordize;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@Description("Display an item in chat")
@Aliases("showenchants")
public class ShowItemCommand extends CustomCommand {

	@Data
	private class ItemData {
		private Map<Enchantment, Integer> enchantsMap = new HashMap<>();
		private List<String> customEnchantsList = new ArrayList<>();
		;
		private List<String> loreList = new ArrayList<>();
		;
	}

	public ShowItemCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<hand|offhand|helmet|chestplate|leggings|boots> [message...]")
	@Permission("showitems.use")
	@Cooldown(value = @Part(Time.MINUTE), bypass = "group.admin")
	void run(String type, String message) {
		Player player = player();
		ItemStack item = getItem(player, type);
		if (ItemUtils.isNullOrAir(item))
			error("You're not holding anything in that slot");

		if (message == null)
			message = "";

		String itemId = item.getType().name();
		if (item.getItemMeta() == null)
			error("Your item has no meta");

		String itemName = item.getItemMeta().getDisplayName();
		String material = StringUtils.camelCase(itemId);
		if (isNullOrEmpty(itemName))
			itemName = material;

		int amount = item.getAmount();

		Chatter chatter = new ChatterService().get(player);
		if (!(chatter.getActiveChannel() instanceof PublicChannel))
			error("You can't show enchants in private channels");
		PublicChannel channel = (PublicChannel) chatter.getActiveChannel();

		// Ingame
		ChatColor color = channel.getMessageColor();
		JsonBuilder json = json()
			.next(channel.getChatterFormat(chatter))
			.group()
			.next((isNullOrEmpty(message) ? "" : message + " "))
			.next(color + "&l[" + itemName + color + (amount > 1 ? " x" + amount : "") + "&l]")
			.hover(item);

		Broadcast.ingame().channel(channel).sender(chatter).message(json).messageType(MessageType.CHAT).send();

		/*
			Discord

		 	TODO show...
		 	- Potion effects
		 	- Arrow effects
		 	- Firework stuff
		 	- Book title
		 */
		if (channel.getDiscordTextChannel() != null) {
			ItemData data = new ItemData();
			setupEnchantsAndLore(item, data);
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

			DiscordUser user = new DiscordUserService().get(player);

			String discordMessage = discordize(message);
			discordMessage = IngameBridgeListener.parseMentions(discordMessage);

			MessageBuilder content = new MessageBuilder()
				.setContent(stripColor(user.getBridgeName() + " **>** " + discordMessage))
				.setEmbed(embed.build());

			Discord.send(content, channel.getDiscordTextChannel());
		}
	}

	private void setupEnchantsAndLore(ItemStack item, ItemData data) {
		if (item.getType().equals(Material.ENCHANTED_BOOK)) {
			EnchantmentStorageMeta book = (EnchantmentStorageMeta) item.getItemMeta();
			data.setEnchantsMap(book.getStoredEnchants());
		} else
			data.setEnchantsMap(item.getEnchantments());

		if (item.getItemMeta().hasLore()) {
			List<String> lore = item.getItemMeta().getLore();
			if (lore != null)
				data.getLoreList().addAll(lore);
		}
	}

	private String getEnchantsDiscord(ItemData data) {
		StringBuilder string = new StringBuilder();
		for (Map.Entry<Enchantment, Integer> entry : data.getEnchantsMap().entrySet()) {
			String enchant = entry.getKey().getKey().getKey();
			int level = entry.getValue();
			string.append(getEnchantNameAndLevel(enchant, level)).append(System.lineSeparator());
		}
		string.append(getLoreDiscord(data));
		return stripColor(string.toString());
	}

	private String getLoreDiscord(ItemData data) {
		StringBuilder string = new StringBuilder();
		List<String> enchants = data.getEnchantsMap().keySet().stream().map(enchantment -> enchantment.getKey().getKey()).toList();
		for (String line : data.getLoreList()) {
			String _line = stripColor(line)
				.replaceAll(" [IVXLC]+", "")
				.replaceAll(" ", "_")
				.toLowerCase();

			if (!enchants.contains(_line))
				string.append(line).append(System.lineSeparator());
		}

		return string.toString();
	}

	private ItemStack getItem(Player player, String arg) throws InvalidInputException {
		ItemStack item = new ItemStack(Material.AIR);
		PlayerInventory inv = player.getInventory();
		switch (arg.toLowerCase()) {
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

		if (item == null || item.getType().equals(Material.AIR))
			throw new InvalidInputException("You selected nothing!");

		return item;
	}

	static String getEnchantNameAndLevel(String enchantment, int lvl) {
		String level = StringUtils.toRoman(lvl);
		String enchant = StringUtils.camelCase(enchantment);
		return enchant + " " + level;
	}
}
