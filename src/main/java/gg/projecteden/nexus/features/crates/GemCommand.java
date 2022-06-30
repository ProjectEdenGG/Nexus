package gg.projecteden.nexus.features.crates;

import gg.projecteden.nexus.features.customenchants.CustomEnchants;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.Sound;
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

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@NoArgsConstructor
@Permission(Group.ADMIN)
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
			addGemEnchantToTool(player, gem, tool);
			event.setCancelled(true);
		} else if (isGem(inventory.getItemInOffHand())) {
			ItemStack gem = inventory.getItemInOffHand();
			ItemStack tool = inventory.getItemInMainHand();
			addGemEnchantToTool(player, gem, tool);
			event.setCancelled(true);
		}
	}

	public void addGemEnchantToTool(Player player, ItemStack gem, ItemStack tool) {
		Enchantment enchantment = gem.getEnchantments().entrySet().stream().findFirst().get().getKey();
		int level = gem.getEnchantments().entrySet().stream().findFirst().get().getValue();
		if (isNullOrAir(tool)) {
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
		ComponentLike displayName = gem.getItemMeta().displayName();
		gem.setAmount(gem.getAmount() - 1);
		tool.addUnsafeEnchantment(enchantment, level);
		CustomEnchants.update(tool);
		player.sendActionBar(new JsonBuilder("&aYou added a ")
			.next(displayName)
			.next(" &a to your " + StringUtils.camelCase(tool.getType())));
		player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
	}

	public boolean isGem(ItemStack item) {
		if (isNullOrAir(item)) return false;
		if (!item.getType().equals(CustomMaterial.GEM_SAPPHIRE.getMaterial())) return false;
		if (item.getEnchantments().isEmpty()) return false;
		return ModelId.of(item) == CustomMaterial.GEM_SAPPHIRE.getModelId();
	}

	public static ItemStack makeGem(Enchantment enchantment, int level) {
		return new ItemBuilder(CustomMaterial.GEM_SAPPHIRE)
			.name("&#0fa8ffGem of " + StringUtils.camelCase(enchantment.getKey().getKey()))
			.enchant(enchantment, level)
			.lore(" ", "&fHold this gem and a tool", "&fto apply this enchantment")
			.build();
	}

}
