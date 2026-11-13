package tallyismanythings.meliussupport.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.WorldChunk;
import tallyismanythings.meliussupport.MeliusSupport;
import tallyismanythings.meliussupport.data.CropSoilHandler;
import tallyismanythings.meliussupport.data.MeliusDataResources;

import java.util.*;

public class ServerEvents {
    public static int DAYS = 0;
    public static HashMap<WorldChunk, Integer> chunkTimer = new HashMap<>();
    public static List<WorldChunk> loadedChunks = new ArrayList<>();

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(ServerEvents::endServerTick);

        ServerChunkEvents.CHUNK_LOAD.register(ServerEvents::loadChunk);
        ServerChunkEvents.CHUNK_UNLOAD.register(ServerEvents::unloadChunk);

        ServerWorldEvents.LOAD.register(ServerEvents::loadEvent);
    }

    private static void loadEvent(MinecraftServer minecraftServer, ServerWorld serverWorld) {
    }

    private static void endServerTick(MinecraftServer minecraftServer) {
        Integer newDay = (int) (minecraftServer.getOverworld().getTimeOfDay() / 24000L);
        if (DAYS != newDay) {
            DAYS = newDay;
            newDayChunkUpdate(minecraftServer);
        }

        HashMap<WorldChunk, Integer> useMap = (HashMap<WorldChunk, Integer>) chunkTimer.clone();
        for (WorldChunk key : useMap.keySet()) {
            Integer time = useMap.get(key);
            if (time > 1) {
                chunkTimer.put(key, time - 1);
            } else {
                chunkTimer.remove(key);
                key.setAttached(MeliusSupport.PERSISTENT_DAY_TRACKER, DAYS);
            }
        }
    }

    private static void newDayChunkUpdate(MinecraftServer minecraftServer) {
        List<WorldChunk> processChunks = new ArrayList<>(loadedChunks);
        for (WorldChunk chunk : processChunks) {
            chunk.setAttached(MeliusSupport.PERSISTENT_DAY_TRACKER, DAYS);
        }
    }

    private static void unloadChunk(ServerWorld serverWorld, WorldChunk worldChunk) {
        loadedChunks.removeAll(Collections.singleton(worldChunk));
        worldChunk.setAttached(MeliusSupport.PERSISTENT_DAY_TRACKER, DAYS);
    }

    private static void loadChunk(ServerWorld serverWorld, WorldChunk worldChunk) {
        loadedChunks.removeAll(Collections.singleton(worldChunk));
        loadedChunks.add(worldChunk);
        chunkTimer.put(worldChunk, MeliusSupport.Config.farmRefreshRate);
    }
}
