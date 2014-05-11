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
package io.github.thefishlive.download;

import io.github.thefishlive.BootstrapException;
import io.github.thefishlive.ChecksumGenerator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Sha1Download implements Download {

    private String remote;
    private File local;

    public Sha1Download(File local, String remote) {
        this.local = local;
        this.remote = remote;
    }

    @Override
    public void download() {
        CloseableHttpClient client = HttpClients.createDefault();
        String hash = null;

        // Get sha1 hash from repo
        try {
            HttpGet request = new HttpGet(remote + ".sha1");
            HttpResponse response = client.execute(request);

            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                hash = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
            }
        } catch (IOException ex) {
            throw new BootstrapException(ex);
        }

        if (local.exists()) {
            String localHash = ChecksumGenerator.createSha1(local);

            if (hash != null && hash.equalsIgnoreCase(localHash)) {
                System.out.println("File " + local.getName() + " is up to date with remote, no need to download");
                return;
            }
        }

        if (!local.getParentFile().exists()) {
            local.getParentFile().mkdirs();
        }

        // Download library from remote
        try {
            HttpGet request = new HttpGet(remote);
            HttpResponse response = client.execute(request);

            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() == HttpStatus.SC_OK) {
                System.err.println("Downloading " + local.getName());

                HttpEntity entity = response.getEntity();

                ReadableByteChannel rbc = Channels.newChannel(entity.getContent());
                FileOutputStream fos = new FileOutputStream(local);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

                EntityUtils.consume(entity);

                String localHash = ChecksumGenerator.createSha1(local);
                if (hash != null && !localHash.equalsIgnoreCase(hash)) {
                    throw new BootstrapException("Error downloading file ("+ local.getName() + ")\n[expected hash: " + localHash + " but got " + hash + "]");
                }

                System.out.println("Downloaded " + local.getName());

            } else {
                throw new BootstrapException("Error download update for " + local.getName() + ", Error: " + status.getStatusCode() + status.getReasonPhrase());
            }
        } catch (IOException ex) {
            throw new BootstrapException(ex);
        }
    }

    @Override
    public File getLocalFile() {
        return local;
    }
}
