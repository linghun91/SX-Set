package cn.i7mc.sxset.data;

// 导入所需的依赖类
import lombok.Data;  // 导入Lombok的@Data注解，用于自动生成getter、setter等方法
import org.bukkit.configuration.ConfigurationSection;  // 导入Bukkit配置节点类，用于读取配置文件

// 导入Java集合框架相关类
import java.util.HashMap;  // 导入HashMap类，用于存储键值对数据
import java.util.List;     // 导入List接口，用于存储列表数据
import java.util.Map;      // 导入Map接口，用于定义映射关系

/**
 * 套装数据类
 * 用于存储单个套装的所有相关信息，包括套装ID、名称、部件和属性等
 */
@Data  // Lombok注解，自动生成所有字段的getter、setter、equals、hashCode和toString方法
public class SetData {
    // 套装的唯一标识符
    private String setId;
    
    // 套装的显示名称
    private String name;
    
    // 存储套装不同件数对应的属性列表
    // Key: 激活所需的套装件数（例如2件套、3件套等）
    // Value: 该件数激活时获得的属性列表
    private Map<Integer, List<String>> pieceAttributes;
    
    // 存储套装各个部件的识别关键字
    // Key: 部件位置（如头盔、胸甲等）
    // Value: 用于在物品Lore中识别该部件的关键字
    private Map<String, String> pieces;

    /**
     * 套装数据类的构造函数
     * 从配置文件节点中读取并初始化套装数据
     *
     * @param setId 套装的唯一标识符
     * @param config 包含套装配置信息的配置节点
     */
    public SetData(String setId, ConfigurationSection config) {
        // 初始化套装ID
        this.setId = setId;
        
        // 设置套装名称，如果配置中未指定则使用套装ID作为名称
        this.name = config.getString("name", setId);
        
        // 初始化存储套装件数属性的Map
        this.pieceAttributes = new HashMap<>();
        
        // 初始化存储套装部件识别关键字的Map
        this.pieces = new HashMap<>();

        // 从配置中加载套装部件信息
        ConfigurationSection piecesSection = config.getConfigurationSection("pieces");
        // 如果存在部件配置节点
        if (piecesSection != null) {
            // 遍历所有配置的部件
            for (String key : piecesSection.getKeys(false)) {
                // 将部件位置和对应的识别关键字存入Map
                pieces.put(key, piecesSection.getString(key));
            }
        }

        // 从配置中加载套装属性信息
        ConfigurationSection attributesSection = config.getConfigurationSection("attributes");
        // 如果存在属性配置节点
        if (attributesSection != null) {
            // 遍历所有件数的属性配置
            for (String key : attributesSection.getKeys(false)) {
                // 将配置中的字符串key转换为整数（件数）
                int pieceCount = Integer.parseInt(key);
                // 将件数和对应的属性列表存入Map
                pieceAttributes.put(pieceCount, attributesSection.getStringList(key));
            }
        }
    }
}
