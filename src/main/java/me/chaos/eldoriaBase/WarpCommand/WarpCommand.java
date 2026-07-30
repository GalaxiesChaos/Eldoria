package me.chaos.eldoriaBase.WarpCommand;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.chaos.eldoriaBase.Main;
import me.chaos.eldoriaBase.WarpCommand.WarpExeptiosn.NotEnoughWarpsExeption;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class WarpCommand implements BasicCommand {
    WarpHandler handler;
    public WarpCommand(Main main){
        handler = main.getHandler ( ).getWarpHandler ( );
    }

    @Override
    public void execute (CommandSourceStack stack, String[] args) {

        String Action = args[0];
        String value = args[1];

        if(stack.getExecutor () instanceof Player player){
            switch (Action){
                case "to","To","TO,tO" ->  {
                    if (handler.containsWarp (value,player)){
                        player.teleport (handler.getWarpmanager (player).getWarp (value).getWarpLocation ());
                    } else {
                        player.sendMessage (ChatColor.RED + value + " ist keine gültige ID");
                    }
                }

                case "set", "Set", "SET" -> {
                    try {
                        if (handler.containsWarp (value,player)){
                            player.sendMessage ("Dieser Warp exsistiert schon".formatted (ChatColor.RED));
                            return;
                        }

                        handler.getWarpmanager (player).addWarp (new WarpPoint (player, value));
                        player.sendMessage ("Warp hinzugefügt");
                        return;

                    } catch (NotEnoughWarpsExeption e) {
                        player.sendMessage (e.getMessage ());
                        return;
                    }
                }

                case "addPublic", "AddPublic" -> {
                    if (player.hasPermission ("*")) {
                        PublicWarp publicWarp = new PublicWarp ( );
                        publicWarp.add (new WarpPoint (player , value));
                        publicWarp.SaveToFile ( );

                        handler.ReloadPublic ( );
                    }
                }

                default -> {
                    if (player.hasPermission("*")){
                        player.sendMessage(Component.text(Action + ", " + value));
                    }
                    player.sendMessage (ChatColor.RED + Action + " ist keine Valide Eingabe");
                }
            }
        }


    }

    @Override
    public @NonNull Collection<String> suggest (@NonNull CommandSourceStack commandSourceStack , String @NonNull [] args) {
        if (commandSourceStack.getSender ( ) instanceof Player player) {
            switch (args.length) {
                case 0 -> {
                    List<String> re = new ArrayList<> ( );
                    if (commandSourceStack.getExecutor ( ).hasPermission ("*")) {
                        re.add ("addPublic");
                    }
                    re.add ("set");
                    re.add ("to");
                    return re;
                }
                case 1 ->  {

                    return handler.getIds (player);
                }
                default -> {
                    return Collections.singleton ("");
                }


            }
        }
        return Collections.singleton ("Wie bist du denn her gekommen?");
    }

}
