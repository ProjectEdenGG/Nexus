package me.pugabyte.bncore.features.commands.staff;

import com.onarandombox.multiverseinventories.share.Sharables;
import com.onarandombox.multiverseinventories.utils.configuration.json.JsonConfiguration;
import lombok.NonNull;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.HideFromHelp;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleteIgnore;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Aliases("restoreinv")
@Permission("group.moderator")
public class RestoreInventoryCommand extends CustomCommand {
	public static HashMap<Player, RestoreInventoryPlayer> restorers = new HashMap<>();

	public RestoreInventoryCommand(@NonNull CommandEvent event) {
		super(event);
	}

	public void add(Player restorer, RestoreInventoryPlayer restoreInventoryPlayer) {
		restorers.put(restorer, restoreInventoryPlayer);
	}

	public RestoreInventoryPlayer get(Player restorer) {
		return restorers.get(restorer);
	}

	@Path("<player> <pastecode>")
	void code(Player owner, String code) {
		Tasks.async(() -> {
			try {
				String data = getPaste(code);

				JsonConfiguration jsonConfig = new JsonConfiguration();
				jsonConfig.loadFromString(data);

				add(player(), new RestoreInventoryPlayer(player(), owner, jsonConfig, code));

				sendRestoreButtons("Survival");
				sendRestoreButtons("Creative");
			} catch (InvalidConfigurationException ex) {
				error("An error occurred while loading the json configuration: " + ex.getMessage());
			}
		});
	}

	@HideFromHelp
	@TabCompleteIgnore
	@Path("do <gamemode> <type>")
	void restore(GameMode gameMode, String type) {
		RestoreInventoryPlayer restoreInventoryPlayer = get(player());
		if (restoreInventoryPlayer == null)
			error("You must run /restoreinv <player> <pastecode> first");

		Player owner = restoreInventoryPlayer.getOwner();
		JsonConfiguration jsonConfig = restoreInventoryPlayer.getJsonConfig();
		String code = restoreInventoryPlayer.getCode();

		if (!Arrays.asList(GameMode.SURVIVAL, GameMode.CREATIVE).contains(gameMode))
			error("You can only restore Survival and Creative inventories");

		ConfigurationSection gamemode = jsonConfig.getConfigurationSection(gameMode.name());
		owner.setGameMode(gameMode);

		Tasks.wait(3, () -> {
			try {
				switch (type.toLowerCase()) {
					case "inventory":
						if (!inventoryIsEmpty(owner.getInventory())) {
							sendInventoryRestoreNotEmptyMessage(owner, "inventory");
							break;
						}

						owner.getInventory().setContents(getInventory(gamemode));
						owner.getInventory().setArmorContents(getArmour(gamemode));
						owner.getInventory().setItemInOffHand(getOffHand(gamemode));
						sendInventoryRestoreSuccessMessage(owner, "inventory");
						break;
					case "enderchest":
						if (!inventoryIsEmpty(owner.getEnderChest())) {
							sendInventoryRestoreNotEmptyMessage(owner, "ender chest");
							break;
						}

						owner.getEnderChest().setContents(getEnderChest(gamemode));
						sendInventoryRestoreSuccessMessage(owner, "ender chest");
						break;
					case "exp":
						owner.setTotalExperience((int) (owner.getTotalExperience() + getExp(gamemode)));
						sendExperienceRestoreSuccessMessage(owner);
						break;
					default:
						error("You can only restore inventory contents, ender chest contents, and experience");
				}
			} catch (InvalidInputException ex) {
				send(PREFIX + ex.getMessage());
			}
		});

		Discord.log(PREFIX + player().getName() + " restored " + owner.getName() + "'s " + gameMode.name().toLowerCase()
				+ " " + type + " from <https://paste.bnn.gg/" + code + ".json>");
	}

	private void sendRestoreButtons(String gamemode) {
		new JsonBuilder()
				.newline()
				.next("&e " + gamemode)
				.newline()
				.next("  &e|&e|  ").group()
				.next("&3Inventory").command("/restoreinv do " + gamemode.toLowerCase() + " inventory").group()
				.next("  &e|&e|  ").group()
				.next("&3Ender Chest").command("/restoreinv do " + gamemode.toLowerCase() + " enderchest").group()
				.next("  &e|&e|  ").group()
				.next("&3Experience").command("/restoreinv do " + gamemode.toLowerCase() + " exp").group()
				.next("  &e|&e|")
				.send(player());
	}

	private void sendInventoryRestoreNotEmptyMessage(Player owner, String type) throws InvalidInputException {
		owner.sendMessage(PREFIX + ChatColor.RED + player().getName() + " is trying to restore your " + type + ", " +
				" your current " + type + " must be " + ChatColor. YELLOW + "empty " + ChatColor.RED + "to avoid lost items!");
		throw new InvalidInputException(ChatColor.RED + "The player's " + type + " contents must be empty to complete a restore. " +
				"They have been asked to empty their " + type + ".");
	}

	private void sendInventoryRestoreSuccessMessage(Player owner, String type) {
		owner.sendMessage(PREFIX + ChatColor.YELLOW + player().getName() + ChatColor.DARK_AQUA + " has successfully restored your " + type + ". " +
				"Please confirm that all your items are present.");
		player().sendMessage(PREFIX + "Successfully restored " + type + " of " + ChatColor.YELLOW + owner.getName());
	}

	private void sendExperienceRestoreSuccessMessage(Player owner) {
		owner.sendMessage(PREFIX + "Successfully added your lost experience to your current experience");
		player().sendMessage(PREFIX + "Successfully added " + ChatColor.YELLOW + owner.getName() + ChatColor.DARK_AQUA + "'s lost experience to their current experience");
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

	public class RestoreInventoryPlayer {
		private final Player restorer;
		private final Player owner;
		private final JsonConfiguration jsonConfig;
		private final String code;

		public RestoreInventoryPlayer(Player restorer, Player owner, JsonConfiguration jsonConfig, String code) {
			this.restorer = restorer;
			this.owner = owner;
			this.jsonConfig = jsonConfig;
			this.code = code;
		}

		public Player getRestorer() {
			return restorer;
		}

		public Player getOwner() {
			return owner;
		}

		public JsonConfiguration getJsonConfig() {
			return jsonConfig;
		}

		public String getCode() {
			return code;
		}
	}

}