package cn.i7mc.sxset.manager;

import cn.i7mc.sxset.SXSet;  
import cn.i7mc.sxset.data.SetData;  
import github.saukiya.sxattribute.data.attribute.SXAttributeData; 
import github.saukiya.sxattribute.util.NbtUtil;  
import lombok.Getter;  
import org.bukkit.configuration.ConfigurationSection; 
import org.bukkit.configuration.file.YamlConfiguration; 
import org.bukkit.entity.Player; 
import org.bukkit.inventory.ItemStack; 
import org.bukkit.inventory.meta.ItemMeta; 

import java.io.File;  
import java.util.*;  

/**
 * 套装管理器类
 * 负责管理所有套装的数据和相关操作
 */
public class SetManager {
    // 使用单例模式，创建唯一的SetManager实例
    @Getter  // Lombok注解，自动生成getInstance()方法
    private static final SetManager inst = new SetManager();
    
    // 存储所有套装数据的Map，键为套装ID，值为套装数据对象
    private final Map<String, SetData> setDataMap = new HashMap<>();
    
    // 插件实例的引用
    private final SXSet plugin;

    /**
     * 私有构造函数，确保单例模式
     */
    private SetManager() {
        // 初始化时获取插件实例
        this.plugin = SXSet.getInst();
    }

    /**
     * 重新加载所有套装数据
     */
    public void reload() {
        // 清空现有的套装数据
        setDataMap.clear();
        
        // 获取套装配置文件夹
        File setsFolder = new File(plugin.getDataFolder(), "sets");
        
        // 如果文件夹不存在，创建它并保存示例配置
        if (!setsFolder.exists()) {
            setsFolder.mkdirs();  // 创建文件夹
            plugin.saveResource("sets/example.yml", false);  // 保存示例配置文件
        }

        // 获取所有.yml结尾的配置文件
        File[] files = setsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        
        // 如果存在配置文件
        if (files != null) {
            // 遍历每个配置文件
            for (File file : files) {
                // 加载YAML配置
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                // 遍历配置中的每个套装ID
                for (String setId : config.getKeys(false)) {
                    // 获取套装配置节点
                    ConfigurationSection setSection = config.getConfigurationSection(setId);
                    // 如果节点存在，创建并存储套装数据
                    if (setSection != null) {
                        setDataMap.put(setId, new SetData(setId, setSection));
                    }
                }
            }
        }
    }

    /**
     * 获取玩家当前激活的套装属性
     * @param player 目标玩家
     * @return 套装属性数据
     */
    public SXAttributeData getSetAttributes(Player player) {
        // 创建新的属性数据对象
        SXAttributeData attributeData = new SXAttributeData();
        // 用于统计每个套装的件数
        Map<String, Integer> setPieceCounts = new HashMap<>();

        // 获取玩家装备栏的所有物品
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        // 遍历每个装备槽位的物品
        for (ItemStack item : armorContents) {
            // 检查物品是否存在且有元数据
            if (item != null && item.hasItemMeta()) {
                // 获取物品的元数据
                ItemMeta meta = item.getItemMeta();
                // 检查是否有lore
                if (meta.hasLore()) {
                    // 从lore中获取套装ID
                    String setId = getSetIdFromLore(meta.getLore());
                    // 如果物品属于某个套装，增加计数
                    if (setId != null) {
                        setPieceCounts.merge(setId, 1, Integer::sum);
                    }
                }
            }
        }

        // 处理每个套装的效果
        for (Map.Entry<String, Integer> entry : setPieceCounts.entrySet()) {
            // 获取套装数据
            SetData setData = setDataMap.get(entry.getKey());
            // 如果套装数据存在
            if (setData != null) {
                // 获取当前套装的件数
                int pieceCount = entry.getValue();
                // 获取所有件数阈值并排序
                List<Integer> thresholds = new ArrayList<>(setData.getPieceAttributes().keySet());
                Collections.sort(thresholds);
                // 遍历每个件数阈值
                for (Integer threshold : thresholds) {
                    // 如果当前件数达到或超过阈值，添加对应属性
                    if (pieceCount >= threshold) {
                        attributeData.add(SXSet.getInst().getApi().loadListData(setData.getPieceAttributes().get(threshold)));
                    }
                }
            }
        }

        // 更新所有套装装备的LORE显示状态
        updateAllSetLoreForPlayer(player, setPieceCounts);

        // 返回最终的属性数据
        return attributeData;
    }

