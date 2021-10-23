package gg.projecteden.nexus.features.recipes.functionals;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.recipes.RecipeUtils;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import java.util.ArrayList;
import java.util.List;

public class RepairCostDiminisher extends FunctionalRecipe {

	@Getter
	private final static ItemStack item = new ItemBuilder(Material.EMERALD)
		.customModelData(6)
		.name("§eRepair Cost Diminisher")
		.lore(
			"&7Use with an equipment item in",
			"&7your offhand to reduce the repair",
			"&7cost of the item",
			"&7",
			"&cRequires 30 XP Levels to Craft"
		)
		.enchant(Enchantment.ARROW_INFINITE, 1)
		.itemFlags(ItemFlag.values())
		.build();

	@Override
	public ItemStack getResult() {
		return getItem();
	}

	@Override
	public Recipe getRecipe() {
		NamespacedKey key = new NamespacedKey(Nexus.getInstance(), "custom_repair_cost_diminisher");
		ShapedRecipe recipe = new ShapedRecipe(key, getResult());
		recipe.shape(getPattern());
		recipe.setIngredient('1', Material.DIAMOND);
		recipe.setIngredient('2', Material.GOLD_BLOCK);
		recipe.setIngredient('3', Material.NETHERITE_BLOCK);
		return recipe;
	}

	@Override
	public List<ItemStack> getIngredients() {
		return new ArrayList<>() {{
			add(new ItemStack(Material.DIAMOND));
			add(new ItemStack(Material.GOLD_BLOCK));
			add(new ItemStack(Material.NETHERITE_BLOCK));
		}};
	}

	@Override
	public String[] getPattern() {
		return new String[] { "121", "232", "121" };
	}

	@Override
	public RecipeChoice.MaterialChoice getMaterialChoice() {
		return null;
	}

	@EventHandler
	public void onPrepareItem(PrepareItemCraftEvent event) {
		if (!RecipeUtils.areSimilar(event.getRecipe(), getRecipe())) return;
		Player player = (Player) event.getView().getPlayer();
		if (player.getLevel() < 30) {
			event.getInventory().setResult(null);
		}
	}

	@EventHandler
	public void onPlayerCraft(CraftItemEvent event) {
		if (!RecipeUtils.areSimilar(event.getRecipe(), getRecipe())) return;
		Player player = (Player) event.getView().getPlayer();
		player.setLevel(Math.max(0, player.getLevel() - 30));
	}

	@EventHandler
	public void onClickOfDiminisher(PlayerInteractEvent event) {
		if (!Utils.ActionGroup.RIGHT_CLICK.applies(event))
			return;
		if (event.getHand() != EquipmentSlot.HAND)
			return;
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
			if (MaterialTag.CONTAINERS.isTagged(event.getClickedBlock().getType()))
				return;

		Player player = event.getPlayer();
		PlayerInventory inventory = player.getInventory();
		if (ItemUtils.isSimilar(inventory.getItemInMainHand(), getItem())) {
			ItemStack diminisher = inventory.getItemInMainHand();
			ItemStack tool = inventory.getItemInOffHand();
			lowerRepairCost(player, diminisher, tool);
			event.setCancelled(true);
		} else if (ItemUtils.isSimilar(inventory.getItemInOffHand(), getItem())) {
			ItemStack diminisher = inventory.getItemInOffHand();
			ItemStack tool = inventory.getItemInMainHand();
			lowerRepairCost(player, diminisher, tool);
			event.setCancelled(true);
		}
	}

	public void lowerRepairCost(Player player, ItemStack diminisher, ItemStack tool) {
		if (ItemUtils.isNullOrAir(tool)) {
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
			tool.setItemMeta((ItemMeta) repairable);
			diminisher.setAmount(diminisher.getAmount() - 1);
			PlayerUtils.send(player, "&aThe item's repair cost has been halved");
			player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1f);
		}
		else {
			PlayerUtils.send(player, "&cThat tool is not repairable");
			return;
		}
	}

}
