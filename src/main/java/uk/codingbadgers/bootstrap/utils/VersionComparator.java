package uk.codingbadgers.bootstrap.utils;

import java.util.Arrays;
import java.util.Comparator;

public class VersionComparator implements Comparator<String> {

    private static final int LESS_THAN = -1;
    private static final int EQUAL = 0;
    private static final int GREATER_THAN = 1;

    @Override
    public int compare(String local, String remote) {
        if (local == null) {
            throw new IllegalArgumentException("Local version cannot be null");
        }
        if (remote == null) {
            throw new IllegalArgumentException("Remote version cannot be null");
        }

        boolean localSnapshot = local.contains("-SNAPSHOT");

        if (localSnapshot) {
            local = local.substring(0, local.indexOf('-'));
        }

        boolean remoteSnapshot = remote.contains("-SNAPSHOT");

        if (remoteSnapshot) {
            remote = remote.substring(0, local.indexOf('-'));
        }

        String[] localSplit = local.split("\\.");
        String[] remoteSplit = remote.split("\\.");

        if (localSplit.length < 3) {
            localSplit = Arrays.copyOf(localSplit, 3);
            localSplit[2] = "0";
        }

        if (remoteSplit.length != 3) {
            remoteSplit = Arrays.copyOf(remoteSplit, 3);
            remoteSplit[2] = "0";
        }

        for (int i = 0; i < localSplit.length; i++) {
            int localPart = Integer.parseInt(localSplit[i]);
            int remotePart = Integer.parseInt(remoteSplit[i]);

            if (localPart < remotePart) {
                return LESS_THAN; // UPDATE NOW
            } else if (localPart > remotePart){
                return GREATER_THAN; // DEV BUILD
            }
        }

        if (localSnapshot) {
            return LESS_THAN;
        }

        return EQUAL;
    }
}
