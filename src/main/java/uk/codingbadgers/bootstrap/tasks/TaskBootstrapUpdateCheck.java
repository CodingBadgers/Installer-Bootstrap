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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import uk.codingbadgers.bootstrap.BootstrapException;
import uk.codingbadgers.bootstrap.Bootstrap;
import uk.codingbadgers.bootstrap.BootstrapConstants;
import uk.codingbadgers.bootstrap.utils.VersionComparator;

import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

public class TaskBootstrapUpdateCheck extends AsyncTask {

    private static final JsonParser PARSER = new JsonParser();

    public TaskBootstrapUpdateCheck() {
        super(null);
    }

    public TaskBootstrapUpdateCheck(CountDownLatch latch) {
        super(latch);
    }

    @Override
    public void run(Bootstrap bootstrap) {
        try {
            HttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(BootstrapConstants.BOOTSTRAP_UPDATE_URL);
            request.setHeader(new BasicHeader("Accept", BootstrapConstants.GITHUB_MIME_TYPE));

            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                JsonArray json = PARSER.parse(new InputStreamReader(entity.getContent())).getAsJsonArray();
                JsonObject release = json.get(0).getAsJsonObject();
                String version = release.get("name").getAsString();

                if (VersionComparator.getInstance().compare(BootstrapConstants.VERSION, version) >= 0) {
                    System.out.println("Up to date bootstrap");
                } else {
                    Desktop.getDesktop().browse(URI.create(release.get("html_url").getAsString()));
                    throw new BootstrapException("Outdated bootstrap.\nPlease update your bootstrap.\n");
                }

                EntityUtils.consume(entity);
            } else {
                System.err.println("Error sending request to github. Error " + response.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
