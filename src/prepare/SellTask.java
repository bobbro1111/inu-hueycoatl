package prepare;

import com.google.inject.Inject;
import org.rspeer.game.adapter.component.StockMarketable;
import org.rspeer.game.adapter.component.inventory.Backpack;
import org.rspeer.game.adapter.component.inventory.Bank;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.config.item.entry.ItemEntry;
import org.rspeer.game.script.Task;
import org.rspeer.game.script.TaskDescriptor;
import config.Config;
import org.rspeer.game.service.stockmarket.StockMarketEntry;
import org.rspeer.game.service.stockmarket.StockMarketService;

@TaskDescriptor(
        name = "Selling items",
        blocking = true
)
public class SellTask extends Task {

    private final int VARROCK_TELEPORT_ID = 8007;

    private final int[] SELL_ITEMS = {
            30085,30086,30066,30067,
            30070,30071,1433,
            1334,1124,560,557,564,
            561,5298,5299,5302,30088,
            5296,5304,5295,30068,9380,
            574,384,28924,537,
            2,450,824,226,536,1123,
            1432,1433,383,449,1333,573,
            225,144,142,140};
    private final int[] INV_SORT = {
            30085,30086,30066,30067,
            30070,30071,1433,
            1334,1124,560,557,564,
            561,5298,5299,5302,30088,
            5296,5304,5295,30068,9380,
            574,384,28924,537,
            2,450,824,226,536,1123,
            1432,1433,383,449,1333,573,
            225,144,142,140, VARROCK_TELEPORT_ID};
    private final Config config;
    private final StockMarketService service;

    @Inject
    public SellTask(Config config, StockMarketService service) {
        this.config = config;
        this.service = service;
    }

    @Override
    public boolean execute() {
        if (!config.shouldSell()) {
            return false;
        }
        if (!Bank.isOpen()) {
            return false;
        }
        Bank bank = Inventories.bank();
        Backpack backpack = Inventories.backpack();
        int coinAmount = bank.query().ids(995).results().first().getStackSize();
        if (coinAmount > config.getSellThreshold() && config.shouldSell()) {
            return true;
        }

        bank.depositAllExcept(iq -> iq.ids(INV_SORT).results());
        bank.setWithdrawMode(Bank.WithdrawMode.NOTED);
        Inventories.backpack().getItems().asList();
        bank.withdraw(VARROCK_TELEPORT_ID,1);
        for (int item : SELL_ITEMS) {
            if (bank.contains(iq -> iq.ids(item).results())) {
                bank.withdrawAll(iq -> iq.ids(item).results().first());
            }
        }
        for (int item : SELL_ITEMS) {
            if (backpack.contains(iq -> iq.ids(item).results())) {
                service.submit(StockMarketable.Type.SELL, new StockMarketEntry(item, ItemEntry.ALL, 1));
            }
        }
        sleep(5);
        return false;
    }
}