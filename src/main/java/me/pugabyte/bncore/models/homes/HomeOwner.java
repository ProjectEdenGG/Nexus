package me.pugabyte.bncore.models.homes;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HomeOwner {
	private String uuid;
	private List<Home> homes;
	private List<String> fullAccessList;

	enum PermissionType {
		ALLOW,
		ALLOW_ALL
	}

}
