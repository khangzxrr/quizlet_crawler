/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import dto.Bank;
import dto.Key;
import j2html.TagCreator;
import static j2html.TagCreator.a;
import static j2html.TagCreator.attrs;
import static j2html.TagCreator.body;
import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.span;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.i;
import static j2html.TagCreator.join;
import static j2html.TagCreator.li;
import static j2html.TagCreator.link;
import static j2html.TagCreator.main;
import static j2html.TagCreator.script;
import static j2html.TagCreator.title;
import static j2html.TagCreator.ul;
import j2html.tags.Tag;
import java.util.List;

/**
 *
 * @author skyho
 */
public class HTMLUtil {

    public static String replaceSpecialHTMLCharacters(String str) {
        String out = "";
        for (char c : str.toCharArray()) {
            switch (c) {
                case '<':
                    out += "&lt;";
                    break;
                case '>':
                    out += "&gt;";
                    break;
                case '\'':
                    out += "&#39;";
                    break;
                case '\"':
                    out += "&quot;";
                    break;
                case '&':
                    out += "&amp;";
                    break;
                default:
                    out += c;
                    break;
            }

        }
        return out;
    }

    public static Tag getHTMLSource(Bank bank, List<Key> keys) {
        return div(
                a(attrs(".url"), bank.getName() + " by " + bank.getOwner()).withHref(bank.getSourceUrl()),
                    each(keys, key -> div(attrs(".row.card-panel"),
                            join(
                                    div(attrs(".col.s6.question"), 
                                            join(span(key.getId() + "").withClass("new badge").withData("badge-caption", ""), 
                                                 replaceSpecialHTMLCharacters(key.getQuestion()).replaceAll("\n", "<br>")
                                            )
                                    ),
                                    div(attrs(".col.s6.answer"), 
                                            join(replaceSpecialHTMLCharacters(key.getAnswer()).replaceAll("\n", "<br>")
                                            )
                                    )
                            )
                        )
                    )
                );

    }

    public static String generateHTMLPage(List<Tag> divs, List<Bank> banks) {
        return html(
                head(
                        title("vo ngoc khang crawler!"),
                        link().withRel("stylesheet").withHref("materialize.min.css"),
                        link().withRel("stylesheet").withHref("main.css")
                ),
                body(
                        ul(
                                each(banks,
                                        bank -> li(
                                                a(bank.getName() + " : " + bank.getOwner()).withHref("#div" + banks.indexOf(bank))
                                        )
                                )
                        ).withId("slide-out").withClass("sidenav"),
                        div(attrs(".fixed-action-btn"),
                                a(attrs(".btn-floating .btn-large red .sidenav-trigger"),
                                        i(attrs(".large .material-icons")).withText("mode_edit")
                                ).withHref("#").withData("target", "slide-out")
                        ),
                        div(attrs(".container"), each(divs, div -> div.withId("div" + divs.indexOf(div)))),
                        script().withSrc("materialize.min.js"),
                        script().withSrc("main.js")
                )
        ).renderFormatted();
    }
}
