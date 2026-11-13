package org.dailyRewards.Getter;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.dailyRewards.DailyRewards;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Gui {

    public static NamespacedKey DAY_KEY = new NamespacedKey(DailyRewards.getInstance(),"day");

    public static String getTitle(){
        return DailyRewards.getInstance().getConfig().getString("gui.title","§aDaily");
    }

    public static ItemStack getCanOpen(){
        return DailyRewards.GUI_CONFIG.getConfig().getItemStack("can_open",new ItemStack(Material.DIAMOND_BLOCK));
    }

    public static long oneDay(){
        return DailyRewards.getInstance().getConfig().getLong("how-long-is-day",86400000);
    }


    public static Inventory getGui(Player p){
        FileConfiguration config = DailyRewards.getInstance().getConfig();
        FileConfiguration playerCollectedFileConfig = DailyRewards.PLAYER_COLLECTED.getConfig();
        FileConfiguration guiFileConfig = DailyRewards.GUI_CONFIG.getConfig();

        int size = config.getInt("gui.size",27);
        if(size%9!=0||size>54||size<=0){
            size=27;
        }

        Inventory gui = Bukkit.createInventory(p,size,getTitle());

        for(int i=0;i<size;i++){
            int day=-1;
            ConfigurationSection rewards = guiFileConfig.getConfigurationSection("rewards");
            if(rewards!=null){
                for(String dayString : rewards.getKeys(false)){
                    int slot = rewards.getInt(dayString+".slot",-1);
                    if(slot==i){
                        try {
                            day=Integer.parseInt(dayString);
                            break;
                        }
                        catch (NumberFormatException ignored){
                        }
                    }
                }
            }

            if(day==-1){
                day=i+1;

                if(guiFileConfig.contains("rewards."+day+".slot")){
                    continue;
                }
            }

            if(guiFileConfig.contains("rewards."+day)) {
                Date date = new Date();
                Date playerDate = (Date) playerCollectedFileConfig.get(p.getUniqueId()+".lastDayCollected");
                if(playerDate==null){
                    playerDate=new Date();
                }


                if(date.getTime()-playerDate.getTime()>=oneDay()*2){
                    playerCollectedFileConfig.set(p.getUniqueId()+".day",0);
                    date.setTime(date.getTime()-oneDay());
                    playerCollectedFileConfig.set(p.getUniqueId()+".lastDayCollected",date);
                    DailyRewards.PLAYER_COLLECTED.save();

                    return getGui(p);
                }
                else if(day<=playerCollectedFileConfig.getInt(p.getUniqueId()+".day")){
                    gui.setItem(i,guiFileConfig.getItemStack("opened",new ItemStack(Material.COAL_BLOCK)));
                }
                else if(date.getTime()-playerDate.getTime()>=oneDay()&&date.getTime()-playerDate.getTime()<oneDay()*2&&playerCollectedFileConfig.getInt(p.getUniqueId()+".day")+1==day){
                    ItemStack itemStack = getCanOpen().clone();

                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.getPersistentDataContainer().set(DAY_KEY, PersistentDataType.INTEGER,day);
                    itemStack.setItemMeta(itemMeta);
                    gui.setItem(i,itemStack);
                }
                else {
                    ItemStack itemStack=  guiFileConfig.getItemStack("types." + guiFileConfig.getString("rewards." + day + ".type",""),new ItemStack(Material.EMERALD_BLOCK)).clone();

                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if(itemMeta!=null) {
                        List<String> loreList = new ArrayList<>();
                        for (String lore : guiFileConfig.getStringList("rewards." + day + ".lore")) {
                            String loreAdd = lore.replace("%player%",p.getName());
                            if(DailyRewards.IS_PLACEHOLDER_API){
                                loreAdd= PlaceholderAPI.setPlaceholders(p,loreAdd);
                            }
                            loreList.add(ChatColor.translateAlternateColorCodes('&', loreAdd));
                        }

                        itemMeta.setLore(loreList);

                        if (guiFileConfig.contains("rewards." + day + ".name")) {
                            String name = guiFileConfig.getString("rewards." + day + ".name", "");
                            String nameAdd = name.replace("%player%",p.getName());
                            if(DailyRewards.IS_PLACEHOLDER_API){
                                nameAdd=PlaceholderAPI.setPlaceholders(p,nameAdd);
                            }
                            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', nameAdd));
                        }

                        itemStack.setItemMeta(itemMeta);
                    }

                    gui.setItem(i,itemStack);
                }
            }
            else if (guiFileConfig.contains("decorations."+i)){
                gui.setItem(i,guiFileConfig.getItemStack("decorations."+i));
            }
        }

        return gui;
    }
}
