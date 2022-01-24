package com.thizthizzydizzy.resourcespawner;
import com.thizthizzydizzy.resourcespawner.scanner.Scanner;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
public class CommandResourceSpawner implements TabExecutor{
    private final ResourceSpawnerCore plugin;
    public CommandResourceSpawner(ResourceSpawnerCore plugin){
        this.plugin = plugin;
    }
    private final ArrayList<ResourceSpawnerCommand> commands = new ArrayList<>();
    {
        commands.add(new ResourceSpawnerCommand("help"){
            @Override
            protected boolean run(CommandSender sender, Command command, String label, String[] args){
                for(ResourceSpawnerCommand cmd : commands){
                    if(cmd.hasPermission(sender)){
                        String s = cmd.getFullUsage();
                        if(s!=null)sender.sendMessage("/resourcespawner "+s);
                    }
                }
                return true;
            }
        });
        ResourceSpawnerCommand debugOn = new ResourceSpawnerCommand("on", "debug"){
            @Override
            protected boolean run(CommandSender sender, Command command, String label, String[] args){
                ResourceSpawnerCore.debug = true;
                sender.sendMessage("Debug mode enabled");
                return true;
            }
        };
        ResourceSpawnerCommand debugOff = new ResourceSpawnerCommand("off", "debug"){
            @Override
            protected boolean run(CommandSender sender, Command command, String label, String[] args){
                ResourceSpawnerCore.debug = false;
                sender.sendMessage("Debug mode disabled");
                return true;
            }
        };
        commands.add(new ResourceSpawnerCommand("debug", debugOn, debugOff) {
            @Override
            protected boolean run(CommandSender sender, Command command, String label, String[] args){
                ResourceSpawnerCore.debug = !ResourceSpawnerCore.debug;
                sender.sendMessage("Debug mode "+(ResourceSpawnerCore.debug?"enabled":"disabled"));
                return true;
            }
        });
        commands.add(new ResourceSpawnerCommand("scan") {
            @Override
            protected boolean run(CommandSender sender, Command command, String label, String[] args){
                if(sender instanceof Player){
                    Location loc = ((Player)sender).getLocation();
                    sender.sendMessage("Scanning...");
                    ArrayList<String> messages = new ArrayList<>();
                    for(Scanner scanner : plugin.activeScanners){
                        ArrayList<String> theseMessages = scanner.scan(plugin, loc);
                        for(int i = 0; i<Math.min(scanner.maxResults, theseMessages.size()); i++){
                            messages.add(theseMessages.get(i));
                        }
                    }
                    for(String s : messages)sender.sendMessage(s);
                    if(messages.isEmpty())sender.sendMessage("Nothing was found");
                }
                return true;
            }
        });
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(args.length<1){
            sendUsageResponse(sender);
            return true;
        }
        for(ResourceSpawnerCommand cmd : commands){
            if(args[0].equals(cmd.command)){
                return cmd.onCommand(sender, command, label, trim(args, 1), args);
            }
        }
        sendUsageResponse(sender);
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
        ArrayList<String> strs = new ArrayList<>();
        if(args.length==1){
            for(ResourceSpawnerCommand cmd : commands){
                if(cmd.command.substring(0, cmd.command.length()-1).startsWith(args[0])&&cmd.hasPermission(sender))strs.add(cmd.command);
            }
        }
        if(args.length>1){
            for(ResourceSpawnerCommand cmd : commands){
                if(args[0].equals(cmd.command))return cmd.onTabComplete(sender, command, label, trim(args, 1));
            }
        }
        return strs;
    }
    public String[] trim(String[] data, int beginning){
        if(data==null)return null;
        String[] newData = new String[Math.max(0,data.length-beginning)];
        for(int i = 0; i<newData.length; i++){
            newData[i] = data[i+beginning];
        }
        return newData;
    }
    private String getFullUsage(CommandSender sender){
        String usage = "/resourcespawner ";
        boolean foundValidCommand = false;
        String subUsage = "";
        for(ResourceSpawnerCommand cmd : commands){
            if(cmd.hasPermission(sender)){
                subUsage+="|"+cmd.getFullUsage();
                foundValidCommand = true;
            }
        }
        if(!foundValidCommand)return null;
        usage+=subUsage.substring(1);
        return usage;
    }
    private void sendUsageResponse(CommandSender sender){
        String usage = getFullUsage(sender);
        if(usage==null)sender.sendMessage(ChatColor.RED+"Unknown Command");
        else sender.sendMessage("Usage: "+usage);
    }
}