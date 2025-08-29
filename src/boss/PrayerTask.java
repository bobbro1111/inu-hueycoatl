package boss;

import data.HueyData;
import org.rspeer.commons.logging.Log;
import org.rspeer.game.Game;
import org.rspeer.game.adapter.component.inventory.Bank;
import org.rspeer.game.adapter.scene.Npc;
import org.rspeer.game.component.tdi.Prayer;
import org.rspeer.game.component.tdi.Prayers;
import org.rspeer.game.component.tdi.Skill;
import org.rspeer.game.component.tdi.Skills;
import org.rspeer.game.position.Position;
import org.rspeer.game.position.area.Area;
import org.rspeer.game.scene.Npcs;
import org.rspeer.game.scene.Players;
import org.rspeer.game.scene.Projectiles;
import org.rspeer.game.script.Task;
import org.rspeer.game.script.TaskDescriptor;

import java.util.ArrayList;
import java.util.List;

//Remove combat prayers?
//turn off combat prayers if not attacking

@TaskDescriptor(name = "Prayer")
public class PrayerTask extends Task {
    private static final int[] enemies = new int[]{14017, 14014, 14009};
    private final Area combatArea = Area.polygonal(
            Position.from(1502, 3293),
            Position.from(1521, 3293),
            Position.from(1521, 3286),
            Position.from(1534, 3286),
            Position.from(1534, 3268),
            Position.from(1507, 3268),
            Position.from(1507, 3275),
            Position.from(1502, 3275)
    );

    @Override
    public boolean execute() {
        if (Bank.isOpen()) {
            return true;
        }

        Npc enemy = Npcs.query().ids(enemies).actions("Attack").results().first();
        if (!Players.self().getPosition().fromInstance().within(combatArea) || enemy == null) {
            deactivateAllPrayers();
            return true;
        }

        // Check for magic attack
        if (Projectiles.query().ids(HueyData.MAGIC_ATTACK).results().nearest() != null && !Prayers.isActive(Prayer.Modern.PROTECT_FROM_MAGIC)) {
            deactivateAllPrayers();
            Prayers.select(true, Prayer.Modern.PROTECT_FROM_MAGIC);
        }

        // Check for range attack
        if (Projectiles.query().ids(HueyData.RANGE_ATTACK).results().nearest() != null && !Prayers.isActive(Prayer.Modern.PROTECT_FROM_MISSILES)) {
            deactivateAllPrayers();
            Prayers.select(true, Prayer.Modern.PROTECT_FROM_MISSILES);
        }

        // Check for melee attack
        if (Projectiles.query().ids(HueyData.MELEE_ATTACK).results().nearest() != null && !Prayers.isActive(Prayer.Modern.PROTECT_FROM_MELEE)) {
            deactivateAllPrayers();
            Prayers.select(true, Prayer.Modern.PROTECT_FROM_MELEE);
        }

        if (Players.self().getPosition().fromInstance().within(combatArea) && Players.self().getTarget() != null) { // Melee prayers
            if (Skills.getLevel(Skill.PRAYER) > 69 && Prayers.isUnlocked(Prayer.Modern.PIETY)) {
                if (!Prayers.isActive(Prayer.Modern.PIETY)) {
                    Prayers.select(true, Prayer.Modern.PIETY);
                }
            } else if (Skills.getLevel(Skill.PRAYER) > 33) {
                if (!Prayers.isActive(Prayer.Modern.INCREDIBLE_REFLEXES)) {
                    Prayers.select(true, Prayer.Modern.INCREDIBLE_REFLEXES);
                }
                if (!Prayers.isActive(Prayer.Modern.ULTIMATE_STRENGTH)) {
                    Prayers.select(true, Prayer.Modern.ULTIMATE_STRENGTH);
                }
            }
        }

        if (!Prayers.getActive().isEmpty()) {
            Prayers.flick(Prayers.getActive());
        }
        return true;
    }
    // Turn off all prayers before selecting a new one.
    private void deactivateAllPrayers() {
        for (Prayer prayer : Prayers.getActive()) {
            Prayers.select(true, prayer);
        }
    }
}

//55209 floor damage
//2975 blue
//2972 green
//2969 red
