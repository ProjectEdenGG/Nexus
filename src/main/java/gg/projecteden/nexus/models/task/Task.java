package gg.projecteden.nexus.models.task;

import com.google.gson.Gson;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class Task {
	@Id
	@GeneratedValue
	private int taskId;
	@NonNull
	private String type;
	@NonNull
	private String data;
	@NonNull
	private LocalDateTime created;
	@NonNull
	private LocalDateTime timestamp;
	private Status status = Status.PENDING;

	public Task(@NonNull String type, @NonNull Map<String, Object> data, @NonNull LocalDateTime timestamp) {
		this.type = type;
		this.data = new Gson().toJson(new HashMap<>(data));
		this.created = LocalDateTime.now();
		this.timestamp = timestamp;
	}

	public Map<String, Object> getJson() {
		return new Gson().fromJson(data, Map.class);
	}

	public enum Status {
		PENDING,
		RUNNING,
		COMPLETED
	}
}
