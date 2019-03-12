package me.pugabyte.bncore.models.nerds;

import lombok.Data;
import lombok.NonNull;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Votes {
	private List<Vote> votes;

}
