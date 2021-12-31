package gg.projecteden.nexus.features.store.perks.autosort.commands;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.TemporaryListener;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.store.perks.autosort.AutoSortFeature;
import gg.projecteden.nexus.features.store.perks.autosort.features.AutoCraft;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.autosort.AutoSortUser;
import gg.projecteden.nexus.models.autosort.AutoSortUser.AutoSortInventoryType;
import gg.projecteden.nexus.models.autosort.AutoSortUser.AutoTrashBehavior;
import gg.projecteden.nexus.models.autosort.AutoSortUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils.ItemStackComparator;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.ItemUtils.isNullOrAir;
import static java.util.stream.Collectors.joining;

@NoArgsConstructor
@Aliases("autoinv")
@Redirect(from = "/autosort", to = "/autoinv")
@Redirect(from = "/autotrash", to = "/autoinv trash")
@Redirect(from = "/autocraft", to = "/autoinv craft")
public class AutoInventoryCommand extends CustomCommand implements Listener {
	public static final String PERMISSION = "store.autosort";
	private final AutoSortUserService service = new AutoSortUserService();
	private AutoSortUser user;

	public AutoInventoryCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Path("sort")
	void sort() {
		runCommand("sort");
	}

	@Path("<feature> [enable]")
	void toggle(AutoSortFeature feature, Boolean enable) {
		feature.checkPermission(player());

		if (enable == null)
			enable = !user.hasFeatureEnabled(feature);

		if (enable)
			if (!user.getDisabledFeatures().contains(feature))
				error(feature + " is already enabled");
			else
				user.getDisabledFeatures().remove(feature);
		else
			if (user.getDisabledFeatures().contains(feature))
				error(feature + " is already disabled");
			else
				user.getDisabledFeatures().add(feature);

		service.save(user);
		send(PREFIX + feature + " " + (enable ? "&aenabled" : "&cdisabled"));
	}

	@Path("inventoryTypes")
	void types() {
		new AutoSortInventoryTypeEditor().open(player());
	}

	private static class AutoSortInventoryTypeEditor extends MenuUtils implements InventoryProvider {

		@Override
		public void open(Player player, int page) {
			SmartInventory.builder()
					.provider(this)
					.title("AutoSort Inventory Editor")
					.size(6, 9)
					.build()
					.open(player, page);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			final AutoSortUserService service = new AutoSortUserService();
			final AutoSortUser user = service.get(player);

			addCloseItem(contents);

			List<ClickableItem> items = new ArrayList<>();
			for (AutoSortInventoryType inventoryType : AutoSortInventoryType.values()) {
				Material material = inventoryType.getMaterial();
				int customModelData = inventoryType.getCustomModelData();

				ItemBuilder item = new ItemBuilder(material).name(StringUtils.camelCase(inventoryType));
				if (customModelData > 0)
					item.customModelData(customModelData);

				if (!user.getDisabledInventoryTypes().contains(inventoryType))
					item.lore("&aEnabled");
				else
					item.lore("&cDisabled");

				items.add(ClickableItem.from(item.build(), e -> {
					if (user.getDisabledInventoryTypes().contains(inventoryType))
						user.getDisabledInventoryTypes().remove(inventoryType);
					else
						user.getDisabledInventoryTypes().add(inventoryType);

					service.save(user);

					open(player, contents.pagination().getPage());
				}));
			}

			paginator(player, contents, items);
		}
	}

	@Path("trash materials")
	void trash_materials() {
		new AutoTrashMaterialEditor(user);
	}

	@Path("trash behavior [behavior]")
	void trash_behavior(AutoTrashBehavior behavior) {
		if (behavior == null) {
			send("Current behavior is " + camelCase(user.getAutoTrashBehavior()));
			return;
		}

		user.setAutoTrashBehavior(behavior);
		service.save(user);
		send(PREFIX + "Auto Trash behavior set to " + camelCase(behavior));
	}

	public static class AutoTrashMaterialEditor implements TemporaryListener {
		private static final String TITLE = StringUtils.colorize("&eAuto Trash");
		private final AutoSortUser user;

		@Override
		public Player getPlayer() {
			return user.getOnlinePlayer();
		}

		public AutoTrashMaterialEditor(AutoSortUser user) {
			this.user = user;

			Inventory inv = Bukkit.createInventory(null, 6 * 9, TITLE);
			inv.setContents(user.getAutoTrashInclude().stream()
				.map(ItemStack::new)
				.sorted(new ItemStackComparator())
				.toArray(ItemStack[]::new));

			Nexus.registerTemporaryListener(this);
			user.getOnlinePlayer().openInventory(inv);
		}

		@EventHandler
		public void onChestClose(InventoryCloseEvent event) {
			if (event.getInventory().getHolder() != null) return;
			if (!Utils.equalsInvViewTitle(event.getView(), TITLE)) return;
			if (!event.getPlayer().equals(user.getOnlinePlayer())) return;

			Set<Material> materials = Arrays.stream(event.getInventory().getContents())
				.filter(item -> !isNullOrAir(item))
				.map(ItemStack::getType)
				.collect(Collectors.toSet());
			user.setAutoTrashInclude(materials);

			new AutoSortUserService().save(user);

			user.sendMessage(StringUtils.getPrefix("AutoTrash") + "Automatically trashing " + materials.size() + " materials");

			Nexus.unregisterTemporaryListener(this);
			event.getPlayer().closeInventory();
		}
	}

	@Path("craft")
	void edit() {
		new AutoCraftEditor().open(player());
	}

	private static class AutoCraftEditor extends MenuUtils implements InventoryProvider {

		@Override
		public void open(Player player, int page) {
			SmartInventory.builder()
				.provider(this)
				.title("AutoCraft Editor")
				.size(6, 9)
				.build()
				.open(player, page);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			final AutoSortUserService service = new AutoSortUserService();
			final AutoSortUser user = service.get(player);

			addCloseItem(contents);

			List<ClickableItem> items = new ArrayList<>();
			for (Material material : AutoCraft.getAutoCraftable().keySet()) {
				ItemBuilder item = new ItemBuilder(material);

				if (!user.getAutoCraftExclude().contains(material))
					item.lore("&aEnabled").glow();
				else
					item.lore("&cDisabled");

				item.lore("", "&f" + AutoCraft.getIngredients(material).stream()
					.map(StringUtils::pretty)
					.collect(joining(", ")));

				items.add(ClickableItem.from(item.build(), e -> {
					if (user.getAutoCraftExclude().contains(material))
						user.getAutoCraftExclude().remove(material);
					else
						user.getAutoCraftExclude().add(material);

					service.save(user);

					open(player, contents.pagination().getPage());
				}));
			}

			paginator(player, contents, items);
		}

	}

}
