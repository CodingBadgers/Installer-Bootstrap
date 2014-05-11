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
package uk.codingbadgers.bootstrap;

public final class BootstrapConstants {

    public static final String NAME;
    public static final String VERSION;
    public static final String VENDOR;

    public static final String POPUP_TITLE = "Installer Bootstrap";
    public static final String MONITOR_TITLE = "Launching ModPack Installer";
    public static final String MONITOR_TEXT = "Setting up environment for installer";

    public static final String GITHUB_MIME_TYPE = "application/vnd.github.v3+json";
    public static final String ASSET_MIME_TYPE = "application/octet-stream";

    public static final String INSTALLER_LABEL = "ModPack-Installer.jar";
    public static final String INSTALLER_LIBS_LABEL = "ModPack-Installer-Libs.json";

    public static final String GITHUB_API = "https://api.github.com/";

    public static final String BOOTSTRAP_UPDATE_URL = GITHUB_API + "repos/CodingBadgers/Installer-Bootstrap/releases";
    public static final String INSTALLER_UPDATE_URL = GITHUB_API + "repos/CodingBadgers/ModPack-Installer/releases";

    static {
        NAME = BootstrapConstants.class.getPackage().getImplementationTitle();
        VENDOR = BootstrapConstants.class.getPackage().getImplementationVendor();
        VERSION = BootstrapConstants.class.getPackage().getImplementationVersion();
    }

}
