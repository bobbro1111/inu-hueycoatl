package prepare;

import com.google.inject.Inject;
import config.Domain;
import data.CombatPotion;
import data.SpecWeapon;
import org.rspeer.commons.logging.Log;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.config.item.entry.ItemEntry;
import org.rspeer.game.config.item.entry.builder.FuzzyItemEntryBuilder;
import org.rspeer.game.config.item.entry.builder.ItemEntryBuilder;
import org.rspeer.game.config.item.loadout.BackpackLoadout;
import org.rspeer.game.config.item.loadout.EquipmentLoadout;
import org.rspeer.game.config.item.restock.RestockMeta;
import org.rspeer.game.service.stockmarket.StockMarketService;

//Ty doga
public class Loadout {
    private final Domain domain;
    private final StockMarketService service;
    private final int VARROCK_TELEPORT_ID = 8007;
    @Inject
    public Loadout(Domain domain, StockMarketService service) {
        this.domain = domain;
        this.service = service;
    }

    public BackpackLoadout getLoadout() {
        BackpackLoadout loadout = new BackpackLoadout("Loadout");
        loadout.add(new ItemEntryBuilder()
                .key("Varrock teleport")
                .stackable(true)
                .quantity(2)
                .restockMeta(new RestockMeta(VARROCK_TELEPORT_ID, 30, 1000))
                .build()
        );
        if (!domain.getConfig().getLoadout().getSpecWeapon().equals(SpecWeapon.None)
        && !Inventories.equipment().contains(iq -> iq.ids(domain.getConfig().getLoadout().getSpecWeapon().getId()).results())) {
            loadout.add(new ItemEntryBuilder()
                    .key(domain.getConfig().getLoadout().getSpecWeapon().getName())
                    .stackable(false)
                    .quantity(1)
                    .restockMeta(new RestockMeta(domain.getConfig().getLoadout().getSpecWeapon().getId(), 0, -5))
                    .build()
            );
        }
        if (!domain.getConfig().getConsumption().getCombatPotion().equals(CombatPotion.None))
        loadout.add(new ItemEntryBuilder()
                .key(domain.getConfig().getConsumption().getCombatPotion().getName())
                .stackable(false)
                .quantity(2)
                .restockMeta(new RestockMeta(domain.getConfig().getConsumption().getCombatPotion().getId(), 30, -5))
                .build()
        );
        loadout.add(new ItemEntryBuilder()
                .key(domain.getConfig().getConsumption().getPrayer().getName())
                .stackable(false)
                .quantity(2)
                .restockMeta(new RestockMeta(domain.getConfig().getConsumption().getPrayer().getId(), 30, -5))
                .build()
        );

        //add equipment too so we can easily withdraw everything in one pass
        EquipmentLoadout equipmentLoadout = getEquipmentLoadout();
        if (!equipmentLoadout.isEquipmentValid()) {
            for (ItemEntry equipmentEntry : equipmentLoadout.getMissingEquipmentEntries()) {
                loadout.add(equipmentEntry);
            }
        }

        //add food last, so we can fill up remaining slots if needed
        int remaining = 28 - loadout.getAllocated();
        if (remaining > 0) {
            int amount = Math.min(0, remaining);
            Log.info("Remaining:" + remaining);
            if (amount <= 0) {
                amount = remaining;
            }

            loadout.add(new ItemEntryBuilder()
                    .key(domain.getConfig().getConsumption().getFood().getName())
                    .quantity(amount)
                    .restockMeta(new RestockMeta(domain.getConfig().getConsumption().getFood().getId(), 500, -5))
                    .build());
        }
        return loadout;
    }

    private EquipmentLoadout getEquipmentLoadout() {
        EquipmentLoadout equipmentLoadout = domain.getConfig().getLoadout().getTailLoadout();
        return equipmentLoadout;
    }
}