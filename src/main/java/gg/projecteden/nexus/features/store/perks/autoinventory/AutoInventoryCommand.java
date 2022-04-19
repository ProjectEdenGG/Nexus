package gg.projecteden.nexus.features.store.perks.autoinventory;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.TemporaryListener;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.store.perks.autoinventory.features.AutoCraft;
import gg.projecteden.nexus.features.store.perks.autoinventory.tasks.FindChestsThread;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUser;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUser.AutoSortInventoryType;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUser.AutoTrashBehavior;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils.ItemStackComparator;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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

import static java.util.stream.Collectors.joining;

@NoArgsConstructor
@Aliases("autoinv")
@Redirect(from = "/depositall", to = "/autoinv depositall")
@Redirect(from = "/autosort", to = "/autoinv")
@Redirect(from = "/autotrash", to = "/autoinv settings trash")
@Redirect(from = "/autocraft", to = "/autoinv settings craft")
public class AutoInventoryCommand extends CustomCommand implements Listener {
	private final AutoInventoryUserService service = new AutoInventoryUserService();
	private AutoInventoryUser user;

	public AutoInventoryCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Path("depositall")
	void depositall() {
		Location location = player().getLocation();
		Chunk centerChunk = location.getChunk();
		World world = location.getWorld();
		ChunkSnapshot[][] snapshots = new ChunkSnapshot[3][3];
		for (int x = -1; x <= 1; x++)
			for (int z = -1; z <= 1; z++) {
				Chunk chunk = world.getChunkAt(centerChunk.getX() + x, centerChunk.getZ() + z);
				snapshots[x + 1][z + 1] = chunk.getChunkSnapshot();
			}

		// Create a thread to search those snapshots and create a chain of quick deposit attempts
		int minY = Math.max(world.getMinHeight(), player().getEyeLocation().getBlockY() - 10);
		int maxY = Math.min(world.getMaxHeight(), player().getEyeLocation().getBlockY() + 10);
		int startY = player().getEyeLocation().getBlockY();
		int startX = player().getEyeLocation().getBlockX();
		int startZ = player().getEyeLocation().getBlockZ();
		Thread thread = new FindChestsThread(world, snapshots, minY, maxY, startX, startY, startZ, player());
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	@Path("features")
	void features() {
		send(PREFIX + "Features");
		for (AutoInventoryFeature feature : AutoInventoryFeature.values()) {
			final boolean enabled = user.hasFeatureEnabledRaw(feature);
			final JsonBuilder json = json("&e" + feature + " &7- " + (enabled ? "&aEnabled" : "&cDisabled"))
				.hover("Click to " + (enabled ? "&cdisable" : "&aenable"))
				.command("/autoinv features toggle " + feature.name().toLowerCase() + " " + !enabled)
				.group().newline()
				.next("&7  " + feature.getDescription());

			if (feature.hasExtraDescription())
				json.newline().next("&c  " + feature.getExtraDescription());

			send(json);
		}
	}

	@Path("features toggle <feature> [enable]")
	void features_toggle(AutoInventoryFeature feature, Boolean enable) {
		feature.checkPermission(player());

		if (enable == null)
			enable = !user.hasFeatureEnabledRaw(feature);

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

	@Path("settings inventoryTypes")
	void settings_inventoryTypes() {
		new AutoSortInventoryTypeEditor().open(player());
	}

	private static class AutoSortInventoryTypeEditor extends MenuUtils implements InventoryProvider {

		@Override
		public void open(Player player, int page) {
			SmartInventory.builder()
					.provider(this)
					.title("AutoSort Inventory Editor")
					.maxSize()
					.build()
					.open(player, page);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			final AutoInventoryUserService service = new AutoInventoryUserService();
			final AutoInventoryUser user = service.get(player);

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

				items.add(ClickableItem.of(item.build(), e -> {
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

	@Path("settings tools includeSword [enable]")
	void settings_tools_includeSword(Boolean enable) {
		if (enable == null)
			enable = !user.isAutoToolIncludeSword();

		user.setAutoToolIncludeSword(enable);
		service.save(user);
		send(PREFIX + "AutoTool now " + (enable ? "&aincludes" : "&cexcludes") + " &3swords");
	}

	@Path("settings trash materials")
	void settings_trash_materials() {
		new AutoTrashMaterialEditor(user);
	}

	@Path("settings trash behavior [behavior]")
	void settings_trash_behavior(AutoTrashBehavior behavior) {
		if (behavior == null) {
			send("Current behavior is " + camelCase(user.getAutoTrashBehavior()));
			return;
		}

		user.setAutoTrashBehavior(behavior);
		service.save(user);
		send(PREFIX + "AutoTrash behavior set to " + camelCase(behavior));
	}

	public static class AutoTrashMaterialEditor implements TemporaryListener {
		private static final String TITLE = StringUtils.colorize("&eAutoTrash");
		private final AutoInventoryUser user;

		@Override
		public Player getPlayer() {
			return user.getOnlinePlayer();
		}

		public AutoTrashMaterialEditor(AutoInventoryUser user) {
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
				.filter(Nullables::isNotNullOrAir)
				.map(ItemStack::getType)
				.collect(Collectors.toSet());
			user.setAutoTrashInclude(materials);

			new AutoInventoryUserService().save(user);

			user.sendMessage(StringUtils.getPrefix("AutoTrash") + "Automatically trashing " + materials.size() + " materials");

			Nexus.unregisterTemporaryListener(this);
			event.getPlayer().closeInventory();
		}
	}

	@Path("settings crafting")
	void settings_crafting() {
		new AutoCraftEditor().open(player());
	}

	private static class AutoCraftEditor extends MenuUtils implements InventoryProvider {

		@Override
		public void open(Player player, int page) {
			SmartInventory.builder()
				.provider(this)
				.title("AutoCraft Editor")
				.maxSize()
				.build()
				.open(player, page);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			final AutoInventoryUserService service = new AutoInventoryUserService();
			final AutoInventoryUser user = service.get(player);

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

				items.add(ClickableItem.of(item.build(), e -> {
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
