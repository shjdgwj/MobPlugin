package nukkitcoders.mobplugin.entities.spawners;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import nukkitcoders.mobplugin.AutoSpawnTask;
import nukkitcoders.mobplugin.entities.autospawn.AbstractEntitySpawner;
import nukkitcoders.mobplugin.entities.autospawn.SpawnResult;
import nukkitcoders.mobplugin.entities.monster.flying.Blaze;

/**
 * @author PikyCZ
 */
public class BlazeSpawner extends AbstractEntitySpawner {

    public BlazeSpawner(AutoSpawnTask spawnTask, Config pluginConfig) {
        super(spawnTask, pluginConfig);
    }

    @Override
    public SpawnResult spawn(Player player, Position pos, Level level) {
        SpawnResult result = SpawnResult.OK;

        int blockId = level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z);
        int biomeId = level.getBiomeId((int) pos.x, (int) pos.z);

        if (Block.transparent[blockId]) {
            result = SpawnResult.WRONG_BLOCK;
        } else if (biomeId != 8) {
            result = SpawnResult.WRONG_BIOME;
        } else if ((pos.y > 255 || (level.getName().equals("nether") && pos.y > 127)) || pos.y < 1 || blockId == Block.AIR) {
            result = SpawnResult.POSITION_MISMATCH;
        } else {
            this.spawnTask.createEntity("Blaze", pos.add(0, 1, 0));
        }

        return result;
    }

    @Override
    public int getEntityNetworkId() {
        return Blaze.NETWORK_ID;
    }
}
