package gg.projecteden.nexus.features.chat.bridge;

import com.google.gson.Gson;
import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.discord.DiscordId.TextChannel;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import org.apache.commons.io.FileUtils;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Permission(Group.ADMIN)
public class BridgeCommand extends CustomCommand {
	private final DiscordUserService service;

	public BridgeCommand(CommandEvent event) {
		super(event);
		service = new DiscordUserService();
		if (isCommandEvent())
			if (Discord.getGuild() == null)
				error("Not connected to Discord");
	}

	@Path("set <player> <roleId>")
	@Description("Sets a player's linked role ID")
	void set(DiscordUser user, String roleId) {
		user.setRoleId(roleId);
		service.save(user);
		RoleManager.update(user);
		send(user.getIngameName() + "'s bridge role updated");
	}

	@Path("countRoles")
	@Description("Count the total number of roles in the server")
	void countRoles() {
		send(PREFIX + "Found " + Discord.getGuild().getRoles().size() + " roles");
	}

	@Async
	@Path("countBridgeRoles")
	@Description("Count the number of bridge roles in the server")
	void countBridgeRoles() {
		send(PREFIX + "Found " + Discord.getGuild().getRoleById("331279736691228676").getPosition() + " roles");
	}

	@Async
	@Path("updateRoleColors <rank>")
	@Description("Update the color of all roles of a rank")
	void updateRoleColors(Rank rank) {
		int updated = 0;
		for (DiscordUser user : service.getAll()) {
			if (user.getRoleId() == null || user.getUuid() == null)
				continue;

			Rank playerRank = Rank.of(user);
			if (playerRank != rank)
				continue;

			net.dv8tion.jda.api.entities.Role role = Discord.getGuild().getRoleById(user.getRoleId());
			if (role == null)
				continue;

			role.getManager().setColor(rank.getDiscordColor()).queue();
			++updated;
		}

		send("Updated " + updated + " roles");
	}

	@Async
	@Path("setMentionableFalse [test]")
	@Description("Set the mentionable state of all bridge roles")
	void setMentionableFalse(boolean test) {
		int startingPosition = Discord.getGuild().getRoleById("331279736691228676").getPosition();
		int count = 0;
		for (Role role : Discord.getGuild().getRoles()) {
			if (role.getPosition() <= startingPosition && role.isMentionable()) {
				++count;
				if (!test)
					role.getManager().setMentionable(false).queue();
			}
		}

		send(PREFIX + (test ? "Will update" : "Updated") + " " + count + " roles");
	}

	private static BridgeArchive archive;
	private static BridgeChannel loadedChannel;

	@Getter
	@AllArgsConstructor
	private enum BridgeChannel {
		BRIDGE(TextChannel.BRIDGE),
		STAFF_BRIDGE(TextChannel.STAFF_BRIDGE),
		OPS_BRIDGE(TextChannel.ARCHIVED_OPS_BRIDGE),
		OPERATORS(TextChannel.STAFF_OPERATORS),
		ADMINS(TextChannel.STAFF_ADMINS);

		private final TextChannel textChannel;

		private GuildMessageChannel getTextChannel(Bot bot) {
			return getTextChannel().get(bot.jda());
		}
	}

	@Async
	@SneakyThrows
	@Path("archive load <channel>")
	@Description("Load an archive into memory")
	void archive_load(BridgeChannel channel) {
		loadedChannel = channel;
		String data = "{\"roleMap\":" + FileUtils.readFileToString(IOUtils.getPluginFile("role-archives/" + channel.getTextChannel().getId() + ".json")) + "}";
		archive = new Gson().fromJson(data, BridgeArchive.class);
		send(PREFIX + "Loaded " + archive.getRoleMap().size() + " roles from the archive");
	}

	@Async
	@Path("archive leastUsedRoles [page]")
	@Description("View least used roles in an archive")
	void archive_leastUsedRoles(@Arg("1") int page) {
		if (archive == null) error("No archive loaded");

		BiFunction<String, String, JsonBuilder> formatter = (roleId, index) -> {
			Role role = Discord.getGuild().getRoleById(roleId);
			DiscordUser user = new DiscordUserService().getFromRoleId(roleId);
			boolean tied = user != null;
			String name = role == null ? roleId : user == null ? role.getName() : user.getIngameName();
			int size = archive.getRoleMap().get(roleId).size();
			return json(index + " " + (tied ? "&e" : "&c") + name + " &7- " + size + " messages")
					.insert(roleId)
					.hover("Shift+Click to insert");
		};

		Set<String> values = Utils.sortByValue(new HashMap<String, Integer>() {{
			archive.getRoleMap().forEach((k, v) -> put(k, v.size()));
		}}).keySet();

		new Paginator<String>()
			.values(values)
			.formatter(formatter)
			.command("/bridge archive leastUsedRoles")
			.page(page)
			.send();
	}

