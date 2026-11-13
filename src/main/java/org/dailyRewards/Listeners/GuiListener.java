package org.dailyRewards.Listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.dailyRewards.DailyRewards;
import org.dailyRewards.Getter.Gui;

import java.util.*;

import static org.dailyRewards.Getter.Language.getTranslation;

public class GuiListener implements Listener {



    @EventHandler
    public void PlayerJoin(PlayerJoinEvent e){
        FileConfiguration playerFileConfig = DailyRewards.PLAYER_COLLECTED.getConfig();
        if(!playerFileConfig.contains(e.getPlayer().getUniqueId().toString())){
            String uuid = e.getPlayer().getUniqueId().toString();
            playerFileConfig.set(uuid+".day",0);
            Date date = new Date();
            date.setTime(date.getTime()-Gui.oneDay());
            playerFileConfig.set(uuid+".lastDayCollected",date);
            DailyRewards.PLAYER_COLLECTED.save();
        }
    }

    @EventHandler
    public void InventoryClick(InventoryClickEvent e){
        if(e.getView().getTitle().equals(Gui.getTitle())&&e.getInventory().getHolder()!=null&&e.getInventory().getHolder().equals(e.getWhoClicked())){
            e.setCancelled(true);
            if(e.getClickedInventory()!=null&&e.getClickedInventory().equals(e.getView().getTopInventory())){
                FileConfiguration guiFileConfig = DailyRewards.GUI_CONFIG.getConfig();
                Player p = (Player) e.getWhoClicked();

                ItemStack itemStack = e.getCurrentItem();

                if(itemStack==null){
                    return;
                }

                ItemMeta itemMeta = itemStack.getItemMeta();
                if(itemMeta==null){
                    return;
                }

                if(!itemMeta.getPersistentDataContainer().has(Gui.DAY_KEY, PersistentDataType.INTEGER)){
                    return;
                }

                int day = itemMeta.getPersistentDataContainer().get(Gui.DAY_KEY,PersistentDataType.INTEGER);

                List<ItemStack> itemStackList = DailyRewards.transformList(guiFileConfig.getList("rewards." + day + ".items",new ArrayList<>()),ItemStack.class);

                if(36< DailyRewards.sizeOfArrayWithoutNull(p.getInventory().getStorageContents())+itemStackList.size()){
                    p.sendMessage(getTranslation("too-litle-space"));
                    return;
                }

                for (ItemStack itemStack1 : itemStackList) {
                    p.getInventory().addItem(itemStack1.clone());
                }

                List<String> commandList = guiFileConfig.getStringList("rewards."+day+".commands");

                for(String command : commandList){
                    String executeCommand = command.replace("%player%",p.getName());

                    if(DailyRewards.IS_PLACEHOLDER_API){
                        executeCommand= PlaceholderAPI.setPlaceholders(p,executeCommand);
                    }

                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),executeCommand);
                }

                FileConfiguration playerFileConfig = DailyRewards.PLAYER_COLLECTED.getConfig();

                playerFileConfig.set(p.getUniqueId()+".day",playerFileConfig.getInt(p.getUniqueId()+".day")+1);
                playerFileConfig.set(p.getUniqueId()+".lastDayCollected", new Date());

                DailyRewards.PLAYER_COLLECTED.save();

                p.sendMessage(getTranslation("received"));
                p.closeInventory();
            }
        }
    }
}
