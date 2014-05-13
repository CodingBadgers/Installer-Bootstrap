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

import java.util.jar.Attributes.Name;

public final class BootstrapConstants {

    /* Bootstrap Version Constants*/

    public static final String NAME;
    public static final String VERSION;
    public static final String VENDOR;
    public static final boolean DEV;

    static {
        if (BootstrapConstants.class.getPackage().getImplementationTitle() != null) {
            NAME = BootstrapConstants.class.getPackage().getImplementationTitle();
            DEV = false;
        } else {
            NAME = "installer-boostrap-dev";
            DEV = true;
        }

        if (BootstrapConstants.class.getPackage().getImplementationVendor() != null) {
            VENDOR = BootstrapConstants.class.getPackage().getImplementationVendor();
        } else {
            VENDOR = "uk.codingbadgers";
        }

        if (BootstrapConstants.class.getPackage().getImplementationVersion() != null) {
            VERSION = BootstrapConstants.class.getPackage().getImplementationVersion();
        } else {
            VERSION = "dev-SNAPSHOT";
        }
    }

    /* UI Constants */

    public static final String POPUP_TITLE = "Installer Bootstrap";
    public static final String MONITOR_TITLE = "Launching ModPack Installer";
    public static final String MONITOR_TEXT = "Setting up environment for installer";

    /* Manifest Entry Constants */

    public static final String MANIFEST_SECTION = "Bootstrap";
    public static final Name MANIFEST_JAVA_VERSION = new Name("Java-Version");
    public static final Name MANIFEST_JAVA_CLASS_VERSION = new Name("Java-Class-Version");

    public static final String PROPERTY_JAVA_VERSION = "java.version";
    public static final String PROPERTY_JAVA_CLASS_VERSION = "java.class.version";

    /* Github Api Constants */

    public static final String GITHUB_API = "https://api.github.com/";
    public static final String GITHUB_MIME_TYPE = "application/vnd.github.v3+json";
    public static final String ASSET_MIME_TYPE = "application/octet-stream";

    public static final String INSTALLER_LABEL = "ModPack-Installer.jar";
    public static final String INSTALLER_LIBS_LABEL = "ModPack-Installer-Libs.json";

    public static final String BOOTSTRAP_UPDATE_URL = GITHUB_API + "repos/CodingBadgers/Installer-Bootstrap/releases";
    public static final String INSTALLER_UPDATE_URL = GITHUB_API + "repos/CodingBadgers/ModPack-Installer/releases";

}
