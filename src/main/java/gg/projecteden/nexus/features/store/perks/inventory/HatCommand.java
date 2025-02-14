package gg.projecteden.nexus.features.store.perks.inventory;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@NoArgsConstructor
@Permission(HatCommand.PERMISSION)
@WikiConfig(rank = "Store", feature = "Inventory")
public class HatCommand extends CustomCommand implements Listener {
	public static final String PERMISSION = "essentials.hat";

	public HatCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Wear the item you are holding on your head")
	void run() {
		final PlayerInventory inv = inventory();
		final ItemStack hat = inv.getHelmet();
		final EquipmentSlot slot = getHandWithTool();
		if (slot == null)
			error("There is nothing in your hand");

		final ItemStack hand = inv.getItem(slot);
		final ItemStack air = new ItemStack(Material.AIR);

		if (!Nullables.isNullOrAir(hat))
			if (hat.getEnchantments().containsKey(Enchantment.BINDING_CURSE))
				if (!isStaff())
					error("You cannot remove your hat, as it has the Curse of Binding!");

		if (Nullables.isNullOrAir(hand))
			if (Nullables.isNullOrAir(hat))
				error("There is nothing on your head or in your hand");
			else {
				inv.setHelmet(air);
				inv.setItem(slot, hat);
				send(PREFIX + "Hat removed");
			}
		else {
			inv.setHelmet(hand);
			inv.setItem(slot, hat);
			send(PREFIX + "Hat updated");
		}
	}

	@Path("remove")
	@Description("Remove your hat")
	void remove() {
		final PlayerInventory inv = inventory();
		final ItemStack hat = inv.getHelmet();
		final ItemStack hand = getTool();
		final ItemStack air = new ItemStack(Material.AIR);

		if (Nullables.isNullOrAir(hat))
			error("You do not have a hat");

		inv.setHelmet(air);
		if (Nullables.isNullOrAir(hand))
			inv.setItemInMainHand(hat);
		else
			PlayerUtils.giveItem(player(), hat);
		send(PREFIX + "Hat removed");
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		final Inventory top = event.getView().getTopInventory();
		final Inventory clickedInventory = event.getRawSlot() < 0 ? null : event.getRawSlot() < top.getSize() ? top : event.getView().getBottomInventory();

		if (clickedInventory == null || clickedInventory.getType() != InventoryType.PLAYER) return;
		Player player = (Player) event.getWhoClicked();
		if (event.getClick() != ClickType.LEFT) return;
		if (event.getSlot() != 39) return;
		if (Nullables.isNullOrAir(event.getCursor())) return;
		if (MaterialTag.SKULLS.isTagged(event.getCursor().getType())) return;
		if (!(clickedInventory instanceof PlayerInventory playerInventory)) return;
		if (isPreventBindingHat(player, playerInventory))
			return;
		if (!player.hasPermission("essentials.hat") && !Model.hasModel(event.getCursor())) return;

		event.setCancelled(true);
		final PlayerInventory inv = (PlayerInventory) clickedInventory;
		final ItemStack head = Nullables.isNullOrAir(inv.getHelmet()) ? new ItemStack(Material.AIR) : inv.getHelmet().clone();
		inv.setHelmet(event.getCursor().clone());
		event.setCursor(head);
	}

	private boolean isPreventBindingHat(Player player, PlayerInventory inventory) {
		final ItemStack head = inventory.getHelmet();
		return head != null && head.getEnchantments().containsKey(Enchantment.BINDING_CURSE) && !player.hasPermission("essentials.hat.ignore-binding");
	}

}
