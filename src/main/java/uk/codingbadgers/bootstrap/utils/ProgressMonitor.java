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

public class ProgressMonitor {

    private javax.swing.ProgressMonitor monitor;
    private int progress;

    public ProgressMonitor() {
        monitor = new javax.swing.ProgressMonitor(null, BootstrapConstants.MONITOR_TITLE, BootstrapConstants.MONITOR_TEXT, 0, 1);
        monitor.setMillisToPopup(0);
        monitor.setMillisToDecideToPopup(0);
    }

    public void addProgress(int progress) {
        setProgress(this.progress + progress);
    }

    public void setMaximum(int max) {
        monitor.setMaximum(max);
    }

    public void setNote(String note) {
        monitor.setNote(note);
    }

    public void close() {
        monitor.close();
    }

    public int getMaximum() {
        return monitor.getMaximum();
    }

    public void next() {
        this.addProgress(1);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        monitor.setProgress(progress);
    }

}
