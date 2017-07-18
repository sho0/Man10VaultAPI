package man10vaultapi.vaultapi;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;
import java.util.UUID;

public final class VaultAPI extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        setupEconomy();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Economy economy = null;

    private boolean setupEconomy() {
        Bukkit.getLogger().info("setupEconomy");
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            Bukkit.getLogger().warning("Vault plugin is not installed");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            Bukkit.getLogger().warning("Can't get vault service");
            return false;
        }
        economy = rsp.getProvider();
        Bukkit.getLogger().info("Economy setup");
        return economy != null;
    }

    /////////////////////////////////////
    //      残高確認
    /////////////////////////////////////
    public double  getBalance(UUID uuid){
        return economy.getBalance(Bukkit.getOfflinePlayer(uuid).getPlayer());
    }

    /////////////////////////////////////
    //      残高確認
    /////////////////////////////////////
    public void showBalance(UUID uuid){
        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid).getPlayer();
        double money = getBalance(uuid);
        p.getPlayer().sendMessage(ChatColor.YELLOW + "あなたの所持金は$" + (int)money);
    }
    /////////////////////////////////////
    //      引き出し
    /////////////////////////////////////
    public Boolean  withdraw(UUID uuid, double money){
        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
        if(p == null){
            Bukkit.getLogger().info(uuid.toString()+"は見つからない");
            return false;
        }
        EconomyResponse resp = economy.withdrawPlayer(p,money);
        if(resp.transactionSuccess()){
            if(p.isOnline()) {
                p.getPlayer().sendMessage(ChatColor.YELLOW + "$" + (int) money + "支払いました");
            }
            return true;
        }
        return  false;
    }
    /////////////////////////////////////
    //      お金を入れる
    /////////////////////////////////////
    public Boolean  deposit(UUID uuid,double money){
        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
        if(p == null){
            Bukkit.getLogger().info(uuid.toString()+"は見つからない");

            return false;
        }
        EconomyResponse resp = economy.depositPlayer(p,money);
        if(resp.transactionSuccess()){
            if(p.isOnline()){
                p.getPlayer().sendMessage(ChatColor.YELLOW + "$"+(int)money+"受取りました");
            }
            return true;
        }

        return  false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("test")){
            Player p = (Player) sender;
            p.sendMessage(String.valueOf(getBalance(p.getUniqueId())));
        }
        return false;
    }
}
