//  Copyright 2004 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.hivemind.build;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/**
 * Ant task for conditionally downloading a file and checking its md5 sum. MD5 checking occurs only
 * if a download occurs. This is very similar to Ant's <code>Get</code> task, with a few
 * differences:
 * <ul>
 * <li>Always does a timestamp comparison, where possible
 * <li>Has additional parameter for specifying where the MD5 sum is stored
 * <li>Less verbose --- no output if the file isn't downloaded
 * </ul>
 *
 * @author Howard Lewis Ship
 */
public class Grabber extends Task
{
    private URL _src;
    private File _dest;
    private URL _md5;

    public void execute() throws BuildException
    {
        if (_src == null)
            throw new BuildException("src is required", getLocation());

        if (_dest == null)
            throw new BuildException("dest is required", getLocation());

        if (_dest.exists())
        {

            if (_dest.isDirectory())
                throw new BuildException("The specified destination is a directory", getLocation());

            if (isNonZeroLength(_dest))
                return;

            if (!_dest.canWrite())
                throw new BuildException(
                    "Can't write to " + _dest.getAbsolutePath(),
                    getLocation());
        }

        try
        {

            URLConnection connection = _src.openConnection();

            connection.connect();

            MessageDigest digest = copyConnectionToFile(connection);

            if (_md5 != null)
                compareMD5(digest);
        }
        catch (IOException ex)
        {
            log("Failure accessing " + _src + ": " + ex.getMessage(), Project.MSG_ERR);
        }
    }

    private boolean isNonZeroLength(File file)
    {
        return file.length() > 0;
    }

    private MessageDigest copyConnectionToFile(URLConnection connection) throws IOException
    {
        log("Downloading " + _src + " to " + _dest);

        MessageDigest result = null;

        if (_md5 != null)
        {
            try
            {
                result = MessageDigest.getInstance("MD5");
            }
            catch (NoSuchAlgorithmException ex)
            {
                throw new BuildException(
                    "No MD5 message digest provider: " + ex.getMessage(),
                    ex,
                    getLocation());
            }
        }

        InputStream is = open(connection);

        if (result != null)
            is = new DigestInputStream(is, result);

        FileOutputStream fos = new FileOutputStream(_dest);
        OutputStream os = new BufferedOutputStream(fos);

        byte[] buffer = new byte[100 * 1024];

        while (true)
        {
            int bytesRead = is.read(buffer);

            if (bytesRead < 0)
                break;

            os.write(buffer, 0, bytesRead);
        }

        is.close();
        os.close();

        return result;
    }

    private void compareMD5(MessageDigest digest) throws IOException
    {
        String downloadedDigest = createDigestString(digest);

        URLConnection connection = _md5.openConnection();

        InputStream is = open(connection);

        InputStreamReader reader = new InputStreamReader(is);

        StringBuffer buffer = new StringBuffer();

        while (true)
        {
            int ch = reader.read();

            if (ch < 0)
                break;

            buffer.append((char) ch);
        }

        reader.close();

        String recordedDigest = buffer.toString().trim();

        // Sometimes a digest has a string identifying the file after the hex string; discard
        // that.

        int firstSpace = recordedDigest.indexOf(' ');
        if (firstSpace > 0)
            recordedDigest = recordedDigest.substring(0, firstSpace);

        if (downloadedDigest.equals(recordedDigest))
            return;

        throw new BuildException(
            "Downloaded file had an MD5 sum of "
                + downloadedDigest
                + " but the expected sum was "
                + recordedDigest,
            getLocation());
    }

    private InputStream open(URLConnection connection)
    {
        InputStream result = null;

        int i = 0;
        while (true)
        {
            try
            {
                result = connection.getInputStream();
                break;
            }
            catch (IOException ex)
            {
                if (i++ == 3)
                    throw new BuildException(
                        "Unable to open " + _src + ": " + ex.getMessage(),
                        ex,
                        getLocation());
            }
        }

        if (result == null)
            throw new BuildException("Unable to open " + _src, getLocation());

        return result;
    }

    private String createDigestString(MessageDigest digest)
    {
        byte[] bytes = digest.digest();

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++)
        {
            String hex = Integer.toHexString(0x00ff & bytes[i]);
            if (hex.length() < 2)
                buffer.append("0");

            buffer.append(hex);
        }

        return buffer.toString();
    }

    public void setDest(File file)
    {
        _dest = file;
    }

    public void setMd5(String url)
    {
        if (url == null || url.length() == 0)
            return;

        try
        {
            _md5 = new URL(url);
        }
        catch (MalformedURLException ex)
        {
            log("Error in md5: " + ex.getMessage());
        }
    }

    public void setSrc(URL url)
    {
        _src = url;
    }

}