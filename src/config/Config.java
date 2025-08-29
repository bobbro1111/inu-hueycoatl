package config;

import com.google.inject.Singleton;
import data.CombatPotion;
import data.Food;
import data.Prayer;
import data.SpecWeapon;
import org.rspeer.game.config.item.loadout.EquipmentLoadout;
import org.rspeer.game.script.model.ConfigModel;
import org.rspeer.game.script.model.ui.schema.checkbox.CheckBoxComponent;
import org.rspeer.game.script.model.ui.schema.selector.SelectorComponent;
import org.rspeer.game.script.model.ui.schema.structure.Section;
import org.rspeer.game.script.model.ui.schema.text.TextFieldComponent;
import org.rspeer.game.script.model.ui.schema.text.TextInputType;

@Singleton
public class Config extends ConfigModel {
    @Section(value = "Loadout")
    private LoadoutConfig loadout;

    @Section(value = "Consumption")
    private ConsumptionConfig consumption;

    @Section(value = "Grouping")
    private GroupingConfig grouping;

    @Section(value = "Muling")
    private MulingConfig muling;

    @CheckBoxComponent(name = "Thralls", key = "thralls")
    private boolean useThralls = false;

    @CheckBoxComponent(name = "Should sell drops (buggy)", key = "should sell")
    private boolean shouldSell = false;

    @TextFieldComponent(name = "Sell threshold (1.5m at least)", key = "sell threshold", inputType = TextInputType.NUMERIC)
    private int sellThreshold = 1500000;

    public Boolean shouldUseThralls() {
        return useThralls;
    }

    public Boolean shouldSell() {
        return shouldSell;
    }

    public int getSellThreshold() {
        return sellThreshold;
    }

    public LoadoutConfig getLoadout() {
        return loadout;
    }

    public ConsumptionConfig getConsumption() {
        return consumption;
    }

    public GroupingConfig getGrouping() {
        return grouping;
    }

    public MulingConfig getMuling() {
        return muling;
    }

    @Singleton
    public static class GroupingConfig extends ConfigModel {

        @CheckBoxComponent(name = "Should Instance", key = "instance")
        private boolean instance;

        @CheckBoxComponent(name = "Should host", key = "host")
        private boolean shouldHost;

        @TextFieldComponent(name = "Hostname", key = "host_name", inputType = TextInputType.ANY)
        private String hostName;

        public boolean shouldInstance() {
            return instance;
        }

        public boolean shouldHost() {
            return shouldHost;
        }

        public String getHostName() {
            return hostName;
        }
    }

    @Singleton
    public static class LoadoutConfig extends ConfigModel {

        @TextFieldComponent(name = "Boss loadout", key = "boss loadout", inputType = TextInputType.ANY)
        private EquipmentLoadout bossLoadout;

        @TextFieldComponent(name = "Tail loadout", key = "tail loadout", inputType = TextInputType.ANY)
        private EquipmentLoadout tailLoadout;

        @SelectorComponent(name = "Special Attack Weapon", key = "spec wep", type = SpecWeapon.class)
        private SpecWeapon specWeapon;

        public EquipmentLoadout getBossLoadout() {
            return bossLoadout;
        }

        public EquipmentLoadout getTailLoadout() {
            return tailLoadout;
        }

        public SpecWeapon getSpecWeapon() {return specWeapon;}
    }

    @Singleton
    public static class ConsumptionConfig extends ConfigModel {

        @SelectorComponent(name = "Food", key = "food", type = Food.class)
        private Food food;

        @SelectorComponent(name = "Prayer", key = "prayer", type = Prayer.class)
        private Prayer prayer;

        @SelectorComponent(name = "Combat potion", key = "combat pot", type = CombatPotion.class)
        private CombatPotion combatPotion;

        public Food getFood() {
            return food;
        }

        public Prayer getPrayer() {
            return prayer;
        }

        public CombatPotion getCombatPotion() {return combatPotion;}
    }

    @Singleton
    public static class MulingConfig extends ConfigModel {

        @CheckBoxComponent(name = "Should mule", key = "mule")
        private boolean mule;

        @TextFieldComponent(name = "Mule name", key = "male_name", inputType = TextInputType.ANY)
        private String muleName = null;

        @TextFieldComponent(name = "Mule world", key = "mule_world", inputType = TextInputType.NUMERIC)
        private int muleWorld;

        public boolean shouldMule() {
            return mule;
        }

        public String getMuleName() {
            return muleName;
        }

        public int getMuleWorld() {
            return muleWorld;
        }
    }
}