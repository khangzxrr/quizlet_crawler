/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import dto.Bank;
import dto.Key;
import static j2html.TagCreator.a;
import static j2html.TagCreator.attrs;
import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.join;
import static j2html.TagCreator.link;
import static j2html.TagCreator.main;
import static j2html.TagCreator.title;
import j2html.tags.Tag;
import java.util.List;

/**
 *
 * @author skyho
 */
public class HTMLUtil {

    public static Tag getHTMLSource(Bank bank, List<Key> keys) {
        return div(
                a(attrs(".url"),bank.getName() + " by " + bank.getOwner()).withHref(bank.getSourceUrl()),
                each(keys, key -> div(attrs(".row.card-panel"), 
                                      join(
                                         div(attrs(".col.s6.question"), join(key.getQuestion())
                                         ),
                                         div(attrs(".col.s6.answer"), join(key.getAnswer())
                                         )
                                      )
                                     )
                    )
        );

    }

    public static String generateHTMLPage(List<Tag> divs) {
        return html(
                head(
                        title("vo ngoc khang crawler!"),
                        link().withRel("stylesheet").withHref("materialize.min.css"),
                        link().withRel("stylesheet").withHref("main.css")
                ),
                main(
                        div(attrs(".container"), each(divs, div -> div))
                )
        ).renderFormatted();
    }
}
