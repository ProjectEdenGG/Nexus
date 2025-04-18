package gg.projecteden.nexus.features.recipes.functionals;

import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.features.recipes.models.builders.RecipeBuilder;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.Repairable;
import org.jetbrains.annotations.NotNull;

public class RepairCostDiminisher extends FunctionalRecipe {

	private static final String NBT_KEY = "REPAIR_COST_DIMINISHER";

	@Getter
	private final static ItemStack item = new ItemBuilder(ItemModelType.GEM_BLACK_OPAL)
		.name("&eRepair Cost Diminisher")
		.lore("&7Use with an equipment item in")
		.lore("&7your offhand to reduce the repair")
		.lore("&7cost of the item")
		.lore("&7")
		.lore("&cRequires 30 XP Levels to Craft")
		.enchant(Enchantment.INFINITY, 1)
		.itemFlags(ItemFlag.values())
		.nbt(nbt -> nbt.setBoolean(NBT_KEY, true))
		.build();

	@Override
	public ItemStack getResult() {
		return getItem();
	}

	@Override
	public @NotNull Recipe getRecipe() {
		return RecipeBuilder.shaped("121", "232", "121")
			.add('1', Material.DIAMOND_BLOCK)
			.add('2', Material.NETHERITE_INGOT)
			.add('3', Material.GOLD_BLOCK)
			.toMake(getResult())
			.getRecipe();
	}

	@EventHandler
	public void onPrepareItem(PrepareItemCraftEvent event) {
		if (!ItemUtils.isSimilar(getResult(), event.getInventory().getResult())) return;
		Player player = (Player) event.getView().getPlayer();
		if (player.getLevel() < 30) {
			event.getInventory().setResult(null);
		}
	}

	@EventHandler
	public void onPlayerCraft(CraftItemEvent event) {
		if (!ItemUtils.isSimilar(getResult(), event.getInventory().getResult())) return;
		Player player = (Player) event.getView().getPlayer();
		player.setLevel(Math.max(0, player.getLevel() - 30));
	}

	@EventHandler
	public void onClickOfDiminisher(PlayerInteractEvent event) {
		if (!Utils.ActionGroup.RIGHT_CLICK.applies(event))
			return;
		if (event.getHand() != EquipmentSlot.HAND)
			return;
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !Nullables.isNullOrAir(event.getClickedBlock()))
			if (MaterialTag.CONTAINERS.isTagged(event.getClickedBlock().getType()))
				return;

		Player player = event.getPlayer();
		PlayerInventory inventory = player.getInventory();

		final ItemStack diminisher;
		final ItemStack tool;
		ItemStack itemInMainHand = inventory.getItemInMainHand();
		ItemStack itemInOffHand = inventory.getItemInOffHand();

		if (ItemUtils.isModelMatch(item, itemInMainHand)) {
			diminisher = itemInMainHand;
			tool = itemInOffHand;
		} else if (ItemUtils.isModelMatch(item, itemInOffHand)) {
			diminisher = itemInOffHand;
			tool = itemInMainHand;
		} else
			return;

		lowerRepairCost(player, diminisher, tool);
		event.setCancelled(true);
	}

	public void lowerRepairCost(Player player, ItemStack diminisher, ItemStack tool) {
		if (Nullables.isNullOrAir(tool)) {
			PlayerUtils.send(player, "&cYou must hold an item in your other hand");
			return;
		}

		if (tool.getItemMeta() instanceof Repairable repairable) {
			if (!MaterialTag.ARMOR.isTagged(tool) && !MaterialTag.TOOLS.isTagged(tool) && !MaterialTag.WEAPONS.isTagged(tool) && tool.getType() != Material.ENCHANTED_BOOK) {
				PlayerUtils.send(player, "&cThat tool is not repairable");
				return;
			}

			if (repairable.getRepairCost() == 1) {
				PlayerUtils.send(player, "&cThat tool already has the minimum repair cost");
				return;
			}

			repairable.setRepairCost(Math.max(1, repairable.getRepairCost() / 2));
			tool.setItemMeta(repairable);
			diminisher.setAmount(diminisher.getAmount() - 1);
			PlayerUtils.send(player, "&aThe item's repair cost has been halved");
			player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1f);
		} else {
			PlayerUtils.send(player, "&cThat tool is not repairable");
			return;
		}
	}

}
