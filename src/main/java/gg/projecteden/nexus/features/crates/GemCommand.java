package gg.projecteden.nexus.features.crates;

import gg.projecteden.nexus.features.customenchants.CustomEnchants;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.stream.Collectors;

@NoArgsConstructor
@Permission("group.admin")
public class GemCommand extends CustomCommand implements Listener {

	public GemCommand(CommandEvent event) {
		super(event);
	}

	@Path("<enchantment> <level>")
	void get(Enchantment enchantment, Integer level) {
		PlayerUtils.giveItem(player(), makeGem(enchantment, level));
	}

	@Path("all")
	void all() {
		for (Enchantment enchantment : Arrays.stream(Enchantment.values()).filter(enchantment ->
				!enchantment.equals(Enchantment.BINDING_CURSE) && !enchantment.equals(Enchantment.VANISHING_CURSE)).collect(Collectors.toList()))
			PlayerUtils.giveItem(player(), makeGem(enchantment, 1));
	}

	@EventHandler
	public void onClickOfGem(PlayerInteractEvent event) {
		if (!Utils.ActionGroup.RIGHT_CLICK.applies(event))
			return;
		if (event.getHand() != EquipmentSlot.HAND)
			return;
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
			if (MaterialTag.CONTAINERS.isTagged(event.getClickedBlock().getType()))
				return;

		Player player = event.getPlayer();
		PlayerInventory inventory = player.getInventory();
		if (isGem(inventory.getItemInMainHand())) {
			ItemStack gem = inventory.getItemInMainHand();
			ItemStack tool = inventory.getItemInOffHand();
			addGemEnchantToTool(player, inventory, gem, tool);
		} else if (isGem(inventory.getItemInOffHand())) {
			ItemStack gem = inventory.getItemInOffHand();
			ItemStack tool = inventory.getItemInMainHand();
			addGemEnchantToTool(player, inventory, gem, tool);
		}
	}

	public void addGemEnchantToTool(Player player, PlayerInventory inventory, ItemStack gem, ItemStack tool) {
		Enchantment enchantment = gem.getEnchantments().entrySet().stream().findFirst().get().getKey();
		int level = gem.getEnchantments().entrySet().stream().findFirst().get().getValue();
		if (ItemUtils.isNullOrAir(tool)) {
			PlayerUtils.send(player, "&cYou must hold an item in your other hand to apply the enchantment");
			return;
		}
		if (!ItemUtils.getApplicableEnchantments(tool).contains(enchantment)) {
			PlayerUtils.send(player, "&cThe enchantment on this gem is not applicable to the tool you are holding");
			return;
		}
		if (tool.getEnchantments().containsKey(enchantment)) {
			if (enchantment == Enchant.GLOWING) {
				level = tool.getEnchantmentLevel(Enchant.GLOWING) + 1;
			}
			else if (tool.getEnchantments().get(enchantment) > level) {
				PlayerUtils.send(player, "&cThe tool you are holding already has that enchantment at a higher level");
				return;
			}
			else if (tool.getEnchantments().get(enchantment) == level) {
				level++;
			}
		}
		if (inventory.getItemInOffHand().equals(gem))
			inventory.setItemInOffHand(null);
		else
			inventory.removeItem(gem);
		tool.addUnsafeEnchantment(enchantment, level);
		CustomEnchants.update(tool);
	}

	public boolean isGem(ItemStack item) {
		if (ItemUtils.isNullOrAir(item)) return false;
		if (!item.getType().equals(Material.EMERALD)) return false;
		if (item.getEnchantments().isEmpty()) return false;
		return new ItemBuilder(item).customModelData() == 1;
	}

	public static ItemStack makeGem(Enchantment enchantment, int level) {
		return new ItemBuilder(Material.EMERALD)
				.name("&#0fa8ffGem of " + StringUtils.camelCase(enchantment.getKey().getKey()))
				.enchant(enchantment, level)
				.lore(" ", "&fHold this gem and a tool", "&fto apply this enchantment")
				.customModelData(1)
				.build();
	}

}
