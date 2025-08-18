package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.chat.bridge.IngameBridgeListener;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.ChatterService;
import gg.projecteden.nexus.models.chat.PublicChannel;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.kyori.adventure.audience.MessageType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aliases("showenchants")
public class ShowItemCommand extends CustomCommand {

	@Data
	@RequiredArgsConstructor
	private class ItemData {
		private @NonNull ItemStack item;
		private Map<Enchantment, Integer> enchantsMap = new HashMap<>();
		private List<String> loreList = new ArrayList<>();
	}

	public ShowItemCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<hand|offhand|helmet|chestplate|leggings|boots> [message...]")
	@Cooldown(value = TickTime.SECOND, x = 15, bypass = Group.ADMIN)
	@Description("Display an item in chat")
	void run(String slot, String message) {
		ItemStack item = getItem(player(), slot);
		if (Nullables.isNullOrAir(item))
			error("You're not holding anything in that slot");

		if (message == null)
			message = "";

		String itemId = item.getType().name();
		if (item.getItemMeta() == null)
			error("Your item has no meta");

		String itemName = item.getItemMeta().getDisplayName();
		String material = StringUtils.camelCase(itemId);
		if (Nullables.isNullOrEmpty(itemName))
			itemName = material;

		int amount = item.getAmount();

		Chatter chatter = new ChatterService().get(player());
		if (!(chatter.getActiveChannel() instanceof PublicChannel))
			error("You can't show enchants in private channels");
		PublicChannel channel = (PublicChannel) chatter.getActiveChannel();

		// Ingame
		ChatColor color = channel.getMessageColor();

		String finalMessage = message;
		String finalItemName = itemName;

		Broadcast.ingame()
			.channel(channel)
			.sender(chatter)
			.message(viewer -> json()
				.next(channel.getChatterFormat(chatter, viewer == null ? null : new ChatterService().get(viewer), false))
				.group()
				.next((Nullables.isNullOrEmpty(finalMessage) ? "" : finalMessage + " "))
				.next(color + "&l[" + finalItemName + color + (amount > 1 ? " x" + amount : "") + "&l]")
				.hover(item))
			.messageType(MessageType.CHAT)
			.send();

		/*
			Discord

			TODO show...
			- Potion effects
			- Arrow effects
			- Firework stuff
			- Book title
		*/
		if (channel.getDiscordTextChannel() != null) {
			ItemData data = new ItemData(item);
			setupEnchantsAndLore(item, data);
			String enchants = getEnchantsDiscord(data);

			String durability = String.valueOf(((int) item.getType().getMaxDurability()) - ((Damageable) item.getItemMeta()).getDamage());
			durability += "/" + item.getType().getMaxDurability();

			String discordName = StringUtils.stripColor(itemName) + " ";
			if (itemName.equalsIgnoreCase(item.getItemMeta().getDisplayName()))
				discordName += "(" + material + ")";
			if (amount > 1) discordName += " x" + amount;

			EmbedBuilder embed = new EmbedBuilder()
					.setTitle(discordName)
					.appendDescription(enchants);

			if (!"0/0".equals(durability))
				embed.setFooter(durability);

			DiscordUser user = new DiscordUserService().get(player());

			message = IngameBridgeListener.parseMentions(message);

			MessageCreateBuilder content = new MessageCreateBuilder()
				.addContent(StringUtils.stripColor(user.getBridgeName() + " **>** " + Discord.discordize(message)))
				.setEmbeds(embed.build());

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
		if (!data.getItem().hasItemFlag(ItemFlag.HIDE_ENCHANTS))
			for (Map.Entry<Enchantment, Integer> entry : data.getEnchantsMap().entrySet()) {
				String enchant = StringUtils.camelCase(entry.getKey().getKey().getKey());
				String level = StringUtils.toRoman(entry.getValue());
				String enchantLevel = enchant + " " + level;

				string.append(enchantLevel).append(System.lineSeparator());
			}
		string.append(getLoreDiscord(data));

		return StringUtils.stripColor(string.toString());
	}

	private String getLoreDiscord(ItemData data) {
		StringBuilder string = new StringBuilder();
		List<String> enchants = data.getEnchantsMap().keySet().stream().map(enchantment -> enchantment.getKey().getKey()).toList();
		for (String line : data.getLoreList()) {
			String _line = StringUtils.stripColor(line)
				.replaceAll(" [IVXLC]+", "")
				.replaceAll(" ", "_")
				.toLowerCase();

			if (!enchants.contains(_line))
				string.append(line).append(System.lineSeparator());
		}

		return string.toString();
	}

	private ItemStack getItem(Player player, String slot) {
		ItemStack item = null;
		PlayerInventory inv = player.getInventory();
		switch (slot.toLowerCase()) {
			case "offhand" -> item = inv.getItemInOffHand();
			case "mainhand", "hand" -> item = inv.getItemInMainHand();
			case "hat", "head", "helm", "helmet" -> item = inv.getHelmet();
			case "chest", "chestplate" -> item = inv.getChestplate();
			case "pants", "legs", "leggings" -> item = inv.getLeggings();
			case "boots", "feet", "shoes" -> item = inv.getBoots();
			default -> error("Unknown slot &e" + slot);
		}

		if (Nullables.isNullOrAir(item))
			error("Item in " + slot + " not found");

		ItemMeta meta = item.getItemMeta();
		if (!meta.hasEnchants() && !meta.hasLore() && Nullables.isNullOrEmpty(meta.getDisplayName()))
			error("Item must have enchants, lore, or a custom name");

		return item;
	}
}
