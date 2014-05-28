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

import com.google.gson.*;

import uk.codingbadgers.bootstrap.Bootstrap;
import uk.codingbadgers.bootstrap.BootstrapConstants;
import uk.codingbadgers.bootstrap.BootstrapException;
import uk.codingbadgers.bootstrap.download.DownloadType;
import uk.codingbadgers.bootstrap.download.EtagDownload;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import uk.codingbadgers.bootstrap.utils.VersionComparator;

import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static uk.codingbadgers.bootstrap.BootstrapConstants.*;

public class TaskInstallerUpdateCheck extends AsyncTask {

    private static final JsonParser PARSER = new JsonParser();

    public TaskInstallerUpdateCheck() {
        super(null);
    }

    public TaskInstallerUpdateCheck(CountDownLatch latch) {
        super(latch);
    }

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

                String localVersion = null;

                if (bootstrap.getInstallerFile().exists()) {
                    JarFile jar = new JarFile(bootstrap.getInstallerFile());
                    Manifest manifest = jar.getManifest();
                    localVersion = manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
                    jar.close();
                }

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

                if (VersionComparator.getInstance().compare(localVersion, release.get("name").getAsString()) < 0) {
                    bootstrap.addDownload(DownloadType.INSTALLER, new EtagDownload(installerAsset.get("url").getAsString(), bootstrap.getInstallerFile()));
                    localVersion = release.get("name").getAsString();
                }

                File libs = new File(bootstrap.getInstallerFile() + ".libs");
                boolean update = true;

                if (libs.exists()) {
                    FileReader reader = null;

                    try {
                        reader = new FileReader(libs);
                        JsonElement parsed = PARSER.parse(reader);

                        if (parsed.isJsonObject()) {
                            JsonObject libsJson = parsed.getAsJsonObject();

                            if (libsJson.has("installer")) {
                                JsonObject installerJson = libsJson.get("installer").getAsJsonObject();
                                if (installerJson.get("version").getAsString().equals(localVersion)) {
                                    update = false;
                                }
                            }
                        }
                    } catch (JsonParseException ex) {
                        throw new BootstrapException(ex);
                    } finally {
                        reader.close();
                    }
                }

                if (update) {
                    new EtagDownload(librariesAsset.get("url").getAsString(), new File(bootstrap.getInstallerFile() + ".libs")).download();

                    FileReader reader = null;
                    FileWriter writer = null;

                    try {
                        reader = new FileReader(libs);
                        JsonObject libsJson = PARSER.parse(reader).getAsJsonObject();

                        JsonObject versionJson = new JsonObject();
                        versionJson.add("version", new JsonPrimitive(localVersion));

                        libsJson.add("installer", versionJson);
                        writer = new FileWriter(libs);
                        new Gson().toJson(libsJson, writer);
                    } catch (JsonParseException ex) {
                        throw new BootstrapException(ex);
                    } finally {
                        reader.close();
                        writer.close();
                    }
                }

                EntityUtils.consume(entity);
            } else if (statusLine.getStatusCode() == HttpStatus.SC_FORBIDDEN) {
                System.err.println("Hit rate limit, skipping update check");
            } else {
                throw new BootstrapException("Error sending request to github. Error " + statusLine.getStatusCode() + statusLine.getReasonPhrase());
            }
        } catch (IOException e) {
            throw new BootstrapException(e);
        }
    }

}
