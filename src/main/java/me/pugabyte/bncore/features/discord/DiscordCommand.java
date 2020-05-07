package me.pugabyte.bncore.features.discord;

import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.discord.DiscordService;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.utils.Tasks;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class DiscordCommand extends CustomCommand {

	DiscordService service = new DiscordService();
	DiscordUser user;

	public DiscordCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayer())
			user = service.get(player());
	}

	@Path
	void run() {
		send("&3Join our discord to stay up to date with the community");
		send("&e" + Discord.getUrl());
	}

	@Path("link update roles")
	@Permission("group.seniorstaff")
	void updateRoles() {
		Tasks.async(() -> {
			Role verified = Discord.getGuild().getRoleById(DiscordId.Role.VERIFIED.getId());
			new DiscordService().getAll().stream().filter(discordUser -> !isNullOrEmpty(discordUser.getUserId())).forEach(discordUser -> {
				Member member = Discord.getGuild().getMemberById(discordUser.getUserId());
				if (member == null) return;
				if (!member.getRoles().contains(verified))
					Discord.addRole(discordUser.getUserId(), DiscordId.Role.VERIFIED);
			});
		});
	}

	@Path("link [code]")
	void link(String code) {
		if (isNullOrEmpty(code)) {
			if (!isNullOrEmpty(user.getUserId())) {
				User userById = Bot.KODA.jda().getUserById(user.getUserId());
				if (userById == null)
					send(PREFIX + "Your minecraft account is linked to a Discord account, but I could not find that account. " +
							"Are you in our discord server? &e" + Discord.getUrl());
				else
					send(PREFIX + "Your minecraft account is linked to " + user.getName());
				send(PREFIX + "You can unlink your account with &c/discord unlink");
				return;
			} else {
				send(PREFIX + "Hello! Looking to &elink &3your &eDiscord &3and &eMinecraft &3accounts? Here's how:");
				line();
				send("&3Step 1: &eOpen our Discord server");
				send("&3Step 2: Type &c/discord link " + player().getName() + " &3in any channel");
				send("&3Step 3: &eCopy the command &3that appears in your &eDMs &3and &epaste it &3into Minecraft");
			}
		} else {
			if (Discord.getCodes().containsKey(code)) {
				DiscordUser newUser = Discord.getCodes().get(code);
				if (!player().getUniqueId().toString().equals(newUser.getUuid()))
					error("There is no pending confirmation with this account");

				String name = newUser.getName();
				String discrim = newUser.getDiscrim();
				Bot.KODA.jda().getUserById(newUser.getUserId()).openPrivateChannel().complete().sendMessage("You have successfully linked your Discord account with the Minecraft account **" + player().getName() + "**").queue();
				send(PREFIX + "You have successfully linked your Minecraft account with the Discord account &e" + name + "#" + discrim);
				Discord.addRole(newUser.getUserId(), DiscordId.Role.VERIFIED);
				user.setUserId(newUser.getUserId());
				service.save(user);
				Discord.staffLog("**" + player().getName() + "** has linked their discord account to **" + name + "#" + discrim + "**");
			} else
				error("Invalid confirmation code");
		}
	}

	@Path("unlink")
	void unlink() {
		if (isNullOrEmpty(user.getUserId()))
			error("This account is not linked to any Discord account");

		User userById = Bot.KODA.jda().getUserById(user.getUserId());
		String name = user.getName();
		String discrim = user.getDiscrim();

		user.setUserId(null);
		service.save(user);

		userById.openPrivateChannel().complete().sendMessage("This Discord account has been unlinked from the Minecraft account **" + player().getName() + "**").queue();
		send(PREFIX + "Successfully unlinked this Minecraft account from Discord account " + name);
		Discord.staffLog("**" + player().getName() + "** has unlinked their account from **" + name + "#" + discrim + "**");
	}

	@Path("connect")
	@Permission("group.staff")
	void connect() {
		BNCore.discord.connect();
	}

}
