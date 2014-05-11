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

import io.github.thefishlive.BootstrapConstants;
import io.github.thefishlive.BootstrapException;
import io.github.thefishlive.ChecksumGenerator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class EtagDownload implements Download {

    private final File local;
    private final String remote;

    public EtagDownload(String remote, File local) {
        this.remote = remote;
        this.local = local;
    }

    public void download() {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet assetRequest = new HttpGet(remote);
        assetRequest.setHeader(new BasicHeader("Accept", BootstrapConstants.ASSET_MIME_TYPE));

        if (local.exists()) {
            assetRequest.setHeader(new BasicHeader("If-None-Match", "\"" + ChecksumGenerator.createMD5(local) + "\""));
        }

        try {
            HttpResponse response = client.execute(assetRequest);
            StatusLine status = response.getStatusLine();

            if (status.getStatusCode() == HttpStatus.SC_NOT_MODIFIED) {
                // No need to download, its already latest
                System.out.println("File " + local.getName() + " is up to date with remote, no need to download");
            } else if (status.getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY || status.getStatusCode() == HttpStatus.SC_OK) {
                // Update local version
                System.err.println("Downloading " + local.getName());

                HttpEntity entity = response.getEntity();

                ReadableByteChannel rbc = Channels.newChannel(entity.getContent());
                FileOutputStream fos = new FileOutputStream(local);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

                EntityUtils.consume(entity);

                String hash = "\"" + ChecksumGenerator.createMD5(local) + "\"";
                if (!hash.equalsIgnoreCase(response.getFirstHeader("Etag").getValue())) {
                    throw new BootstrapException("Error downloading file ("+ local.getName() + ")\n[expected hash: " + response.getFirstHeader("Etag").getValue() + " but got " + hash + "]");
                }

                System.out.println("Downloaded " + local.getName());

            } else {
                throw new BootstrapException("Error getting update from github. Error: " + status.getStatusCode() + status.getReasonPhrase());
            }
        } catch (IOException ex) {
            throw new BootstrapException(ex);
        } finally {
            close(client);
        }
    }

    @Override
    public File getLocalFile() {
        return local;
    }

    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
