import boss.*;
import cots.COTSQuest;
import org.rspeer.commons.ArrayUtils;
import org.rspeer.commons.logging.Log;
import org.rspeer.game.script.Task;
import org.rspeer.game.script.TaskScript;
import org.rspeer.game.script.meta.ScriptMeta;
import org.rspeer.game.script.meta.paint.PaintBinding;
import org.rspeer.game.script.meta.paint.PaintScheme;
import org.rspeer.commons.StopWatch;
import org.rspeer.game.component.tdi.Skill;
import org.rspeer.game.script.tools.RestockTask;
import prepare.*;
import config.Config;

import java.util.function.Supplier;
@ScriptMeta(
        name = "Hueycoatl",
        developer = "Bob",
        version = 0.9,
        paint = PaintScheme.class,
        model = Config.class,
        desc = "kill bird")

public class Hueycoatl extends TaskScript {
    @PaintBinding("Runtime")
    private final StopWatch runtime = StopWatch.start();

    @PaintBinding("Task")
    private final Supplier<String> task = () -> manager.getLastTaskExecutionSequence();

    @PaintBinding(value = "Coins", rate = true)
    private final Supplier<Integer> coinAmountSupplier = () -> BankingTask.getCoins();

    @PaintBinding(value = "Bank Value", rate = true)
    private final Supplier<String> bankValueSupplier = () -> BankingTask.getBankValue();

    @PaintBinding(value = "Host Name", rate = true)
    private final Supplier<String> hostNameSupplier = () -> OrderTask.getHostName();


    @PaintBinding("Experience")
    private final Skill[] skills = {Skill.HITPOINTS, Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE, Skill.RANGED, Skill.MAGIC};

    @Override
    public void initialize() {
        Log.info("BIRD");
    }

    @Override
    public void shutdown() {
        Log.info("Stopping bullshit!");
    }

    @Override
    public Class<? extends Task>[] tasks() {
        return ArrayUtils.getTypeSafeArray(
                UITask.class,
                RestockTask.class,
                DeathTask.class,
                EquipmentTask.class,
                LootTask.class,
                COTSQuest.class,
                BankingTask.class,
                EnterTask.class,
                PrayerTask.class,
                BodyTask.class,
                BossTask.class,
                TailTask.class
        );
    }
}