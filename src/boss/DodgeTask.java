package boss;

import org.rspeer.commons.logging.Log;
import org.rspeer.game.adapter.scene.Entity;
import org.rspeer.game.adapter.scene.Npc;
import org.rspeer.game.adapter.scene.Player;
import org.rspeer.game.adapter.scene.SceneObject;
import org.rspeer.game.movement.Movement;
import org.rspeer.game.position.Position;
import org.rspeer.game.position.area.Area;
import org.rspeer.game.scene.*;
import prepare.OrderTask;

import java.util.ArrayList;
import java.util.List;

import java.util.Collections;
import java.util.Comparator;

public class DodgeTask {
    public static final Area CENTER = Area.rectangular(1508, 3287, 1515, 3275, 0);

    private static class PotentialPosition {
        Position position;
        double distance;

        public PotentialPosition(Position position, double distance) {
            this.position = position;
            this.distance = distance;
        }
    }

    public static void dodgeSymbols() {
        if (!Movement.isRunEnabled()) {
            Movement.toggleRun(true);
        }

        List<SceneObject> glowingSymbols = SceneObjects.query().ids(55209).results().asList();
        List<Position> symbolPositions = new ArrayList<>();
        for (SceneObject symbol : glowingSymbols) {
            symbolPositions.add(symbol.getPosition());
        }

        int[][] potentialOffsets;
        Entity target;
        Npc body = Npcs.query().nameContains("body").actions("Attack").results().nearest();
        Npc tail = Npcs.query().ids(14014).actions("Attack").results().nearest();
        Npc boss = Npcs.query().ids(14009).actions("Attack").results().nearest();

        if (Players.self().getPosition().fromInstance().within(CENTER)) {
            potentialOffsets = new int[][]{
                    {0, 0}, {0, 1}, {1, 1},
                    {1, 0}, {-1, 0}, {-1, 1},
                    {0, -1}, {-1, -1}, {1, -1}};
            target = Players.self();
        } else if (body != null) {
            potentialOffsets = decodeOffset(body.getPosition().fromInstance());
            target = body;
        } else if (tail != null) {
            potentialOffsets = decodeOffset(tail.getPosition().fromInstance());
            target = tail;
        } else if (boss != null) {
            potentialOffsets = decodeOffset(boss.getPosition().fromInstance());
            target = boss;
        } else {
            return;
        }

        Position targetPos = target.getPosition();
        Position currentPlayerPos = Players.self().getPosition();
        Log.info(targetPos.fromInstance());
        // Create a list of potential safe positions with their distances
        List<PotentialPosition> potentialPositionsWithDistance = new ArrayList<>();
        for (int[] offset : potentialOffsets) {
            int targetX = targetPos.getX() + offset[0];
            int targetY = targetPos.getY() + offset[1];
            Position potentialSafePos = Position.from(targetX, targetY, 0);
            double distance = potentialSafePos.distance(currentPlayerPos);
            potentialPositionsWithDistance.add(new PotentialPosition(potentialSafePos, distance));
        }

        //Sorts list so player moves to the closest safe position
        Collections.sort(potentialPositionsWithDistance, new Comparator<PotentialPosition>() {
            @Override
            public int compare(PotentialPosition p1, PotentialPosition p2) {
                return Double.compare(p1.distance, p2.distance);
            }
        });

        Position safePosition = null;
        for (PotentialPosition pp : potentialPositionsWithDistance) {
            boolean isSafe = true;
            for (Position symbolPos : symbolPositions) {
                if (pp.position.equals(symbolPos)) {
                    isSafe = false;
                    break;
                }
            }
            if (isSafe) {
                safePosition = pp.position;
                break;
            }
        }

        if (safePosition != null) {
            if (currentPlayerPos.equals(safePosition)) {
                shouldAttack();
                OrderTask.setSubtask("Already at closest safe spot: " + safePosition.getX() + "," + safePosition.getY());
                return;
            }
            if (Players.self().isMoving()) {
                OrderTask.setSubtask("Already moving");
                return;
            }
            Movement.walkTowards(safePosition);
            OrderTask.setSubtask("Dodging symbols to closest safe spot: " + safePosition.getX() + "," + safePosition.getY());
        }
    }

