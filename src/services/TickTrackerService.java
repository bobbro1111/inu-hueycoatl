package services;

import com.google.inject.Singleton;
import org.rspeer.event.Service;
import org.rspeer.event.Subscribe;
import org.rspeer.game.component.tdi.Skill;
import org.rspeer.game.component.tdi.Skills;
import org.rspeer.game.event.ChatMessageEvent;
import org.rspeer.game.event.TickEvent;

@Singleton
public class TickTrackerService implements Service {

    private int now = 0;

    private int thrallTick;
    private int deathChargeTick;

    public int now() {
        return now;
    }

    public int since(int tick) {
        return now - tick;
    }

    public boolean isDeathChargeActive() {
        return (now - deathChargeTick) < 0;
    }

    public boolean isThrallActive() {
        return (now - thrallTick) < 0;
    }

    @Subscribe
    public void notify(TickEvent event) {
        now++;
    }

    @Subscribe
    public void onChatMessage(ChatMessageEvent event) {
        String msg = event.getContents().toLowerCase();
        ChatMessageEvent.Type type = event.getType();

        if (type != ChatMessageEvent.Type.GAME) {
            return;
        }

        if (msg.contains("some of your special attack energy will be") || msg.contains(
                "cast death charge every") || msg.contains("death charge pending")) {
            deathChargeTick = now + 101;
        }

        if (msg.contains("you resurrect a") && msg.contains("thrall")) {
            thrallTick = now + Math.max(Skills.getCurrentLevel(Skill.MAGIC), 16);
        }
    }
}