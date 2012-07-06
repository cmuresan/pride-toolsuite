package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.gui.task.TaskAdapter;
import uk.ac.ebi.pride.gui.url.HttpUtilities;
import uk.ac.ebi.pride.gui.utils.Constants;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.*;

/**
 * Abstract class to provide methods to connect pride web services
 * <p/>
 * User: rwang
 * Date: 14/09/2011
 * Time: 13:46
 */
public abstract class AbstractConnectPrideTask extends TaskAdapter<List<Map<String, String>>, String> {
    private static final Logger logger = LoggerFactory.getLogger(DownloadPrideExperimentTask.class);
    private static final int BUFFER_SIZE = 1024;

    /**
     * Create http connection to pride download
     *
     * @return HttpURLConnection    http connection
     */
    HttpURLConnection connect(String url) {
        HttpURLConnection connection = null;

        try {
            connection = HttpUtilities.createHttpConnection(url, "POST");
        } catch (Exception ex) {
            logger.warn("Fail to create connection to the remote server: {}", ex.getMessage());
            publish("Warning: Fail to create connection to the remote server");
        }

        return connection;
    }

    /**
     * login to pride to download
     *
     * @param connection http connection.
     * @param accession  pride experiment accession
     * @param user       user name
     * @param password   password
     */
    void initExperimentDownload(HttpURLConnection connection, Comparable accession,
                                          String user, String password) {
        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(connection.getOutputStream());
            StringBuilder cmd = new StringBuilder();
            cmd.append("username=");
            cmd.append(URLEncoder.encode(String.valueOf(user), "UTF-8"));
            cmd.append("&password=");
            cmd.append(URLEncoder.encode(String.valueOf(password), "UTF-8"));
            cmd.append("&action=downloadFile");
            cmd.append("&accession=");
            cmd.append(accession);
            out.write(cmd.toString());
            out.close();
        } catch (IOException ex) {
            logger.warn("Fail to send download request to the remote server: {}", ex.getMessage());
            publish("Warning: Fail to send download request to the remote server");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.warn("Fail to close output stream", e);
                }
            }
        }
    }

    /**
     * log in for ProteomeXchange meta data information.
     *
     * @param connection http connection
     * @param accessions a list of pride experiment accessions
     * @param user       user name
     * @param password   password
     */
    void initPxMetaDataDownload(HttpURLConnection connection,
                                Collection<Comparable> accessions,
                                String user,
                                String password) {
        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(connection.getOutputStream());
            StringBuilder cmd = new StringBuilder();
            cmd.append("username=");
            cmd.append(URLEncoder.encode(String.valueOf(user), "UTF-8"));
            cmd.append("&password=");
            cmd.append(URLEncoder.encode(String.valueOf(password), "UTF-8"));
            if (accessions != null && !accessions.isEmpty()) {
                cmd.append("&pxaccession=");
                String accStr = "";
                for (Comparable accession : accessions) {
                    accStr += accession.toString() + Constants.COMMA;
                }
                cmd.append(accStr);
            }
            out.write(cmd.toString());
            out.close();
        } catch (IOException ex) {
            logger.warn("Fail to send request to PRIDE server: {}", ex.getMessage());
            publish("Warning: Fail to send request to PRIDE server");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.warn("Failed to close output stream", e);
                }
            }
        }
    }

    /**
     * login to proteomexchange to download
     *
     * @param connection http connection.
     * @param accession  pride experiment accession
     * @param user       user name
     * @param password   password
     */
    void initPxSubmissionDownload(HttpURLConnection connection,
                                  Comparable accession,
                                  String fileId,
                                  String user,
                                  String password) {
        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(connection.getOutputStream());
            StringBuilder cmd = new StringBuilder();
            cmd.append("username=");
            cmd.append(URLEncoder.encode(String.valueOf(user), "UTF-8"));
            cmd.append("&password=");
            cmd.append(URLEncoder.encode(String.valueOf(password), "UTF-8"));
            cmd.append("&pxaccession=");
            cmd.append(accession);
            cmd.append("&fileid=");
            cmd.append(fileId);
            out.write(cmd.toString());
            out.close();
        } catch (IOException ex) {
            logger.warn("Fail to send download request to the remote server: {}", ex.getMessage());
            publish("Warning: Fail to send download request to the remote server");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.warn("Fail to close output stream", e);
                }
            }
        }
    }


    /**
     * Download pride experiment
     *
     * @param connection http connection
     * @param file       output file path
     * @param size       size of the file
     */
    @SuppressWarnings("unchecked")
    void downloadFile(HttpURLConnection connection, File file, Double size) {
        BufferedOutputStream boutStream = null;
        InputStream in = null;
        try {
            publish("Downloading " + file);
            in = connection.getInputStream();

            FileOutputStream outStream = new FileOutputStream(file);
            boutStream = new BufferedOutputStream(outStream, BUFFER_SIZE);
            byte data[] = new byte[BUFFER_SIZE];
            int count;
            long readCount = 0;
            if (size != null) {
                setProgress(1);
            }
            while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
                readCount += count;
                boutStream.write(data, 0, count);
                if (size != null) {
                    int progress = (int) Math.round((readCount / size) * 100);
                    setProgress(progress >= 100 ? 99 : progress);
                }
            }
            if (size != null) {
                setProgress(100);
            }
            boutStream.flush();
            publish("Download has finished");
        } catch (IOException ex) {
            String msg = ex.getMessage();
            if (msg.contains("403")) {
                logger.warn("Wrong login credentials: {}", msg);
                publish("Warning: Wrong login credentials");
            } else if (msg.contains("400")) {
                logger.warn("Fail to connect to the remote server: {}", msg);
                publish("Warning: Fail to connect to the remote server");
            } else {
                logger.warn("Unexpected error: " + ex.getMessage());
                publish("Unexpected error: " + ex.getMessage());
            }
        } finally {
            if (boutStream != null) {
                try {
                    boutStream.close();
                } catch (IOException e) {
                    logger.error("Failed to close output stream", e);
                }
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("Failed to close Gzip input stream", e);
                }
            }
        }
    }

    /**
     * log in for meta data information.
     *
     * @param connection http connection
     * @param accessions a list of pride experiment accessions
     * @param user       user name
     * @param password   password
     */
    void initMetaDataDownload(HttpURLConnection connection,
                              Collection<Comparable> accessions,
                              String user,
                              String password) {
        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(connection.getOutputStream());
            StringBuilder cmd = new StringBuilder();
            cmd.append("username=");
            cmd.append(URLEncoder.encode(String.valueOf(user), "UTF-8"));
            cmd.append("&password=");
            cmd.append(URLEncoder.encode(String.valueOf(password), "UTF-8"));
            cmd.append("&action=metadata");
            if (accessions != null && !accessions.isEmpty()) {
                cmd.append("&accession=");
                String accStr = "";
                for (Comparable accession : accessions) {
                    accStr += accession.toString() + Constants.COMMA;
                }
                cmd.append(accStr);
            }
            out.write(cmd.toString());
            out.close();
        } catch (IOException ex) {
            logger.warn("Fail to send request to PRIDE server: {}", ex.getMessage());
            publish("Warning: Fail to send request to PRIDE server");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.warn("Failed to close output stream", e);
                }
            }
        }
    }

    /**
     * Download experiment metadata
     *
     * @param connection http connection
     * @return List<Map<String, String>>   experiment meta data
     */
    List<Map<String, String>> downloadMetaData(HttpURLConnection connection) {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String str;
            Map<String, String> entry = new HashMap<String, String>();
            while ((str = in.readLine()) != null) {
                str = str.trim();
                if ("//".equals(str) && !entry.isEmpty()) {
                    result.add(entry);
                    entry = new HashMap<String, String>();
                } else if (!"".equals(str)) {
                    String[] parts = str.split(Constants.TAB);
                    entry.put(parts[0], parts[1]);
                }
            }
            in.close();
        } catch (IOException ex) {
            String msg = ex.getMessage();
            if (msg.contains("403")) {
                logger.warn("Wrong login credentials: {}", msg);
                publish("Warning: Wrong login credentials");
            } else {
                logger.warn("Fail to connect to the remote server: {}", msg);
                publish("Warning: Fail to connect to the remote server");
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.warn("Failed to close output stream", e);
                }
            }
        }

        return result;
    }
}
