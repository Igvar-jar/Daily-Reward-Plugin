package org.dailyRewards.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dailyRewards.DailyRewards;
import org.dailyRewards.Getter.Gui;
import org.dailyRewards.Getter.Language;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.dailyRewards.Getter.Language.getTranslation;

public class DailyCommand extends BukkitCommand {

    private String adminPermission(){
        return DailyRewards.getInstance().getConfig().getString("admin-permission","daily.command.admin");
    }

    public DailyCommand(String name,String description,String usage,List<String> aliases, String defaultPermission, PermissionDefault permissionDefault) {
        super(name, description, usage, aliases);

        this.setName(name);
        this.setDescription(description);
        this.setUsage(usage);
        this.setAliases(aliases);
        this.setPermission(defaultPermission);
        try {
            Field f;
            f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            CommandMap commandMap = (CommandMap) f.get(Bukkit.getServer());
            commandMap.register(name, this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        DailyRewards.getInstance().getServer().getPluginManager().addPermission(new Permission(defaultPermission,permissionDefault));
    }

    @Override
    public boolean execute(@NonNull CommandSender commandSender, @NonNull String commandLabel, String[] strings) {
        if(strings.length==0){
            if(!(commandSender instanceof Player p)){
                commandSender.sendMessage(getTranslation("you-must-be-a-player"));
                return true;
            }

            p.openInventory(Gui.getGui(p));
        }
        else if(commandSender.hasPermission(adminPermission())){
            FileConfiguration guiFileConfig = DailyRewards.GUI_CONFIG.getConfig();
            switch (strings[0]) {
                case "add" -> {
                    if (strings.length < 2) {
                        commandSender.sendMessage(getTranslation("correct-information"));
                        return true;
                    }

                    if (strings[1].equals("opened")) {
                        if (!(commandSender instanceof Player p)) {
                            commandSender.sendMessage(getTranslation("you-must-be-a-player"));
                            return true;
                        }

                        if (p.getInventory().getItemInMainHand().getType() == Material.AIR) {
                            commandSender.sendMessage(getTranslation("must-have-item-in-hand"));
                            return true;
                        }


                        guiFileConfig.set("opened", p.getInventory().getItemInMainHand());

                        DailyRewards.GUI_CONFIG.save();

                        commandSender.sendMessage(getTranslation("opened-itemstack-added"));

                        return true;
                    }

                    if (strings[1].equals("can_open")) {
                        if (!(commandSender instanceof Player p)) {
                            commandSender.sendMessage(getTranslation("you-must-be-a-player"));
                            return true;
                        }

                        if (p.getInventory().getItemInMainHand().getType() == Material.AIR) {
                            commandSender.sendMessage(getTranslation("must-have-item-in-hand"));
                            return true;
                        }


                        guiFileConfig.set("can_open", p.getInventory().getItemInMainHand());

                        DailyRewards.GUI_CONFIG.save();

                        commandSender.sendMessage(getTranslation("can-open-itemstack-added"));

                        return true;
                    }

                    if (strings.length < 3) {
                        commandSender.sendMessage(getTranslation("correct-information"));
                        return true;
                    }

                    switch (strings[1]) {
                        case "item" -> {
                            if (!(commandSender instanceof Player p)) {
                                commandSender.sendMessage(getTranslation("you-must-be-a-player"));
                                return true;
                            }

                            if (p.getInventory().getItemInMainHand().getType() == Material.AIR) {
                                commandSender.sendMessage(getTranslation("must-have-item-in-hand"));
                                return true;
                            }

                            int number;
                            try {
                                number = Integer.parseInt(strings[2]);
                                if (number <= 0) {
                                    commandSender.sendMessage(getTranslation("number-above-zero"));
                                    return true;
                                }
                            } catch (NumberFormatException e) {
                                commandSender.sendMessage(getTranslation("correct-number"));
                                return true;
                            }

                            List<ItemStack> itemStackList = DailyRewards.transformList(guiFileConfig.getList("rewards." + number + ".items",new ArrayList<>()),ItemStack.class);

                            itemStackList.add(p.getInventory().getItemInMainHand());

                            guiFileConfig.set("rewards." + number + ".items", itemStackList);

                            DailyRewards.GUI_CONFIG.save();

                            commandSender.sendMessage(getTranslation("item-added"));
                        }
                        case "command" -> {
                            int number;
                            try {
                                number = Integer.parseInt(strings[2]);
                                if (number <= 0) {
                                    commandSender.sendMessage(getTranslation("number-above-zero"));
                                    return true;
                                }
                            } catch (NumberFormatException e) {
                                commandSender.sendMessage(getTranslation("correct-number"));
                                return true;
                            }

                            List<String> commands = guiFileConfig.getStringList("rewards." + number + ".commands");

                            String commandGiven = String.join(" ", Arrays.copyOfRange(strings, 3, strings.length));

                            if (commandGiven.isBlank()) {
                                commandSender.sendMessage(getTranslation("give-command"));
                                return true;
                            }

                            commands.add(commandGiven);

                            guiFileConfig.set("rewards." + number + ".commands", commands);

                            DailyRewards.GUI_CONFIG.save();

                            commandSender.sendMessage(getTranslation("command-added"));
                        }
                        case "type" -> {
                            if (!(commandSender instanceof Player p)) {
                                commandSender.sendMessage(getTranslation("you-must-be-a-player"));
                                return true;
                            }

                            if (p.getInventory().getItemInMainHand().getType() == Material.AIR) {
                                commandSender.sendMessage(getTranslation("must-have-item-in-hand"));
                                return true;
                            }


                            guiFileConfig.set("types." + strings[2], p.getInventory().getItemInMainHand());

                            DailyRewards.GUI_CONFIG.save();

                            commandSender.sendMessage(getTranslation("type-added"));
                        }
                        case "decoration" ->{
                            if (!(commandSender instanceof Player p)) {
                                commandSender.sendMessage(getTranslation("you-must-be-a-player"));
                                return true;
                            }

                            if (p.getInventory().getItemInMainHand().getType() == Material.AIR) {
                                commandSender.sendMessage(getTranslation("must-have-item-in-hand"));
                                return true;
                            }

                            int slot;
                            try {
                                slot=Integer.parseInt(strings[2]);
                                if(slot<0){
                                    commandSender.sendMessage(getTranslation("number-above-zero"));
                                    return true;
                                }
                            }
                            catch (NumberFormatException e){
                                commandSender.sendMessage(getTranslation("correct-number"));
                                return true;
                            }

                            guiFileConfig.set("decorations."+slot,p.getInventory().getItemInMainHand());
                            DailyRewards.GUI_CONFIG.save();

                            commandSender.sendMessage(getTranslation("decoration-added"));
                        }
                    }
                }
                case "remove" -> {
                    if (strings.length != 2) {
                        commandSender.sendMessage(getTranslation("correct-information"));
                        return true;
                    }

                    int number;
                    try {
                        number = Integer.parseInt(strings[1]);
                        if (number <=0) {
                            commandSender.sendMessage(getTranslation("number-above-zero"));
                            return true;
                        }
                    } catch (NumberFormatException e) {
                        commandSender.sendMessage(getTranslation("correct-number"));
                        return true;
                    }

                    guiFileConfig.set("rewards." + number, null);

                    DailyRewards.GUI_CONFIG.save();

                    commandSender.sendMessage(getTranslation("reward-deleted"));

                }
                case "reload" -> {
                    DailyRewards.getInstance().reloadConfig();
                    DailyRewards.GUI_CONFIG.reload();
                    DailyRewards.PLAYER_COLLECTED.reload();

                    Language.reload();

                    commandSender.sendMessage(getTranslation("config-reloaded"));
                }
                case "set" -> {
                    if (strings.length != 4) {
                        commandSender.sendMessage(getTranslation("correct-information"));
                        return true;
                    }

                    int day;
                    try {
                        day = Integer.parseInt(strings[1]);
                        if (day <=0) {
                            commandSender.sendMessage(getTranslation("number-above-zero"));
                            return true;
                        }
                    } catch (NumberFormatException e) {
                        commandSender.sendMessage(getTranslation("correct-number"));
                        return true;
                    }

                    switch (strings[2]){
                        case "type" ->{
                            guiFileConfig.set("rewards." + day + ".type", strings[3]);

                            DailyRewards.GUI_CONFIG.save();

                            commandSender.sendMessage(getTranslation("type-set"));
                        }
                        case "slot" ->{
                            int slot;
                            try {
                                slot = Integer.parseInt(strings[3]);
                                if (slot <=0) {
                                    commandSender.sendMessage(getTranslation("number-above-zero"));
                                    return true;
                                }
                            } catch (NumberFormatException e) {
                                commandSender.sendMessage(getTranslation("correct-number"));
                                return true;
                            }

                            guiFileConfig.set("rewards." + day + ".slot", slot);

                            DailyRewards.GUI_CONFIG.save();

                            commandSender.sendMessage(getTranslation("slot-set"));
                        }
                    }
                }
                case "lore" -> {
                    if (strings.length < 2) {
                        commandSender.sendMessage(getTranslation("correct-information"));
                        return true;
                    }
                    if (strings[1].equals("add")) {
                        if (strings.length < 3) {
                            commandSender.sendMessage(getTranslation("correct-information"));
                            return true;
                        }

                        int index;
                        try {
                            index = Integer.parseInt(strings[2]);
                            if (index <=0) {
                                commandSender.sendMessage(getTranslation("number-above-zero"));
                                return true;
                            }
                        } catch (NumberFormatException e) {
                            commandSender.sendMessage(getTranslation("correct-number"));
                            return true;
                        }

                        List<String> lore = guiFileConfig.getStringList("rewards." + index + ".lore");

                        String line = String.join(" ", Arrays.copyOfRange(strings, 3, strings.length));

                        if (line.isBlank()) {
                            commandSender.sendMessage(getTranslation("give-line"));
                            return true;
                        }

                        lore.add(line);

                        guiFileConfig.set("rewards." + index + ".lore", lore);

                        DailyRewards.GUI_CONFIG.save();

                        commandSender.sendMessage(getTranslation("lore-added"));
                    } else if (strings[1].equals("set")) {
                        if (strings.length < 4) {
                            commandSender.sendMessage(getTranslation("correct-information"));
                            return true;
                        }

                        int day;
                        try {
                            day = Integer.parseInt(strings[2]);
                            if (day <=0) {
                                commandSender.sendMessage(getTranslation("number-above-zero"));
                                return true;
                            }
                        } catch (NumberFormatException e) {
                            commandSender.sendMessage(getTranslation("correct-number"));
                            return true;
                        }

                        List<String> lore = guiFileConfig.getStringList("rewards." + day + ".lore");

                        int index;
                        try {
                            index = Integer.parseInt(strings[3]);
                            if (index <0) {
                                commandSender.sendMessage(getTranslation("number-above-zero"));
                                return true;
                            }
                            if (index >= lore.size()) {
                                commandSender.sendMessage(getTranslation("index-within-list"));
                                return true;
                            }
                        } catch (NumberFormatException e) {
                            commandSender.sendMessage(getTranslation("correct-number"));
                            return true;
                        }

                        String line = String.join(" ", Arrays.copyOfRange(strings, 4, strings.length));

                        lore.set(index, line);

                        guiFileConfig.set("rewards." + day + ".lore", lore);

                        DailyRewards.GUI_CONFIG.save();

                        commandSender.sendMessage(getTranslation("lore-set"));
                    }
                }
                case "name" -> {
                    if (strings.length < 2) {
                        commandSender.sendMessage(getTranslation("correct-information"));
                        return true;
                    }

                    int day;
                    try {
                        day = Integer.parseInt(strings[1]);
                        if (day <=0) {
                            commandSender.sendMessage(getTranslation("number-above-zero"));
                            return true;
                        }
                    } catch (NumberFormatException e) {
                        commandSender.sendMessage(getTranslation("correct-number"));
                        return true;
                    }

                    String name = String.join(" ", Arrays.copyOfRange(strings, 2, strings.length));

                    guiFileConfig.set("rewards." + day + ".name", name);

                    DailyRewards.GUI_CONFIG.save();

                    commandSender.sendMessage(getTranslation("name-added"));
                }
                default ->{
                    commandSender.sendMessage(getTranslation("correct-information"));
                    return true;
                }
            }
        }
        else {
            commandSender.sendMessage(getTranslation("correct-information"));
            return true;
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, @NonNull String alias, String @NonNull [] strings) {
        if(commandSender.hasPermission(adminPermission())){
            if(strings.length==1){
                return List.of("add","remove","reload","set","lore","name");
            }
            else if(strings.length==2){
                switch (strings[0]) {
                    case "add" -> {
                        return List.of("item", "command", "type", "opened", "can_open","decoration");
                    }
                    case "remove", "set", "name" -> {
                        return List.of("<day>","1","2","3","4","5","6","10","100");
                    }
                    case "lore" -> {
                        return List.of("add", "set");
                    }
                }
            }
            else if(strings.length==3){
                switch (strings[0]) {
                    case "add" -> {
                        if (strings[1].equals("type")) {
                            return List.of("<type>");
                        }
                        else if (strings[1].equals("decoration")){
                            return List.of("0","1","2","3","4","5","10","27","54");
                        }
                        else if (!strings[1].equals("opened")&&!strings[1].equals("can_open")) {
                            return List.of("<day>", "1", "2", "3", "4", "5", "6", "10", "100");
                        }
                    }
                    case "set" -> {
                        return List.of("type","slot");
                    }
                    case "lore" -> {
                        return List.of("<day>","1","2","3","4","5","6","10","100");
                    }
                    case "name" -> {
                        return List.of("<name>");
                    }
                }
            }
            else if(strings.length==4){
                switch (strings[0]) {
                    case "add" -> {
                        if (strings[1].equals("command")) {
                            return List.of("<command>");
                        }
                    }
                    case "lore" -> {
                        if (strings[1].equals("set")) {
                            List<String> list = new ArrayList<>();
                            try {
                                int day = Integer.parseInt(strings[2]);

                                int size = DailyRewards.GUI_CONFIG.getConfig().getStringList("rewards." + day + ".lore").size();

                                for(int i=0;i<size;i++){
                                    list.add(i+"");
                                }

                            }
                            catch (NumberFormatException ignored){

                            }
                            return list;
                        }
                        if (strings[1].equals("add")) {
                            return List.of("<lore>");
                        }
                    }
                    case "set" -> {
                        if (strings[2].equals("type")) {
                            ConfigurationSection section = DailyRewards.GUI_CONFIG.getConfig().getConfigurationSection("types");
                            List<String> list = new ArrayList<>();
                            if (section != null) {
                                list.addAll(section.getKeys(false));
                            }
                            return list;
                        }
                        else if (strings[2].equals("slot")){
                            return List.of("0","1","2","3","4","5","10","27","54");
                        }
                    }
                }
            }
            else if(strings.length==5){
                if(strings[0].equals("lore")&&strings[1].equals("set")){
                    return List.of("<lore>");
                }
            }
        }
        return List.of();
    }
}
