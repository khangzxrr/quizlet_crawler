/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vongockhang.crawler;

import dao.QuestionBankManager;
import dto.Bank;
import dto.Key;
import j2html.tags.Tag;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import util.HTMLUtil;

/**
 *
 * @author skyho
 */
public class SourceExporter implements Runnable {
    String exportName;
    public SourceExporter(String exportName) {
        this.exportName = exportName;
    }

    
    @Override
    public void run() {
        try {
            QuestionBankManager manager = new QuestionBankManager();

            manager.removeDuplicateKeys(); //reduce duplicated keys

            List<Tag> renderedSource = new ArrayList<>();

            List<Bank> banks = manager.getBanks();
            for (Bank bank : banks) {
                List<Key> keys = manager.getKeysOfBank(bank.getId());
                renderedSource.add(HTMLUtil.getHTMLSource(bank, keys));
            }
            PrintWriter outputHTML = new PrintWriter("output/" + exportName + ".html");
            outputHTML.print(HTMLUtil.generateHTMLPage(renderedSource, banks));
            outputHTML.close();

            System.out.println("Generated " + banks.size() + " sources");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
