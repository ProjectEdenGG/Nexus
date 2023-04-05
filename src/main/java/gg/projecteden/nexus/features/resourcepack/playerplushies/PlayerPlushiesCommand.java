package gg.projecteden.nexus.features.resourcepack.playerplushies;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieConfigService;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieUser;
import gg.projecteden.nexus.models.playerplushie.PlayerPlushieUserService;

@Permission(Group.ADMIN)
public class PlayerPlushiesCommand extends CustomCommand {
	private static final PlayerPlushieUserService userService = new PlayerPlushieUserService();

	public PlayerPlushiesCommand(CommandEvent event) {
		super(event);
	}

	@Path("store")
	void run() {
		new PlayerPlushieStoreMenu().open(player());
	}

	@Path("addOwner <user>")
	@Permission(Group.ADMIN)
	void addOwner(PlayerPlushieUser user) {
		new PlayerPlushieConfigService().edit0(config -> config.addOwner(user.getUuid()));
		send(PREFIX + "Added &e" + user.getNickname() + " &3to plushie users");
		runCommand("rp deploy");
	}

	@Path("vouchers [player]")
	@Description("View how many player plushie vouchers you have")
	void vouchers(@Arg(value = "self", permission = Group.STAFF) PlayerPlushieUser user) {
		if (user.getVouchers().isEmpty())
			error((isSelf(user) ? "You have" : user.getNickname() + " has") + " no vouchers");

		send(PREFIX + (isSelf(user) ? "Your" : user.getNickname() + "'s") + " vouchers:");
		user.getVouchers().forEach((tier, amount) -> send("&3" + camelCase(tier) + " &7- &e" + amount));
		line();
		send(json(PREFIX + "Spend them in &c/playerplushies store").command("/playerplushies store"));
	}

	@Path("vouchers add <tier> <amount> [player]")
	@Permission(Group.ADMIN)
	@Description("Modify a player's vouchers")
	void vouchers_add(Tier tier, int amount, @Arg("self") PlayerPlushieUser user) {
		user.addVouchers(tier, amount);
		userService.save(user);
		send(PREFIX + "Gave &e" + amount + " &3vouchers to &e" + user.getNickname() + "&3. New balance: &e" + user.getVouchers());
	}

	@Path("vouchers remove <tier> <amount> [player]")
	@Permission(Group.ADMIN)
	@Description("Modify a player's vouchers")
	void vouchers_remove(Tier tier, int amount, @Arg("self") PlayerPlushieUser user) {
		user.takeVouchers(tier, amount);
		userService.save(user);
		send(PREFIX + "Removed &e" + amount + " &3vouchers from &e" + user.getNickname() + "&3. New balance: &e" + user.getVouchers());
	}

}
