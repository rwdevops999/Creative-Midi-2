package util.filter;

import entity.events.FilterBlock;
import fx.dialog.SrcDstListDialog;
import lombok.Getter;
import util.app.AppInfo;
import util.midi.EventKeyType;

import java.util.*;

public class DetailFilter {
    @Getter
    private static DetailFilter instance = new DetailFilter();
    private DetailFilter() {
    }

    public FilterBlock executeFiltering(String type, FilterBlock filterBlock) {
        String key = type.toUpperCase() + "_";

        List<EventKeyType> dstTypeList = filterBlock.getEventsToFilter(type);

        // 1. Retrieve the events from EventKeyType
        List<EventKeyType> srcTypeList = new ArrayList<>(Arrays.asList(EventKeyType.values()));
        srcTypeList.removeAll(dstTypeList);

        List<String> srcEvents = srcTypeList.stream().map(Enum::toString).filter(s -> s.startsWith(key)).toList();

        // 2. Make list readable;
        // - This means remove key from the elements
        // - Change XXX_YYY to pascal case Xxx Yyy (e. NOTE_ON => Note On)
        srcEvents = srcEvents.stream().map(s -> toPascalCase(s.replaceFirst("_", "@#").split("@#")[1])).sorted().toList();

        // 3. Get the events from FilterBlock
//        List<EventKeyType> dstTypeList = filterBlock.getEventsToFilter(type);
        List<String> dstEvents = dstTypeList.stream().map(Enum::toString).filter(s -> s.contains(key)).toList();

        // 4. Make List readable
        dstEvents = dstEvents.stream().map(s -> toPascalCase(s.replaceFirst("_", "@#").split("@#")[1])).sorted().toList();

        SrcDstListDialog<String> dialog = new SrcDstListDialog<>("Filter " + type + " events", "Determine the events to be shown or hidden", "Filter", "Visible", "Hidden", srcEvents, dstEvents);
        Optional<List<String>> result = dialog.showAndWait();
        result.ifPresent(list -> {
            if (list.isEmpty()) {
                filterBlock.setEventsToFilter(type, new ArrayList<>());
            } else {
                // 5. Convert list back to EventTypeKey values
                List<EventKeyType> res = list.stream().map(s -> {
                   s = s.toUpperCase().replace(" ", "_");
                   return EventKeyType.valueOf(key + s);
                }).toList();

                filterBlock.setEventsToFilter(type, res);
            }
        });

        return filterBlock;
    }

    private static String toPascalCase(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // Split by underscores, spaces, or hyphens
        String[] words = input.split("[_\\s-]+");
        StringBuilder pascalCaseString = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                // Capitalize first letter, lowercase the rest
                pascalCaseString.append(" ").append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase());
            }
        }

        return pascalCaseString.toString().trim();
    }

    public void printAlive() {
        if (AppInfo.INSTANCE.isDebugMode()) {
            System.out.println("Detail Filter Up and Running");
        }
    }
}
