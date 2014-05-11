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
package uk.codingbadgers.bootstrap.tasks;

import uk.codingbadgers.bootstrap.Bootstrap;
import uk.codingbadgers.bootstrap.Main;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class TaskLaunchInstaller implements Task {

    @Override
    public void run(Bootstrap bootstrap) {
        String mainclass = "";

        try {
            JarFile file = new JarFile(bootstrap.getInstallerFile());

            Manifest manifest = file.getManifest();
            mainclass = manifest.getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();

            Class<?> bootstrapper = loader.loadClass("io.github.thefishlive.bootstrap.Bootstrapper");
            Class<?> launcher = loader.loadClass(mainclass);

            Method launch = bootstrapper.getMethod("launch", Class.class, String[].class);
            launch.invoke(null, new Object[] { launcher, Main.getCliArgs() }); // TODO parse cli arguments
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}
