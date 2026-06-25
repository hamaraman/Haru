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
                .addProtocols("img", "src", "http", "https")
                .addProtocols("video", "src", "http", "https")
                .addProtocols("source", "src", "http", "https")
                .addProtocols("a", "href", "http", "https", "mailto")
                // 상대경로(/uploads/...)를 그대로 보존한다. baseUri를 주어 프로토콜 검사를
                // 통과시키되, preserveRelativeLinks(true)로 원본 상대 URL을 유지한다.
                .preserveRelativeLinks(true);
        return Jsoup.clean(html, "http://localhost/", safelist);
    }
}
