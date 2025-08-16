package gg.projecteden.nexus.features.commands.staff.operator;

import org.mvplugins.multiverse.inventories.share.Sharables;
import org.mvplugins.multiverse.inventories.utils.configuration.json.JsonConfiguration;
import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Aliases("restoreinv")
@Permission(Group.SENIOR_STAFF)
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

	@Async
	@Path("<player> <pastecode>")
	@Description("Restore a player's inventory from a paste of a Multiverse backup")
	void code(Player owner, String code) {
		try {
			String data = StringUtils.getPaste(code);

			JsonConfiguration jsonConfig = new JsonConfiguration();
			jsonConfig.loadFromString(data);

			add(player(), new RestoreInventoryPlayer(player(), owner, jsonConfig, code));

			sendRestoreButtons("Survival");
			sendRestoreButtons("Creative");
		} catch (InvalidConfigurationException ex) {
			error("An error occurred while loading the json configuration: " + ex.getMessage());
		}
	}

	@HideFromWiki
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
					case "inventory" -> {
						if (!ItemUtils.isInventoryEmpty(owner.getInventory())) {
							sendInventoryRestoreNotEmptyMessage(owner, "inventory");
							break;
						}
						owner.getInventory().setContents(getInventory(gamemode));
						owner.getInventory().setArmorContents(getArmour(gamemode));
						owner.getInventory().setItemInOffHand(getOffHand(gamemode));
						sendInventoryRestoreSuccessMessage(owner, "inventory");
					}
					case "enderchest" -> {
						if (!ItemUtils.isInventoryEmpty(owner.getEnderChest())) {
							sendInventoryRestoreNotEmptyMessage(owner, "ender chest");
							break;
						}
						owner.getEnderChest().setContents(getEnderChest(gamemode));
						sendInventoryRestoreSuccessMessage(owner, "ender chest");
					}
					case "exp" -> {
						owner.setLevel(getExp(gamemode));
						sendExperienceRestoreSuccessMessage(owner);
					}
					default -> error("You can only restore inventory contents, ender chest contents, and experience");
				}
			} catch (InvalidInputException ex) {
				send(PREFIX + ex.getMessage());
			}
		});

		Discord.log(PREFIX + name() + " restored " + owner.getName() + "'s " + gameMode.name().toLowerCase()
				+ " " + type + " from <https://paste." + Nexus.DOMAIN + "/" + code + ".json>");
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
		send(owner, PREFIX + "&c" + name() + " is trying to restore your " + type + ", " +
				" your current " + type + " must be &eempty &cto avoid lost items!");
		throw new InvalidInputException("&cThe player's " + type + " contents must be empty to complete a restore. " +
				"They have been asked to empty their " + type + ".");
	}

	private void sendInventoryRestoreSuccessMessage(Player owner, String type) {
		send(owner, PREFIX + "&e" + name() + " &3has successfully restored your " + type + ". " +
				"Please confirm that all your items are present.");
		send(player(), PREFIX + "Successfully restored " + type + " of &e" + owner.getName());
	}

	private void sendExperienceRestoreSuccessMessage(Player owner) {
		send(owner, PREFIX + "Successfully added your lost experience to your current experience");
		send(player(), PREFIX + "Successfully added &e" + owner.getName() + "&3's lost experience to their current experience");
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

	private int getExp(ConfigurationSection gamemode) {
		return Integer.parseInt(gamemode.getConfigurationSection("stats").getString("el"));
	}

	private Map<String, Object> convertSection(ConfigurationSection section) {
		Map<String, Object> resultMap = new HashMap<>();
		for (String key : section.getKeys(false)) {
			Object obj = section.get(key);
			if (obj instanceof ConfigurationSection subSection) {
				resultMap.put(key, convertSection(subSection));
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