    private static int[][] decodeOffset(Position target) {
        int[][] offsets = new int[][]{{-1, 0}};
        if (target.fromInstance().equals(1530, 3276, 0)) {
            return new int[][]{
                    {-1, 0}, {-1, -1}, {-1, 1},
                    {-2, 0}, {-2, -1}, {-2, 1},
                    {-3, 0}, {-3, -1}, {-3, 1}};
        }
        if (target.fromInstance().equals(1524, 3277, 0)) {
            return new int[][]{
                    {1, 0}, {1, -1}, {1, 1},
                    {2, 0}, {2, -1}, {2, 1},
                    {3, 0}, {3, -1}, {3, 1}};
        }
        if (target.fromInstance().equals(1527, 3273, 0)) {
            return new int[][]{
                    {0, 1}, {-1, 0}, {-1, -1},
                    {-2, -1}, {-2, 0}, {-1, 1},
                    {1, 1}, {2, 1}};
        }
        if (target.fromInstance().equals(1524, 3270, 0)) {
            return new int[][]{
                    {0, 1}, {-1, 1}, {1, 1},
                    {0, 2}, {-1, 2}, {1, 2},
                    {0, 3}, {-1, 3}, {1, 3}};
        }
        if (target.fromInstance().equals(1520, 3273, 0)) {
            return new int[][]{
                    {0, -1}, {-1, -1}, {1, -1},
                    {0, -2}, {-1, -2}, {1, -2},
                    {0, -3}, {-1, -3}, {1, -3}};
        }
        if (target.fromInstance().equals(1509, 3290, 0)) {
            return new int[][]{
                    {3, -1}, {4, -1}, {2, -1},
                    {5, -1}, {1, -1}, {6, -1},
                    {7, -1}, {7, -1}, {4, -2}};
        }
        if (target.fromInstance().equals(1502, 3281, 0)) { //left side
            return new int[][]{
                    {5, 2}, {5, 3}, {5, 4},
                    {6, 4}, {5, 5},
                    {6, 5}, {5, 6}};
        }
        if (target.fromInstance().equals(1518, 3281, 0)) {
            return new int[][]{
                    {-3, 1}, {-3, 2}, {-2, -1},
                    {-1, -1}, {-2, 3},
                    {-1, 3}, {-3, 3}};
        }
        return offsets;
    }

    private static void shouldAttack() {
        // Your existing shouldAttack logic remains unchanged
        Player self = Players.self();
        Npc body = Npcs.query().nameContains("body").actions("Attack").results().nearest();
        Npc tail = Npcs.query().nameContains("tail").actions("Attack").results().nearest();
        Npc boss = Npcs.query().ids(14009).actions("Attack").results().nearest();
        if (body != null) {
            if (self.getPosition().fromInstance().equals(1523, 3276, 0)) {
                body.interact("Attack");
                return;
            }
            if (self.getPosition().fromInstance().equals(1525, 3277, 0)) {
                body.interact("Attack");
                return;
            }
            if (self.getPosition().fromInstance().equals(1527, 3274, 0)) {
                body.interact("Attack");
                return;
            }
            if (self.getPosition().fromInstance().equals(1526, 3273, 0)) {
                body.interact("Attack");
                return;
            }
            if (self.getPosition().fromInstance().equals(1524, 3271, 0)) {
                body.interact("Attack");
                return;
            }
            if (self.getPosition().fromInstance().equals(1520, 3272, 0)) {
                body.interact("Attack");
                return;
            }
        }
        if (boss != null) {
            if (self.getPosition().fromInstance().getY() == 3289
                    && (self.getPosition().fromInstance().getX() > 1508 && self.getPosition().fromInstance().getX() < 1516)) {
                boss.interact("Attack");
                return;
            }
        }
        if (tail != null) {
            if (self.getPosition().fromInstance().equals(1505, 3286, 0)) { //Left side
                tail.interact("Attack");
                return;
            }
            if (self.getPosition().fromInstance().equals(1506, 3286, 0)) {
                tail.interact("Attack");
                return;
            }
            if (self.getPosition().fromInstance().equals(1507, 3284, 0)) {
                tail.interact("Attack");
                return;
            }
            if (self.getPosition().fromInstance().equals(1507, 3283, 0)) {
                tail.interact("Attack");
                return;
            }
            if (self.getPosition().fromInstance().equals(1507, 3282, 0)) {
                tail.interact("Attack");
                return;
            }
            if (self.getPosition().fromInstance().equals(1507, 3281, 0)) {
                tail.interact("Attack");
                return;
            }

            if (self.getPosition().fromInstance().equals(1505, 3285, 0)) { //Right side
                tail.interact("Attack");
                return;
            }
            if (self.getPosition().fromInstance().equals(1506, 3285, 0)) {
                tail.interact("Attack");
                return;
            }
            if (self.getPosition().fromInstance().equals(1517, 3284, 0)) {
                tail.interact("Attack");
                return;
            }
            if (self.getPosition().fromInstance().equals(1517, 3283, 0)) {
                tail.interact("Attack");
                return;
            }
            if (self.getPosition().fromInstance().equals(1517, 3282, 0)) {
                tail.interact("Attack");
                return;
            }
            if (self.getPosition().fromInstance().equals(1517, 3281, 0)) {
                tail.interact("Attack");
                return;
            }
        }
    }
}