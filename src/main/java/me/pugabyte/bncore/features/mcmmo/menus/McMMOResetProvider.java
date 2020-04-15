package me.pugabyte.bncore.features.mcmmo.menus;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.player.UserManager;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.Koda;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.mcmmo.McMMOPrestige;
import me.pugabyte.bncore.models.mcmmo.McMMOService;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class McMMOResetProvider extends MenuUtils implements InventoryProvider {
	McMMOService service = new McMMOService();

	@Getter
	public enum ResetSkillType {
		SWORDS(1, 3, Material.DIAMOND_SWORD,
				"A frozen sword that slows your enemies and may trap them in ice") {
				@Override
				void onClick(Player player) {
					Utils.runConsoleCommand("ce give " + player.getName() + " diamond_sword damage_all:2 looting:2 mending:1 iceaspect:3 knockback:1 durability:2");
				}
		},
		MINING(1, 5, Material.DIAMOND_PICKAXE,
				"Any helmet of your choice that gives you night vision for deep mining expeditions") {
				@Override
				void onClick(Player player) {
					player.sendMessage(StringUtils.colorize("&ePut in a &c/ticket &eto have a staff member add glowing to a helmet you own."));
				}
		},
		EXCAVATION(2, 2, Material.DIAMOND_SHOVEL,
				"A shovel that hastens the user after every block broken") {
				@Override
				void onClick(Player player) {
					Utils.runConsoleCommand("ce give " + player.getName() + " diamond_spade efficiency:5 durability:3 silk_touch:1 mending:1");
				}
		},
		AXES(2, 4, Material.DIAMOND_AXE,
				"The power of Thor is imbued in this axe, giving you a chance of smiting your target with lightning") {
				@Override
				void onClick(Player player) {
					Utils.runConsoleCommand("ce give " + player.getName() + " diamond_axe thunderingblow:2 damage_all:3 mending:1 durability:3");
				}
		},
		HERBALISM(2, 6, Material.DIAMOND_HOE,
				"The boots of Demeter give you the power to increase agricultural rates around you.") {
			@Override
			void onClick(Player player) {
				Utils.giveItem(player, new ItemBuilder(Material.GOLDEN_BOOTS).lore("&bBonemeal Boots").build());
			}
		},
		FISHING(3, 1, Material.FISHING_ROD,
				"A godly rod that increases your catch") {
				@Override
				void onClick(Player player) {
					Utils.runConsoleCommand("ce give " + player.getName() + " fishing_rod lure:4 luck:5 durability:3 mending:1");
				}
		},
		ACROBATICS(3, 3, Material.DIAMOND_BOOTS,
				"Jump higher and run faster with these boots") {
				@Override
				void onClick(Player player) {
					Utils.runConsoleCommand("ce give " + player.getName() + " diamond_boots protection:2 gears:3 springs:2 feather_falling:3 mending:1 durability:2");
				}
		},
		REPAIR(3, 5, Material.ANVIL,
				"Want an item that auto repairs itself? Mending without xp? Thats what you get here, " +
						"a chance to add that enchantment to one item of your choice!") {
				@Override
				void onClick(Player player) {
					player.sendMessage(StringUtils.colorize("&ePut in a &c/ticket &eto have a staff member add auto repair to one item you own."));
				}
		},
		ARCHERY(3, 7, Material.BOW,
				"Now you can be the wither- enemies take wither damage for a short period from every successful hit") {
				@Override
				void onClick(Player player) {
					Utils.runConsoleCommand("ce give " + player.getName() + " bow power:2 wither:2 infinity:1 mending:1 durability:2 knockback:1");
				}
		},
		TAMING(4, 0, Material.BONE,
				"One horse, your favorite color, max stats. Simple, No?") {
				@Override
				void onClick(Player player) {
					BNCore.getPerms().playerAdd(player, "horsepicker.pick");
					player.sendMessage(StringUtils.colorize("&eUse &c/horsepicker &eto pick your horse. Make sure you are standing in an open area or the horse might die!"));
				}
		},
		WOODCUTTING(4, 2, Material.OAK_LOG,
				"For every log or plank broken this shiny new axe will give you a short burst of haste") {
				@Override
				void onClick(Player player) {
					Utils.runConsoleCommand("ce give " + player.getName() + " diamond_axe energizing:3 mending:1");
					Utils.runConsoleCommand("ce give " + player.getName() + " leather_helmet implants:1");
				}
		},
		UNARMED(4, 6, Material.ROTTEN_FLESH,
				"Punching your enemies to death can be dangerous work, this bandage and stick will help with that.") {
			@Override
			void onClick(Player player) {
				Utils.runConsoleCommand("ce give " + player.getName() + " paper bandage");
				Utils.runConsoleCommand("ce give " + player.getName() + " stick Quarterstaff disarming:1");
			}
		},
		ALCHEMY(4, 8, Material.SPLASH_POTION,
				"You want to throw potions mate? Why not launch them from this hopper? " +
						"Watch your potions soar like they were shot from a bow to hit friend and foe alike from afar!") {
				@Override
				void onClick(Player player) {
					Utils.runConsoleCommand("ce give " + player.getName() + " hopper potionlauncher");
					BNCore.getPerms().playerAdd(player, "combine.use");
					player.sendMessage(StringUtils.colorize("&eTo shoot potions, first run &c/combine &eto combine all similar potions in your inventory into one stack, " +
							"then place the stack in the slot to the right of the Potion Launcher. If you place the hopper, hold it and do &c/fixpotionlauncher"));
				}
		};

		int row, column;
		Material material;
		String rewardDescription;
		ItemStack itemStack;

		ResetSkillType(int row, int column, Material material, String rewardsDescription) {
			this.row = row;
			this.column = column;
			this.material = material;
			this.rewardDescription = rewardsDescription;
		}

		abstract void onClick(Player player);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		McMMOPlayer mcmmoPlayer = UserManager.getPlayer(player);

		ItemStack all = new ItemBuilder(Material.BEACON)
				.name("&cAll Skills")
				.lore("&6Power Level: " + mcmmoPlayer.getPowerLevel() + "/1300" +
						"||&f||&e&lReward:||" +
						"&f- $150,000||&f- All normal rewards||" + "&f- When your health gets low, this breastplate will give you the " +
						"strength of an angry barbarian!").build();
		if (mcmmoPlayer.getPowerLevel() >= 1300) addGlowing(all);
		ItemStack reset = new ItemBuilder(Material.BARRIER).name("&c&lReset all with &4no reward").build();

		contents.set(0, 4, ClickableItem.from(all, (e) -> {
			if (mcmmoPlayer.getPowerLevel() < 1300)
				return;

			MenuUtils.confirmMenu(player, ConfirmationMenu.builder().title("&4Confirm Prestige All?").onConfirm((e2) -> {
				player.closeInventory();
				prestigeAll(player);
			}).build());
		}));

		contents.set(5, 4, ClickableItem.from(reset, (e) -> {
			MenuUtils.confirmMenu(player, ConfirmationMenu.builder().title("&4Confirm Reset All? (No Rewards)").onConfirm((e2) -> {
				player.closeInventory();
				resetAll(mcmmoPlayer);
			}).build());
		}));

		for (ResetSkillType skill : ResetSkillType.values()) {
			ItemStack item = new ItemBuilder(skill.getMaterial())
					.name("&c" + StringUtils.camelCase(skill.name()))
					.lore("&6Level: " + mcmmoPlayer.getSkillLevel(SkillType.valueOf(skill.name())) +
							"|| ||&e&lReward:" +
							"||&f$10,000" +
							"||&f" + skill.getRewardDescription())
					.build();

			if (mcmmoPlayer.getSkillLevel(SkillType.valueOf(skill.name())) >= 100)
				addGlowing(item);

			contents.set(skill.getRow(), skill.getColumn(), ClickableItem.from(item, (e) -> {
				if (mcmmoPlayer.getSkillLevel(SkillType.valueOf(skill.name())) < 100)
					return;

				MenuUtils.confirmMenu(player, ConfirmationMenu.builder().title("Confirm Prestige?").onConfirm((e2) -> {
					player.closeInventory();
					prestige(player, skill, true);
				}).build());
			}));
		}
	}

	public void resetAll(McMMOPlayer player) {
		for (SkillType skillType : SkillType.values()) {
			player.modifySkill(skillType, 0);
		}
		player.getPlayer().sendMessage("&3You successfully reset all of your McMMO skills");
	}

	public void prestigeAll(Player player) {
		// TODO Koda Broadcasts
		Koda.say(player.getName() + " has reset all of their McMMO skills!");

		Utils.runConsoleCommand("ce give " + player.getName() + " diamond_chestplate enlighted:1 beserk:1 durability:3 mending:1");
		BNCore.getEcon().depositPlayer(player, 20000);

		for (SkillType skillType : SkillType.values()) {
			if (skillType.isChildSkill()) continue;
			prestige(player, ResetSkillType.valueOf(skillType.name()), false);
		}
		McMMOPrestige mcMMOPrestige = service.getPrestige(player.getUniqueId().toString());
		mcMMOPrestige.prestige("all");
		service.save(mcMMOPrestige);
	}

	public void prestige(Player player, ResetSkillType skill, boolean broadcast) {
		McMMOPlayer mcmmoPlayer = UserManager.getPlayer(player);

		skill.onClick(player);
		BNCore.getEcon().depositPlayer(player, 10000);
		mcmmoPlayer.modifySkill(SkillType.valueOf(skill.name()), 0);

		McMMOPrestige mcMMOPrestige = service.getPrestige(player.getUniqueId().toString());
		mcMMOPrestige.prestige(skill.name());
		service.save(mcMMOPrestige);

		// TODO Koda Broadcast
		if (broadcast)
			Koda.say(player.getName() + " has reset their " + skill.name().toLowerCase() + " skill for the " +
					StringUtils.getNumberSuffix(mcMMOPrestige.getPrestige(skill.name())) + " time!");
	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}
}
