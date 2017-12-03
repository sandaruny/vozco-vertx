package com.zegates.vozco.services;

import org.apache.commons.io.IOUtils;
import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;
import org.lightcouch.View;


import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * Created by sandaruwann on 10/16/17.
 */
public class CouchDBServiceImpl implements CouchDBService {

    private CouchDbClient dbClient;

    public CouchDBServiceImpl() {
        dbClient = new CouchDbClient("mycustomer", true, "http", "127.0.0.1", 5984, "t", "t");
    }

    public String getDocument(String doc) throws IOException {
        System.out.println(doc);
        if (doc.contains("_view/")) {
            System.out.println(doc);
            View view = dbClient.view(doc.substring("_view/".length()))
                    .includeDocs(true)
                    .startKey("start-key")
                    .endKey("end-key")
                    .limit(10);
            return view.queryForString();
        }

        if (dbClient.contains(doc)){
            InputStream documentStream = dbClient.find(doc);
            StringWriter writer = new StringWriter();
            IOUtils.copy(documentStream, writer, "UTF-8");
            String document = writer.toString();
            System.out.println(document);
            documentStream.close();
            return document;
        }
        return null;
    }

    public String getView(String design, String view) throws IOException {
        String viewUrl = design + "/" + view;
        View viewDoc = dbClient.view(viewUrl);
//                    .includeDocs(true)
//                    .descending(true)
//                    .startKey("0")
//                    .endKey("9999999999999999")
//                    .limit(10);

//        view.
        InputStream documentStream = null;
        try {   
            documentStream = viewDoc.queryForStream();
            StringWriter writer = new StringWriter();
            IOUtils.copy(documentStream, writer, "UTF-8");
            String document = writer.toString();
            System.out.println(document);

            return document;
        } catch (NoDocumentException ex) {
            ex.printStackTrace();
            return "{}";
        } finally {
            if (documentStream != null) {
                documentStream.close();
            }
        }


    }


}
