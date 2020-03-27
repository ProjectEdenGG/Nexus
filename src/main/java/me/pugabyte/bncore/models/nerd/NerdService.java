package me.pugabyte.bncore.models.nerd;

import com.google.common.base.Strings;
import me.pugabyte.bncore.models.MySQLService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// TODO: Support past names
public class NerdService extends MySQLService {
	@Override
	public Nerd get(String uuid) {
		Nerd nerd = database.where("uuid = ?", uuid).first(Nerd.class);
		nerd.fromPlayer(Utils.getPlayer(uuid));
		return nerd;
	}

	public List<Nerd> find(String partialName) {
		return find(partialName, "name");
	}

	public List<Nerd> find(String partialName, String column) {
		List<Nerd> nerds = database
				.select("nerd.*")
				.table("nerd")
				.leftJoin("hours")
					.on("hours.uuid = nerd.uuid")
				.where("nerd." + sanitize(column) + " like ?")
				.orderBy("position(? in name), hours.total desc")
				.args("%" + partialName + "%", partialName)
				.results(Nerd.class);
		for (Nerd nerd : nerds)
			nerd.fromPlayer(Utils.getPlayer(nerd.getUuid()));
		return nerds;
	}

	public List<Nerd> getOnlineNerds() {
		List<Nerd> nerds = database.where("uuid in ?", asList(Utils.getOnlineUuids())).results(Nerd.class);
		for (Nerd nerd : nerds)
			nerd.fromPlayer(Utils.getPlayer(nerd.getUuid()));
		return nerds;
	}

	public List<Nerd> getOnlineNerdsWith(String permission) {
		List<String> filtered = Bukkit.getOnlinePlayers().stream()
				.filter(player -> player.hasPermission(permission))
				.map(player -> player.getUniqueId().toString())
				.collect(Collectors.toList());

		List<Nerd> nerds = database.where("uuid in (" + asList(filtered) + ")").results(Nerd.class);
		for (Nerd nerd : nerds)
			nerd.fromPlayer(Utils.getPlayer(nerd.getUuid()));
		return nerds;
	}

	public List<Nerd> getNerdsWithBirthdays() {
		List<Nerd> nerds = database.where("birthday IS NOT NULL").results(Nerd.class);
		for (Nerd nerd : nerds)
			nerd.fromPlayer(Utils.getPlayer(nerd.getUuid()));
		return nerds;
	}

	public OfflinePlayer getFromNickname(String nickname) {
		String uuid = database.select("uuid").table("nickname").where("nickname = ?", nickname).first(String.class);
		if (!Strings.isNullOrEmpty(uuid))
			return Utils.getPlayer(uuid);
		return null;
	}

	public List<String> getNicknames(UUID uuid) {
		return database.select("nickname").table("nickname").where("uuid = ?", uuid.toString()).results(String.class);
	}

	public void addNickname(UUID uuid, String nickname) {
		Tasks.async(() ->
				database.sql("insert into nickname values (?, ?)", nickname, uuid.toString()).execute());
	}

	public void removeNickname(UUID uuid, String nickname) {
		database.where("uuid = ?, nickname = ?", uuid.toString(), nickname).delete();
	}

	public void addPastName(Player player) {
		Tasks.async(() ->
				database.sql("insert ignore into name_history values (?, ?)").args(player.getName(), player.getUniqueId().toString()).execute());
	}

	public List<String> getPastNames(UUID uuid) {
		return database.select("name").table("name_history").where("uuid = ?", uuid.toString()).results(String.class);
	}

}
