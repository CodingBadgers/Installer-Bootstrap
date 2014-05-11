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
import uk.codingbadgers.bootstrap.download.Download;
import uk.codingbadgers.bootstrap.download.DownloadWorker;

import java.util.concurrent.CountDownLatch;

public class TaskDownload implements Task {

    @Override
    public void run(Bootstrap bootstrap) {
        int i = 0;

        CountDownLatch latch = new CountDownLatch(bootstrap.getDownloads().size());


        for (Download download : bootstrap.getDownloads()) {
            DownloadWorker worker = new DownloadWorker(download, latch);
            new Thread(worker, "download-worker-" + i++).start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("All downloads complete");
    }

}
