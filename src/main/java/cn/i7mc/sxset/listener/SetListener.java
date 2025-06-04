package cn.i7mc.sxset.listener;

import cn.i7mc.sxset.SXSet;
import cn.i7mc.sxset.manager.SetManager;
import github.saukiya.sxattribute.event.SXGetAttributeEvent;
import github.saukiya.sxattribute.data.attribute.SXAttributeData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * 套装监听器类
 * 负责监听并处理与套装属性相关的事件
 */
public class SetListener implements Listener {

    /**
     * 处理属性更新事件
     * 当玩家的属性需要更新时，此方法会被调用
     * 用于为玩家添加当前激活的套装属性
     *
     * @param event SX属性获取事件，包含了需要更新属性的实体信息
     */
    @EventHandler
    public void onAttributeUpdate(SXGetAttributeEvent event) {
        // 检查属性更新的实体是否为玩家
        if (event.getEntity() instanceof Player player) {
            // 获取属性数据对象
            SXAttributeData data = event.getData();
            if (data != null) {
                // 通过SetManager获取并添加玩家当前激活的套装属性
                data.add(SetManager.getInst().getSetAttributes(player));
            }
        }
    }

    /**
     * 处理玩家加入服务器事件
     * 当玩家加入时更新套装LORE显示
     *
     * @param event 玩家加入事件
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // 延迟1tick更新套装LORE，确保玩家数据完全加载
        Bukkit.getScheduler().runTaskLater(SXSet.getInst(), () -> {
            SetManager.getInst().updatePlayerSetLore(player);
        }, 1L);
    }

    /**
     * 处理背包点击事件
     * 当玩家在装备栏进行操作时更新套装LORE显示
     *
     * @param event 背包点击事件
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        // 检查是否为玩家操作且未被取消
        if (event.isCancelled() || !(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        // 检查是否为装备栏操作
        if (event.getInventory().getType() == InventoryType.CRAFTING ||
            event.getInventory().getType() == InventoryType.PLAYER) {

            // 检查是否涉及装备槽位（36-39为装备槽位）
            int slot = event.getSlot();
            if (slot >= 36 && slot <= 39) {
                // 延迟1tick更新套装LORE，确保装备变化完成
                Bukkit.getScheduler().runTaskLater(SXSet.getInst(), () -> {
                    SetManager.getInst().updatePlayerSetLore(player);
                }, 1L);
            }
        }
    }

    /**
     * 处理玩家切换手持物品事件
     * 虽然不直接影响装备，但为了保持一致性也进行检查
     *
     * @param event 玩家切换手持物品事件
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        // 延迟1tick更新套装LORE，确保状态同步
        Bukkit.getScheduler().runTaskLater(SXSet.getInst(), () -> {
            SetManager.getInst().updatePlayerSetLore(event.getPlayer());
        }, 1L);
    }
}
