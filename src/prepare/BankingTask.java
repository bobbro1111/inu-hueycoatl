package prepare;

import com.google.inject.Inject;
import data.HueyData;
import config.Domain;
import org.rspeer.commons.logging.Log;
import org.rspeer.commons.math.Distance;
import org.rspeer.event.ScriptService;
import org.rspeer.game.adapter.component.StockMarketable;
import org.rspeer.game.adapter.component.inventory.Backpack;
import org.rspeer.game.adapter.component.inventory.Bank;
import org.rspeer.game.adapter.scene.Npc;
import org.rspeer.game.adapter.scene.SceneObject;
import org.rspeer.game.component.Dialog;
import org.rspeer.game.component.Interfaces;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.component.Item;
import org.rspeer.game.config.item.entry.ItemEntry;
import org.rspeer.game.config.item.entry.builder.ItemEntryBuilder;
import org.rspeer.game.config.item.loadout.BackpackLoadout;
import org.rspeer.game.config.item.restock.RestockMeta;
import org.rspeer.game.effect.Health;
import org.rspeer.game.movement.Movement;
import org.rspeer.game.position.Position;
import org.rspeer.game.scene.Npcs;
import org.rspeer.game.scene.Players;
import org.rspeer.game.scene.SceneObjects;
import org.rspeer.game.script.Task;
import org.rspeer.game.script.TaskDescriptor;
import org.rspeer.game.service.stockmarket.StockMarketEntry;
import org.rspeer.game.service.stockmarket.StockMarketService;

@ScriptService(StockMarketService.class)
@TaskDescriptor(name = "Banking", blocking = true, blockIfSleeping = true, children = SellTask.class)
public class BankingTask extends Task {
    private static Integer coinAmount = 0;
    private static String bankValue = "null";
    private int[] combatPot = {12695,12697,12699,12701}; //12695,12697,12699,12701 combat //23733,23736,23739,23742 range

    private static int[] itemIds = {
            30085,30086,30066,30067,
            30070,30071,1433,
            1334,1124,560,557,564,
            561,5298,5299,5302,30088,
            5296,5304,5295,30068,9380,
            574,384,28924,537,
            2,450,824,226,536,1123,
            1432,1433,383,449,1333,573,
            225,144,142,140};

    private final Domain domain;
    private final Loadout loadout;
    private final StockMarketService service;
    @Inject
    public BankingTask(Domain domain, Loadout loadout, StockMarketService service) {
        this.domain = domain;
        this.loadout = loadout;
        this.service = service;
    }

    @Override
    public boolean execute() {
        if (!shouldBank()) {
            return false;
        }

        if (Players.self().getPosition().fromInstance().within(HueyData.COMBAT_AREA)) {
            escapeBoss();
            return true;
        }
        SceneObject banker = SceneObjects.query().actions("Bank").results().nearest();
        if (banker==null) {
            Movement.walkTo(Position.from(1527,3292,0));
            return true;
        }
        if (!Bank.isOpen()) {
            banker.interact("Bank");
            return true;
        }
        if (Bank.isOpen()) {
            Bank bank = Inventories.bank();
            findBankValue();
            coinAmount = bank.query().ids(995).results().first().getStackSize();
            if (coinAmount==null) {
                coinAmount = 0;
            }
            if (coinAmount < domain.getConfig().getSellThreshold() && domain.getConfig().shouldSell()) {
                return true;
            }
            Loadout loadout = new Loadout(domain, service);
            BackpackLoadout backpackLoadout = loadout.getLoadout();

            backpackLoadout.setOutOfItemListener(entry -> {
                Log.info("Restocking: " + entry.getKey());
                service.submit(StockMarketable.Type.BUY, entry);
            });
            backpackLoadout.withdraw();
            Interfaces.closeSubs();
            sleep(1);
        }
        return true;
    }
    private void escapeBoss() {
        if (Health.getCurrent() < Health.getLevel() * .5) {
            Item food = Inventories.backpack().query().actions("Eat").results().first();
            if (food != null) {
                food.interact("Eat");
                OrderTask.setSubtask("Eating");
                return;
            } //Prevents common death
        }
        SceneObject chain = SceneObjects.query().ids(55401).results().nearest();
        SceneObject slide = SceneObjects.query().ids(55234).results().nearest();
        Double dist1 = Distance.between(Players.self().getPosition().fromInstance(), chain.getPosition().fromInstance());
        Double dist2 = Distance.between(Players.self().getPosition().fromInstance(), slide.getPosition().fromInstance());
        if (dist1 < dist2) {
            chain.interact("Quick-climb");
            return;
        }
        slide.interact("Quick-slide");
        return;
    }
    public static void findBankValue() {
        String text = Interfaces.getDirect(12,3).getText();

        int startIndex = text.indexOf("(GE: ");
        if (startIndex == -1) {
            return;
        } //fail safe if lagging or something idk

        startIndex += "(GE: ".length();
        int endIndex = text.indexOf(")", startIndex);

        bankValue = text.substring(startIndex, endIndex);
        return;
    } //Updates bank value when bank is open (ai slop)
    private static Boolean shouldBank() {
        Npc deadBoss = Npcs.query().ids(HueyData.DEAD_HUEY_ID).results().first();
        if (deadBoss!=null && Inventories.backpack().getEmptySlots() > 3) { //If boss is dead and our inv isn't fresh, then bank
            return true;
        }
        Backpack backpack = Inventories.backpack();
        int hasFood = backpack.getCount(iq -> iq.actions("Eat").results());
        Boolean hasPrayer = backpack.contains(iq -> iq.nameContains("Prayer").results());
        Boolean hasSuperRestore = backpack.contains(iq -> iq.nameContains("restore").results());
        return hasFood < 2 || (!hasPrayer && !hasSuperRestore);
    }
    public static Integer getCoins() {
        return coinAmount;
    }
    public static String getBankValue() {
        return bankValue;
    }
}