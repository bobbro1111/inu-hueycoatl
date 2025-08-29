package boss;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import config.Config;
import data.HueyData;
import org.rspeer.event.Subscribe;
import org.rspeer.game.component.Inventories;
import org.rspeer.game.component.tdi.Magic;
import org.rspeer.game.component.tdi.Spell;
import org.rspeer.game.event.ChatMessageEvent;
import org.rspeer.game.scene.Players;
import org.rspeer.game.script.Task;
import org.rspeer.game.script.TaskDescriptor;


@TaskDescriptor(name = "Thrall", register = true)
public class ThrallTask extends Task {

    private static Boolean isActive = false;
    private final Config config;
    private final ChatMessageEvent event;
    @Inject
    public ThrallTask(Config config, ChatMessageEvent event) {
        this.config = config;
        this.event = event;
    }
    @Subscribe
    public void onChatMessage(ChatMessageEvent event) {
        String m = event.getContents().toLowerCase();
        if (m.contains("returns to the grave")) {
            isActive = false;
        }
    }
    @Override
    public boolean execute() {
        if (!Players.self().getPosition().fromInstance().within(HueyData.MAIN_AREA)
        || !Magic.getCastableSpells().contains(Spell.Arceuus.RESURRECT_GREATER_GHOST)
        || !Inventories.backpack().contains(iq -> iq.nameContains("Book of the Dead").results())) {
            return false;
        }

        if (config.shouldUseThralls() && !isActive) {
            Magic.cast(Spell.Arceuus.RESURRECT_GREATER_GHOST);
            isActive = true;
        }
        return true;
    }

}