	@Async
	@Path("archive editMessages removeReference <roleId> [name]")
	@Description("Edit messages to remove role references")
	void archive_editMessages_removeReference(String roleId, String name) {
		if (archive == null) error("No archive loaded");

		DiscordUser user = new DiscordUserService().getFromRoleId(roleId);
		if (name == null)
			if (user == null)
				error("Role is not tied to a user, you must provide the name to use");
			else
				name = Nickname.of(user);

		List<String> messageIds = archive.getRoleMap().get(roleId);
		send(PREFIX + "Editing " + messageIds.size() + " messages for user " + name);
		for (String messageId : messageIds)
			updateRoleMention(roleId, "**" + name + "**", messageId);

		send(json(PREFIX + "Done. Click here to remove the role").command("/bridge archive deleteRole " + roleId));
	}

	@Async
	@Path("archive editMessages updateReference <oldRoleId> <newRoleId>")
	@Description("Edit messages to update role references")
	void archive_editMessages_updateReference(String oldRoleId, String newRoleId) {
		if (archive == null) error("No archive loaded");

		List<String> messageIds = archive.getRoleMap().get(oldRoleId);
		send(PREFIX + "Editing " + messageIds.size() + " messages");
		for (String messageId : messageIds)
			updateRoleMention(oldRoleId, "<@&" + newRoleId + ">", messageId);

		send(json(PREFIX + "Done. Click here to remove the old role").command("/bridge archive deleteRole " + oldRoleId));
	}

	@Async
	@Confirm
	@Path("archive deleteRole <roleId>")
	@Description("Delete a role")
	void archive_deleteRole(String roleId) {
		Discord.getGuild().getRoleById(roleId).delete().queue(success -> send(PREFIX + "Deleted"), this::rethrow);
	}

	@Async
	@Path("archive findDuplicateRoles [page]")
	@Description("Find duplicate roles")
	void archive_findDuplicateRoles(@Arg("1") int page) {
		Map<UUID, List<String>> duplicates = new HashMap<>() {{
			for (String roleId : archive.getRoleMap().keySet()) {
				Role role = Discord.getGuild().getRoleById(roleId);
				DiscordUser user = new DiscordUserService().getFromRoleId(roleId);
				String name = user == null ? role == null ? null : role.getName() : user.getIngameName();
				if (!Nullables.isNullOrEmpty(name)) {
					UUID uuid = PlayerUtils.getPlayer(name).getUniqueId();
					List<String> roleIds = getOrDefault(uuid, new ArrayList<>());
					roleIds.add(roleId);
					put(uuid, roleIds);
				}
			}
		}};

		for (UUID uuid : new HashSet<>(duplicates.keySet())) {
			if (duplicates.get(uuid).size() == 1)
				duplicates.remove(uuid);
		}

		BiFunction<UUID, String, JsonBuilder> formatter = (uuid, index) -> {
			OfflinePlayer player = PlayerUtils.getPlayer(uuid);
			int size = duplicates.get(uuid).size();
			JsonBuilder json = json(index + " &e" + Nickname.of(player) + " &7- " + size + " roles")
					.newline();

			for (String roleId : duplicates.get(uuid))
				json.next("    &7" + roleId + " - " + archive.getRoleMap().get(roleId).size() + " messages")
						.newline();

			return json;
		};

		new Paginator<UUID>()
			.values(Utils.sortByValue(new HashMap<UUID, Integer>() {{
			duplicates.forEach((k, v) -> put(k, v.size()));
		}}).keySet())
			.formatter(formatter)
			.command("/bridge archive findDuplicateRoles")
			.page(page)
			.send();
	}


	private void executeOnMessage(String messageId, Consumer<Message> consumer) {
		Discord.executeOnMessage(loadedChannel.getTextChannel().getId(), messageId, consumer);
	}

	private void updateRoleMention(String oldRoleId, String replacement, String messageId) {
		executeOnMessage(messageId, message -> {
			String oldContent = message.getContentRaw();
			String newContent = oldContent.replaceFirst("<@&" + oldRoleId + ">", replacement);
			if (oldContent.equals(newContent))
				return;

			message.editMessage(new MessageBuilder(message)
					.setContent(newContent)
					.build()
			).queue();
		});
	}

	@Data
	private static class BridgeArchive {
		private Map<String, List<String>> roleMap;
	}

}
