package dev.dc.james.common;

import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;
import javax.swing.JTextArea;

public class TextAreaHandler extends StreamHandler {

    JTextArea textArea;
    long time;

    String log;

    public TextAreaHandler(JTextArea textArea) {
        this.textArea = textArea;
        this.textArea.setText("");

        time = System.currentTimeMillis() / 1000L;
    }

    @Override
    public void publish(LogRecord record) {
        super.publish(record);
        flush();
        log += record.getLevel().getName() + " " + record.getMessage();

        if (log.length() > 10000) {
            log = "";
        }

        if (textArea != null) {
            if (textArea.getText().length() > 50000) {
                textArea.setText("");
            }

            textArea.append(getFormatter().format(record));
            textArea.append("\n");

            textArea.setCaretPosition(textArea.getDocument().getLength());

            textArea.validate();
            textArea.repaint();
        }
    }
}
