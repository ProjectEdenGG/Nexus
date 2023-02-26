package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nerd.NBTPlayer;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Nerd.StaffMember;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static gg.projecteden.api.common.utils.TimeUtils.shortDateFormat;
import static gg.projecteden.api.common.utils.TimeUtils.shortDateTimeFormat;
import static gg.projecteden.nexus.utils.StringUtils.paste;

@HideFromWiki
public class NerdCommand extends CustomCommand {

	public NerdCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void nou() {
		send("no u");
	}

	@Path("setFirstJoin <player> <date>")
	@Permission(Group.ADMIN)
	void setFirstJoin(Nerd nerd, LocalDateTime firstJoin) {
		new NerdService().edit(nerd, _nerd -> _nerd.setFirstJoin(firstJoin));
		send(PREFIX + "Set " + nerd.getNickname() + "'s first join date to &e" + shortDateTimeFormat(nerd.getFirstJoin()));
	}

	@Path("setPromotionDate <player> <date>")
	@Permission(Group.ADMIN)
	void setPromotionDate(Nerd nerd, LocalDate promotionDate) {
		new NerdService().edit(nerd, _nerd -> _nerd.setPromotionDate(promotionDate));
		send(PREFIX + "Set " + nerd.getNickname() + "'s promotion date to &e" + shortDateFormat(nerd.getPromotionDate()));
	}

	@Async
	@Path("getDataFile [player]")
	@Permission(Group.ADMIN)
	void getDataFile(@Arg("self") Nerd nerd) {
		send(json().next(paste(new NBTPlayer(nerd).getNbtFile().asNBTString())));
	}

	@ConverterFor(Nerd.class)
	Nerd convertToNerd(String value) {
		return Nerd.of(convertToOfflinePlayer(value));
	}

	@ConverterFor(StaffMember.class)
	StaffMember convertToStaffMember(String value) {
		OfflinePlayer player = convertToOfflinePlayer(value);
		if (!Rank.of(player).isStaff())
			error(Nickname.of(player) + " is not staff");
		return new StaffMember(player.getUniqueId());
	}

	@TabCompleterFor(StaffMember.class)
	List<String> tabCompleteStaffMember(String filter) {
		return new HoursService().getActivePlayers().stream()
			.filter(player -> Rank.of(player).isStaff())
			.map(PlayerUtils::getPlayer)
			.map(Nickname::of)
			.filter(name -> name.toLowerCase().startsWith(filter.toLowerCase()))
			.collect(Collectors.toList());
	}

}
