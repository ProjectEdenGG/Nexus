package gg.projecteden.nexus.features.events.y2020.halloween20.quest.menus;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2020.halloween20.Halloween20;
import gg.projecteden.nexus.features.events.y2020.halloween20.models.QuestStage;
import gg.projecteden.nexus.features.events.y2020.halloween20.quest.Gate;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.SmartInvsPlugin;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.halloween20.Halloween20Service;
import gg.projecteden.nexus.models.halloween20.Halloween20User;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Title("Combination Lock")
public class CombinationLockProvider extends InventoryProvider {
	private final String CORRECT_CODE = "186710318";
	private int foundIndex = 0;
	private String playerCode = "";

	@Override
	public void init() {

		contents.fill(ClickableItem.empty(new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).name(" ").build()));

		contents.fillRow(1, ClickableItem.empty(new ItemStack(Material.AIR)));
		contents.fillRow(4, ClickableItem.empty(new ItemStack(Material.AIR)));
		contents.set(2, 3, ClickableItem.empty(new ItemStack(Material.AIR)));
		contents.set(2, 5, ClickableItem.empty(new ItemStack(Material.AIR)));

		contents.set(5, 7, ClickableItem.of(new ItemBuilder(Material.LIME_WOOL).name("&aSubmit Code").build(), e -> {
			parseCode(viewer, contents);
		}));

		contents.set(5, 8, ClickableItem.of(new ItemBuilder(Material.RED_WOOL).name("&cReset").build(), e -> new CombinationLockProvider().open(viewer)));

		int[] numberSlots = {9, 10, 11, 12, 13, 14, 15, 16, 17, 21, 23};

		Halloween20Service service = new Halloween20Service();
		Halloween20User user = service.get(viewer);

		for (int i = 0; i < user.getFoundComboLockNumbers().size(); i++) {
			int j = i;
			contents.set(numberSlots[i], ClickableItem.of(new ItemBuilder(user.getFoundComboLockNumbers().get(i).getItem())
					.name("&e" + user.getFoundComboLockNumbers().get(i).getNumericalValue()).build(), e -> {
				if (foundIndex == 9) return;
				contents.set(numberSlots[j], ClickableItem.empty(new ItemStack(Material.AIR)));
				contents.set(4, foundIndex++,
						ClickableItem.empty(new ItemBuilder(user.getFoundComboLockNumbers().get(j).getItem())
								.name("&e" + user.getFoundComboLockNumbers().get(j).getNumericalValue()).build()));
				playerCode += user.getFoundComboLockNumbers().get(j).getNumericalValue();
			}));
		}
	}

	public void parseCode(Player player, InventoryContents contents) {
		int[][] groups = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
		boolean[] correct = {false, false, false};

		for (int i = 0; i < groups.length; i++) {
			if (!Nullables.isNullOrEmpty(playerCode) && playerCode.length() == CORRECT_CODE.length())
				for (int j = 0; j < groups[i].length; j++)
					if (playerCode.charAt(groups[i][j]) == CORRECT_CODE.charAt(groups[i][j]))
						correct[i] = true;
					else {
						correct[i] = false;
						break;
					}
			for (int j = 0; j < groups[i].length; j++) {
				Material mat = correct[i] ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
				contents.set(4, groups[i][j], ClickableItem.empty(new ItemBuilder(mat).name(" ").build()));
			}
		}
		Tasks.wait(TickTime.SECOND.x(5), () -> {
			if (correct[0] && correct[1] && correct[2])
				complete(player);
			else {
				SmartInventory inv = SmartInvsPlugin.manager().getInventory(player).orElse(null);
				if (inv != null && inv.getProvider() == this)
					new CombinationLockProvider().open(player);
			}
		});
	}

	public void complete(Player player) {
		player.closeInventory();
		Halloween20Service service = new Halloween20Service();
		Halloween20User user = service.get(player);
		Gate gate = new Gate(player);
		if (user.getCombinationStage() == QuestStage.Combination.COMPLETE) {
			gate.open();
			Tasks.wait(TickTime.SECOND.x(4), gate::teleportOut);
			return;
		}
		gate.teleportIn();
		player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f);
		PlayerUtils.send(player, "&7&oThe gate opens before you. You can now leave the land of the dead.");
		Tasks.wait(TickTime.SECOND.x(1), gate::open);
		Tasks.wait(TickTime.SECOND.x(4), gate::teleportOut);
		user.setCombinationStage(QuestStage.Combination.COMPLETE);
		service.save(user);
		Tasks.wait(TickTime.SECOND.x(6), () -> {
			PlayerUtils.send(player, Halloween20.PREFIX + "To return to the land of the dead to continue exploring, simply use &c/halloween20 &3to be teleported inside of the gate.");
			Tasks.wait(TickTime.SECOND.x(5), () -> {
				new BankerService().deposit(player, 10000, ShopGroup.SURVIVAL, TransactionCause.EVENT);
				PlayerUtils.send(player, "&a$10,000 has been added to your account.");
				//new MysteryChest(player).give(2, RewardChestType.MYSTERY);
			});
		});
	}
}
