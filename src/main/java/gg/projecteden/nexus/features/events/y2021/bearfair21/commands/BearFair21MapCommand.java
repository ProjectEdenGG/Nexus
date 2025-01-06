package gg.projecteden.nexus.features.events.y2021.bearfair21.commands;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2021.bearfair21.BearFair21;
import gg.projecteden.nexus.features.events.y2021.bearfair21.Quests;
import gg.projecteden.nexus.features.events.y2021.bearfair21.islands.BearFair21Renderer;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.bearfair21.BearFair21User;
import gg.projecteden.nexus.models.bearfair21.BearFair21UserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

@Disabled
@HideFromWiki
@Aliases("bf21map")
@NoArgsConstructor
@Permission(Group.ADMIN)
public class BearFair21MapCommand extends CustomCommand implements Listener {
	private BearFair21Renderer myRenderer;

	public BearFair21MapCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			myRenderer = BearFair21Renderer.getRenderer(uuid());
	}

	@Path
	void init() {
		final ItemStack item = getToolRequired();
		if (!isMap(item))
			error("You are not holding a map");
		setupMap(player(), item);
	}

	@Path("set coords <x> <y>")
	void set_coords(byte x, byte y) {
		myRenderer.getCursor().setX(x);
		myRenderer.getCursor().setY(y);
		send(PREFIX + "Set coords to " + x + " / " + y);
	}

	@Path("get coords")
	void get_coords() {
		byte x = myRenderer.getCursor().getX();
		byte y = myRenderer.getCursor().getY();
		send(PREFIX + "Current coords are " + x + " / " + y);
	}

	@Path("toggle updating")
	void toggle_updating() {
		myRenderer.setUpdating(!myRenderer.isUpdating());
		send(PREFIX + "Map updating " + (myRenderer.isUpdating() ? "&aenabled" : "&cdisabled"));
	}

	@Path("deactivate")
	void deactivate() {
		myRenderer.deactivate();
		send(PREFIX + "&cDeactivated");
	}

	@Path("get grabACopy")
	void get_grabACopy() {
		PlayerUtils.giveItem(player(), grabACopy.build());
		send(PREFIX + "Gave grab a copy item");
	}

	static {
		BearFair21Renderer.init();
	}

	@Override
	public void _shutdown() {
		BearFair21Renderer.shutdown();
	}

	private static final ItemBuilder grabACopy = new ItemBuilder(Material.MAP).name("&eGrab a copy!");

	private static final int mapId = Nexus.getEnv() == Env.PROD ? 6267 : 3250;
	public static final String NBT_KEY = "bf21-map";
	private static final ItemBuilder map = new ItemBuilder(Material.FILLED_MAP).name("Fairgrounds Map").undroppable().nbt(nbtItem -> nbtItem.setBoolean(NBT_KEY, true));

	@EventHandler
	public void onGrabCopy(PlayerInteractEntityEvent event) {
		if (!BearFair21.canDoBearFairQuest(event))
			return;

		Entity entity = event.getRightClicked();
		final Player player = event.getPlayer();
		if (!(entity instanceof ItemFrame itemFrame))
			return;
		if (!itemFrame.getItem().isSimilar(grabACopy.build()))
			return;
		if (!Nullables.isNullOrAir(findMap(player)))
			return;

		ItemStack item = getMap(player);
		setupMap(player, item);
		Quests.giveItem(player, item);
	}

	private static ItemStack getMap(Player player) {
		final ItemBuilder builder = map.clone();
		final BearFair21UserService service = new BearFair21UserService();
		final BearFair21User user = service.get(player);
		if (user.getMapId() == 0) {
			builder.createMapView(BearFair21.getWorld());
			user.setMapId(builder.getMapId());
			service.save(user);
		} else
			builder.mapId(user.getMapId());

		return builder.build();
	}

	static {
		for (Player player : BearFair21.getPlayers())
			fixMap(player);
	}

	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		final Player player = event.getPlayer();
		if (!BearFair21.isNotAtBearFair(player))
			waitAndFixMap(player);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		waitAndFixMap(player);
	}

	private void waitAndFixMap(Player player) {
		Tasks.wait(TickTime.SECOND.x(1), () -> {
			if (player.isOnline())
				if (!BearFair21.isNotAtBearFair(player))
					fixMap(player);
		});
	}

	private static void fixMap(Player player) {
		final ItemStack item = findMap(player);
		if (Nullables.isNullOrAir(item))
			return;

		setupMap(player, item);
	}

	private static void setupMap(Player player, ItemStack item) {
		MapMeta meta = (MapMeta) item.getItemMeta();
		MapView view = meta.getMapView();

		if (view == null) {
			setupMap(player, getMap(player));
			return;
		}

		for (MapRenderer mapRenderer : view.getRenderers())
			if (mapRenderer instanceof BearFair21Renderer renderer)
				renderer.deactivate();
		view.getRenderers().clear();
		view.addRenderer(BearFair21Renderer.getRenderer(player.getUniqueId()));
	}

	private static ItemStack findMap(Player player) {
		for (ItemStack content : player.getInventory().getContents())
			if (isMap(content))
				return content;
		return null;
	}

	private static boolean isMap(ItemStack itemStack) {
		if (Nullables.isNullOrAir(itemStack))
			return false;
		if (itemStack.getType() != Material.FILLED_MAP)
			return false;

		final NBTItem nbtItem = new NBTItem(itemStack);
		return nbtItem.hasKey(NBT_KEY) && nbtItem.getBoolean(NBT_KEY);
	}

}
