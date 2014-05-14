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

import uk.codingbadgers.bootstrap.download.Download;
import uk.codingbadgers.bootstrap.download.DownloadType;
import uk.codingbadgers.bootstrap.utils.ProgressMonitor;
import uk.codingbadgers.bootstrap.tasks.*;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Bootstrap {

    private static final File installerFile;

    private Map<DownloadType, Set<Download>> downloads = new HashMap<DownloadType, Set<Download>>();
    private final ReadWriteLock downloadsLock = new ReentrantReadWriteLock();

    private BootstrapState state;
    private ProgressMonitor monitor;
    private int threadcount;

    static {
        installerFile = new File(getAppData(), "adminpack-installer.jar");
    }

    public void launch() {
        try {
            long time = System.nanoTime();
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            monitor = new ProgressMonitor();
            monitor.setMaximum(5);

            updateState(BootstrapState.UPDATE_CHECK);
            {
                CountDownLatch latch = new CountDownLatch(2);
                runTask(TaskBootstrapUpdateCheck.class, latch);
                runTask(TaskInstallerUpdateCheck.class, latch);
                latch.await();
            }

            updateState(BootstrapState.LOAD_DEPENDENCIES);
            {
                runTask(TaskLoadDependencies.class);
            }

            updateState(BootstrapState.DOWNLOAD);
            {
                runTask(TaskDownload.class);
            }

            updateState(BootstrapState.SETUP_ENVIRONMENT);
            {
                runTask(TaskCheckJavaVersion.class);
                runTask(TaskBuildClasspath.class);
            }

            System.out.println("Took " + ((System.nanoTime() - time) / 1000000f) + "ms to launch installer");
            updateState(BootstrapState.START_INSTALLER);
            {
                runTask(TaskLaunchInstaller.class);
            }

            updateState(BootstrapState.FINISHED);
        } catch (BootstrapException ex) {
            handleException(ex);
            System.exit(2);
        } catch (Exception ex) {
            handleException(ex);
            System.exit(-1);
        } finally {
            monitor.close();
        }
    }

    private void runTask(final Class<? extends AsyncTask> clazz, final CountDownLatch latch) {
        final Bootstrap instance = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Constructor<? extends AsyncTask> ctor = clazz.getConstructor(CountDownLatch.class);
                    AsyncTask task = ctor.newInstance(latch);
                    task.execute(instance);
                } catch (ReflectiveOperationException e) {
                    handleException(e);
                }
            }
        }, "task-execute-" + threadcount++).start();
    }

    public void runTask(Class<? extends Task> clazz) {
        try {
            Task task = clazz.newInstance();
            task.run(this);
        } catch (ReflectiveOperationException e) {
            handleException(e);
        }
    }

    public Set<Download> getDownloads() {
        Lock read = this.downloadsLock.readLock();
        read.lock();

        try {
            Set<Download> downloads = new HashSet<Download>();

            for (Set<Download> entry : this.downloads.values()) {
                downloads.addAll(entry);
            }
            return downloads;
        } finally {
            read.unlock();
        }
    }

    public Set<Download> getDownloads(DownloadType type) {
        Lock read = this.downloadsLock.readLock();
        read.lock();

        try {
            return new HashSet<Download>(this.downloads.get(type));
        } finally {
            read.unlock();
        }
    }

    public void addDownload(DownloadType type, Download download) {
        Lock write = this.downloadsLock.writeLock();
        write.lock();

        try {
            if (this.downloads.containsKey(type)) {
                this.downloads.get(type).add(download);
            } else {
                this.downloads.put(type, new HashSet<Download>(Arrays.asList(download)));
            }
        } finally {
            write.unlock();
        }
    }

    public File getInstallerFile() {
        return installerFile;
    }

    private void handleException(Exception e) {
        JOptionPane.showMessageDialog(null, e.getMessage(), BootstrapConstants.POPUP_TITLE, JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    private void updateState(BootstrapState state) {
        this.state = state;
        this.monitor.next();
    }

    public static File getAppData() {
        String userHomeDir = System.getProperty("user.home", ".");
        String osType = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

        if (osType.contains("win") && System.getenv("APPDATA") != null) {
            return new File(new File(System.getenv("APPDATA")), ".minecraft");
        } else if (osType.contains("mac")) {
            return new File(new File(userHomeDir, "Library"), "Application Support");
        } else {
            return new File(new File(userHomeDir), ".minecraft");
        }
    }
}
