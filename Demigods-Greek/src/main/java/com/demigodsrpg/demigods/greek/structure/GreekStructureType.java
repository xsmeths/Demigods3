package com.demigodsrpg.demigods.greek.structure;

import com.demigodsrpg.demigods.engine.entity.player.DemigodsCharacter;
import com.demigodsrpg.demigods.engine.structure.DemigodsStructure;
import com.demigodsrpg.demigods.engine.structure.DemigodsStructureType;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Set;

public class GreekStructureType implements DemigodsStructureType {
    private String name;
    private Design[] designs;
    private Function<Location, Design> getDesign;
    private Function<Design, DemigodsStructure> createNew;
    private InteractFunction<Boolean> sanctify, corrupt, birth, kill;
    private Set<DemigodsStructureType.Flag> flags;
    private Listener listener;
    private int radius, generationPoints;
    private Predicate<Player> allowed;
    private float sanctity, sanctityRegen;

    public GreekStructureType(String name, Design[] designs, Function<Location, Design> getDesign, Function<Design, DemigodsStructure> createNew, InteractFunction<Boolean> sanctify, InteractFunction<Boolean> corrupt, InteractFunction<Boolean> birth, InteractFunction<Boolean> kill, Set<DemigodsStructureType.Flag> flags, Listener listener, int radius, Predicate<Player> allowed, float sanctity, float sanctityRegen, int generationPoints) {
        this.name = name;
        this.designs = designs.clone();
        this.getDesign = getDesign;
        this.createNew = createNew;
        this.sanctify = sanctify;
        this.corrupt = corrupt;
        this.birth = birth;
        this.kill = kill;
        this.flags = flags;
        this.listener = listener;
        this.radius = radius;
        this.allowed = allowed;
        this.sanctity = sanctity;
        this.sanctityRegen = sanctityRegen;
        this.generationPoints = generationPoints;
    }

    public String getName() {
        return name;
    }

    public Design getDesign(final String name) {
        try {
            return Iterables.find(Sets.newHashSet(designs), new Predicate<Design>() {
                @Override
                public boolean apply(Design design) {
                    return design.getName().equalsIgnoreCase(name);
                }
            });
        } catch (NoSuchElementException ignored) {
            ignored.printStackTrace();
        }
        return null;
    }

    public Set<Design> getDesigns() {
        return Sets.newHashSet(designs);
    }

    public Set<DemigodsStructureType.Flag> getFlags() {
        return flags;
    }

    public Listener getUniqueListener() {
        return listener;
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public int getRequiredGenerationCoords() {
        return generationPoints;
    }

    @Override
    public boolean isAllowed(DemigodsStructure unused, Player player) {
        return allowed.apply(player);
    }

    public boolean sanctify(DemigodsStructure data, DemigodsCharacter demigodsCharacter) {
        return sanctify.apply(data, demigodsCharacter);
    }

    public boolean corrupt(DemigodsStructure data, DemigodsCharacter demigodsCharacter) {
        return corrupt.apply(data, demigodsCharacter);
    }

    public boolean birth(DemigodsStructure data, DemigodsCharacter demigodsCharacter) {
        return birth.apply(data, demigodsCharacter);
    }

    public boolean kill(DemigodsStructure data, DemigodsCharacter demigodsCharacter) {
        return kill.apply(data, demigodsCharacter);
    }

    public float getDefSanctity() {
        return sanctity;
    }

    public float getSanctityRegen() {
        return sanctityRegen;
    }

    public Collection<DemigodsStructure> getAll() {
        return Collections2.filter(DemigodsStructure.all(), new Predicate<DemigodsStructure>() {
            @Override
            public boolean apply(DemigodsStructure save) {
                return save.getTypeName().equals(getName());
            }
        });
    }

    public DemigodsStructure createNew(boolean generate, String designName, Location... reference) {
        // Define variables
        Design design;
        DemigodsStructure save;

        // Determine the design
        if (designName == null) {
            design = getDesign.apply(reference[0]);
        } else {
            design = getDesign(designName);
        }

        // Create the save
        save = createNew.apply(design);

        // All structures need these
        save.generateId();
        save.setLocation(reference[0]);
        save.setType(getName());
        save.setDesign(design.getName());
        save.addFlags(getFlags());
        save.setActive(true);
        save.save();

        if (generate) save.generate();
        return save;
    }

    @Override
    public String toString() {
        return getName();
    }
}
