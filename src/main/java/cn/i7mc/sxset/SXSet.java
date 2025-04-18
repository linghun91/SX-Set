// 定义包名
package cn.i7mc.sxset;

// 导入所需的依赖类
import cn.i7mc.sxset.listener.SetListener;    // 导入套装监听器类
import cn.i7mc.sxset.manager.SetManager;      // 导入套装管理器类
import github.saukiya.sxattribute.SXAttribute; // 导入SX-Attribute主类
import github.saukiya.sxattribute.api.SXAPI;   // 导入SX-Attribute API接口
import lombok.Getter;                          // 导入Lombok的Getter注解
import org.bukkit.Bukkit;                      // 导入Bukkit服务器核心类
import org.bukkit.plugin.java.JavaPlugin;      // 导入Bukkit插件基类

/**
 * SX-Set插件主类
 * 继承自JavaPlugin，负责插件的初始化、启动和关闭等生命周期管理
 */
public class SXSet extends JavaPlugin {
    // 使用Lombok的@Getter注解，自动生成getInstance()方法
    @Getter
    // 存储插件实例的静态变量，用于单例模式
    private static SXSet inst;
    
    // 使用Lombok的@Getter注解，自动生成getApi()方法
    @Getter
    // 存储SX-Attribute API的实例
    private SXAPI api;

    /**
     * 插件启用时的处理方法
     * 负责初始化插件的各个组件
     */
    @Override
    public void onEnable() {
        // 初始化插件实例，用于单例模式
        inst = this;
        
        // 检查依赖插件SX-Attribute是否已加载
        if (Bukkit.getPluginManager().getPlugin("SX-Attribute") == null) {
            // 如果未找到依赖插件，输出错误日志
            getLogger().severe("未找到SX-Attribute插件，插件将被禁用！");
            // 禁用当前插件
            Bukkit.getPluginManager().disablePlugin(this);
            // 终止后续初始化
            return;
        }
        
        // 获取并保存SX-Attribute的API实例
        api = SXAttribute.getApi();
        
        // 保存默认配置文件
        saveDefaultConfig();
        // 确保插件数据文件夹存在
        if (!getDataFolder().exists()) {
            // 如果文件夹不存在，创建它
            getDataFolder().mkdirs();
        }

        // 初始化套装管理器，加载所有套装配置
        SetManager.getInst().reload();

        // 向Bukkit注册事件监听器
        Bukkit.getPluginManager().registerEvents(new SetListener(), this);

        // 输出插件启用成功的日志信息
        getLogger().info("SX-Set 套装插件已启用！");
    }

    /**
     * 插件禁用时的处理方法
     * 负责清理插件资源
     */
    @Override
    public void onDisable() {
        // 输出插件禁用的日志信息
        getLogger().info("SX-Set 套装插件已禁用！");
    }
}
