package me.pugabyte.nexus.models.autotorch;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(AutoTorchUser.class)
public class AutoTorchService extends MongoService {
    private final static Map<UUID, AutoTorchUser> cache = new HashMap<>();

    public Map<UUID, AutoTorchUser> getCache() {
        return cache;
    }

}

