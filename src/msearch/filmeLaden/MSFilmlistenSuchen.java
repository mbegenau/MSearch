/*
 * MediathekView
 * Copyright (C) 2008 W. Xaver
 * W.Xaver[at]googlemail.com
 * http://zdfmediathk.sourceforge.net/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package msearch.filmeLaden;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import msearch.daten.MSConfig;
import msearch.tool.DatumZeit;
import msearch.tool.MSConst;
import msearch.tool.MSGuiFunktionen;
import msearch.tool.MSLog;

public class MSFilmlistenSuchen {

    // damit werden die DownloadURLs zum Laden einer Filmliste gesucht
    // Liste mit den URLs zum Download der Filmliste
    public ListeFilmlistenUrls listeFilmlistenUrls_old = new ListeFilmlistenUrls();
    public ListeFilmlistenUrls listeFilmlistenUrls_akt = new ListeFilmlistenUrls();
    public ListeFilmlistenUrls listeFilmlistenUrls_diff = new ListeFilmlistenUrls();

    public String suchenOld(ArrayList<String> bereitsVersucht) {
        // passende URL zum Laden der Filmliste suchen
        String retUrl;
        updateURLsFilmlisten(true, false, false);
        retUrl = listeFilmlistenUrls_old.getRand(bereitsVersucht, 0); //eine Zufällige Adresse wählen
        if (bereitsVersucht != null) {
            bereitsVersucht.add(retUrl);
        }
        return retUrl;
    }

    public String suchenAkt(ArrayList<String> bereitsVersucht) {
        // passende URL zum Laden der Filmliste suchen
        String retUrl;
        if (listeFilmlistenUrls_akt.isEmpty()) {
            // da sich die Listen nicht ändern nur eimal pro Start laden
            updateURLsFilmlisten(false, true, false);
        }
        retUrl = (listeFilmlistenUrls_akt.getRand(bereitsVersucht, 0)); //eine Zufällige Adresse wählen
        if (bereitsVersucht != null) {
            bereitsVersucht.add(retUrl);
        }
        return retUrl;
    }

    public String suchenDiff(ArrayList<String> bereitsVersucht) {
        // passende URL zum Laden der Filmliste suchen
        String retUrl;
        if (listeFilmlistenUrls_diff.isEmpty()) {
            // da sich die Listen nicht ändern nur eimal pro Start laden
            updateURLsFilmlisten(false, false, true);
        }
        retUrl = (listeFilmlistenUrls_diff.getRand(bereitsVersucht, 0)); //eine Zufällige Adresse wählen
        if (bereitsVersucht != null) {
            bereitsVersucht.add(retUrl);
        }
        return retUrl;
    }

    public void updateURLsFilmlisten(boolean old, boolean akt, boolean diff) {
        ListeFilmlistenUrls tmp = new ListeFilmlistenUrls();
        if (old) {
            getDownloadUrlsFilmlisten(MSConst.ADRESSE_FILMLISTEN_SERVER_JSON, tmp, MSConfig.getUserAgent(), DatenFilmlisteUrl.SERVER_ART_OLD);
            if (!tmp.isEmpty()) {
                listeFilmlistenUrls_old = tmp;
            }
            if (listeFilmlistenUrls_old.size() < 5) {
                // dann gibts ein paar fest hinterlegt URLs
                listeFilmlistenUrls_old.add(new DatenFilmlisteUrl("http://85.25.49.47/json2/Filmliste-json_16_00.xz", "1", "16:40:00", getTag("09:40:00"), DatenFilmlisteUrl.SERVER_ART_OLD));
                listeFilmlistenUrls_old.add(new DatenFilmlisteUrl("http://176.28.8.161/json2/Filmliste-json_14_00.xz", "1", "14:40:00", getTag("13:40:00"), DatenFilmlisteUrl.SERVER_ART_OLD));
                listeFilmlistenUrls_old.add(new DatenFilmlisteUrl("http://176.28.8.161/json2/Filmliste-json_18_00.xz", "1", "18:40:00", getTag("16:40:00"), DatenFilmlisteUrl.SERVER_ART_OLD));
                listeFilmlistenUrls_old.add(new DatenFilmlisteUrl("http://85.25.49.47/json1/Filmliste-json_19_00.xz", "1", "19:40:00", getTag("19:40:00"), DatenFilmlisteUrl.SERVER_ART_OLD));
                listeFilmlistenUrls_old.add(new DatenFilmlisteUrl("http://176.28.8.161/json2/Filmliste-json_20_00.xz", "1", "20:40:00", getTag("20:40:00"), DatenFilmlisteUrl.SERVER_ART_OLD));
                listeFilmlistenUrls_old.add(new DatenFilmlisteUrl("http://176.28.8.161/json1/Filmliste-json_22_00.xz", "1", "22:40:00", getTag("22:40:00"), DatenFilmlisteUrl.SERVER_ART_OLD));
            }
            listeFilmlistenUrls_old.sort();
        }
        if (akt) {
            getDownloadUrlsFilmlisten(MSConst.ADRESSE_FILMLISTEN_SERVER_AKT, tmp, MSConfig.getUserAgent(), DatenFilmlisteUrl.SERVER_ART_AKT);
            if (!tmp.isEmpty()) {
                listeFilmlistenUrls_akt = tmp;
            } else if (listeFilmlistenUrls_akt.isEmpty()) {
                listeFilmlistenUrls_akt.add(new DatenFilmlisteUrl("http://www.wp11128329.server-he.de/filme/Filmliste-akt.xz", "1", "", "", DatenFilmlisteUrl.SERVER_ART_AKT));
                listeFilmlistenUrls_akt.add(new DatenFilmlisteUrl("http://mv.mynews.de/filme/Filmliste-akt.xz", "1", "", "", DatenFilmlisteUrl.SERVER_ART_AKT));
                listeFilmlistenUrls_akt.add(new DatenFilmlisteUrl("http://mv.hostingkunde.de/filme/Filmliste-akt.xz", "1", "", "", DatenFilmlisteUrl.SERVER_ART_AKT));
                listeFilmlistenUrls_akt.add(new DatenFilmlisteUrl("http://mv-1.df-kunde.de/filme/Filmliste-akt.xz", "1", "", "", DatenFilmlisteUrl.SERVER_ART_AKT));
                listeFilmlistenUrls_akt.add(new DatenFilmlisteUrl("http://mv-2.df-kunde.de/filme/Filmliste-akt.xz", "1", "", "", DatenFilmlisteUrl.SERVER_ART_AKT));
                listeFilmlistenUrls_akt.add(new DatenFilmlisteUrl("http://mv-3.df-kunde.de/filme/Filmliste-akt.xz", "1", "", "", DatenFilmlisteUrl.SERVER_ART_AKT));
            }
            listeFilmlistenUrls_akt.sort();
        }
        if (diff) {
            getDownloadUrlsFilmlisten(MSConst.ADRESSE_FILMLISTEN_SERVER_DIFF, tmp, MSConfig.getUserAgent(), DatenFilmlisteUrl.SERVER_ART_DIFF);
            if (!tmp.isEmpty()) {
                listeFilmlistenUrls_diff = tmp;
            } else if (listeFilmlistenUrls_diff.isEmpty()) {
                listeFilmlistenUrls_diff.add(new DatenFilmlisteUrl("http://www.wp11128329.server-he.de/filme/Filmliste-diff.xz", "1", "", "", DatenFilmlisteUrl.SERVER_ART_DIFF));
                listeFilmlistenUrls_diff.add(new DatenFilmlisteUrl("http://mv.mynews.de/filme/Filmliste-diff.xz", "1", "", "", DatenFilmlisteUrl.SERVER_ART_DIFF));
                listeFilmlistenUrls_diff.add(new DatenFilmlisteUrl("http://mv.hostingkunde.de/filme/Filmliste-diff.xz", "1", "", "", DatenFilmlisteUrl.SERVER_ART_DIFF));
                listeFilmlistenUrls_diff.add(new DatenFilmlisteUrl("http://mv-1.df-kunde.de/filme/Filmliste-diff.xz", "1", "", "", DatenFilmlisteUrl.SERVER_ART_DIFF));
                listeFilmlistenUrls_diff.add(new DatenFilmlisteUrl("http://mv-2.df-kunde.de/filme/Filmliste-diff.xz", "1", "", "", DatenFilmlisteUrl.SERVER_ART_DIFF));
                listeFilmlistenUrls_diff.add(new DatenFilmlisteUrl("http://mv-3.df-kunde.de/filme/Filmliste-diff.xz", "1", "", "", DatenFilmlisteUrl.SERVER_ART_DIFF));
            }
            listeFilmlistenUrls_diff.sort();
        }
        if (tmp.isEmpty()) {
            MSLog.systemMeldung(new String[]{"Es ist ein Fehler aufgetreten!",
                "Es konnten keine Updateserver zum aktualisieren der Filme",
                "gefunden werden."});
        }
    }

    private String getTag(String zeit) {
        Date tmp;
        SimpleDateFormat sdf_zeit = new SimpleDateFormat("dd.MM.yyyy__HH:mm:ss");
        try {
            tmp = sdf_zeit.parse(DatumZeit.getHeute_dd_MM_yyyy() + "__" + zeit);
            if (tmp.compareTo(new Date()) > 0) {
                return DatumZeit.getGestern_dd_MM_yyyy();
            } else {
                return DatumZeit.getHeute_dd_MM_yyyy();
            }
        } catch (Exception ex) {
        }
        return DatumZeit.getHeute_dd_MM_yyyy();
    }

    public static void getDownloadUrlsFilmlisten(String dateiUrl, ListeFilmlistenUrls listeFilmlistenUrls, String userAgent, String art) {
        //String[] ret = new String[]{""/* version */, ""/* release */, ""/* updateUrl */};
        try {
            int event;
            XMLInputFactory inFactory = XMLInputFactory.newInstance();
            inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
            XMLStreamReader parser;
            InputStreamReader inReader;
            if (MSGuiFunktionen.istUrl(dateiUrl)) {
                // eine URL verarbeiten
                int timeout = 20000; //ms
                URLConnection conn;
                conn = new URL(dateiUrl).openConnection();
                conn.setRequestProperty("User-Agent", userAgent);
                conn.setReadTimeout(timeout);
                conn.setConnectTimeout(timeout);
                inReader = new InputStreamReader(conn.getInputStream(), MSConst.KODIERUNG_UTF);
            } else {
                // eine Datei verarbeiten
                File f = new File(dateiUrl);
                if (!f.exists()) {
                    return;
                }
                inReader = new InputStreamReader(new FileInputStream(f), MSConst.KODIERUNG_UTF);
            }
            parser = inFactory.createXMLStreamReader(inReader);
            while (parser.hasNext()) {
                event = parser.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    String parsername = parser.getLocalName();
                    if (parsername.equals("Server")) {
                        //wieder ein neuer Server, toll
                        getServer(parser, listeFilmlistenUrls, art);
                    }
                }
            }
        } catch (Exception ex) {
            MSLog.fehlerMeldung(821069874, MSLog.FEHLER_ART_PROG, MSFilmlistenSuchen.class.getName(), ex, "Die URL-Filmlisten konnte nicht geladen werden: " + dateiUrl);
        }
    }

    private static void getServer(XMLStreamReader parser, ListeFilmlistenUrls listeFilmlistenUrls, String art) {
        String zeit = "";
        String datum = "";
        String serverUrl = "";
        String prio = "";
        int event;
        try {
            while (parser.hasNext()) {
                event = parser.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    //parsername = parser.getLocalName();
                    switch (parser.getLocalName()) {
                        case "URL":
                            serverUrl = parser.getElementText();
                            break;
                        case "Prio":
                            prio = parser.getElementText();
                            break;
                        case "Datum":
                            datum = parser.getElementText();
                            break;
                        case "Zeit":
                            zeit = parser.getElementText();
                            break;
                    }
                }
                if (event == XMLStreamConstants.END_ELEMENT) {
                    //parsername = parser.getLocalName();
                    if (parser.getLocalName().equals("Server")) {
                        if (!serverUrl.equals("")) {
                            //public DatenFilmUpdate(String url, String prio, String zeit, String datum, String anzahl) {
                            if (prio.equals("")) {
                                prio = DatenFilmlisteUrl.FILM_UPDATE_SERVER_PRIO_1;
                            }
                            listeFilmlistenUrls.addWithCheck(new DatenFilmlisteUrl(serverUrl, prio, zeit, datum, art));
                        }
                        break;
                    }
                }
            }
        } catch (XMLStreamException ex) {
        }

    }

    public static File ListeFilmlistenSchreiben(ListeFilmlistenUrls listeFilmlistenUrls) {
        File tmpFile = null;
        XMLOutputFactory outFactory;
        XMLStreamWriter writer;
        OutputStreamWriter out;
        final String TAG_LISTE = "Mediathek";
        final String TAG_SERVER = "Server";
        final String TAG_SERVER_URL = "URL";
        final String TAG_SERVER_DATUM = "Datum";
        final String TAG_SERVER_ZEIT = "Zeit";
        try {
            tmpFile = File.createTempFile("mediathek", null);
            tmpFile.deleteOnExit();
            outFactory = XMLOutputFactory.newInstance();
            out = new OutputStreamWriter(new FileOutputStream(tmpFile), MSConst.KODIERUNG_UTF);
            writer = outFactory.createXMLStreamWriter(out);
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeCharacters("\n");//neue Zeile
            writer.writeStartElement(TAG_LISTE);
            writer.writeCharacters("\n");//neue Zeile
            Iterator<DatenFilmlisteUrl> it = listeFilmlistenUrls.iterator();
            while (it.hasNext()) {
                DatenFilmlisteUrl d = it.next();
                writer.writeStartElement(TAG_SERVER);
                writer.writeCharacters("\n");
                // Tags schreiben: URL
                writer.writeCharacters("\t");// Tab
                writer.writeStartElement(TAG_SERVER_URL);
                writer.writeCharacters(d.arr[DatenFilmlisteUrl.FILM_UPDATE_SERVER_URL_NR]);
                writer.writeEndElement();
                writer.writeCharacters("\n");
                // fertig
                // Tags schreiben: Datum
                writer.writeCharacters("\t");// Tab
                writer.writeStartElement(TAG_SERVER_DATUM);
                writer.writeCharacters(d.arr[DatenFilmlisteUrl.FILM_UPDATE_SERVER_DATUM_NR]);
                writer.writeEndElement();
                writer.writeCharacters("\n");
                // fertig
                // Tags schreiben: Zeit
                writer.writeCharacters("\t");// Tab
                writer.writeStartElement(TAG_SERVER_ZEIT);
                writer.writeCharacters(d.arr[DatenFilmlisteUrl.FILM_UPDATE_SERVER_ZEIT_NR]);
                writer.writeEndElement();
                writer.writeCharacters("\n");
                // fertig
                writer.writeEndElement();
                writer.writeCharacters("\n");
            }
            // Schließen
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            MSLog.fehlerMeldung(634978521, MSLog.FEHLER_ART_PROG, MSFilmlistenSuchen.class.getName(), ex, "Die URL-Filmlisten konnten nicht geschrieben werden");
        }
        return tmpFile;
    }
}
