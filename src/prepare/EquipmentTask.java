package prepare;

import com.google.inject.Inject;
import config.Domain;
import data.HueyData;
import org.rspeer.event.ScriptService;
import org.rspeer.game.adapter.scene.Npc;
import org.rspeer.game.scene.Npcs;
import org.rspeer.game.scene.Players;
import org.rspeer.game.script.Task;
import org.rspeer.game.script.TaskDescriptor;
import org.rspeer.game.service.stockmarket.StockMarketService;

@ScriptService(StockMarketService.class)
@TaskDescriptor(name = "Equipment", blocking = true, blockIfSleeping = true, children = WithdrawEquipment.class)
public class EquipmentTask extends Task {

    private final Domain domain;
    @Inject
    public EquipmentTask(Domain domain) {
        this.domain = domain;
    }

    @Override
    public boolean execute() {
        if (Players.self().getPosition().within(HueyData.COMBAT_AREA)) {
            return false;
        }
        Npc boss = Npcs.query().ids(14009).results().nearest();
        Npc tail = Npcs.query().ids(14014).actions("Attack").results().nearest();
        Npc body = Npcs.query().nameContains("Hueycoatl body").results().nearest();

        if (tail!=null || body!=null || boss!=null) {
            return false;
        }
        return !domain.getConfig().getLoadout().getBossLoadout().isEquipmentValid();
    }
}