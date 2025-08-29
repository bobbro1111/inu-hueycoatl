package prepare;

import com.google.inject.Inject;
import config.Domain;
import org.rspeer.game.adapter.component.inventory.Bank;
import org.rspeer.game.config.item.entry.ItemEntry;
import org.rspeer.game.config.item.loadout.BackpackLoadout;
import org.rspeer.game.config.item.loadout.EquipmentLoadout;
import org.rspeer.game.script.Task;
import org.rspeer.game.script.TaskDescriptor;

@TaskDescriptor(name = "Withdraw equipment", blocking = true, blockIfSleeping = true)
public class WithdrawEquipment extends Task {

    private final Domain domain;
    @Inject
    public WithdrawEquipment(Domain domain) {
        this.domain = domain;
    }

    @Override
    public boolean execute() {
        EquipmentLoadout equipmentLoadout = domain.getConfig().getLoadout().getBossLoadout();
        BackpackLoadout loadout = new BackpackLoadout("loadout");
        if (!equipmentLoadout.isEquipmentValid()) {
            for (ItemEntry equipmentEntry : equipmentLoadout.getMissingEquipmentEntries()) {
                loadout.add(equipmentEntry);
            }
        }
        if (!loadout.isBackpackValid()) {
            if (!Bank.open()) {
                return Bank.open(Bank.Location.getNearest());
            }
            return loadout.withdraw();
        }
        equipmentLoadout.equip();
        return false;
    }
}