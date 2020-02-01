package me.pugabyte.bncore.models.discord;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "discord_user")
public class DiscordUser {
	private String uuid;
	private String userId;
	private String roleId;
}
