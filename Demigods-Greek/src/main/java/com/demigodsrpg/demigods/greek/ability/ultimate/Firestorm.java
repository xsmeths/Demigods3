package com.demigodsrpg.demigods.greek.ability.ultimate;

import com.demigodsrpg.demigods.engine.DemigodsPlugin;
import com.demigodsrpg.demigods.engine.battle.Battle;
import com.demigodsrpg.demigods.engine.entity.player.DemigodsCharacter;
import com.demigodsrpg.demigods.engine.entity.player.attribute.Skill;
import com.demigodsrpg.demigods.greek.ability.GreekAbility;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Firestorm extends GreekAbility {
    private static final String NAME = "Firestorm", COMMAND = "firestorm";
    private static final int COST = 5500, DELAY = 300, REPEAT = 0;
    private static final List<String> DETAILS = Lists.newArrayList("Rain fireballs from the sky.");
    private static final Skill.Type TYPE = Skill.Type.ULTIMATE;

    public Firestorm(String deity) {
        super(NAME, COMMAND, deity, COST, DELAY, REPEAT, DETAILS, TYPE, null, new Predicate<Player>() {
            @Override
            public boolean apply(final Player player) {
                // Define variables
                DemigodsCharacter character = DemigodsCharacter.of(player);

                // Define variables
                int ultimateSkillLevel = character.getMeta().getSkill(Skill.Type.ULTIMATE).getLevel();
                int total = 3 * (int) Math.pow(ultimateSkillLevel, 0.25);
                int radius = (int) Math.log10(10 * ultimateSkillLevel) * 25;

                List<LivingEntity> entities = new ArrayList<>();

                for (final Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                    // Validate them first
                    if (!(entity instanceof LivingEntity)) continue;
                    if (entity instanceof Player) {
                        DemigodsCharacter opponent = DemigodsCharacter.of((Player) entity);
                        if (opponent != null && character.alliedTo(opponent)) continue;
                    }
                    if (Battle.canParticipate(entity) && !Battle.canTarget(Battle.defineParticipant(entity))) continue;

                    entities.add((LivingEntity) entity);
                }

                final List<LivingEntity> targets = Lists.newArrayList(entities);

                // Now shoot them
                for (int i = 0; i <= total; i++)
                    shootFireball(player, targets, i);

                return true;
            }
        }, null, null);
    }

    public static void shootFireball(final Player shooter, final Collection<LivingEntity> targets, int delay) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(DemigodsPlugin.getInst(), new BukkitRunnable() {
            @Override
            public void run() {
                for (final LivingEntity target : targets) {
                    // Skip if they died, reduces lag
                    if (target.isDead()) continue;

                    // Shoot 'em up!
                    Location ground = target.getLocation();
                    Location air = new Location(ground.getWorld(), ground.getX(), ground.getY() + 20, ground.getZ());

                    org.bukkit.entity.Fireball fireball = (org.bukkit.entity.Fireball) target.getWorld().spawnEntity(air, EntityType.FIREBALL);
                    fireball.setShooter(shooter);
                    fireball.getDirection().zero();
                    fireball.setIsIncendiary(true);
                    fireball.setBounce(false);
                    fireball.setYield(0F);
                    Vector path = ground.toVector().subtract(air.toVector());
                    fireball.setDirection(path.multiply(2));
                    fireball.setVelocity(fireball.getDirection().multiply(3));
                }
            }
        }, delay * 20);
    }
}
