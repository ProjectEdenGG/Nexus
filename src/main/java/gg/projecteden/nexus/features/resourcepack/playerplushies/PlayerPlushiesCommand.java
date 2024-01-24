package gg.projecteden.nexus.features.resourcepack.playerplushies;

import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieConfig;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieConfigService;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieUser;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieUserService;
import gg.projecteden.nexus.models.warps.WarpType;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@NoArgsConstructor
public class PlayerPlushiesCommand extends CustomCommand implements Listener {
	private static final PlayerPlushieUserService userService = new PlayerPlushieUserService();

	public PlayerPlushiesCommand(CommandEvent event) {
		super(event);
	}

	@EventHandler
	public void on(ResourcePackUpdateCompleteEvent event) {
		PlayerPlushieConfig.generate();
	}

	@Path("store")
	@Description("View the Player Plushie store")
	void store() {
		new PlayerPlushieStoreMenu().open(player());
	}

	@Path("view")
	@Description("View Player Plushies in the Store Gallery")
	void view() {
		WarpType.NORMAL.get("playerplushies").teleportAsync(player());
	}

	@Path("addOwner <user>")
	@Permission(Group.ADMIN)
	@Description("Give a player access to Player Plushies")
	void addOwner(PlayerPlushieUser user) {
		new PlayerPlushieConfigService().edit0(config -> config.addOwner(user.getUuid()));
		send(PREFIX + "Added &e" + user.getNickname() + " &3to plushie users");
	}

	@Path("vouchers [player]")
	@Description("View how many vouchers you have")
	void vouchers(@Arg(value = "self", permission = Group.STAFF) PlayerPlushieUser user) {
		send(PREFIX + (isSelf(user) ? "Your" : user.getNickname() + "'s") + " vouchers: &e" + user.getVouchers());
		send(json(PREFIX + "Spend them in &c/playerplushies store").command("/playerplushies store"));
	}

	@Path("vouchers give <player> <amount>")
	@Description("Send vouchers to another player")
	void vouchers_give(PlayerPlushieUser user, int amount) {
		final PlayerPlushieUser self = userService.get(player());
		self.takeVouchers(amount);
		user.addVouchers(amount);
		new PlayerPlushieConfigService().edit0(config -> config.addOwner(user.getUuid()));
		userService.save(self);
		userService.save(user);
		send(PREFIX + "Gave &e" + amount + " &3vouchers to &e" + user.getNickname() + "&3. New balance: &e" + self.getVouchers());
	}

	@Path("vouchers add <amount> [player]")
	@Permission(Group.ADMIN)
	@Description("Modify a player's vouchers")
	void vouchers_add(int amount, @Arg("self") PlayerPlushieUser user) {
		user.addVouchers(amount);
		userService.save(user);
		send(PREFIX + "Gave &e" + amount + " &3vouchers to &e" + user.getNickname() + "&3. New balance: &e" + user.getVouchers());
	}

	@Path("vouchers remove <amount> [player]")
	@Permission(Group.ADMIN)
	@Description("Modify a player's vouchers")
	void vouchers_remove(int amount, @Arg("self") PlayerPlushieUser user) {
		user.takeVouchers(amount);
		userService.save(user);
		send(PREFIX + "Removed &e" + amount + " &3vouchers from &e" + user.getNickname() + "&3. New balance: &e" + user.getVouchers());
	}

}
