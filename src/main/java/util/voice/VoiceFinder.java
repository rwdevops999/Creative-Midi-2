package util.voice;

import entity.voice.Group;
import entity.voice.Patch;
import util.app.AppInfo;

import java.util.*;

public class VoiceFinder {
    private static AppInfo appInfo = AppInfo.INSTANCE;

    public static List<Patch> findVoice(String voiceToFind) {
        List<Patch> result = new ArrayList<>();

        InstrumentProvider instrumentProvider = appInfo.getInstrumentProvider();

        if (voiceToFind != null) {
            voiceToFind = voiceToFind.toLowerCase();
        } else {
            voiceToFind = "";
        }

        for (Group group : instrumentProvider.getGroups()) {
            result.addAll(findVoiceInGroup(group, voiceToFind));
        }

        return result;
    }

    public static List<Patch> findVoice(Patch patchToFind) {
        List<Patch> result = new ArrayList<>();

        InstrumentProvider instrumentProvider = appInfo.getInstrumentProvider();

        for (Group group : instrumentProvider.getGroups()) {
            result.addAll(findPatchInGroup(group, patchToFind));
        }

        return result;
    }

    public static List<Patch> findVoiceInGroup(Group group, String voiceName) {
        List<Patch> result = new ArrayList<>();

        if (group.getName().toLowerCase().contains(voiceName.toLowerCase())) {
            result.addAll(group.getPatches());
        } else {
            for (Patch patch : group.getPatches()) {
                if ((! result.contains(patch)) && (patch.getName().toLowerCase().contains(voiceName))) {
                    result.add(patch);
                }
            }
        }

        if (! group.getGroups().isEmpty()) {
            for (Group g : group.getGroups()) {
                result.addAll(findVoiceInGroup(g, voiceName));
            }
        }

        return result;
    }

    public static List<Patch> findPatchInGroup(Group group, Patch patchToFind) {

        List<Patch> patches;
        if (patchToFind.getPc() != null) {
            patches = group.getPatches().stream().filter(p ->
                    (p.getBank().equals(patchToFind.getBank()) && p.getPc().equals(patchToFind.getPc()))).toList();
        } else {
            patches = group.getPatches().stream().filter(p ->
                    (p.getBank().equals(patchToFind.getBank()))).toList();

        }
        List<Patch> result = new ArrayList<>(patches);

        if (! group.getGroups().isEmpty()) {
            for (Group g : group.getGroups()) {
                result.addAll(findPatchInGroup(g, patchToFind));
            }
        }

        return result;
    }

    public static List<String> getGroupsNames(List<Group> groups, String baseName) {
        List<String> result = new ArrayList<>();

        for (Group group : groups) {
            String name = baseName + "/" + group.getName();
            result.add(name.substring(1));
            if (! group.getGroups().isEmpty()) {
                result.addAll(getGroupsNames(group.getGroups(), name));
            }
        }

        return result;
    }

    public static List<Patch> getVoicesOfGroup(List<Group> groups, String groupName) {
        String[] split = groupName.split("/");
        List<Patch> result = new ArrayList<>();

        List<Group> groupsToSearch = groups;
        for (int i = 0; i < split.length; i++) {
            String groupToSearch = split[i];

            for (Group group : groupsToSearch) {
                if (group.getName().equals(groupToSearch)) {
                    if (i == split.length - 1) {
                        result.addAll(group.getPatches());
                        if (! group.getGroups().isEmpty()) {
                            result.addAll(getVoicesOfGroups(group.getGroups()));
                        }
                    } else {
                        groupsToSearch = group.getGroups();
                    }
                }
            }
        }

        return result;
    }

    public static List<Patch> getVoicesOfGroups(List<Group> groups) {
        List<Patch> result = new ArrayList<>();

        for (Group group : groups) {
            result.addAll(group.getPatches());

            if (! group.getGroups().isEmpty()) {
                result.addAll(getVoicesOfGroups(group.getGroups()));
            }
        }

        return result;
    }

    public static List<Patch> getVoices(List<Group> groups) {
        List<Patch> result = new ArrayList<>();

        for (Group group : groups) {
            result.addAll(group.getPatches());

            if (! group.getGroups().isEmpty()) {
                result.addAll(getVoices(group.getGroups()));
            }
        }

        return result;
    }

    public static Set<String> getVoiceTypes() {
        List<Patch> result = getVoices(appInfo.getInstrumentProvider().getGroups());
        Map<String, Integer> voiceTypes = new HashMap<>();

        for (Patch patch : result) {
            String patchName = patch.getName();
            int index = patchName.indexOf(" ");
            if (index != -1) {
                String type = patchName.substring(0, index);
                voiceTypes.merge(type, 1, Integer::sum);
            }
        }

        return voiceTypes.keySet();
    }
}
