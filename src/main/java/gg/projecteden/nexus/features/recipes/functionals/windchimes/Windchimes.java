package gg.projecteden.nexus.features.recipes.functionals.windchimes;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.recipes.models.FunctionalRecipe;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static gg.projecteden.utils.StringUtils.camelCase;

public abstract class Windchimes extends FunctionalRecipe {

	@Getter
	@AllArgsConstructor
	protected enum WindchimeType {
		IRON(Material.IRON_INGOT),
		GOLD(Material.GOLD_INGOT),
		COPPER(Material.COPPER_INGOT),
		AMETHYST(Material.AMETHYST_SHARD),
		;

		private final Material ingot;
	}

	@Getter
	public ItemStack item = new ItemBuilder(Material.AMETHYST_SHARD)
		.name(camelCase(getWindchimeType()) + " Windchimes")
		.customModelData(getWindchimeType().ordinal() + 1)
		.build();

	abstract WindchimeType getWindchimeType();

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public ItemStack getResult() {
		return item;
	}

	@Override
	public String[] getPattern() {
		return new String[]{"111", "222", "343"};
	}

	@Override
	public Recipe getRecipe() {
		NamespacedKey key = new NamespacedKey(Nexus.getInstance(), "custom_windchimes_" + getWindchimeType().name().toLowerCase());
		ShapedRecipe recipe = new ShapedRecipe(key, item);
		recipe.shape(getPattern());
		recipe.setIngredient('1', Material.STICK);
		recipe.setIngredient('2', Material.CHAIN);
		recipe.setIngredient('3', getWindchimeType().getIngot());
		recipe.setIngredient('4', getMaterialChoice());
		return recipe;
	}

	@Override
	public List<ItemStack> getIngredients() {
		return new ArrayList<>(List.of(
			new ItemStack(Material.STICK),
			new ItemStack(Material.CHAIN),
			new ItemStack(getWindchimeType().getIngot()),
			new ItemStack(Material.OAK_BUTTON)
		));
	}

	@Override
	public MaterialChoice getMaterialChoice() {
		return new RecipeChoice.MaterialChoice(MaterialTag.WOOD_BUTTONS);
	}

	public static boolean isWindchime(ItemStack item) {
		if (!item.getType().equals(Material.AMETHYST_SHARD))
			return false;

		if (new ItemBuilder(item).customModelData() > WindchimeType.values().length)
			return false;

		return true;

	}

	@EventHandler
	public void onClickWindchime(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (player.isSneaking()) return;
		if (!EquipmentSlot.HAND.equals(event.getHand())) return;
		if (!ActionGroup.RIGHT_CLICK.applies(event)) return;

		ItemFrame itemFrame = PlayerUtils.getTargetItemFrame(player, 4, Map.of(BlockFace.UP, 1));
		if (itemFrame == null || ItemUtils.isNullOrAir(itemFrame.getItem())) return;
		if (!isWindchime(itemFrame.getItem())) return;

		if (!Nerd.of(player).getRank().isAdmin()) return;

		event.setCancelled(true);
		player.sendMessage("\nTODO: make windchime sound");
	}

	@EventHandler
	public void onInteractItemFrame(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();

		if (player.isSneaking()) return;
		if (!EquipmentSlot.HAND.equals(event.getHand())) return;

		Entity clicked = event.getRightClicked();
		if (!(clicked instanceof ItemFrame itemFrame)) return;
		if (!isWindchime(itemFrame.getItem())) return;

		if (!Nerd.of(player).getRank().isAdmin()) return;

		event.setCancelled(true);
		player.sendMessage("\nTODO: make windchime sound");
	}

}
