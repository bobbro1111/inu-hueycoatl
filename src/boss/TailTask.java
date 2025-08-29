package boss;

import boss.DodgeTask;
import boss.Wave;
import com.google.inject.Inject;
import config.Domain;
import org.rspeer.commons.logging.Log;
import org.rspeer.game.adapter.scene.EffectObject;
import org.rspeer.game.adapter.scene.Npc;
import org.rspeer.game.adapter.scene.SceneObject;
import org.rspeer.game.config.item.loadout.EquipmentLoadout;
import org.rspeer.game.movement.Movement;
import org.rspeer.game.position.Position;
import org.rspeer.game.scene.EffectObjects;
import org.rspeer.game.scene.Npcs;
import org.rspeer.game.scene.Players;
import org.rspeer.game.scene.SceneObjects;
import org.rspeer.game.script.Task;
import org.rspeer.game.script.TaskDescriptor;
import prepare.OrderTask;
import data.HueyData;


@TaskDescriptor(name = "Tail", children = {ConsumeTask.class, CombatStyleTask.class})
public class TailTask extends Task {
    private final Domain domain;
    @Inject
    public TailTask(Domain domain) {
        this.domain = domain;
    }

    private static Boolean shouldDodge = false;
    @Override
    public boolean execute() {
        Npc tail = Npcs.query().ids(14014).actions("Attack").results().nearest();
        if (tail==null) {
            return false;
        }

        EquipmentLoadout tailLoadout = domain.getConfig().getLoadout().getTailLoadout();
        if (!tailLoadout.isEquipmentValid()) {
            Log.info("TAIL");
            tailLoadout.equip();
        }

        SceneObject glowingSymbols = SceneObjects.query().ids(HueyData.GLOWING_SYMBOL).results().first();
        EffectObject flash = EffectObjects.query().ids(HueyData.FLASH).results().first();
        if (glowingSymbols != null && flash == null) {
            DodgeTask.dodgeSymbols();
            return true;
        }

        EffectObject wave = EffectObjects.query().ids(HueyData.WAVE_IDS).results().first();
        if (wave != null && shouldDodge) {
            Wave.dodgeWave();
            return true;
        } else if (wave == null && !Players.self().isMoving()) {
            shouldDodge = true;
        }

        if (tail != null) {
            if (tail.getAnimationId()==11721) {
                Movement.walkTowards(Position.from(Players.self().getX(), Players.self().getY() + 3));
                return true;
            }
            tail.interact("Attack");
            OrderTask.setSubtask("Attacking tail");
            return true;
        }

        return true;
    }
    public static void setDodge() {
        shouldDodge = false;
    }

}