package me.pugabyte.bncore.features.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.ItemClickData;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.Utils;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static me.pugabyte.bncore.utils.Utils.colorize;
import static me.pugabyte.bncore.utils.Utils.loreize;

public abstract class MenuUtils {

	protected ItemStack addGlowing(ItemStack itemStack) {
		return Utils.addGlowing(itemStack);
	}

	public int getRows(int items) {
		return (int) Math.ceil((double) items / 9);
	}

	protected ItemStack nameItem(Material material, String name) {
		return nameItem(new ItemStack(material), name, null);
	}

	protected ItemStack nameItem(Material material, String name, String lore) {
		return nameItem(new ItemStack(material), name, lore);
	}

	protected ItemStack nameItem(ItemStack item, String name) {
		return nameItem(item, name, null);
	}

	protected ItemStack nameItem(ItemStack item, String name, String lore) {
		ItemMeta meta = item.getItemMeta();
		if (name != null)
			meta.setDisplayName(colorize(name));
		if (lore != null)
			meta.setLore(Arrays.asList(loreize(colorize(lore)).split("\\|\\|")));
		item.setItemMeta(meta);
		return item;
	}

	protected void warp(Player player, String warp) {
		Bukkit.dispatchCommand(player, "essentials:warp " + warp);
	}

	public void command(Player player, String command) {
		Bukkit.dispatchCommand(player, command);
	}

	public static String getLocationLore(Location location) {
		if (location == null) return null;
		return "&3X:&e " + (int) location.getX() + "||&3Y:&e " + (int) location.getY() + "||&3Z:&e " + (int) location.getZ();
	}

	protected void addBackItem(InventoryContents contents, Consumer<ItemClickData> consumer) {
		contents.set(0, 0, ClickableItem.from(backItem(), consumer));
	}

	protected void addCloseItem(InventoryContents contents) {
		contents.set(0, 0, ClickableItem.from(backItem(), e -> e.getPlayer().closeInventory()));
	}

	protected ItemStack backItem() {
		return nameItem(new ItemStack(Material.BARRIER), "&cBack");
	}

	protected ItemStack closeItem() {
		return nameItem(new ItemStack(Material.BARRIER), "&cClose");
	}

	public static void openAnvilMenu(Player player, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete, Consumer<Player> onClose) {
		new AnvilGUI.Builder()
				.text(text)
				.onComplete(onComplete)
				.onClose(onClose)
				.plugin(BNCore.getInstance())
				.open(player);
	}

	public static void confirmMenu(Player player, ConfirmationMenu provider) {
		SmartInventory inv = SmartInventory.builder()
				.title(colorize(provider.getTitle()))
				.provider(provider)
				.size(3, 9)
				.build();
		inv.open(player);
	}

	@Builder
	@AllArgsConstructor
	public static class ConfirmationMenu extends MenuUtils implements InventoryProvider {
		@Getter
		@Builder.Default
		private String title = "&4Are you sure?";
		@Builder.Default
		private String cancelText = "&cNo";
		private String cancelLore;
		@Builder.Default
		private String confirmText = "&aYes";
		private String confirmLore;
		@Builder.Default
		private Consumer<ItemClickData> onCancel = (e) -> e.getPlayer().closeInventory();
		@NonNull
		private Consumer<ItemClickData> onConfirm;

		@Override
		public void init(Player player, InventoryContents contents) {
			ItemStack cancelItem = nameItem(ColorType.RED.getItemStack(Material.CONCRETE), cancelText, cancelLore);
			ItemStack confirmItem = nameItem(ColorType.LIGHT_GREEN.getItemStack(Material.CONCRETE), confirmText, confirmLore);

			contents.set(1, 2, ClickableItem.from(cancelItem, (e) -> {
				if (onCancel != null)
					onCancel.accept(e);

				if (title.equals(e.getPlayer().getOpenInventory().getTitle()))
					e.getPlayer().closeInventory();
			}));

			contents.set(1, 6, ClickableItem.from(confirmItem, e -> {
				onConfirm.accept(e);

				if (title.equals(e.getPlayer().getOpenInventory().getTitle()))
					e.getPlayer().closeInventory();
			}));
		}

		@Override
		public void update(Player player, InventoryContents inventoryContents) {
		}

	}

}
