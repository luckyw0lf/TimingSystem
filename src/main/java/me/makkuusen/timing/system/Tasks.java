package me.makkuusen.timing.system;

import com.sk89q.worldedit.math.BlockVector2;
import me.makkuusen.timing.system.track.Track;
import me.makkuusen.timing.system.track.TrackPolyRegion;
import me.makkuusen.timing.system.track.TrackRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.UUID;

public class Tasks {

    public Tasks(TimingSystem plugin) {

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

        scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                for (UUID uuid : TimingSystem.playerEditingSession.keySet()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null) continue;
                    Track track = TimingSystem.playerEditingSession.get(uuid);

                    track.getRegions().stream().forEach(trackRegion -> setParticles(player, trackRegion));
                    track.getGridLocations().values().forEach(location -> setParticles(player, location, Particle.WAX_OFF));
                }
            }
        }, 0, 10);
    }
    private void setParticles(Player player, Location location, Particle particle) {
        player.spawnParticle(particle, location,5);
    }

    private void setParticles(Player player, TrackRegion region) {

        if (!region.isDefined()) {
            return;
        }
        Particle particle;

        if (region.getRegionType().equals(TrackRegion.RegionType.CHECKPOINT)) {
            particle = Particle.GLOW;
        } else if (region.getRegionType().equals(TrackRegion.RegionType.RESET)) {
            particle = Particle.WAX_ON;
        } else if (region.getRegionType().equals(TrackRegion.RegionType.START)) {
            particle = Particle.VILLAGER_HAPPY;
        } else if (region.getRegionType().equals(TrackRegion.RegionType.END)) {
            particle = Particle.VILLAGER_ANGRY;
        } else if (region.getRegionType().equals(TrackRegion.RegionType.PIT)) {
            particle = Particle.HEART;
        } else if (region.getRegionType().equals(TrackRegion.RegionType.INPIT)) {
            particle = Particle.SPELL_WITCH;
        }else {
            particle = Particle.WAX_OFF;
        }


        Location min = region.getMinP();
        Location max = region.getMaxP();

        int maxY = max.getBlockY() + 1;
        int maxX = max.getBlockX() + 1;
        int maxZ = max.getBlockZ() + 1;


        if (region instanceof TrackPolyRegion polyRegion) {
            for (BlockVector2 point : polyRegion.getPolygonal2DRegion().getPoints()) {
                var loc = new Location(region.getSpawnLocation().getWorld(), point.getBlockX(), maxY, point.getBlockZ());
                setParticles(player, loc, particle);
                loc.setY(min.getBlockY());
                setParticles(player, loc, particle);
            }
        } else {
            drawLineX(player, particle, min.getBlockX(), maxX, min.getBlockY(), min.getBlockZ());
            drawLineX(player, particle, min.getBlockX(), maxX, maxY, min.getBlockZ());
            drawLineX(player, particle, min.getBlockX(), maxX, min.getBlockY(), maxZ);
            drawLineX(player, particle, min.getBlockX(), maxX, maxY, maxZ);

            drawLineY(player, particle, min.getBlockX(), min.getBlockY(), maxY, min.getBlockZ());
            drawLineY(player, particle, min.getBlockX(), min.getBlockY(), maxY, maxZ);
            drawLineY(player, particle, maxX, min.getBlockY(), maxY, min.getBlockZ());
            drawLineY(player, particle, maxX, min.getBlockY(), maxY, maxZ);

            drawLineZ(player, particle, min.getBlockX(), min.getBlockY(), min.getBlockZ(), maxZ);
            drawLineZ(player, particle, min.getBlockX(), maxY, min.getBlockZ(), maxZ);
            drawLineZ(player, particle, maxX, min.getBlockY(), min.getBlockZ(), maxZ);
            drawLineZ(player, particle, maxX, maxY, min.getBlockZ(), maxZ);
        }

    }

    private void drawLineX(Player player, Particle particle, int x1, int x2, int y, int z){
        for (int x = x1; x <= x2; x++){
            player.spawnParticle(particle, x, y, z, 1);
        }
    }
    private void drawLineY(Player player, Particle particle, int x, int y1, int y2, int z){
        for (int y = y1; y <= y2; y++){
            player.spawnParticle(particle, x, y, z, 1);
        }
    }
    private void drawLineZ(Player player, Particle particle, int x, int y, int z1, int z2){
        for (int z = z1; z <= z2; z++){
            player.spawnParticle(particle, x, y, z, 1);
        }
    }
}


