package boss;

import com.google.inject.Inject;
import config.Domain;
import data.HueyData;
import org.rspeer.commons.logging.Log;
import org.rspeer.game.adapter.scene.EffectObject;
import org.rspeer.game.adapter.scene.Npc;
import org.rspeer.game.adapter.scene.SceneObject;
import org.rspeer.game.config.item.loadout.EquipmentLoadout;
import org.rspeer.game.scene.EffectObjects;
import org.rspeer.game.scene.Npcs;
import org.rspeer.game.scene.SceneObjects;
import org.rspeer.game.script.Task;
import org.rspeer.game.script.TaskDescriptor;


@TaskDescriptor(name = "Body", children = {ConsumeTask.class, CombatStyleTask.class})
public class BodyTask extends Task {
    private final Domain domain;
    @Inject
    public BodyTask(Domain domain) {
        this.domain = domain;
    }
    @Override
    public boolean execute() {
        Npc body = Npcs.query().nameContains("Hueycoatl body").results().nearest();
        if (body==null) {
            return false;
        }

        EquipmentLoadout bossLoadout = domain.getConfig().getLoadout().getBossLoadout();
        if (!bossLoadout.isEquipmentValid()) {
            bossLoadout.equip();
        }

        SceneObject glowingSymbols = SceneObjects.query().ids(HueyData.GLOWING_SYMBOL).results().first();
        EffectObject flash = EffectObjects.query().ids(HueyData.FLASH).results().first();
        if (glowingSymbols != null && flash == null) {
            DodgeTask.dodgeSymbols();
            return true;
        }

        if (body != null) {
            body.interact("Attack");
            return true;
        }

        return true;
    }
}