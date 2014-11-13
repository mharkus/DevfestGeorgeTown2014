package com.marctan.gdgpenangwear.wolfram;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class WolframAPIParser {
    private static final String ns = null;
    private static final String TAG = WolframAPIParser.class.getName();

    public List<Pod> parse(StringReader in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List<Pod> readFeed(XmlPullParser parser) {
        List<Pod> pods = new ArrayList<Pod>();
        try {
            parser.require(XmlPullParser.START_TAG, ns, "queryresult");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals("pod")) {
                    pods.add(readEntry(parser));
                } else {
                    skip(parser);
                }
            }
        } catch (XmlPullParserException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return pods;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private Pod readEntry(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "pod");
        String id = parser.getAttributeValue(ns, "id");
        String title = parser.getAttributeValue(ns, "title");
        String data = null;
        String image = null;
        boolean inSubPod = false;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("subpod")) {
                inSubPod = true;
            } else if (name.equals("plaintext") && inSubPod) {
                data = readPlaintext(parser);
            } else if (name.equals("img") && inSubPod) {
                image = readImage(parser);
            } else {
                skip(parser);
            }
        }

        return new Pod(id, title, data, image);
    }

    private String readPlaintext(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "plaintext");
        String data = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "plaintext");
        return data;
    }

    private String readImage(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "img");
        String src = parser.getAttributeValue(ns, "src");
        Log.d(TAG, "src: " + src);
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, ns, "img");
        return src;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}
