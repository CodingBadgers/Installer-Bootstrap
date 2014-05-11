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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import uk.codingbadgers.bootstrap.Bootstrap;
import uk.codingbadgers.bootstrap.BootstrapException;
import uk.codingbadgers.bootstrap.download.DownloadType;
import uk.codingbadgers.bootstrap.download.Sha1Download;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskLoadDependencies implements Task {

    private static final Pattern ARTIFACT_PATTERN = Pattern.compile("([\\w\\.-]+):([\\w\\.-]+):([\\w\\.-]+)");
    private static final JsonParser PARSER = new JsonParser();

    @Override
    public void run(Bootstrap bootstrap) {
        try {
            JsonObject json = PARSER.parse(new FileReader(bootstrap.getInstallerFile() + ".libs")).getAsJsonObject();

            for (JsonElement element : json.get("libs").getAsJsonArray()) {
                JsonObject lib = element.getAsJsonObject();

                File libFile = new File(new File(Bootstrap.getAppData(), "libraries"), createPath(lib.get("name").getAsString()));
                bootstrap.addDownload(DownloadType.LIBRARY, new Sha1Download(libFile, lib.get("url").getAsString()));
            }
        } catch (IOException ex) {
            throw new BootstrapException(ex);
        }
    }

    private static String createPath(String artifact) {
        Matcher matcher = ARTIFACT_PATTERN.matcher(artifact);

        if (!matcher.matches()) {
            throw new BootstrapException(String.format("Artifact id is in the wrong format, (should be '<group>:<name>:<version>' got '%1$s')", artifact));
        }

        StringBuilder dest = new StringBuilder();
        dest.append(matcher.group(1).replace('.', '/')).append(File.separator);
        dest.append(matcher.group(2)).append("/").append(matcher.group(3)).append(File.separator);
        dest.append(String.format("%1$s-%2$s.jar", matcher.group(2), matcher.group(3)));
        return dest.toString();
    }
}
