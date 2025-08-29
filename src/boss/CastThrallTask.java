package boss;

import com.google.inject.Inject;
import config.Config;
import data.HueyData;
import org.rspeer.game.Vars;
import org.rspeer.game.Vars.Type;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.component.tdi.Magic;
import org.rspeer.game.component.tdi.Spell;
import org.rspeer.game.scene.Players;
import org.rspeer.game.script.Task;
import org.rspeer.game.script.TaskDescriptor;
import services.TickTrackerService;

@TaskDescriptor(name = "Casting thrall")
public class CastThrallTask extends Task {

    private final TickTrackerService tickTrackerService;
    private final Config config;

    @Inject
    public CastThrallTask(
            TickTrackerService tickTrackerService,
            Config config
    ) {
        this.tickTrackerService = tickTrackerService;
        this.config = config;
    }

    @Override
    public boolean execute() {
        if (!config.shouldUseThralls()) {
            return false;
        }

        if (!Players.self().getPosition().fromInstance().within(HueyData.MAIN_AREA)
                || !Magic.getCastableSpells().contains(Spell.Arceuus.RESURRECT_GREATER_GHOST)
                || !Inventories.backpack().contains(iq -> iq.nameContains("Book of the Dead").results())) {
            return false;
        }

        if (tickTrackerService.isThrallActive()) {
            return false;
        }

        if (Vars.get(Type.VARBIT, 6099) < 50) {
            return false;
        }

        Spell spell = Spell.Arceuus.RESURRECT_GREATER_GHOST;
        if (Magic.canCast(spell)) {
            Magic.cast(spell);
            //need this sleep because the thrall chat message doesn't instantly come
            sleepUntil(tickTrackerService::isThrallActive, 10);
            return true;
        }

        return false;
    }
}