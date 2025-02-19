package org.foodust.catchTheTail.Command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.foodust.catchTheTail.Message.BaseMessage;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandSub implements TabCompleter {

    Set<String> mainSub = new HashSet<>(EnumSet.range(BaseMessage.COMMAND_START, BaseMessage.COMMAND_RELOAD)).stream().map(BaseMessage::getMessage).collect(Collectors.toSet());
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], mainSub, completions);
        } else if (args.length == 2) {
        } else if (args.length == 3) {
        }
        Collections.sort(completions);
        return completions;
    }
}
