package gg.projecteden.nexus.features.survival.gem;

import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.survival.MendingIntegrity;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.ComponentLike;
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

@NoArgsConstructor
@Permission(Group.ADMIN)
public class GemCommand extends CustomCommand implements Listener {

	public GemCommand(CommandEvent event) {
		super(event);
	}

	@Path("<enchantment> <level>")
	@Description("Receive a gem")
	void get(Enchantment enchantment, Integer level) {
		PlayerUtils.giveItem(player(), makeGem(enchantment, level));
	}

	@Path("all")
	@Description("Receive all possible gems")
	void all() {
		Arrays.stream(Enchantment.values())
			.filter(enchantment -> !enchantment.equals(Enchantment.BINDING_CURSE))
			.filter(enchantment -> !enchantment.equals(Enchantment.VANISHING_CURSE))
			.forEach(enchant -> PlayerUtils.giveItem(player(), makeGem(enchant, 1)));
	}

	@EventHandler
	public void onClickOfGem(PlayerInteractEvent event) {
		if (!Utils.ActionGroup.RIGHT_CLICK.applies(event))
			return;
		if (event.getHand() != EquipmentSlot.HAND)
			return;
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !Nullables.isNullOrAir(event.getClickedBlock()))
			if (MaterialTag.CONTAINERS.isTagged(event.getClickedBlock().getType()))
				return;

		Player player = event.getPlayer();
		PlayerInventory inventory = player.getInventory();

		final ItemStack gem;
		final ItemStack tool;
		if (isGem(inventory.getItemInMainHand())) {
			gem = inventory.getItemInMainHand();
			tool = inventory.getItemInOffHand();
		} else if (isGem(inventory.getItemInOffHand())) {
			gem = inventory.getItemInOffHand();
			tool = inventory.getItemInMainHand();
		} else {
			return;
		}

		addGemEnchantToTool(player, gem, tool);
		event.setCancelled(true);
	}

	public void addGemEnchantToTool(Player player, ItemStack gem, ItemStack tool) {
		Enchantment enchantment = gem.getEnchantments().entrySet().stream().findFirst().get().getKey();
		int level = gem.getEnchantments().entrySet().stream().findFirst().get().getValue();
		if (Nullables.isNullOrAir(tool)) {
			PlayerUtils.send(player, "&cYou must hold an item in your other hand to apply the enchantment");
			return;
		}

		if (isGem(tool)) { // Gem combining
			Enchantment gemEnchant = tool.getEnchantments().entrySet().stream().findFirst().get().getKey();
			if (gemEnchant != enchantment) {
				PlayerUtils.send(player, "&cYou cannot combine gems of seperate types");
				return;
			}
			if (level != tool.getEnchantmentLevel(enchantment)) {
				PlayerUtils.send(player, "&cYou cannot combine gems of different enchantment levels");
				return;
			}
		}
		else if (!ItemUtils.getApplicableEnchantments(tool).contains(enchantment)) {
			PlayerUtils.send(player, "&cThe enchantment on this gem is not applicable to the tool you are holding");
			return;
		}

		if (tool.getItemMeta().hasEnchant(enchantment)) {
			if (enchantment == Enchant.GLOWING) {
				level = tool.getEnchantmentLevel(Enchant.GLOWING) + 1;
			} else if (tool.getEnchantments().get(enchantment) > level) {
				PlayerUtils.send(player, "&cThe tool you are holding already has that enchantment at a higher level");
				return;
			} else if (tool.getEnchantments().get(enchantment) == level) {
				if (enchantment.getMaxLevel() == 1) {
					PlayerUtils.send(player, "&cThis enchantment cannot be leveled up");
					return;
				}

				level++;
			}
		}

		if (Enchantment.MENDING.equals(enchantment)) {
			MendingIntegrity.setMaxIntegrity(tool);
		}

		ComponentLike displayName = gem.getItemMeta().displayName();
		gem.subtract();
		tool.addUnsafeEnchantment(enchantment, level);

		ItemUtils.update(tool, player);

		JsonBuilder message =
			isGem(tool) ?
				new JsonBuilder("&aYou combined your ")
				.next("&#0fa8ffGems of " + StringUtils.camelCase(enchantment.getKey().getKey()))
				:
				new JsonBuilder("&aYou added a ")
					.next(displayName)
					.next(" &a to your " + StringUtils.camelCase(tool.getType()));

		player.sendActionBar(message);
		player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
	}

	public boolean isGem(ItemStack item) {
		if (Nullables.isNullOrAir(item))
			return false;

		if (!item.getType().equals(CustomMaterial.GEM_SAPPHIRE.getMaterial()))
			return false;

		if (item.getEnchantments().isEmpty())
			return false;

		return CustomMaterial.GEM_SAPPHIRE.getModelId() == ModelId.of(item);
	}

	public static ItemStack makeGem(Enchantment enchantment) {
		return makeGem(enchantment, 1);
	}

	public static ItemStack makeGem(Enchantment enchantment, int level) {
		return new ItemBuilder(CustomMaterial.GEM_SAPPHIRE)
			.name("&#0fa8ffGem of " + StringUtils.camelCase(enchantment.getKey().getKey()))
			.enchant(enchantment, level)
			.lore(" ", "&fHold this gem and a tool", "&fto apply this enchantment")
			.build();
	}

}
