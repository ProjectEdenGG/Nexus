package me.pugabyte.bncore.features.restoreinventory;

import com.onarandombox.multiverseinventories.share.Sharables;
import com.onarandombox.multiverseinventories.utils.configuration.json.JsonConfiguration;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.restoreinventory.models.RestoreInventoryPlayer;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.NoPermissionException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.PreConfiguredException;
import me.pugabyte.bncore.skript.SkriptFunctions;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RestoreInventoryCommand implements CommandExecutor, TabCompleter {
	private final static String PREFIX = Utils.getPrefix("RestoreInventory");
	private final static String USAGE = ChatColor.RED + "/restoreinv <player> <paste code>";

	public RestoreInventoryCommand() {
		BNCore.registerCommand("restoreinventory", this);
		BNCore.registerTabCompleter("restoreinventory", this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if (!(sender instanceof Player))
				throw new InvalidInputException("You must be in-game to use this command");
			Player player = (Player) sender;

			if (!player.hasPermission("restoreinventory.use"))
				throw new NoPermissionException();

			if (args[0].equalsIgnoreCase("do")) {
				RestoreInventoryPlayer restoreInventoryPlayer = RestoreInventory.get(player);
				if (restoreInventoryPlayer == null)
					throw new InvalidInputException(ChatColor.RED + "You must run " + USAGE + " first");

				Player restorer = restoreInventoryPlayer.getRestorer();
				Player owner = restoreInventoryPlayer.getOwner();
				JsonConfiguration jsonConfig = restoreInventoryPlayer.getJsonConfig();
				String code = restoreInventoryPlayer.getCode();

				if (!(args[1].equalsIgnoreCase("SURVIVAL") || args[1].equalsIgnoreCase("CREATIVE")))
					throw new InvalidInputException("You can only restore Survival and Creative inventories");

				ConfigurationSection gamemode = jsonConfig.getConfigurationSection(args[1].toUpperCase());

				owner.setGameMode(GameMode.valueOf(args[1].toUpperCase()));

				Utils.wait(3, () -> {
					try {
						switch (args[2].toLowerCase()) {
							case "inventory":
								if (!inventoryIsEmpty(owner.getInventory())) {
									sendInventoryRestoreNotEmptyMessage(restorer, owner, "inventory");
									break;
								}

								owner.getInventory().setContents(getInventory(gamemode));
								owner.getInventory().setArmorContents(getArmour(gamemode));
								owner.getInventory().setItemInOffHand(getOffHand(gamemode));
								sendInventoryRestoreSuccessMessage(restorer, owner, "inventory");
								break;
							case "enderchest":
								if (!inventoryIsEmpty(owner.getEnderChest())) {
									sendInventoryRestoreNotEmptyMessage(restorer, owner, "ender chest");
									break;
								}

								owner.getEnderChest().setContents(getEnderChest(gamemode));
								sendInventoryRestoreSuccessMessage(restorer, owner, "ender chest");
								break;
							case "exp":
								owner.setTotalExperience((int) (owner.getTotalExperience() + getExp(gamemode)));
								sendExperienceRestoreSuccessMessage(restorer, owner);
								break;
							default:
								throw new InvalidInputException("You can only restore inventory contents, ender chest contents, and experience");
						}
					} catch (InvalidInputException ex) {
						sender.sendMessage(PREFIX + ex.getMessage());
					}
				});

				SkriptFunctions.log(PREFIX + restorer.getName() + " restored " + owner.getName() + "'s " + args[1].toLowerCase()
						+ " " + args[2].toLowerCase() + " from <https://paste.bnn.gg/" + code + ".json>");
			} else {
				Optional<? extends Player> match = Bukkit.getOnlinePlayers().stream()
						.filter(_player -> _player.getName().startsWith(args[0]))
						.findFirst();

				if (!match.isPresent())
					throw new InvalidInputException("Player not found");

				Player owner = match.get();
				String code = args[1];

				Utils.async(() -> {
					try {
						String data = getPaste(code);

						JsonConfiguration jsonConfig = new JsonConfiguration();
						jsonConfig.loadFromString(data);

						RestoreInventory.add(player, new RestoreInventoryPlayer(player, owner, jsonConfig, code));

					} catch (InvalidConfigurationException ex) {
						sender.sendMessage(PREFIX + "An error occurred while loading the json configuration: " + ex.getMessage());
					} catch (InvalidInputException ex) {
						sender.sendMessage(PREFIX + ex.getMessage());
					}
				});

				sendRestoreButtons(player, "Survival");
				sendRestoreButtons(player, "Creative");
			}
		} catch (PreConfiguredException | InvalidInputException ex) {
			sender.sendMessage(PREFIX + ex.getMessage());
		} catch (ArrayIndexOutOfBoundsException ex) {
			sender.sendMessage(PREFIX + USAGE);
		}
		return true;
	}

	private void sendRestoreButtons(Player player, String gamemode) {
		SkriptFunctions.json(player, "&f");
		SkriptFunctions.json(player, "&e " + gamemode);
		SkriptFunctions.json(player, "  &e|&e|  ||&3Inventory||cmd:/restoreinv do " + gamemode.toLowerCase() + " inventory" +
				"||  &e|&e|  ||&3Ender Chest||cmd:/restoreinv do " + gamemode.toLowerCase() + " enderchest" +
				"||  &e|&e|  ||&3Experience||cmd:/restoreinv do " + gamemode.toLowerCase() + " exp" +
				"||  &e|&e|  ||");
	}

	private void sendInventoryRestoreNotEmptyMessage(Player restorer, Player owner, String type) throws InvalidInputException {
		owner.sendMessage(PREFIX + ChatColor.RED + restorer.getName() + " is trying to restore your " + type + ", " +
				" your current " + type + " must be " + ChatColor. YELLOW + "empty " + ChatColor.RED + "to avoid lost items!");
		throw new InvalidInputException(ChatColor.RED + "The player's " + type + " contents must be empty to complete a restore. " +
				"They have been asked to empty their " + type + ".");
	}

	private void sendInventoryRestoreSuccessMessage(Player restorer, Player owner, String type) {
		owner.sendMessage(PREFIX + ChatColor.YELLOW + restorer.getName() + ChatColor.DARK_AQUA + " has successfully restored your " + type + ". " +
				"Please confirm that all your items are present.");
		restorer.sendMessage(PREFIX + "Successfully restored " + type + " of " + ChatColor.YELLOW + owner.getName());
	}

	private void sendExperienceRestoreSuccessMessage(Player restorer, Player owner) {
		owner.sendMessage(PREFIX + "Successfully added your lost experience to your current experience");
		restorer.sendMessage(PREFIX + "Successfully added " + ChatColor.YELLOW + owner.getName() + ChatColor.DARK_AQUA + "'s lost experience to their current experience");
	}

	private boolean inventoryIsEmpty(Inventory inventory) {
		for (ItemStack itemStack : inventory.getContents())
			if (itemStack != null)
				return false;
		return true;
	}

	private ItemStack[] getEnderChest(ConfigurationSection gamemode) {
		ConfigurationSection enderChestContents = gamemode.getConfigurationSection("enderChestContents");
		return Sharables.ENDER_CHEST.getSerializer().deserialize(convertSection(enderChestContents));
	}

	private ItemStack[] getInventory(ConfigurationSection gamemode) {
		ConfigurationSection inventoryContents = gamemode.getConfigurationSection("inventoryContents");
		return Sharables.INVENTORY.getSerializer().deserialize(convertSection(inventoryContents));
	}

	private ItemStack[] getArmour(ConfigurationSection gamemode) {
		ConfigurationSection armorContents = gamemode.getConfigurationSection("armorContents");
		return Sharables.ARMOR.getSerializer().deserialize(convertSection(armorContents));
	}

	private ItemStack getOffHand(ConfigurationSection gamemode) {
		return Sharables.OFF_HAND.getSerializer().deserialize(gamemode.get("offHandItem"));
	}

	private double getExp(ConfigurationSection gamemode) {
		return Double.parseDouble(gamemode.getConfigurationSection("stats").getString("txp"));
	}

	public String getPaste(String code) throws InvalidInputException {
		try {
			URL paste = new URL("https://paste.bnn.gg/raw/" + code);

			HttpURLConnection connection = (HttpURLConnection) paste.openConnection();
			BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuilder response = new StringBuilder();
			while ((line = rd.readLine()) != null) {
				response.append(line.trim());
			}
			connection.disconnect();
			return response.toString();
		} catch (Exception ex) {
			throw new InvalidInputException("An error occurred while retrieving the paste data: " + ex.getMessage());
		}
	}

	private Map<String, Object> convertSection(ConfigurationSection section) {
		Map<String, Object> resultMap = new HashMap<>();
		for (String key : section.getKeys(false)) {
			Object obj = section.get(key);
			if (obj instanceof ConfigurationSection) {
				resultMap.put(key, convertSection((ConfigurationSection) obj));
			} else {
				resultMap.put(key, obj);
			}
		}
		return resultMap;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> completions = new ArrayList<>();
		if (args.length == 1) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				completions.add(player.getName());
			}
			completions.add("do");
		} else if (args.length == 2 && args[0].equalsIgnoreCase("do")) {
			completions.add("survival");
			completions.add("creative");
		} else if (args.length == 3 && args[0].equalsIgnoreCase("do")) {
			completions.add("inventory");
			completions.add("enderchest");
			completions.add("xp");
		}
		return completions;
	}

}
