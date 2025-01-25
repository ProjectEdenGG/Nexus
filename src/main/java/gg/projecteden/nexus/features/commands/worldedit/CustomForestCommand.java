package gg.projecteden.nexus.features.commands.worldedit;

import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.DoubleSlash;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.worldedit.ForestGeneratorConfig;
import gg.projecteden.nexus.models.worldedit.ForestGeneratorConfig.TreeList;
import gg.projecteden.nexus.models.worldedit.ForestGeneratorConfig.TreeList.Tree;
import gg.projecteden.nexus.models.worldedit.ForestGeneratorConfigService;
import gg.projecteden.nexus.models.worldedit.ForestGeneratorUser;
import gg.projecteden.nexus.models.worldedit.ForestGeneratorUserService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.function.BiFunction;

/*
	TODO
		Select trees
			Find region (block at y=4), set clipboard origin to center point
		View trees
			Command to set view origin
			Paginate trees, send client side blocks

 */

@DoubleSlash
@Aliases("cf")
@NoArgsConstructor
@Environments(Env.TEST)
@Permission(Group.STAFF)
public class CustomForestCommand extends CustomCommand implements Listener {
	private static final String PREFIX = StringUtils.getPrefix("CustomForest");
	private final ForestGeneratorConfigService configService = new ForestGeneratorConfigService();
	private final ForestGeneratorConfig config = configService.get0();
	private final ForestGeneratorUserService userService = new ForestGeneratorUserService();
	private ForestGeneratorUser user;

	public CustomForestCommand(@NonNull CommandEvent event) {
		super(event);
		user = userService.get(player());
	}

	private void save() {
		userService.save(user);
		configService.save(config);
	}

	@Path("selecting [state]")
	void selecting(Boolean state) {
		if (state == null)
			state = !user.isSelecting();

		user.setSelecting(state);
		userService.save(user);
		send(PREFIX + "Tree selection " + (state ? "&aenabled" : "&cdisabled"));
	}

	@Path("lists create <id> [--overwrite]")
	void lists_create(String id, @Switch boolean overwrite) {
		if (!overwrite && config.getTreeList(id) != null)
			error("Tree list &e" + id + " already exists, use &e--overwrite &cto replace");

		user.getTreeList().setId(id);
		config.getTreeLists().add(user.getTreeList());
		save();
	}

	@Path("lists view [page]")
	void lists_view(@Arg("1") int page) {
		final BiFunction<TreeList, String, JsonBuilder> formatter = (treeList, index) -> json()
			.next("&3%s &e%s".formatted(index, treeList.getId())).newline()
			.next("&7  - %d trees".formatted(treeList.getTrees().size())).newline()
			.next("&7  - Created by &e%s".formatted(Nickname.of(treeList.getCreator())));

		new Paginator<TreeList>()
			.values(config.getTreeLists())
			.formatter(formatter)
			.command("/cf lists view")
			.page(page)
			.perPage(5)
			.send();
	}

	@Path("list trees view [index]")
	void list_trees_view(@Arg(min = 1) int index) {
		if (index == 0) {
			user.setViewOrigin(location());
			send(PREFIX + "Viewing trees");
		} else {
			final List<Tree> trees = user.getTreeList().getTrees();
			if (gg.projecteden.api.common.utils.Nullables.isNullOrEmpty(trees))
				error("No trees defined in list");
			if (index > trees.size() + 1)
				error("No index at index &e" + index);

			trees.get(index - 1).show(player());
		}
	}

	static {
		Tasks.repeatAsync(TickTime.SECOND, TickTime.SECOND.x(3), () -> {
			final ForestGeneratorUserService service = new ForestGeneratorUserService();
			OnlinePlayers.where().world("buildadmin").region("trees").forEach(player ->
				service.get(player).getTreeList().getTrees().forEach(tree -> tree.showSelected(player)));
		});
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if (!Dev.GRIFFIN.is(player))
			return;

		if (!ActionGroup.CLICK_BLOCK.applies(event))
			return;

		final Block block = event.getClickedBlock();
		if (Nullables.isNullOrAir(block))
			return;

		final WorldGuardUtils worldguard = new WorldGuardUtils(player);
		if (!worldguard.isInRegion(block.getLocation(), "trees"))
			return;

		if (block.getY() <= 3)
			return;

		final var configService = new ForestGeneratorConfigService();
		final var service = new ForestGeneratorUserService();
		final var user = service.get(player);
		if (!user.isSelecting())
			return;

		user.sendMessage(PREFIX + "Finding tree...");
		Tasks.async(() -> {
			try {
				Tree tree = Tree.at(block.getLocation());
				user.add(tree);
				service.save(user);
				configService.save(configService.get0()); // in case its a copied TreeList
				user.sendMessage(PREFIX + "Added tree &e%d &3(&e%d blocks&3)".formatted(tree.getId(), tree.getBlocks().size()));
			} catch (NexusException ex) {
				send(ex.withPrefix(PREFIX));
			} catch (Exception ex) {
				ex.printStackTrace();
				send(PREFIX + "&cError: " + ex.getMessage());
			}
		});
	}

}



