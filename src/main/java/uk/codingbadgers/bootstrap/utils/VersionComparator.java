/**
 * Installer-Bootstrap 1.0.0-SNAPSHOT
 * Copyright (C) 2014 CodingBadgers <plugins@mcbadgercraft.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.codingbadgers.bootstrap.utils;

import uk.codingbadgers.bootstrap.BootstrapConstants;

import java.util.Arrays;
import java.util.Comparator;

public class VersionComparator implements Comparator<String> {

    private static final int UPDATE = -1;
    private static final int EQUAL = 0;
    private static final int DEV_BUILD = 1;

    @Override
    public int compare(String local, String remote) {
        if (local == null) {
            throw new IllegalArgumentException("Local version cannot be null");
        }
        if (remote == null) {
            throw new IllegalArgumentException("Remote version cannot be null");
        }

        if (BootstrapConstants.DEV || local.equals("dev-SNAPSHOT")) {
            return EQUAL;
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
                return UPDATE; // UPDATE NOW
            } else if (localPart > remotePart){
                return DEV_BUILD; // DEV BUILD
            }
        }

        if (localSnapshot) {
            return UPDATE;
        }

        return EQUAL;
    }
}
