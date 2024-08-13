package com.email.modifications;

import java.util.List;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;

import lombok.SneakyThrows;

public class MyFooter implements IEventHandler {
    float fullwidth[] = {500f};
    protected Document doc;
    private List<String> tncList;
    public MyFooter(Document doc, List<String> tncList) {
        this.doc = doc;
        this.tncList = tncList;
        
    }

    @SneakyThrows
    @Override
    public void handleEvent(Event currentEvent) {

        float footerY = doc.getBottomMargin();
        
        Table tb = new Table(fullwidth);
        tb.addCell(new Cell().add("TERMS AND CONDITIONS\n").setBold().setBorder(Border.NO_BORDER));
        for (String tnc : tncList) {

            tb.addCell(new Cell().add(tnc).setBorder(Border.NO_BORDER));
        }
        tb.setFixedPosition(40f, footerY, 530f);
        this.doc.add(tb);




    }
}


