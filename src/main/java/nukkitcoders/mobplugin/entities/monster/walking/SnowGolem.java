package nukkitcoders.mobplugin.entities.monster.walking;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.projectile.EntitySnowball;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.MobPlugin;
import nukkitcoders.mobplugin.entities.monster.WalkingMonster;
import nukkitcoders.mobplugin.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SnowGolem extends WalkingMonster {

    public static final int NETWORK_ID = 21;

    public SnowGolem(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        setFriendly(true);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.7f;
    }

    @Override
    public float getHeight() {
        return 1.9f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        setMaxHealth(4);
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        return !(creature instanceof Player) && creature.isAlive() && distance <= 60;
    }

    @Override
    public void attackEntity(Entity player) {
        if (attackDelay > 23 && Utils.rand(1, 32) < 4 && distanceSquared(player) <= 55) {
            attackDelay = 0;

            double f = 1.2;
            double yaw = yaw + Utils.rand(-220, 220) / 10;
            double pitch = pitch + Utils.rand(-120, 120) / 10;
            Location location = new Location(x + (-Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 0.5), y + getEyeHeight(),
                    z + (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 0.5), yaw, pitch, level);
            Entity k = MobPlugin.create("Snowball", location, this);
            if (k == null) {
                return;
            }

            EntitySnowball snowball = (EntitySnowball) k;
            snowball.setMotion(new Vector3(-Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f, -Math.sin(Math.toRadians(pitch)) * f * f,
                    Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f));

            Vector3 motion = new Vector3(-Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f, -Math.sin(Math.toRadians(pitch)) * f * f,
                    Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f).multiply(f);
            snowball.setMotion(motion);

            EntityShootBowEvent ev = new EntityShootBowEvent(this, Item.get(Item.ARROW, 0, 1), snowball, f);
            server.getPluginManager().callEvent(ev);

            EntityProjectile projectile = ev.getProjectile();
            if (ev.isCancelled()) {
                projectile.kill();
            } else if (projectile != null) {
                ProjectileLaunchEvent launch = new ProjectileLaunchEvent(projectile);
                server.getPluginManager().callEvent(launch);
                if (launch.isCancelled()) {
                    projectile.kill();
                } else {
                    projectile.spawnToAll();
                    level.addSound(this, Sound.MOB_SNOWGOLEM_SHOOT);
                }
            }
        }
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();
        if (lastDamageCause instanceof EntityDamageByEntityEvent) {
            int snowBall = Utils.rand(0, 16); // drops 0-15 snowballs
            for (int i = 0; i < snowBall; i++) {
                drops.add(Item.get(Item.SNOWBALL, 0, 1));
            }
        }
        return drops.toArray(new Item[drops.size()]);
    }

    @Override
    public int getKillExperience() {
        return 0;
    }

}