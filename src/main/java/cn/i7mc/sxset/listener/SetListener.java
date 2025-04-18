package cn.i7mc.sxset.listener;

import cn.i7mc.sxset.SXSet;
import cn.i7mc.sxset.manager.SetManager;
import github.saukiya.sxattribute.event.SXGetAttributeEvent;
import github.saukiya.sxattribute.data.attribute.SXAttributeData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
}
