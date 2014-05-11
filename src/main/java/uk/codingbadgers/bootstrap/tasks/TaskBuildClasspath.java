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
import uk.codingbadgers.bootstrap.BootstrapException;
import uk.codingbadgers.bootstrap.download.Download;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class TaskBuildClasspath implements Task {

    @Override
    public void run(Bootstrap bootstrap) {
        List<URL> urls = new ArrayList<URL>();

        for (Download download : bootstrap.getDownloads()) {
            try {
                urls.add(download.getLocalFile().toURI().toURL());
            } catch (MalformedURLException e) {
                throw new BootstrapException(e);
            }
        }

        URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]), this.getClass().getClassLoader());
        Thread.currentThread().setContextClassLoader(classLoader);
    }

}
