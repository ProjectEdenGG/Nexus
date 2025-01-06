package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.modreview.ModReview;
import gg.projecteden.nexus.models.modreview.ModReview.Mod;
import gg.projecteden.nexus.models.modreview.ModReview.Mod.ModVerdict;
import gg.projecteden.nexus.models.modreview.ModReview.ModReviewRequest;
import gg.projecteden.nexus.models.modreview.ModReviewService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@NoArgsConstructor
@Aliases({"modcheck", "checkmod"})
@Description("A list of client-side modifications that have been reviewed by the staff team")
public class ModReviewCommand extends CustomCommand implements Listener {
	private final ModReviewService service = new ModReviewService();
	private final ModReview modReview = service.get0();
	private final List<Mod> mods = modReview.getMods();
	private final List<ModReviewRequest> requests = modReview.getRequests();

	public ModReviewCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	@Description("View all reviewed mods")
	void list() {
		list(1);
	}

	@Path("<mod>")
	@Description("View whether a mod is allowed on the server")
	void check(Mod mod) {
		line();
		send(PREFIX + "&e" + mod.getName());
		if (mod.getAliases().size() > 0)
			send(" &3Also known as: &7" + String.join(", ", mod.getAliases()));
		send(" &3Verdict: " + mod.getVerdict().getColor() + camelCase(mod.getVerdict()));
		if (!Nullables.isNullOrEmpty(mod.getNotes()))
			send(" &3Notes: &7" + mod.getNotes());
		line();
	}

	@Path("list [page]")
	@Description("List reviewed mods")
	void list(@Arg("1") int page) {
		if (mods.isEmpty())
			error("No available mod reviews");

		if (page == 1)
			send(PREFIX + "List of reviewed mods. Click on a mod for more info");

		BiFunction<Mod, String, JsonBuilder> formatter = (mod, index) -> json()
				.next(index + " &e" + mod.getName() + " &7- " + mod.getVerdict().getColor() + camelCase(mod.getVerdict()))
				.command("/modreview " + mod.getName())
				.hover("&3Click for more info");

		paginate(mods, formatter, "/modreview list", page);

		if (page == 1)
			send(PREFIX + "&3If your mod is not on this list, request it to be reviewed with &c/modreview request <name> [notes...]");
	}

	@Cooldown(value = TickTime.SECOND, x = 30)
	@Path("request <name> [notes...]")
	@Description("Request a mod to be reviewed by the staff team")
	void request(String name, String notes) {
		ModReviewRequest request = new ModReviewRequest(uuid(), name, notes);
		modReview.request(request);
		save();
		send(PREFIX + "Requested mod &e" + name + " &3to be reviewed");
		String message = "&e" + name() + " &3has requested mod &e" + name + " &3to be reviewed";
		Broadcast.staff().prefix("ModReview").message(json(message).command("/modreview requests")).send();
	}

	@Permission(Group.STAFF)
	@Path("requests [page]")
	@Description("View pending review requests")
	void requests(@Arg("1") int page) {
		if (requests.isEmpty())
			error("No pending review requests");

		BiFunction<ModReviewRequest, String, JsonBuilder> formatter = (request, index) -> {
			JsonBuilder json = json(index + " &3" + PlayerUtils.getPlayer(request.getRequester()).getName() + " &e" + request.getName() +
					(Nullables.isNullOrEmpty(request.getNotes()) ? "" : " &7- " + request.getNotes()));
			if (isAdmin())
				json.suggest("/modreview add " + request.getName() + " ").hover("&3Click to review");
			return json;
		};

		paginate(requests, formatter, "/modreview requests", page);
	}

	@Confirm
	@Permission(Group.ADMIN)
	@Path("requests remove <mod>")
	@Description("Delete a pending review request")
	void removeRequest(ModReviewRequest request) {
		requests.remove(request);
		save();
		send(PREFIX + "Removed request for mod &e" + request.getName());
	}

	@Permission(Group.ADMIN)
	@Path("add <name> <verdict> [notes...]")
	@Description("Review a mod and delete its review request if applicable")
	void add(String name, ModVerdict verdict, String notes) {
		Mod mod = new Mod(name, verdict, notes);
		modReview.add(mod);
		save();
		send(PREFIX + "Added mod &e" + mod.getName());
	}

	@Permission(Group.ADMIN)
	@Path("alias add <mod> <aliases...>")
	@Description("Add an alias to a mod")
	void addAliases(Mod mod, @Arg(type = String.class) List<String> aliases) {
		mod.getAliases().addAll(aliases);
		save();
		send(PREFIX + "Added aliases to mod &e" + mod.getName());
	}

	@Permission(Group.ADMIN)
	@Path("alias remove <mod> <aliases...>")
	@Description("Remove an alias from a mod")
	void removeAliases(Mod mod, @Arg(type = String.class) List<String> aliases) {
		mod.getAliases().removeAll(aliases);
		save();
		send(PREFIX + "Removed aliases to mod &e" + mod.getName());
	}

	@Permission(Group.ADMIN)
	@Path("set name <mod> <name>")
	@Description("Set the name of a mod")
	void setName(Mod mod, String name) {
		mod.setName(name);
		save();
		send(PREFIX + "Name updated for mod &e" + mod.getName());
	}

	@Permission(Group.ADMIN)
	@Path("set verdict <mod> <verdict>")
	@Description("Set the verdict of a mod")
	void setVerdict(Mod mod, ModVerdict verdict) {
		mod.setVerdict(verdict);
		save();
		send(PREFIX + "Verdict updated for mod &e" + mod.getName());
	}

	@Permission(Group.ADMIN)
	@Path("set notes <mod> [notes...]")
	@Description("Set the notes for a mod")
	void setNotes(Mod mod, String notes) {
		mod.setNotes(notes);
		save();
		send(PREFIX + "Notes updated for mod &e" + mod.getName() + "&3: " + mod.getNotes());
	}

	@Confirm
	@Permission(Group.ADMIN)
	@Path("delete <mod>")
	@Description("Delete a mod review")
	void delete(Mod mod) {
		modReview.getMods().remove(mod);
		save();
		send(PREFIX + "Deleted mod &e" + mod.getName());
	}

	private void save() {
		service.save(modReview);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!Rank.of(event.getPlayer()).isAdmin())
			return;

		ModReview modReview = new ModReviewService().get0();
		if (modReview.getRequests().isEmpty())
			return;

		PlayerUtils.send(event.getPlayer(), PREFIX + "&c&lThere are "
				+ modReview.getRequests().size() + " mod review requests pending");
	}

	@ConverterFor(Mod.class)
	Mod convertToMod(String value) {
		return modReview.findMatch(value).orElseThrow(() -> new InvalidInputException("Mod &e" + value +" &cnot found"));
	}

	@TabCompleterFor(Mod.class)
	List<String> tabCompleteMod(String filter) {
		return new HashSet<String>() {{
			mods.forEach(mod -> {
				add(mod.getName());
				addAll(mod.getAliases());
			});
		}}.stream()
				.filter(mod -> mod.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}

	@ConverterFor(ModReviewRequest.class)
	ModReviewRequest convertToModReviewRequest(String value) {
		return modReview.findRequestMatch(value).orElseThrow(() -> new InvalidInputException("Mod review request &e" + value +" &cnot found"));
	}

	@TabCompleterFor(ModReviewRequest.class)
	List<String> tabCompleteModReviewRequest(String filter) {
		return requests.stream()
				.map(ModReviewRequest::getName)
				.filter(request -> request.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}

}
