package gg.projecteden.nexus.features.mcmmo.reset;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.player.UserManager;
import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.features.mcmmo.McMMO;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.survival.gem.GemCommand;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser;
import gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUserService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.LuckPermsUtils.PermissionChange;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

@Title("McMMO Reset")
public class McMMOResetProvider extends InventoryProvider {
	private static final McMMOPrestigeUserService service = new McMMOPrestigeUserService();
	private static final int DEPOSIT = 10000; // eco reward for prestige
	private static final String DEPOSIT_PRETTY = StringUtils.prettyMoney(DEPOSIT);
	private static final int DEPOSIT_ALL = 20000; // bonus eco reward for prestiging all
	private static final String DEPOSIT_ALL_PRETTY = StringUtils.prettyMoney(DEPOSIT_ALL);
	private static final float MAX_DEPOSIT_MULTIPLIER = 2.5f; // bonus multiplier for individual eco prestiges for reaching tier two
	private static final float MAX_DEPOSIT_ALL_MULTIPLIER = 1.25f; // bonus multiplier for DEPOSIT_ALL if all are tier two

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
					PlayerUtils.giveItem(player, GemCommand.makeGem(Enchant.GLOWING, 1));
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
		HERBALISM(2, 6, Material.DIAMOND_HOE, "The boots of Demeter give you the power to increase agricultural rates around you") {
			@Override
			void onClick(Player player) {
				PlayerUtils.giveItem(player, new ItemBuilder(Material.GOLDEN_BOOTS).lore("&aBonemeal Boots").build());
			}
		},
		FISHING(3, 1, Material.FISHING_ROD,
				"A godly rod that increases your catch") {
				@Override
				void onClick(Player player) {
					PlayerUtils.giveItem(player, new ItemBuilder(Material.FISHING_ROD)
							.enchant(Enchantment.LURE, 4)
							.enchant(Enchantment.LUCK_OF_THE_SEA, 5)
							.enchant(Enchantment.UNBREAKING, 3)
							.enchant(Enchantment.MENDING).build());
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
					PlayerUtils.giveItem(player, GemCommand.makeGem(Enchant.AUTOREPAIR, 1));
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
					LuckPermsUtils.PermissionChange.set().permissions("horsepicker.pick").player(player).runAsync();
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
		UNARMED(4, 6, Material.ROTTEN_FLESH, "Punching your enemies to death can be dangerous work, this bandage and stick will help with that") {
			@Override
			void onClick(Player player) {
				PlayerUtils.runCommandAsConsole("ce give " + player.getName() + " " + Material.PAPER.name() + " bandage");
				PlayerUtils.giveItem(player, new ItemBuilder(Material.STICK).name("Quarterstaff").enchant(Enchant.DISARMING).build());
			}
		},
		ALCHEMY(4, 8, Material.SPLASH_POTION,
				"You want to throw potions mate? Why not launch them from this hopper? " +
						"Watch your potions soar like they were shot from a bow to hit friend and foe alike from afar!") {
				@Override
				void onClick(Player player) {
					PlayerUtils.runCommandAsConsole("ce give " + player.getName() + " " + Material.HOPPER.name() + " potionlauncher");
					PermissionChange.set().player(player).permissions("combine.use").runAsync();
					PlayerUtils.send(player, "&eTo shoot potions, first run &c/combine &eto combine all similar potions in your inventory into one stack, " +
							"then place the stack in the slot to the right of the Potion Launcher. If you place the hopper, hold it and do &c/fixpotionlauncher");
				}
		};

		private final int row;
		private final int column;
		private final Material material;
		private final String rewardDescription;

		abstract void onClick(Player player);

		public PrimarySkillType asPrimarySkill() {
			return PrimarySkillType.valueOf(name());
		}
	}

	@Override
	public void init() {
		McMMOPlayer mcmmoPlayer = UserManager.getPlayer(viewer);

		int totalPowerLevel = 0;
		boolean _canPrestigeAll = true;
		for (ResetSkillType skill : ResetSkillType.values()) {
			int powerLevel = Math.min(McMMO.TIER_ONE, mcmmoPlayer.getSkillLevel(PrimarySkillType.valueOf(skill.name())));
			totalPowerLevel += powerLevel;
			if (powerLevel < McMMO.TIER_ONE)
				_canPrestigeAll = false;
		}
		final boolean canPrestigeAll = _canPrestigeAll;

		ItemBuilder all = new ItemBuilder(Material.BEACON)
			.name("&eAll Skills")
			.lore("&3Power Level: &e" + totalPowerLevel + "/" + McMMO.TIER_ONE_ALL +
				"",
				"&3&lReward:",
				"&f- " + DEPOSIT_PRETTY + " per level " + McMMO.TIER_ONE + " skill (x" + MAX_DEPOSIT_MULTIPLIER + " if level " + McMMO.TIER_TWO + ")",
				"&f- " + DEPOSIT_ALL_PRETTY + " bonus (x" + MAX_DEPOSIT_ALL_MULTIPLIER + " if every skill is level " + McMMO.TIER_TWO + ")",
				"&f- All normal rewards",
				"&f- When your health gets low, this breastplate will give you the strength of an angry barbarian!")
			.glow(mcmmoPlayer.getPowerLevel() >= McMMO.TIER_ONE_ALL);

		ItemStack reset = new ItemBuilder(Material.BARRIER).name("&cReset all with &lno reward").build();

		contents.set(0, 4, ClickableItem.of(all, e -> {
			if (!canPrestigeAll) return;

			ConfirmationMenu.builder()
				.title("&4Confirm Prestige All?")
				.onConfirm(e2 -> {
					viewer.closeInventory();
					prestigeAll(viewer);
				})
				.open(viewer);
		}));

		contents.set(5, 4, ClickableItem.of(reset, e ->
			ConfirmationMenu.builder()
				.title("&4Confirm Reset All? (No Rewards)")
				.onConfirm(e2 -> {
					viewer.closeInventory();
					resetAll(mcmmoPlayer);
				})
				.open(viewer)));

		var user = service.get(viewer);
		for (ResetSkillType skill : ResetSkillType.values()) {
			ItemBuilder item = new ItemBuilder(skill.getMaterial()).itemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ATTRIBUTES)
				.name("&e" + StringUtils.camelCase(skill.name()))
				.lore(
					"&3Level: &e" + mcmmoPlayer.getSkillLevel(PrimarySkillType.valueOf(skill.name())),
					"",
					"&3&lReward:",
					"&f" + DEPOSIT_PRETTY + " (x" + MAX_DEPOSIT_MULTIPLIER + " for level " + McMMO.TIER_TWO + ")",
					"&f" + skill.getRewardDescription(),
					"",
					"&3Number of Prestieges: &e" + user.getPrestige(skill))
				.glow(mcmmoPlayer.getSkillLevel(PrimarySkillType.valueOf(skill.name())) >= McMMO.TIER_ONE);

			contents.set(skill.getRow(), skill.getColumn(), ClickableItem.of(item, (e) -> {
				if (mcmmoPlayer.getSkillLevel(PrimarySkillType.valueOf(skill.name())) < McMMO.TIER_ONE)
					return;

				ConfirmationMenu.builder()
					.title("Confirm Prestige?")
					.onConfirm((e2) -> {
						viewer.closeInventory();
						prestige(viewer, skill, true);
					})
					.open(viewer);
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
		Koda.say(Nickname.of(player) + " has reset all of their mcMMO skills!");

		PlayerUtils.runCommandAsConsole("ce give " + player.getName() + " diamond_chestplate enlighted:1 beserk:1 durability:3 mending:1");

		McMMOPlayer mcmmoPlayer = UserManager.getPlayer(player);
		boolean allMax = true;
		for (PrimarySkillType skillType : PrimarySkillType.values()) {
			if (skillType.isChildSkill()) continue;
			if (mcmmoPlayer.getSkillLevel(skillType) < McMMO.TIER_TWO)
				allMax = false;
			prestige(player, ResetSkillType.valueOf(skillType.name()), false);
		}
		int deposit = DEPOSIT_ALL;
		if (allMax)
			deposit *= MAX_DEPOSIT_ALL_MULTIPLIER;
		new BankerService().deposit(player, deposit, ShopGroup.SURVIVAL, TransactionCause.MCMMO_RESET);

		service.edit(player, McMMOPrestigeUser::prestigeAll);
	}

	public void prestige(Player player, ResetSkillType skill, boolean broadcast) {
		McMMOPlayer mcmmoPlayer = UserManager.getPlayer(player);

		int reward = DEPOSIT;
		if (mcmmoPlayer.getSkillLevel(PrimarySkillType.valueOf(skill.name())) >= McMMO.TIER_TWO)
			reward *= MAX_DEPOSIT_MULTIPLIER;

		skill.onClick(player);
		new BankerService().deposit(player, reward, ShopGroup.SURVIVAL, TransactionCause.MCMMO_RESET);
		mcmmoPlayer.modifySkill(PrimarySkillType.valueOf(skill.name()), 0);

		final McMMOPrestigeUser user = service.get(player);
		user.prestige(skill);
		service.save(user);

		if (broadcast)
			Koda.say(Nickname.of(player) + " has reset their " + skill.name().toLowerCase() + " skill for the " +
					StringUtils.getNumberWithSuffix(user.getPrestige(skill)) + " time!");
	}
}
