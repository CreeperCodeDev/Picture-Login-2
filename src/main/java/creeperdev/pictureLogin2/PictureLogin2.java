package creeperdev.pictureLogin2;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.util.Base64;

public class Picturelogin2 extends JavaPlugin implements Listener {

    private static final String MINECRAFT_HEAD_API = "https://mc-heads.net/avatar/";
    private OkHttpClient httpClient;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        // 初始化配置
        saveDefaultConfig();
        config = getConfig();

        // 初始化HTTP客戶端
        httpClient = new OkHttpClient();

        // 註冊事件監聽器
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("JoinPhoto插件已啟動!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        // 異步加載並顯示玩家頭像
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                String avatarUrl = MINECRAFT_HEAD_API + playerName + "/64";
                String base64Avatar = fetchBase64Avatar(avatarUrl);

                // 回到主線程顯示
                Bukkit.getScheduler().runTask(this, () -> {
                    displayPlayerAvatar(player, base64Avatar);
                });
            } catch (Exception e) {
                getLogger().warning("無法獲取玩家頭像: " + e.getMessage());
            }
        });
    }

    private String fetchBase64Avatar(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("伺服器返回異常: " + response.code());
            }
            byte[] imageBytes = response.body().bytes();
            return Base64.getEncoder().encodeToString(imageBytes);
        }
    }

    private void displayPlayerAvatar(Player player, String base64Avatar) {
        // 使用 Unicode 方塊字符來創建像素化的圖像顯示
        String[] pixelRows = convertBase64ToPixels(base64Avatar);

        for (String row : pixelRows) {
            Component message = Component.text(row)
                    .color(TextColor.color(0x000000)); // 黑色文本
            player.sendMessage(message);
        }
    }

    private String[] convertBase64ToPixels(String base64Image) {
        // 這裡是一個簡化的像素轉換方法
        // 實際實現會更加複雜，需要解碼Base64並轉換為像素
        return new String[]{
                "▓▓▓▓▓▓▓▓",
                "▓░░░░░░▓",
                "▓░▓░░▓░▓",
                "▓░░░░░░▓",
                "▓▓▓▓▓▓▓▓"
        };
    }

    @Override
    public void onDisable() {
        getLogger().info("JoinPhoto插件已關閉!");
    }
}