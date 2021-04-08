package me.pugabyte.nexus.features.mcmmo.menus;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.player.UserManager;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.chat.Koda;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.models.banker.BankerService;
import me.pugabyte.nexus.models.banker.Transaction.TransactionCause;
import me.pugabyte.nexus.models.mcmmo.McMMOPrestige;
import me.pugabyte.nexus.models.mcmmo.McMMOService;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class McMMOResetProvider extends MenuUtils implements InventoryProvider {
	McMMOService service = new McMMOService();

	@Getter
	@AllArgsConstructor
	public enum ResetSkillType {
		SWORDS(1, 3, Material.DIAMOND_SWORD,
				"A frozen sword that slows your enemies and may trap them in ice") {
				@Override
				void onClick(Player player) {
					PlayerUtils.runCommandAsConsole("ce give " + player.getName() + " " + Material.DIAMOND_SWORD.name() + " sharpness:2 looting:2 mending:1 iceaspect:3 knockback:1 unbreaking:2");
				}
		},
		MINING(1, 5, Material.DIAMOND_PICKAXE,
				"Any helmet of your choice that gives you night vision for deep mining expeditions") {
				@Override
				void onClick(Player player) {
					PlayerUtils.giveItem(player, new ItemBuilder(Material.PAPER).name("&eMcMMOReset Coupon").lore("&3Coupon for Glowing Enchant").build());
					PlayerUtils.send(player, "&ePut in a &c/ticket &eto have a staff member add glowing to a helmet you own.");
				}
		},
		EXCAVATION(2, 2, Material.DIAMOND_SHOVEL,
				"A shovel that hastens the user after every block broken") {
				@Override
				void onClick(Player player) {
					PlayerUtils.runCommandAsConsole("ce give " + player.getName() + " " + Material.DIAMOND_SHOVEL.name() + " energizing:2 efficiency:5 unbreaking:3 silk_touch:1 mending:1");
				}
		},
		AXES(2, 4, Material.DIAMOND_AXE,
				"The power of Thor is imbued in this axe, giving you a chance of smiting your target with lightning") {
				@Override
				void onClick(Player player) {
					PlayerUtils.runCommandAsConsole("ce give " + player.getName() + " " + Material.DIAMOND_AXE.name() + " thunderingblow:2 sharpness:3 mending:1 unbreaking:3");
				}
		},
		HERBALISM(2, 6, Material.DIAMOND_HOE,
				"The boots of Demeter give you the power to increase agricultural rates around you.") {
			@Override
			void onClick(Player player) {
				PlayerUtils.giveItem(player, new ItemBuilder(Material.GOLDEN_BOOTS).lore("&bBonemeal Boots").build());
			}
		},
		FISHING(3, 1, Material.FISHING_ROD,
				"A godly rod that increases your catch") {
				@Override
				void onClick(Player player) {
					PlayerUtils.runCommandAsConsole("ce give " + player.getName() + " " + Material.FISHING_ROD.name() + " lure:4 luck:5 unbreaking:3 mending:1");
				}
		},
		ACROBATICS(3, 3, Material.DIAMOND_BOOTS,
				"Jump higher and run faster with these boots") {
				@Override
				void onClick(Player player) {
					PlayerUtils.runCommandAsConsole("ce give " + player.getName() + " " + Material.DIAMOND_BOOTS.name() + " protection:2 gears:3 springs:2 feather_falling:3 mending:1 unbreaking:2");
				}
		},
		REPAIR(3, 5, Material.ANVIL,
				"Want an item that auto repairs itself? Mending without xp? Thats what you get here, " +
						"a chance to add that enchantment to one item of your choice!") {
				@Override
				void onClick(Player player) {
					PlayerUtils.giveItem(player, new ItemBuilder(Material.PAPER).name("&eMcMMOReset Coupon").lore("&3Coupon for AutoRepair").build());
					PlayerUtils.send(player, "&ePut in a &c/ticket &eto have a staff member add auto repair to one item you own.");
				}
		},
		ARCHERY(3, 7, Material.BOW,
				"Now you can be the wither- enemies take wither damage for a short period from every successful hit") {
				@Override
				void onClick(Player player) {
					PlayerUtils.runCommandAsConsole("ce give " + player.getName() + " " + Material.BOW.name() + " power:2 wither:2 infinity:1 mending:1 unbreaking:2 punch:1");
				}
		},
		TAMING(4, 0, Material.BONE,
				"One horse, your favorite color, max stats. Simple, No?") {
				@Override
				void onClick(Player player) {
					Nexus.getPerms().playerAdd(player, "horsepicker.pick");
					PlayerUtils.send(player, "&eUse &c/horsepicker &eto pick your horse. Make sure you are standing in an open area or the horse might die!");
				}
		},
		WOODCUTTING(4, 2, Material.OAK_LOG,
				"For every log or plank broken this shiny new axe will give you a short burst of haste") {
				@Override
				void onClick(Player player) {
					PlayerUtils.runCommandAsConsole("ce give " + player.getName() + " " + Material.DIAMOND_AXE.name() + " energizing:3 mending:1");
					PlayerUtils.runCommandAsConsole("ce give " + player.getName() + " " + Material.LEATHER_HELMET.name() + " implants:1");
				}
		},
		UNARMED(4, 6, Material.ROTTEN_FLESH,
				"Punching your enemies to death can be dangerous work, this bandage and stick will help with that.") {
			@Override
			void onClick(Player player) {
				PlayerUtils.runCommandAsConsole("ce give " + player.getName() + " " + Material.PAPER.name() + " bandage");
				PlayerUtils.runCommandAsConsole("ce give " + player.getName() + " " + Material.STICK.name() + " Quarterstaff disarming:1");
			}
		},
		ALCHEMY(4, 8, Material.SPLASH_POTION,
				"You want to throw potions mate? Why not launch them from this hopper? " +
						"Watch your potions soar like they were shot from a bow to hit friend and foe alike from afar!") {
				@Override
				void onClick(Player player) {
					PlayerUtils.runCommandAsConsole("ce give " + player.getName() + " " + Material.HOPPER.name() + " potionlauncher");
					Nexus.getPerms().playerAdd(player, "combine.use");
					PlayerUtils.send(player, "&eTo shoot potions, first run &c/combine &eto combine all similar potions in your inventory into one stack, " +
							"then place the stack in the slot to the right of the Potion Launcher. If you place the hopper, hold it and do &c/fixpotionlauncher");
				}
		};

		private final int row;
		private final int column;
		private final Material material;
		private final String rewardDescription;

		abstract void onClick(Player player);
	}

	@Override
	public void open(Player player) {
		SmartInventory.builder()
				.provider(new McMMOResetProvider())
				.size(6, 9)
				.title(StringUtils.colorize("McMMO Reset"))
				.build().open(player);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		McMMOPlayer mcmmoPlayer = UserManager.getPlayer(player);

		ItemStack all = new ItemBuilder(Material.BEACON)
				.name("&eAll Skills")
				.lore("&3Power Level: &e" + mcmmoPlayer.getPowerLevel() + "/1300" +
						"|| ||&3&lReward:||" +
						"&f- $150,000||&f- All normal rewards||" + "&f- When your health gets low, this breastplate will give you the " +
						"strength of an angry barbarian!").build();
		if (mcmmoPlayer.getPowerLevel() >= 1300) addGlowing(all);
		ItemStack reset = new ItemBuilder(Material.BARRIER).name("&cReset all with &lno reward").build();

		contents.set(0, 4, ClickableItem.from(all, (e) -> {
			if (mcmmoPlayer.getPowerLevel() < 1300)
				return;

			ConfirmationMenu.builder()
					.title("&4Confirm Prestige All?")
					.onConfirm((e2) -> {
						player.closeInventory();
						prestigeAll(player);
					})
					.open(player);
		}));

		contents.set(5, 4, ClickableItem.from(reset, (e) ->
				ConfirmationMenu.builder()
						.title("&4Confirm Reset All? (No Rewards)")
						.onConfirm((e2) -> {
							player.closeInventory();
							resetAll(mcmmoPlayer);
						})
						.open(player)));

		McMMOPrestige mcMMOPrestige = service.getPrestige(player.getUniqueId().toString());
		for (ResetSkillType skill : ResetSkillType.values()) {
			ItemStack item = new ItemBuilder(skill.getMaterial()).itemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ATTRIBUTES)
					.name("&e" + StringUtils.camelCase(skill.name()))
					.lore("&3Level: &e" + mcmmoPlayer.getSkillLevel(PrimarySkillType.valueOf(skill.name())) +
							"|| ||&3&lReward:" +
							"||&f$10,000" +
							"||&f" + skill.getRewardDescription() +
							"|| ||&3Number of Prestieges: &e" + mcMMOPrestige.getPrestige(skill.name()))
					.build();

			if (mcmmoPlayer.getSkillLevel(PrimarySkillType.valueOf(skill.name())) >= 100)
				addGlowing(item);

			contents.set(skill.getRow(), skill.getColumn(), ClickableItem.from(item, (e) -> {
				if (mcmmoPlayer.getSkillLevel(PrimarySkillType.valueOf(skill.name())) < 100)
					return;

				ConfirmationMenu.builder()
						.title("Confirm Prestige?")
						.onConfirm((e2) -> {
							player.closeInventory();
							prestige(player, skill, true);
						})
						.open(player);
			}));
		}
	}

	public void resetAll(McMMOPlayer player) {
		for (PrimarySkillType skillType : PrimarySkillType.values()) {
			player.modifySkill(skillType, 0);
		}
		PlayerUtils.send(player.getPlayer(), "&3You successfully reset all of your McMMO skills");
	}

	public void prestigeAll(Player player) {
		// TODO Koda Broadcasts
		Koda.say(player.getName() + " has reset all of their McMMO skills!");

		PlayerUtils.runCommandAsConsole("ce give " + player.getName() + " diamond_chestplate enlighted:1 beserk:1 durability:3 mending:1");
		new BankerService().deposit(player, 20000, ShopGroup.SURVIVAL, TransactionCause.MCMMO_RESET);

		for (PrimarySkillType skillType : PrimarySkillType.values()) {
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
		new BankerService().deposit(player, 10000, ShopGroup.SURVIVAL, TransactionCause.MCMMO_RESET);
		mcmmoPlayer.modifySkill(PrimarySkillType.valueOf(skill.name()), 0);

		McMMOPrestige mcMMOPrestige = service.getPrestige(player.getUniqueId().toString());
		mcMMOPrestige.prestige(skill.name());
		service.save(mcMMOPrestige);

		// TODO Koda Broadcast
		if (broadcast)
			Koda.say(Nickname.of(player) + " has reset their " + skill.name().toLowerCase() + " skill for the " +
					StringUtils.getNumberWithSuffix(mcMMOPrestige.getPrestige(skill.name())) + " time!");
	}
}
