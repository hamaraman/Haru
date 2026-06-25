package org.example.asq.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class HtmlSanitizer {

    public static String sanitize(String html) {
        if (html == null || html.isBlank()) return "";
        Safelist safelist = Safelist.relaxed()
                .addTags("span", "font", "iframe", "figure", "figcaption", "s", "u", "video", "source")
                .addAttributes(":all", "style", "class")
                .addAttributes("iframe", "src", "width", "height",
                        "frameborder", "allowfullscreen", "allow")
                .addAttributes("img", "src", "alt", "width", "height")
                .addAttributes("video", "src", "controls", "width", "height", "preload")
                .addAttributes("source", "src", "type")
                .addProtocols("iframe", "src", "https")
                .addProtocols("img", "src", "http", "https", "/")
                .addProtocols("video", "src", "http", "https", "/")
                .addProtocols("source", "src", "http", "https", "/")
                .addProtocols("a", "href", "http", "https", "mailto");
        return Jsoup.clean(html, safelist);
    }
}
