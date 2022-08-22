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
        commands.add(new ResourceSpawnerCommand("pause"){
            @Override
            protected boolean run(CommandSender sender, Command command, String label, String[] args){
                if(ResourceSpawnerCore.paused)sender.sendMessage("ResourceSpawner is already paused");
                ResourceSpawnerCore.paused = true;
                sender.sendMessage("Pausing Resource Spawners...");
                return true;
            }
        });
        commands.add(new ResourceSpawnerCommand("resume"){
            @Override
            protected boolean run(CommandSender sender, Command command, String label, String[] args){
                if(!ResourceSpawnerCore.paused)sender.sendMessage("ResourceSpawner is already running");
                ResourceSpawnerCore.paused = false;
                sender.sendMessage("Resuming Resource Spawners...");
                return true;
            }
        });
        ResourceSpawnerCommand tasksList = new ResourceSpawnerCommand("list", "tasks.list") {
            @Override
            protected boolean run(CommandSender sender, Command command, String label, String[] args){
                String str = "";
                for(ResourceSpawner rs : plugin.resourceSpawners){
                    int size = rs.tasks.size()+(rs.workingTask!=null?1:0);
                    str+=rs.name+" has "+size+" task"+(size==1?"":"s")+(size==0?"":":")+"\n";
                    if(rs.workingTask!=null)str+=rs.workingTask.toString()+"\n";
                    for(Task t : rs.tasks){
                        str+=t.toString()+"\n";
                    }
                }
                sender.sendMessage(str.trim());
                return true;
            }
        };
        ResourceSpawnerCommand tasksFinish = new ResourceSpawnerCommand("finish-all", "tasks.finish") {
            @Override
            protected boolean run(CommandSender sender, Command command, String label, String[] args){
                int num = 0;
                long steps = 0;
                for(ResourceSpawner rs : plugin.resourceSpawners){
                    for(Task t : rs.tasks){
                        if(rs.workingTask!=null){
                            while(!rs.workingTask.isFinished())rs.workingTask.step();
                            num++;
                        }
                        int stps = 0;
                        while(!t.isFinished()){
                            t.step();
                            stps++;
                        }
                        sender.sendMessage("Task "+rs.name+"/"+t.toString()+" Finished ("+stps+" steps)");
                        steps+=stps;
                    }
                }
                sender.sendMessage("Finished "+num+" tasks ("+steps+" steps)");
                return true;
            }
        };
        ResourceSpawnerCommand tasksCancel = new ResourceSpawnerCommand("cancel-all", "tasks.cancel") {
            @Override
            protected boolean run(CommandSender sender, Command command, String label, String[] args){
                int num = 0;
                for(ResourceSpawner rs : plugin.resourceSpawners){
                    int n = 0;
                    if(rs.workingTask!=null)n++;
                    n+=rs.tasks.size();
                    sender.sendMessage("Cancelling "+n+" Tasks for "+rs.name);
                    rs.workingTask = null;
                    rs.tasks.clear();
                    num+=n;
                }
                sender.sendMessage("Cancelled "+num+" Tasks");
                return true;
            }
        };
        commands.add(new ResourceSpawnerCommand("tasks", tasksList, tasksFinish, tasksCancel) {
            @Override
            protected boolean run(CommandSender sender, Command command, String label, String[] args){
                sender.sendMessage("Usage: /resourcespawner tasks (list|finish-all|cancel-all)");
                return true;
            }
        });
        ResourceSpawnerCommand structuresList = new ResourceSpawnerCommand("list", "structures.list") {
            @Override
            protected boolean run(CommandSender sender, Command command, String label, String[] args){
                String str = "";
                for(ResourceSpawner rs : plugin.resourceSpawners){
                    str+=rs.name+" has "+rs.structures.size()+" structure"+(rs.structures.size()==1?"":"s")+(rs.structures.isEmpty()?"":":")+"\n";
                    for(SpawnedStructure s : rs.structures){
                        Location loc = s.getLocation();
                        str+=s.getName()+": located in "+s.getWorld().getName()+" at ("+loc.getBlockX()+", "+loc.getBlockY()+", "+loc.getBlockZ()+")\n";
                    }
                }
                sender.sendMessage(str.trim());
                return true;
            }
        };
        ResourceSpawnerCommand structuresDecay = new ResourceSpawnerCommand("decay", "structures.decay") {
            @Override
            protected boolean run(CommandSender sender, Command command, String label, String[] args){
                if(!(sender instanceof Player)){
                    sender.sendMessage("You are not a player!");
                    return true;
                }
                ResourceSpawner closestSpawner = null;
                SpawnedStructure closest = null;
                double dist = 0;
                for(ResourceSpawner rs : plugin.resourceSpawners){
                    for(SpawnedStructure s : rs.structures){
                        Location loc = s.getLocation();
                        double d = loc.distance(((Player)sender).getLocation());
                        if(closest==null||d<dist){
                            closestSpawner = rs;
                            closest = s;
                            d = dist;
                        }
                    }
                }
                if(closest==null)sender.sendMessage("No nodes found!");
                if(closest.decayTask==null){
                    closest.decayTask = closest.decay();
                    closestSpawner.tasks.add(closest.decayTask);
                    sender.sendMessage("Decay started for "+closestSpawner.name+"/"+closest.getName());
                }
                return true;
            }
        };
        ResourceSpawnerCommand structuresDecayAll = new ResourceSpawnerCommand("decay-all", "structures.decay") {
            @Override
            protected boolean run(CommandSender sender, Command command, String label, String[] args){
                int num = 0;
                for(ResourceSpawner rs : plugin.resourceSpawners){
                    for(SpawnedStructure s : rs.structures){
                        Location loc = s.getLocation();
                        if(s.decayTask==null){
                            s.decayTask = s.decay();
                            rs.tasks.add(s.decayTask);
                            sender.sendMessage("Decay started for "+rs.name+"/"+s.getName());
                            num++;
                        }
                    }
                }
                sender.sendMessage("Decay started for "+num+" structures");
                return true;
            }
        };
        commands.add(new ResourceSpawnerCommand("structures", structuresList, structuresDecay, structuresDecayAll) {
            @Override
            protected boolean run(CommandSender sender, Command command, String label, String[] args){
                sender.sendMessage("Usage: /resourcespawner structures (list|decay|decay-all)");
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