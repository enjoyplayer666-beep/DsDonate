package ru.dscraft.dsdonate;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DsDonate - меню привилегий /donate: красители привилегий с описанием возможностей,
 * сундуки китов и кнопка "Закрыть". Всё оформление - в config.yml.
 */
public final class DsDonatePlugin extends JavaPlugin implements Listener {

    /** &-коды и hex вида &#RRGGBB. */
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();

    /** Меню - по нему отличаем свой инвентарь от чужих. */
    private static final class Menu implements InventoryHolder {
        private Inventory inventory;
        private final Map<Integer, String> actions = new HashMap<>();

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("DsDonate включен: /donate");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("dsdonate")) {
            if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                sender.sendMessage(text("&a[DsDonate] Конфиг перезагружен."));
            } else {
                sender.sendMessage(text("&7Использование: /dsdonate reload"));
            }
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Меню можно открыть только в игре.");
            return true;
        }
        open(player);
        return true;
    }

    // ---------------- меню ----------------

    private void open(Player player) {
        int rows = Math.max(1, Math.min(6, getConfig().getInt("menu.rows", 5)));
        Menu menu = new Menu();
        Inventory inv = Bukkit.createInventory(menu, rows * 9, text(getConfig().getString("menu.title", "Привилегии")));
        menu.inventory = inv;

        ConfigurationSection privileges = getConfig().getConfigurationSection("privileges");
        if (privileges != null) {
            for (String key : privileges.getKeys(false)) {
                ConfigurationSection s = privileges.getConfigurationSection(key);
                if (s == null) continue;
                place(inv, s);
                menu.actions.put(s.getInt("slot", -1), "message");
            }
        }
        ConfigurationSection items = getConfig().getConfigurationSection("items");
        if (items != null) {
            for (String key : items.getKeys(false)) {
                ConfigurationSection s = items.getConfigurationSection(key);
                if (s == null) continue;
                place(inv, s);
                menu.actions.put(s.getInt("slot", -1), s.getString("click", "none"));
            }
        }
        player.openInventory(inv);
    }

    private void place(Inventory inv, ConfigurationSection s) {
        int slot = s.getInt("slot", -1);
        if (slot < 0 || slot >= inv.getSize()) return;
        Material material = Material.matchMaterial(s.getString("material", "STONE"));
        if (material == null || material.isAir()) material = Material.BARRIER;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(noItalic(text(s.getString("name", ""))));
            List<Component> lore = new ArrayList<>();
            for (String line : s.getStringList("lore")) lore.add(noItalic(text(line)));
            meta.lore(lore);
            meta.addItemFlags(ItemFlag.values());
            item.setItemMeta(meta);
        }
        inv.setItem(slot, item);
    }

    // ---------------- клики ----------------

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof Menu) event.setCancelled(true);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof Menu menu)) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() != event.getInventory()) return;

        String action = menu.actions.get(event.getRawSlot());
        if (action == null) return;
        if (action.equalsIgnoreCase("close")) {
            player.closeInventory();
        } else if (action.equalsIgnoreCase("message")) {
            String msg = getConfig().getString("menu.click-message", "");
            if (msg != null && !msg.isEmpty()) {
                Component c = text(msg);
                String url = getConfig().getString("menu.click-url", "");
                if (url != null && !url.isEmpty()) c = c.clickEvent(ClickEvent.openUrl(url));
                player.closeInventory();
                player.sendMessage(c);
            }
        } else if (action.regionMatches(true, 0, "command:", 0, 8)) {
            String cmd = action.substring(8).trim();
            if (cmd.startsWith("/")) cmd = cmd.substring(1);
            player.closeInventory();
            if (!cmd.isEmpty()) player.performCommand(cmd);
        }
    }

    // ---------------- текст ----------------

    private static Component text(String raw) {
        if (raw == null || raw.isEmpty()) return Component.empty();
        return LEGACY.deserialize(raw.replace('§', '&'));
    }

    private static Component noItalic(Component c) {
        return c.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }
}
