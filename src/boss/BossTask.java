package boss;

import com.google.inject.Inject;
import config.Domain;
import data.HueyData;
import org.rspeer.commons.logging.Log;
import org.rspeer.game.adapter.scene.EffectObject;
import org.rspeer.game.adapter.scene.Npc;
import org.rspeer.game.adapter.scene.SceneObject;
import org.rspeer.game.combat.Combat;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.config.item.loadout.EquipmentLoadout;
import org.rspeer.game.scene.EffectObjects;
import org.rspeer.game.scene.Npcs;
import org.rspeer.game.scene.SceneObjects;
import org.rspeer.game.script.Task;
import org.rspeer.game.script.TaskDescriptor;


@TaskDescriptor(name = "Boss", children = {ConsumeTask.class, CombatStyleTask.class, CastThrallTask.class})
public class BossTask extends Task {
    private final Domain domain;
    @Inject
    public BossTask(Domain domain) {
        this.domain = domain;
    }
    @Override
    public boolean execute() {
        Npc boss = Npcs.query().ids(14009).results().nearest();
        Npc tail = Npcs.query().ids(14014).actions("Attack").results().nearest();
        Npc body = Npcs.query().nameContains("Hueycoatl body").results().nearest();

        if (tail!=null || body!=null || boss==null) {
            return false;
        }

        int specialPercent = domain.getConfig().getLoadout().getSpecWeapon().getPercent();
        int specWeaponId = domain.getConfig().getLoadout().getSpecWeapon().getId();
        if (Combat.getSpecialEnergy() >= specialPercent && specWeaponId!=0) {
            if (Inventories.equipment().contains(iq -> iq.ids(specWeaponId).results())) {
                if (!Combat.isSpecialActive()) {
                    Combat.toggleSpecial(true);
                }
            } else {
                Inventories.backpack().query().ids(specWeaponId).results().first().interact("Wield");
            }
        }

        EquipmentLoadout bossLoadout = domain.getConfig().getLoadout().getBossLoadout();
        if (!bossLoadout.isEquipmentValid() && Combat.getSpecialEnergy() < specialPercent) {
            bossLoadout.equip();
        }

        SceneObject glowingSymbols = SceneObjects.query().ids(HueyData.GLOWING_SYMBOL).results().first();
        EffectObject flash = EffectObjects.query().ids(HueyData.FLASH).results().first();
        if (glowingSymbols != null && flash == null) {
            DodgeTask.dodgeSymbols();
            return true;
        }

        EffectObject wave = EffectObjects.query().ids(HueyData.WAVE_IDS).results().nearest();
        if (wave != null) {
            WaveBoss.dodgeWave();
            return true;
        }

        boss.interact("Attack");
        return true;
    }
}