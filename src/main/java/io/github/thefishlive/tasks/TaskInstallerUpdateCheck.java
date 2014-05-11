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
package io.github.thefishlive.tasks;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.thefishlive.Bootstrap;
import io.github.thefishlive.BootstrapException;
import io.github.thefishlive.download.DownloadType;
import io.github.thefishlive.download.EtagDownload;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.*;

import static io.github.thefishlive.BootstrapConstants.*;

public class TaskInstallerUpdateCheck implements Task {

    private static final JsonParser PARSER = new JsonParser();

    @Override
    public void run(Bootstrap bootstrap) {
        try {
            HttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(INSTALLER_UPDATE_URL);
            request.setHeader(new BasicHeader("Accept", GITHUB_MIME_TYPE));

            HttpResponse response = client.execute(request);

            StatusLine statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();

                JsonArray json = PARSER.parse(new InputStreamReader(entity.getContent())).getAsJsonArray();
                JsonObject release = json.get(0).getAsJsonObject();

                JsonObject installerAsset = null;
                JsonObject librariesAsset = null;
                int i = 0;

                for (JsonElement element : release.get("assets").getAsJsonArray()) {
                    JsonObject object = element.getAsJsonObject();
                    if (INSTALLER_LABEL.equals(object.get("name").getAsString())) {
                        installerAsset = object;
                    } else if (INSTALLER_LIBS_LABEL.equals(object.get("name").getAsString())) {
                        librariesAsset = object;
                    }
                }

                bootstrap.addDownload(DownloadType.INSTALLER, new EtagDownload(installerAsset.get("url").getAsString(), bootstrap.getInstallerFile()));
                new EtagDownload(librariesAsset.get("url").getAsString(), new File(bootstrap.getInstallerFile() + ".libs")).download(); // Has to be downloaded before next task can be run
                EntityUtils.consume(entity);
            } else {
                throw new BootstrapException("Error sending request to github. Error " + statusLine.getStatusCode() + statusLine.getReasonPhrase());
            }
        } catch (IOException e) {
            throw new BootstrapException(e);
        }
    }

}