    /**
     * 从物品lore中获取套装ID
     * @param lore 物品的lore列表
     * @return 套装ID，如果不属于任何套装则返回null
     */
    private String getSetIdFromLore(List<String> lore) {
        // 遍历所有已加载的套装数据
        for (SetData setData : setDataMap.values()) {
            // 获取套装名称
            String setName = setData.getName();
            // 遍历物品的每一行lore
            for (String loreLine : lore) {
                // 移除颜色代码后进行比较
                if (loreLine.replace("§", "&").contains(setName)) {
                    return setData.getSetId();  // 返回匹配的套装ID
                }
            }
        }
        return null;  // 未找到匹配的套装
    }

    /**
     * 更新玩家所有套装装备的LORE状态显示
     * @param player 目标玩家
     * @param setPieceCounts 所有套装的件数统计
     */
    private void updateAllSetLoreForPlayer(Player player, Map<String, Integer> setPieceCounts) {
        // 获取玩家装备栏的所有物品
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        boolean hasUpdates = false;

        // 遍历装备栏的每个位置
        for (int i = 0; i < armorContents.length; i++) {
            // 获取当前位置的物品
            ItemStack item = armorContents[i];
            // 检查物品是否存在且有元数据
            if (item != null && item.hasItemMeta()) {
                // 获取物品的元数据
                ItemMeta meta = item.getItemMeta();
                // 检查是否有lore
                if (meta.hasLore()) {
                    // 从lore中获取套装ID
                    String setId = getSetIdFromLore(meta.getLore());
                    if (setId != null) {
                        // 获取套装数据
                        SetData setData = setDataMap.get(setId);
                        if (setData != null) {
                            // 获取当前套装的件数（如果没有则为0）
                            int currentPieceCount = setPieceCounts.getOrDefault(setId, 0);
                            // 获取套装总件数
                            int totalPieces = setData.getTotalPieces();

                            // 更新物品的套装状态显示
                            if (updateItemSetLore(meta, setData, currentPieceCount, totalPieces)) {
                                item.setItemMeta(meta);
                                armorContents[i] = item;
                                hasUpdates = true;
                            }
                        }
                    }
                }
            }
        }

        // 如果有更新，将更新后的装备应用到玩家身上
        if (hasUpdates) {
            player.getInventory().setArmorContents(armorContents);
        }
    }

    /**
     * 更新单个物品的套装LORE状态显示
     * @param meta 物品元数据
     * @param setData 套装数据
     * @param currentPieceCount 当前套装件数
     * @param totalPieces 套装总件数
     * @return 是否有更新
     */
    private boolean updateItemSetLore(ItemMeta meta, SetData setData, int currentPieceCount, int totalPieces) {
        List<String> lore = meta.getLore();
        boolean updated = false;

        // 遍历每一行lore
        for (int j = 0; j < lore.size(); j++) {
            String line = lore.get(j);
            // 检查是否包含套装名称（移除颜色代码后比较）
            String cleanLine = line.replace("§", "&");
            if (cleanLine.contains(setData.getName())) {
                // 构建新的套装状态显示
                String newStatusDisplay = "(" + currentPieceCount + "/" + totalPieces + ")";

                // 查找现有的状态显示位置
                int lastParenIndex = line.lastIndexOf("(");
                if (lastParenIndex != -1) {
                    // 替换现有的状态显示
                    String prefix = line.substring(0, lastParenIndex);
                    lore.set(j, prefix + newStatusDisplay);
                } else {
                    // 如果没有找到括号，在行末添加状态显示
                    lore.set(j, line + " " + newStatusDisplay);
                }
                updated = true;
                break;
            }
        }

        // 如果有更新，设置新的lore
        if (updated) {
            meta.setLore(lore);
        }

        return updated;
    }

    /**
     * 公共方法：更新玩家的套装LORE显示
     * 供外部调用，用于在装备变化时实时更新套装状态
     * @param player 目标玩家
     */
    public void updatePlayerSetLore(Player player) {
        // 重新计算套装件数
        Map<String, Integer> setPieceCounts = new HashMap<>();

        // 获取玩家装备栏的所有物品
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        // 遍历每个装备槽位的物品
        for (ItemStack item : armorContents) {
            // 检查物品是否存在且有元数据
            if (item != null && item.hasItemMeta()) {
                // 获取物品的元数据
                ItemMeta meta = item.getItemMeta();
                // 检查是否有lore
                if (meta.hasLore()) {
                    // 从lore中获取套装ID
                    String setId = getSetIdFromLore(meta.getLore());
                    // 如果物品属于某个套装，增加计数
                    if (setId != null) {
                        setPieceCounts.merge(setId, 1, Integer::sum);
                    }
                }
            }
        }

        // 更新所有套装装备的LORE显示状态
        updateAllSetLoreForPlayer(player, setPieceCounts);
    }
}
