package gg.projecteden.nexus.models.punishments;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.skincache.SkinCache;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity(value = "skinban_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class SkinBanConfig implements DatabaseObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<UUID> banned = new ArrayList<>();

	public static final Component BAN_MESSAGE = new JsonBuilder("&cYour skin has been banned from this server,")
		.line().next("&cplease change it in order to join").asComponent();

	public boolean isBanned(UUID uuid) {
		return banned.contains(uuid);
	}

	public void ban(UUID uuid) {
		ban(UUIDUtils.UUID0, uuid);
	}

	public void ban(UUID executor, UUID uuid) {
		if (isBanned(uuid))
			throw new InvalidInputException(Nickname.of(uuid) + " is already skin banned");

		banned.add(uuid);
		Punishments.broadcast("&e" + Nickname.of(executor) + " &cskin banned &e" + Nickname.of(uuid) + " &cwith texture &e" + SkinCache.of(uuid).getTextureUrl());
		warn(executor, uuid);

		Player player = Bukkit.getPlayer(uuid);
		if (player != null)
			player.kick(BAN_MESSAGE);
	}

	private void warn(UUID executor, UUID uuid) {
		Punishments.of(uuid).add(Punishment.ofType(PunishmentType.WARN)
				.punisher(executor)
				.input("Your skin is not allowed on this server"));
	}

	public void unban(UUID uuid) {
		unban(UUIDUtils.UUID0, uuid);
	}

	public void unban(UUID executor, UUID uuid) {
		Punishments.broadcast("&e" + Nickname.of(executor) + " &cremoved skin ban of &e" + Nickname.of(uuid));
		banned.remove(uuid);
	}

}
